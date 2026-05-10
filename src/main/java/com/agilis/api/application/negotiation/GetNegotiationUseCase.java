package com.agilis.api.application.negotiation;

import com.agilis.api.domain.negotiation.Negotiation;
import com.agilis.api.domain.negotiation.NegotiationRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class GetNegotiationUseCase {

    private final NegotiationRepository negotiationRepository;

    public GetNegotiationUseCase(NegotiationRepository negotiationRepository) {
        this.negotiationRepository = negotiationRepository;
    }

    public List<Output> executeByBooking(String bookingId) {
        return negotiationRepository.findAllByBookingId(UUID.fromString(bookingId))
                .stream()
                .map(this::toOutput)
                .toList();
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