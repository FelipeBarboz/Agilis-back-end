package com.agilis.api.application.booking;

import com.agilis.api.domain.booking.Booking;
import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.notification.WebhookDispatcher;
import com.agilis.api.domain.notification.WebhookEventType;
import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServiceRepository;

import java.util.Map;
import java.util.UUID;

public class ReportBookingIssueUseCase {

    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final WebhookDispatcher webhookDispatcher;

    public ReportBookingIssueUseCase(BookingRepository bookingRepository, ServiceRepository serviceRepository, WebhookDispatcher webhookDispatcher) {
        this.bookingRepository = bookingRepository;
        this.serviceRepository = serviceRepository;
        this.webhookDispatcher = webhookDispatcher;
    }

    public void execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID bookingId   = UUID.fromString(input.bookingId());

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getClientId().equals(requesterId)) {
            throw new IllegalStateException("Only the customer can report an issue with this booking");
        }

        booking.requestRefundForIssue(input.description());
        bookingRepository.save(booking);

        Service service = serviceRepository.findById(booking.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        webhookDispatcher.dispatch(
                service.getStoreId(),
                WebhookEventType.REFUND_REQUESTED,
                Map.of(
                        "bookingId", booking.getId().toString(),
                        "clientId", booking.getClientId().toString(),
                        "description", input.description()
                )
        );
    }

    public record Input(String requesterId, String bookingId, String description) {}
}