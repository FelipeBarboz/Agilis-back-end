package com.agilis.api.domain.payment;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Payment {

    private final UUID id;
    private final UUID bookingId;
    private String mercadoPagoPaymentId;
    private String mercadoPagoPreferenceId;
    private PaymentStatus status;
    private final BigDecimal amount;
    private String paymentMethod;
    private String paymentType;
    private LocalDateTime paidAt;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Payment(UUID id, UUID bookingId, String mercadoPagoPaymentId, String mercadoPagoPreferenceId,
                    PaymentStatus status, BigDecimal amount, String paymentMethod, String paymentType,
                    LocalDateTime paidAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.mercadoPagoPaymentId = mercadoPagoPaymentId;
        this.mercadoPagoPreferenceId = mercadoPagoPreferenceId;
        this.status = status;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentType = paymentType;
        this.paidAt = paidAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Payment createPending(UUID bookingId, String preferenceId, BigDecimal amount) {
        LocalDateTime now = LocalDateTime.now();
        return new Payment(UUID.randomUUID(), bookingId, null, preferenceId,
                PaymentStatus.PENDING, amount, null, null, null, now, now);
    }

    public static Payment reconstitute(UUID id, UUID bookingId, String mercadoPagoPaymentId, String mercadoPagoPreferenceId,
                                       PaymentStatus status, BigDecimal amount, String paymentMethod, String paymentType,
                                       LocalDateTime paidAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Payment(id, bookingId, mercadoPagoPaymentId, mercadoPagoPreferenceId,
                status, amount, paymentMethod, paymentType, paidAt, createdAt, updatedAt);
    }

    public void markApproved(String mercadoPagoPaymentId, String paymentMethod, String paymentType, LocalDateTime paidAt) {
        this.mercadoPagoPaymentId = mercadoPagoPaymentId;
        this.status = PaymentStatus.APPROVED;
        this.paymentMethod = paymentMethod;
        this.paymentType = paymentType;
        this.paidAt = paidAt != null ? paidAt : LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStatus(PaymentStatus newStatus, String mercadoPagoPaymentId) {
        if (mercadoPagoPaymentId != null && !mercadoPagoPaymentId.isBlank()) {
            this.mercadoPagoPaymentId = mercadoPagoPaymentId;
        }
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isApproved() {
        return this.status == PaymentStatus.APPROVED;
    }
}
