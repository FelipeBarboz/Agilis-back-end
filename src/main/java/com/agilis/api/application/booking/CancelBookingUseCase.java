package com.agilis.api.application.booking;

import com.agilis.api.domain.booking.Booking;
import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.notification.WebhookEventType;
import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServiceRepository;
import com.agilis.api.domain.notification.WebhookDispatcher;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;
import java.util.UUID;

public class CancelBookingUseCase {

    private final BookingRepository bookingRepository;
    private final WebhookDispatcher webhookDispatcher;
    private final ServiceRepository serviceRepository;

    public CancelBookingUseCase(BookingRepository bookingRepository, WebhookDispatcher webhookDispatcher, ServiceRepository serviceRepository) {
        this.bookingRepository = bookingRepository;
        this.webhookDispatcher = webhookDispatcher;
        this.serviceRepository = serviceRepository;
    }

    public void execute(Input input) {
        UUID bookingId = UUID.fromString(input.bookingId());
        UUID requesterId = UUID.fromString(input.requesterId());

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getClientId().equals(requesterId)) {
            throw new IllegalStateException("You do not have permission to cancel this booking");
        }

        Service service = serviceRepository.findById(booking.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        booking.cancel();
        bookingRepository.save(booking);

        webhookDispatcher.dispatch(
                service.getStoreId(),
                WebhookEventType.BOOKING_CANCELLED,
                Map.of(
                        "bookingId", booking.getId().toString(),
                        "clientId", booking.getClientId().toString(),
                        "serviceId", booking.getServiceId().toString()
                )
        );
    }

    @Schema(name = "CancelBookingInput")
    public record Input(String bookingId, String requesterId) {}
}