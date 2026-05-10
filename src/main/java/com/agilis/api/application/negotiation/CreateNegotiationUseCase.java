package com.agilis.api.application.negotiation;

import com.agilis.api.domain.booking.Booking;
import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.booking.BookingStatus;
import com.agilis.api.domain.negotiation.Negotiation;
import com.agilis.api.domain.negotiation.NegotiationRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class CreateNegotiationUseCase {

    private final NegotiationRepository negotiationRepository;
    private final BookingRepository bookingRepository;

    public CreateNegotiationUseCase(
            NegotiationRepository negotiationRepository,
            BookingRepository bookingRepository
    ) {
        this.negotiationRepository = negotiationRepository;
        this.bookingRepository     = bookingRepository;
    }

    public Output execute(Input input) {
        UUID bookingId  = UUID.fromString(input.bookingId());
        UUID senderId   = UUID.fromString(input.senderId());
        UUID receiverId = UUID.fromString(input.receiverId());

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Negotiations can only be initiated for pending bookings.");
        }

        if (!booking.getClientId().equals(senderId) && !booking.getClientId().equals(receiverId)) {
            throw new IllegalStateException("The client must be part of the negotiation.");
        }

        negotiationRepository.findPendingByBookingId(bookingId).ifPresent(n -> {
            throw new IllegalStateException("There is already a pending negotiation for this booking.");
        });

        Negotiation negotiation = Negotiation.create(bookingId, senderId, receiverId, input.amount());
        negotiationRepository.save(negotiation);

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

    public record Input(
            String bookingId,
            String senderId,
            String receiverId,
            BigDecimal amount
    ) {}

    public record Output(
            String negotiationId,
            String bookingId,
            String senderId,
            String receiverId,
            BigDecimal amount,
            String status,
            LocalDateTime sentAt
    ) {}
}