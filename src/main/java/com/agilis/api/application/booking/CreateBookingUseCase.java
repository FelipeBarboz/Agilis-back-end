package com.agilis.api.application.booking;

import com.agilis.api.domain.booking.Booking;
import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.booking.PaymentMethod;
import com.agilis.api.domain.client.ClientRepository;
import com.agilis.api.domain.client.PriorityRebookingRepository;
import com.agilis.api.domain.coupon.Coupon;
import com.agilis.api.domain.coupon.CouponRedemption;
import com.agilis.api.domain.coupon.CouponRedemptionRepository;
import com.agilis.api.domain.coupon.CouponRepository;
import com.agilis.api.domain.notification.WebhookDispatcher;
import com.agilis.api.domain.notification.WebhookEventType;
import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServiceRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class CreateBookingUseCase {

    private final BookingRepository bookingRepository;
    private final ClientRepository clientRepository;
    private final ServiceRepository serviceRepository;
    private final PriorityRebookingRepository priorityRebookingRepository;
    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository couponRedemptionRepository;
    private final WebhookDispatcher webhookDispatcher;

    public CreateBookingUseCase(
            BookingRepository bookingRepository,
            ClientRepository clientRepository,
            ServiceRepository serviceRepository,
            PriorityRebookingRepository priorityRebookingRepository,
            CouponRepository couponRepository,
            CouponRedemptionRepository couponRedemptionRepository,
            WebhookDispatcher webhookDispatcher
    ) {
        this.bookingRepository           = bookingRepository;
        this.clientRepository            = clientRepository;
        this.serviceRepository           = serviceRepository;
        this.priorityRebookingRepository = priorityRebookingRepository;
        this.couponRepository            = couponRepository;
        this.couponRedemptionRepository  = couponRedemptionRepository;
        this.webhookDispatcher           = webhookDispatcher;
    }

    public Output execute(Input input) {
        UUID clientId   = UUID.fromString(input.clientId());
        UUID serviceId  = UUID.fromString(input.serviceId());
        UUID employeeId = input.employeeId() != null ? UUID.fromString(input.employeeId()) : null;

        if (!clientRepository.existsByUserId(clientId)) {
            throw new IllegalArgumentException("Client not found.");
        }

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found."));

        boolean hasPriority = priorityRebookingRepository
                .findValidByClientAndService(clientId, serviceId)
                .map(priority -> {
                    priority.markUsed();
                    priorityRebookingRepository.save(priority);
                    return true;
                })
                .orElse(false);

        if (!hasPriority && bookingRepository.existsConflict(serviceId, input.scheduledAt())) {
            throw new IllegalStateException("There is already a booking at this time.");
        }

        UUID couponId = null;
        BigDecimal discountAmount = null;

        if (input.couponCode() != null && !input.couponCode().isBlank()) {
            Coupon coupon = couponRepository.findByCode(input.couponCode())
                    .orElseThrow(() -> new IllegalArgumentException("Cupom não encontrado"));

            if (!coupon.isValidNow()) {
                throw new IllegalStateException("Cupom expirado ou inativo");
            }
            if (couponRedemptionRepository.existsByCouponIdAndClientId(coupon.getId(), clientId)) {
                throw new IllegalStateException("Você já utilizou este cupom");
            }
            if (!coupon.meetsMinimumOrder(service.getPrice())) {
                throw new IllegalStateException("Valor do serviço abaixo do mínimo exigido pelo cupom");
            }

            discountAmount = coupon.calculateDiscount(service.getPrice());
            couponId = coupon.getId();
        }

        Booking booking = Booking.create(clientId, serviceId, employeeId, input.scheduledAt(),
                input.notes(), input.paymentMethod(), couponId, discountAmount);
        bookingRepository.save(booking);

        if (couponId != null) {
            CouponRedemption redemption = CouponRedemption.create(couponId, clientId, booking.getId());
            couponRedemptionRepository.save(redemption);
        }

        webhookDispatcher.dispatch(
                service.getStoreId(),
                WebhookEventType.BOOKING_CREATED,
                Map.of(
                        "bookingId", booking.getId().toString(),
                        "clientId", booking.getClientId().toString(),
                        "serviceId", booking.getServiceId().toString(),
                        "scheduledAt", booking.getScheduledAt().toString()
                )
        );

        BigDecimal finalPrice = discountAmount != null ? service.getPrice().subtract(discountAmount) : service.getPrice();

        return new Output(
                booking.getId().toString(), booking.getClientId().toString(), booking.getServiceId().toString(),
                booking.getScheduledAt(), booking.getNotes(), booking.getPaymentMethod(), booking.getStatus().name(),
                discountAmount, finalPrice
        );
    }

    @Schema(name = "CreateBookingInput")
    public record Input(String clientId, String serviceId, String employeeId, LocalDateTime scheduledAt,
                        String notes, PaymentMethod paymentMethod, String couponCode) {}

    @Schema(name = "CreateBookingOutput")
    public record Output(String bookingId, String clientId, String serviceId, LocalDateTime scheduledAt,
                         String notes, PaymentMethod paymentMethod, String status,
                         BigDecimal discountAmount, BigDecimal finalPrice) {}
}