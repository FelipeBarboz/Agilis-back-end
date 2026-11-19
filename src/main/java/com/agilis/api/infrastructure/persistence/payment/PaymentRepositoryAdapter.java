package com.agilis.api.infrastructure.persistence.payment;

import com.agilis.api.domain.payment.Payment;
import com.agilis.api.domain.payment.PaymentRepository;

import java.util.Optional;
import java.util.UUID;

public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;

    public PaymentRepositoryAdapter(PaymentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Payment save(Payment payment) {
        jpaRepository.save(toEntity(payment));
        return payment;
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Payment> findByBookingId(UUID bookingId) {
        return jpaRepository.findByBookingId(bookingId).map(this::toDomain);
    }

    @Override
    public Optional<Payment> findByMercadoPagoPaymentId(String mercadoPagoPaymentId) {
        return jpaRepository.findByMercadoPagoPaymentId(mercadoPagoPaymentId).map(this::toDomain);
    }

    @Override
    public Optional<Payment> findByMercadoPagoPreferenceId(String preferenceId) {
        return jpaRepository.findByMercadoPagoPreferenceId(preferenceId).map(this::toDomain);
    }

    private PaymentEntity toEntity(Payment domain) {
        PaymentEntity entity = new PaymentEntity();
        entity.setId(domain.getId());
        entity.setBookingId(domain.getBookingId());
        entity.setMercadoPagoPaymentId(domain.getMercadoPagoPaymentId());
        entity.setMercadoPagoPreferenceId(domain.getMercadoPagoPreferenceId());
        entity.setStatus(domain.getStatus());
        entity.setAmount(domain.getAmount());
        entity.setPaymentMethod(domain.getPaymentMethod());
        entity.setPaymentType(domain.getPaymentType());
        entity.setPaidAt(domain.getPaidAt());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private Payment toDomain(PaymentEntity entity) {
        return Payment.reconstitute(
                entity.getId(),
                entity.getBookingId(),
                entity.getMercadoPagoPaymentId(),
                entity.getMercadoPagoPreferenceId(),
                entity.getStatus(),
                entity.getAmount(),
                entity.getPaymentMethod(),
                entity.getPaymentType(),
                entity.getPaidAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
