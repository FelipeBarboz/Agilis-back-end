package com.agilis.api.domain.payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(UUID id);
    Optional<Payment> findByBookingId(UUID bookingId);
    Optional<Payment> findByMercadoPagoPaymentId(String mercadoPagoPaymentId);
    Optional<Payment> findByMercadoPagoPreferenceId(String preferenceId);
}
