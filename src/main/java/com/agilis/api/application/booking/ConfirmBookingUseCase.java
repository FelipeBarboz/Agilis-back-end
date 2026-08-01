package com.agilis.api.application.booking;

import com.agilis.api.domain.booking.Booking;
import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.notification.WebhookDispatcher;
import com.agilis.api.domain.notification.WebhookEventType;
import com.agilis.api.domain.provider.StoreMembershipRepository;
import com.agilis.api.domain.service.ServiceRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;
import java.util.UUID;

public class ConfirmBookingUseCase {

    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final StoreMembershipRepository storeMembershipRepository;
    private final WebhookDispatcher webhookDispatcher;

    public ConfirmBookingUseCase(
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository,
            WebhookDispatcher webhookDispatcher
    ) {
        this.bookingRepository      = bookingRepository;
        this.serviceRepository      = serviceRepository;
        this.storeMembershipRepository = storeMembershipRepository;
        this.webhookDispatcher = webhookDispatcher;
    }

    public void execute(Input input) {
        UUID bookingId   = UUID.fromString(input.bookingId());
        UUID requesterId = UUID.fromString(input.requesterId());

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        var service = serviceRepository.findById(booking.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Service not found."));

        storeMembershipRepository
                .findByProviderIdAndStoreId(requesterId, service.getStoreId())
                .orElseThrow(() -> new IllegalStateException("You do not have permission to confirm this booking."));

        booking.confirm();
        bookingRepository.save(booking);

        webhookDispatcher.dispatch(
                service.getStoreId(),
                WebhookEventType.BOOKING_CONFIRMED,
                Map.of(
                        "bookingId", booking.getId().toString(),
                        "clientId", booking.getClientId().toString(),
                        "serviceId", booking.getServiceId().toString(),
                        "scheduledAt", booking.getScheduledAt().toString()
                )
        );
    }

    @Schema(name = "ConfirmBookingInput")
    public record Input(String bookingId, String requesterId) {}
}