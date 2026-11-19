package com.agilis.api.application.payment;

import com.agilis.api.domain.booking.Booking;
import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.booking.BookingStatus;
import com.agilis.api.domain.payment.Payment;
import com.agilis.api.domain.payment.PaymentGateway;
import com.agilis.api.domain.payment.PaymentRepository;
import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServiceRepository;
import com.agilis.api.domain.user.User;
import com.agilis.api.domain.user.UserRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

public class CreatePaymentPreferenceUseCase {

    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;
    private final String defaultFrontendUrl;
    private final String webhookNotificationUrl;

    public CreatePaymentPreferenceUseCase(
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            UserRepository userRepository,
            PaymentRepository paymentRepository,
            PaymentGateway paymentGateway,
            String defaultFrontendUrl,
            String webhookNotificationUrl
    ) {
        this.bookingRepository = bookingRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.paymentGateway = paymentGateway;
        this.defaultFrontendUrl = defaultFrontendUrl;
        this.webhookNotificationUrl = webhookNotificationUrl;
    }

    public Output execute(Input input) {
        UUID bookingId = UUID.fromString(input.bookingId());
        UUID requesterId = UUID.fromString(input.requesterId());

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado."));

        if (!booking.getClientId().equals(requesterId)) {
            throw new IllegalStateException("Apenas o cliente dono do agendamento pode realizar o pagamento.");
        }

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Este agendamento já foi confirmado e pago.");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Não é possível realizar o pagamento de um agendamento cancelado.");
        }

        Service service = serviceRepository.findById(booking.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado."));

        BigDecimal basePrice = service.getPrice();
        BigDecimal discount = booking.getDiscountAmount();
        BigDecimal effectivePrice = (discount != null)
                ? basePrice.subtract(discount).max(BigDecimal.ZERO)
                : basePrice;

        User client = userRepository.findById(booking.getClientId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));

        String baseFrontend = input.frontendBaseUrl() != null && !input.frontendBaseUrl().isBlank()
                ? input.frontendBaseUrl().replaceAll("/+$", "")
                : this.defaultFrontendUrl.replaceAll("/+$", "");

        String successUrl = input.customSuccessUrl() != null && !input.customSuccessUrl().isBlank()
                ? input.customSuccessUrl()
                : baseFrontend + "/payment?status=success&bookingId=" + bookingId;

        String failureUrl = input.customFailureUrl() != null && !input.customFailureUrl().isBlank()
                ? input.customFailureUrl()
                : baseFrontend + "/payment?status=failure&bookingId=" + bookingId;

        String pendingUrl = input.customPendingUrl() != null && !input.customPendingUrl().isBlank()
                ? input.customPendingUrl()
                : baseFrontend + "/payment?status=pending&bookingId=" + bookingId;

        PaymentGateway.CreatePreferenceCommand command = new PaymentGateway.CreatePreferenceCommand(
                bookingId.toString(),
                service.getTitle(),
                effectivePrice,
                client.getName(),
                client.getEmail(),
                successUrl,
                failureUrl,
                pendingUrl,
                this.webhookNotificationUrl
        );

        PaymentGateway.PreferenceResult preference = paymentGateway.createPreference(command);

        Payment payment = paymentRepository.findByBookingId(bookingId)
                .map(existing -> {
                    Payment updated = Payment.reconstitute(
                            existing.getId(),
                            existing.getBookingId(),
                            existing.getMercadoPagoPaymentId(),
                            preference.preferenceId(),
                            existing.getStatus(),
                            effectivePrice,
                            existing.getPaymentMethod(),
                            existing.getPaymentType(),
                            existing.getPaidAt(),
                            existing.getCreatedAt(),
                            java.time.LocalDateTime.now()
                    );
                    return paymentRepository.save(updated);
                })
                .orElseGet(() -> paymentRepository.save(
                        Payment.createPending(bookingId, preference.preferenceId(), effectivePrice)
                ));

        return new Output(
                preference.preferenceId(),
                preference.initPoint(),
                preference.sandboxInitPoint(),
                payment.getAmount(),
                bookingId.toString()
        );
    }

    @Schema(name = "CreatePaymentPreferenceInput")
    public record Input(
            String bookingId,
            String requesterId,
            String frontendBaseUrl,
            String customSuccessUrl,
            String customFailureUrl,
            String customPendingUrl
    ) {}

    @Schema(name = "CreatePaymentPreferenceOutput")
    public record Output(
            String preferenceId,
            String initPoint,
            String sandboxInitPoint,
            BigDecimal amount,
            String bookingId
    ) {}
}
