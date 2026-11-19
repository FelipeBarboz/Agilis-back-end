package com.agilis.api.application.payment;

import com.agilis.api.domain.booking.Booking;
import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.payment.Payment;
import com.agilis.api.domain.payment.PaymentRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class GetPaymentStatusUseCase {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public GetPaymentStatusUseCase(PaymentRepository paymentRepository, BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    public Output execute(Input input) {
        UUID bookingId = UUID.fromString(input.bookingId());
        UUID requesterId = UUID.fromString(input.requesterId());

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado."));

        if (!booking.getClientId().equals(requesterId)) {
            throw new IllegalStateException("Acesso não autorizado ao status deste pagamento.");
        }

        return paymentRepository.findByBookingId(bookingId)
                .map(p -> new Output(
                        p.getId().toString(),
                        p.getBookingId().toString(),
                        p.getMercadoPagoPaymentId(),
                        p.getMercadoPagoPreferenceId(),
                        p.getStatus().name(),
                        p.getAmount(),
                        p.getPaymentMethod(),
                        p.getPaymentType(),
                        p.getPaidAt(),
                        p.getCreatedAt()
                ))
                .orElse(new Output(
                        null,
                        bookingId.toString(),
                        null,
                        null,
                        "NOT_CREATED",
                        BigDecimal.ZERO,
                        null,
                        null,
                        null,
                        null
                ));
    }

    @Schema(name = "GetPaymentStatusInput")
    public record Input(String bookingId, String requesterId) {}

    @Schema(name = "GetPaymentStatusOutput")
    public record Output(
            String id,
            String bookingId,
            String mercadoPagoPaymentId,
            String mercadoPagoPreferenceId,
            String status,
            BigDecimal amount,
            String paymentMethod,
            String paymentType,
            LocalDateTime paidAt,
            LocalDateTime createdAt
    ) {}
}
