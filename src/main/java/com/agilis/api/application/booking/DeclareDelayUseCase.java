package com.agilis.api.application.booking;

import com.agilis.api.domain.booking.*;
import com.agilis.api.domain.client.PriorityRebooking;
import com.agilis.api.domain.client.PriorityRebookingRepository;
import com.agilis.api.domain.notification.WebhookDispatcher;
import com.agilis.api.domain.notification.WebhookEventType;
import com.agilis.api.domain.provider.StoreMembershipRepository;
import com.agilis.api.domain.service.ServiceRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DeclareDelayUseCase {

    private final BookingRepository bookingRepository;
    private final BookingDelayRepository bookingDelayRepository;
    private final PriorityRebookingRepository priorityRebookingRepository;
    private final StoreMembershipRepository storeMembershipRepository;
    private final WebhookDispatcher webhookDispatcher;

    public DeclareDelayUseCase(
            BookingRepository bookingRepository,
            BookingDelayRepository bookingDelayRepository,
            PriorityRebookingRepository priorityRebookingRepository,
            StoreMembershipRepository storeMembershipRepository,
            ServiceRepository serviceRepository, WebhookDispatcher webhookDispatcher
    ) {
        this.bookingRepository            = bookingRepository;
        this.bookingDelayRepository       = bookingDelayRepository;
        this.priorityRebookingRepository  = priorityRebookingRepository;
        this.storeMembershipRepository    = storeMembershipRepository;
        this.webhookDispatcher            = webhookDispatcher;
    }

    public Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID storeId     = UUID.fromString(input.storeId());
        LocalDate date   = input.date();

        storeMembershipRepository
                .findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store"));

        List<Booking> affected = input.employeeId() != null
                ? bookingRepository.findAllByEmployeeAndDate(UUID.fromString(input.employeeId()), date)
                : bookingRepository.findAllByStoreAndDate(storeId, date);

        affected = affected.stream()
                .filter(b -> b.getStatus() == BookingStatus.PENDING || b.getStatus() == BookingStatus.CONFIRMED)
                .toList();

        int newDelays = 0;
        int autoCancelled = 0;

        for (Booking booking : affected) {
            if (bookingDelayRepository.existsByBookingId(booking.getId())) {
                autoCancelWithPriority(booking);
                autoCancelled++;
                continue;
            }

            BookingDelay delay = BookingDelay.create(booking.getId(), booking.getScheduledAt(), input.delayMinutes(), input.reason());
            bookingDelayRepository.save(delay);
            newDelays++;
        }

        webhookDispatcher.dispatch(
                storeId,
                WebhookEventType.DELAY_DECLARED,
                Map.of(
                        "date", date.toString(),
                        "delayMinutes", input.delayMinutes(),
                        "newDelays", newDelays,
                        "autoCancelled", autoCancelled,
                        "reason", input.reason() != null ? input.reason() : ""
                )
        );

        return new Output(newDelays, autoCancelled);
    }

    private void autoCancelWithPriority(Booking booking) {
        booking.cancel();
        bookingRepository.save(booking);

        PriorityRebooking priority = PriorityRebooking.create(
                booking.getClientId(), booking.getServiceId(), "Automatic cancellation — provider’s second consecutive delay"
        );
        priorityRebookingRepository.save(priority);

        webhookDispatcher.dispatch(
                booking.getServiceId(),
                WebhookEventType.BOOKING_CANCELLED,
                Map.of(
                        "bookingId", booking.getId().toString(),
                        "clientId", booking.getClientId().toString(),
                        "reason", "auto_cancel_repeated_delay"
                )
        );
    }

    @Schema(name = "DeclareDelayInput")
    public record Input(String requesterId, String storeId, String employeeId, LocalDate date, int delayMinutes, String reason) {}
    @Schema(name = "DeclareDelayOutput")
    public record Output(int newDelaysCount, int autoCancelledCount) {}
}