package com.agilis.api.application.negotiation;

import com.agilis.api.domain.negotiation.Negotiation;
import com.agilis.api.domain.negotiation.NegotiationRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class RespondNegotiationUseCase {

    private final NegotiationRepository negotiationRepository;

    public RespondNegotiationUseCase(NegotiationRepository negotiationRepository) {
        this.negotiationRepository = negotiationRepository;
    }

    public Output execute(Input input) {
        UUID negotiationId = UUID.fromString(input.negotiationId());
        UUID requesterId   = UUID.fromString(input.requesterId());

        Negotiation negotiation = negotiationRepository.findById(negotiationId)
                .orElseThrow(() -> new IllegalArgumentException("Negotiation not found."));

        if (!negotiation.getReceiverId().equals(requesterId)) {
            throw new IllegalStateException("Only the recipient can respond to the negotiation.");
        }

        return switch (input.action()) {
            case ACCEPT -> {
                negotiation.accept();
                negotiationRepository.save(negotiation);
                yield toOutput(negotiation);
            }
            case REJECT -> {
                negotiation.reject();
                negotiationRepository.save(negotiation);
                yield toOutput(negotiation);
            }
            case COUNTER -> {
                if (input.counterAmount() == null) {
                    throw new IllegalArgumentException("The counteroffer amount cannot be null.");
                }
                Negotiation counter = negotiation.counter(requesterId, input.counterAmount());
                negotiationRepository.save(negotiation);
                negotiationRepository.save(counter);
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

    public record Input(
            String negotiationId,
            String requesterId,
            Action action,
            BigDecimal counterAmount
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

    public enum Action {
        ACCEPT,
        REJECT,
        COUNTER
    }
}