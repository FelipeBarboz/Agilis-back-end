package com.agilis.api.infrastructure.persistence.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {
    Optional<PaymentEntity> findByBookingId(UUID bookingId);
    Optional<PaymentEntity> findByMercadoPagoPaymentId(String mercadoPagoPaymentId);
    Optional<PaymentEntity> findByMercadoPagoPreferenceId(String mercadoPagoPreferenceId);
}
