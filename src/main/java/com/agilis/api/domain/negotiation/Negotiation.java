package com.agilis.api.domain.negotiation;

import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Negotiation {

    private final UUID id;
    private final UUID bookingId;
    private final UUID senderId;
    private final UUID receiverId;
    private BigDecimal amount;
    private NegotiationStatus status;
    private final LocalDateTime sentAt;
    private final LocalDateTime createdAt;

    private Negotiation(UUID id, UUID bookingId, UUID senderId, UUID receiverId, BigDecimal amount, NegotiationStatus status, LocalDateTime sentAt, LocalDateTime createdAt) {
        this.id         = id;
        this.bookingId  = bookingId;
        this.senderId   = senderId;
        this.receiverId = receiverId;
        this.amount     = validateAmount(amount);
        this.status     = status;
        this.sentAt     = sentAt;
        this.createdAt  = createdAt;
    }

    public static Negotiation create(UUID bookingId, UUID senderId, UUID receiverId, BigDecimal amount) {
        LocalDateTime now = LocalDateTime.now();
        return new Negotiation(
                UUID.randomUUID(),
                bookingId,
                senderId,
                receiverId,
                amount,
                NegotiationStatus.PENDING,
                now,
                now
        );
    }

    public static Negotiation reconstitute(UUID id, UUID bookingId, UUID senderId, UUID receiverId, BigDecimal amount, NegotiationStatus status, LocalDateTime sentAt, LocalDateTime createdAt) {
        return new Negotiation(id, bookingId, senderId, receiverId, amount, status, sentAt, createdAt);
    }

    public void accept() {
        validateTransition(NegotiationStatus.ACCEPTED);
        this.status = NegotiationStatus.ACCEPTED;
    }

    public void reject() {
        validateTransition(NegotiationStatus.REJECTED);
        this.status = NegotiationStatus.REJECTED;
    }

    public Negotiation counter(UUID newSenderId, BigDecimal newAmount) {
        validateTransition(NegotiationStatus.COUNTERED);
        this.status = NegotiationStatus.COUNTERED;

        // vai retornar uma nova negociacao, mas com os papeis invertidos
        return Negotiation.create(
                this.bookingId,
                newSenderId,
                this.senderId,
                newAmount
        );
    }

    private void validateTransition(NegotiationStatus target) {
        if (this.status != NegotiationStatus.PENDING) {
            throw new IllegalStateException(
                    "Invalid transaction: " + this.status + " → " + target
            );
        }
    }

    private BigDecimal validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("The value of the transaction must be greater than 0.");
        }
        return amount;
    }
}