package com.agilis.api.application.booking;

import com.agilis.api.domain.booking.*;
import com.agilis.api.domain.client.PriorityRebooking;
import com.agilis.api.domain.client.PriorityRebookingRepository;
import com.agilis.api.domain.notification.WebhookDispatcher;
import com.agilis.api.domain.notification.WebhookEventType;
import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServiceRepository;

import java.util.Map;
import java.util.UUID;

public class RespondToDelayUseCase {

    private final BookingDelayRepository bookingDelayRepository;
    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final PriorityRebookingRepository priorityRebookingRepository;
    private final WebhookDispatcher webhookDispatcher;

    public RespondToDelayUseCase(
            BookingDelayRepository bookingDelayRepository,
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            PriorityRebookingRepository priorityRebookingRepository,
            WebhookDispatcher webhookDispatcher
    ) {
        this.bookingDelayRepository       = bookingDelayRepository;
        this.bookingRepository            = bookingRepository;
        this.serviceRepository            = serviceRepository;
        this.priorityRebookingRepository  = priorityRebookingRepository;
        this.webhookDispatcher            = webhookDispatcher;
    }

    public void execute(Input input) {
        UUID delayId    = UUID.fromString(input.delayId());
        UUID requesterId = UUID.fromString(input.requesterId());

        BookingDelay delay = bookingDelayRepository.findById(delayId)
                .orElseThrow(() -> new IllegalArgumentException("Delay notice not found"));

        Booking booking = bookingRepository.findById(delay.getBookingId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getClientId().equals(requesterId)) {
            throw new IllegalStateException("You do not have permission to respond to this notice");
        }

        Service service = serviceRepository.findById(booking.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        switch (input.action()) {
            case ACCEPT_NEW_TIME -> {
                delay.respond(DelayResponse.ACCEPTED_NEW_TIME);
                booking.reschedule(delay.getNewScheduledAt());
                bookingRepository.save(booking);
                webhookDispatcher.dispatch(
                        service.getStoreId(),
                        WebhookEventType.BOOKING_RESCHEDULED,
                        Map.of("bookingId", booking.getId().toString(), "newScheduledAt", booking.getScheduledAt().toString())
                );
            }
            case REQUEST_REFUND -> {
                delay.respond(DelayResponse.REFUND_REQUESTED);
                booking.cancel();
                bookingRepository.save(booking);
                webhookDispatcher.dispatch(
                        service.getStoreId(),
                        WebhookEventType.REFUND_REQUESTED,
                        Map.of("bookingId", booking.getId().toString(), "clientId", booking.getClientId().toString())
                );
            }
            case REQUEST_RESCHEDULE -> {
                delay.respond(DelayResponse.RESCHEDULE_REQUESTED);
                booking.cancel();
                bookingRepository.save(booking);

                PriorityRebooking priority = PriorityRebooking.create(
                        booking.getClientId(), booking.getServiceId(), "Rescheduling due to provider delay"
                );
                priorityRebookingRepository.save(priority);

                webhookDispatcher.dispatch(
                        service.getStoreId(),
                        WebhookEventType.RESCHEDULE_PRIORITY_GRANTED,
                        Map.of("clientId", booking.getClientId().toString(), "serviceId", booking.getServiceId().toString())
                );
            }
        }

        bookingDelayRepository.save(delay);
    }

    public record Input(String requesterId, String delayId, Action action) {}

    public enum Action { ACCEPT_NEW_TIME, REQUEST_REFUND, REQUEST_RESCHEDULE }
}