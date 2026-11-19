package com.agilis.api.application.payment;

import com.agilis.api.domain.booking.Booking;
import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.booking.BookingStatus;
import com.agilis.api.domain.notification.WebhookDispatcher;
import com.agilis.api.domain.notification.WebhookEventType;
import com.agilis.api.domain.payment.Payment;
import com.agilis.api.domain.payment.PaymentGateway;
import com.agilis.api.domain.payment.PaymentRepository;
import com.agilis.api.domain.payment.PaymentStatus;
import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServiceRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;
import java.util.UUID;

public class ProcessPaymentWebhookUseCase {

    private final PaymentGateway paymentGateway;
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final WebhookDispatcher webhookDispatcher;

    public ProcessPaymentWebhookUseCase(
            PaymentGateway paymentGateway,
            PaymentRepository paymentRepository,
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            WebhookDispatcher webhookDispatcher
    ) {
        this.paymentGateway = paymentGateway;
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.serviceRepository = serviceRepository;
        this.webhookDispatcher = webhookDispatcher;
    }

    public Output execute(Input input) {
        String paymentId = extractPaymentId(input);
        if (paymentId == null || paymentId.isBlank()) {
            return new Output(false, "IGNORED", null, "No payment ID found in webhook payload");
        }

        PaymentGateway.PaymentDetails details;
        try {
            details = paymentGateway.getPaymentDetails(paymentId);
        } catch (Exception e) {
            return new Output(false, "ERROR", null, "Failed to retrieve payment details: " + e.getMessage());
        }

        if (details.externalReference() == null || details.externalReference().isBlank()) {
            return new Output(false, "IGNORED", null, "No externalReference (bookingId) in payment details");
        }

        UUID bookingId;
        try {
            bookingId = UUID.fromString(details.externalReference());
        } catch (IllegalArgumentException e) {
            return new Output(false, "INVALID_REFERENCE", null, "externalReference is not a valid UUID: " + details.externalReference());
        }

        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return new Output(false, "BOOKING_NOT_FOUND", bookingId.toString(), "Booking not found");
        }

        PaymentStatus newStatus = PaymentStatus.fromMercadoPago(details.status());

        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseGet(() -> paymentRepository.findByMercadoPagoPaymentId(paymentId)
                        .orElseGet(() -> Payment.createPending(bookingId, null, details.amount())));

        if (newStatus == PaymentStatus.APPROVED) {
            payment.markApproved(
                    paymentId,
                    details.paymentMethodId(),
                    details.paymentTypeId(),
                    details.dateApproved()
            );

            if (booking.getStatus() == BookingStatus.PENDING) {
                booking.confirm();
                bookingRepository.save(booking);

                UUID storeId = serviceRepository.findById(booking.getServiceId())
                        .map(Service::getStoreId)
                        .orElse(null);

                if (storeId != null) {
                    webhookDispatcher.dispatch(
                            storeId,
                            WebhookEventType.BOOKING_CONFIRMED,
                            Map.of(
                                    "bookingId", booking.getId().toString(),
                                    "clientId", booking.getClientId().toString(),
                                    "serviceId", booking.getServiceId().toString(),
                                    "scheduledAt", booking.getScheduledAt().toString(),
                                    "paymentId", paymentId,
                                    "amount", details.amount() != null ? details.amount().toString() : "0"
                            )
                    );
                }
            }
        } else {
            payment.updateStatus(newStatus, paymentId);
        }

        paymentRepository.save(payment);

        return new Output(true, newStatus.name(), bookingId.toString(), "Payment processed with status: " + newStatus);
    }

    private String extractPaymentId(Input input) {
        if (input.dataId() != null && !input.dataId().isBlank()) {
            return input.dataId();
        }
        if (input.id() != null && !input.id().isBlank()) {
            return input.id();
        }
        return null;
    }

    @Schema(name = "ProcessPaymentWebhookInput")
    public record Input(
            String topic,
            String type,
            String action,
            String dataId,
            String id
    ) {}

    @Schema(name = "ProcessPaymentWebhookOutput")
    public record Output(
            boolean processed,
            String status,
            String bookingId,
            String message
    ) {}
}
