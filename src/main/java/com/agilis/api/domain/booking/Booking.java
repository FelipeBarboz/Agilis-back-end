package com.agilis.api.domain.booking;

import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Booking {

    private final UUID id;
    private final UUID clientId;
    private final UUID serviceId;
    private LocalDateTime scheduledAt;
    private final LocalDate date;
    private BookingStatus status;
    private final LocalDateTime createdAt;

    private Booking(UUID id, UUID clientId, UUID serviceId, LocalDateTime scheduledAt, BookingStatus status, LocalDateTime createdAt) {
        this.id          = id;
        this.clientId    = clientId;
        this.serviceId   = serviceId;
        this.scheduledAt = validateScheduledAt(scheduledAt);
        this.date        = scheduledAt.toLocalDate();
        this.status      = status;
        this.createdAt   = createdAt;
    }

    public static Booking create(UUID clientId, UUID serviceId, LocalDateTime scheduledAt) {
        return new Booking(
                UUID.randomUUID(),
                clientId,
                serviceId,
                scheduledAt,
                BookingStatus.PENDING,
                LocalDateTime.now()
        );
    }

    public static Booking reconstitute(UUID id, UUID clientId, UUID serviceId, LocalDateTime scheduledAt, BookingStatus status, LocalDateTime createdAt) {
        return new Booking(id, clientId, serviceId, scheduledAt, status, createdAt);
    }

    public void confirm() {
        validateTransition(BookingStatus.CONFIRMED);
        this.status = BookingStatus.CONFIRMED;
    }

    public void cancel() {
        validateTransition(BookingStatus.CANCELLED);
        this.status = BookingStatus.CANCELLED;
    }

    public void complete() {
        validateTransition(BookingStatus.COMPLETED);
        this.status = BookingStatus.COMPLETED;
    }

    public void reschedule(LocalDateTime newScheduledAt) {
        if (this.status != BookingStatus.PENDING && this.status != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only pending or confirmed bookings can be rescheduled.");
        }
        this.scheduledAt = validateScheduledAt(newScheduledAt);
    }

    private void validateTransition(BookingStatus target) {
        boolean allowed = switch (target) {
            case CONFIRMED  -> this.status == BookingStatus.PENDING;
            case CANCELLED  -> this.status == BookingStatus.PENDING || this.status == BookingStatus.CONFIRMED;
            case COMPLETED  -> this.status == BookingStatus.CONFIRMED;
            default         -> false;
        };

        if (!allowed) {
            throw new IllegalStateException(
                    "Invalid transition: " + this.status + " → " + target
            );
        }
    }

    private LocalDateTime validateScheduledAt(LocalDateTime scheduledAt) {
        if (scheduledAt == null || scheduledAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("The scheduled date must be in the future.");
        }
        return scheduledAt;
    }
}