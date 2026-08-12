package com.agilis.api.application.booking;

import com.agilis.api.domain.booking.*;
import com.agilis.api.domain.notification.WebhookDispatcher;
import com.agilis.api.domain.notification.WebhookEventType;
import com.agilis.api.domain.provider.StoreMembershipRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DeclareDelayUseCase {

    private final BookingRepository bookingRepository;
    private final BookingDelayRepository bookingDelayRepository;
    private final StoreMembershipRepository storeMembershipRepository;
    private final WebhookDispatcher webhookDispatcher;

    public DeclareDelayUseCase(
            BookingRepository bookingRepository,
            BookingDelayRepository bookingDelayRepository,
            StoreMembershipRepository storeMembershipRepository,
            WebhookDispatcher webhookDispatcher
    ) {
        this.bookingRepository        = bookingRepository;
        this.bookingDelayRepository   = bookingDelayRepository;
        this.storeMembershipRepository = storeMembershipRepository;
        this.webhookDispatcher        = webhookDispatcher;
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

        for (Booking booking : affected) {
            BookingDelay delay = BookingDelay.create(booking.getId(), booking.getScheduledAt(), input.delayMinutes(), input.reason());
            bookingDelayRepository.save(delay);
        }

        webhookDispatcher.dispatch(
                storeId,
                WebhookEventType.DELAY_DECLARED,
                Map.of(
                        "date", date.toString(),
                        "delayMinutes", input.delayMinutes(),
                        "affectedBookings", affected.size(),
                        "reason", input.reason() != null ? input.reason() : ""
                )
        );

        return new Output(affected.size());
    }

    public record Input(String requesterId, String storeId, String employeeId, LocalDate date, int delayMinutes, String reason) {}
    public record Output(int affectedBookingsCount) {}
}