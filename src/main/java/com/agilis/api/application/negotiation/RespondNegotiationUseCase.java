package com.agilis.api.application.negotiation;

import com.agilis.api.domain.booking.Booking;
import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.negotiation.Negotiation;
import com.agilis.api.domain.negotiation.NegotiationRepository;
import com.agilis.api.domain.notification.WebhookDispatcher;
import com.agilis.api.domain.notification.WebhookEventType;
import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServiceRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class RespondNegotiationUseCase {

    private final NegotiationRepository negotiationRepository;
    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final WebhookDispatcher webhookDispatcher;

    public RespondNegotiationUseCase(
            NegotiationRepository negotiationRepository,
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            WebhookDispatcher webhookDispatcher
    ) {
        this.negotiationRepository = negotiationRepository;
        this.bookingRepository     = bookingRepository;
        this.serviceRepository     = serviceRepository;
        this.webhookDispatcher     = webhookDispatcher;
    }

    public Output execute(Input input) {
        UUID negotiationId = UUID.fromString(input.negotiationId());
        UUID requesterId   = UUID.fromString(input.requesterId());

        Negotiation negotiation = negotiationRepository.findById(negotiationId)
                .orElseThrow(() -> new IllegalArgumentException("Negotiation not found."));

        if (!negotiation.getReceiverId().equals(requesterId)) {
            throw new IllegalStateException("Only the recipient can respond to the negotiation.");
        }

        Booking booking = bookingRepository.findById(negotiation.getBookingId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        Service service = serviceRepository.findById(booking.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Service not found."));

        UUID storeId = service.getStoreId();

        return switch (input.action()) {
            case ACCEPT -> {
                negotiation.accept();
                negotiationRepository.save(negotiation);
                webhookDispatcher.dispatch(
                        storeId,
                        WebhookEventType.NEGOTIATION_ACCEPTED,
                        Map.of("negotiationId", negotiation.getId().toString(), "amount", negotiation.getAmount().toString())
                );
                yield toOutput(negotiation);
            }
            case REJECT -> {
                negotiation.reject();
                negotiationRepository.save(negotiation);
                webhookDispatcher.dispatch(
                        storeId,
                        WebhookEventType.NEGOTIATION_REJECTED,
                        Map.of("negotiationId", negotiation.getId().toString(), "amount", negotiation.getAmount().toString())
                );
                yield toOutput(negotiation);
            }
            case COUNTER -> {
                if (input.counterAmount() == null) {
                    throw new IllegalArgumentException("The counteroffer amount cannot be null.");
                }
                Negotiation counter = negotiation.counter(requesterId, input.counterAmount());
                negotiationRepository.save(negotiation);
                negotiationRepository.save(counter);
                webhookDispatcher.dispatch(
                        storeId,
                        WebhookEventType.NEGOTIATION_COUNTERED,
                        Map.of("negotiationId", counter.getId().toString(), "amount", counter.getAmount().toString())
                );
                yield toOutput(counter);
            }
        };
    }

    private Output toOutput(Negotiation negotiation) {
        return new Output(
                negotiation.getId().toString(),
                negotiation.getBookingId().toString(),
                negotiation.getSenderId().toString(),
                negotiation.getReceiverId().toString(),
                negotiation.getAmount(),
                negotiation.getStatus().name(),
                negotiation.getSentAt()
        );
    }

    @Schema(name = "RespondNegotiationInput")
    public record Input(String negotiationId, String requesterId, Action action, BigDecimal counterAmount) {}

    @Schema(name = "RespondNegotiationOutput")
    public record Output(String negotiationId, String bookingId, String senderId, String receiverId, BigDecimal amount, String status, LocalDateTime sentAt) {}

    public enum Action {
        ACCEPT,
        REJECT,
        COUNTER
    }
}