package com.agilis.api.domain.booking;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
public class Booking {

    private final UUID id;
    private final UUID clientId;
    private final UUID serviceId;
    private UUID employeeId;
    private LocalDateTime scheduledAt;
    private final LocalDate date;
    private BookingStatus status;
    private final LocalDateTime createdAt;

    private Booking(UUID id, UUID clientId, UUID serviceId, UUID employeeId, LocalDateTime scheduledAt, BookingStatus status, LocalDateTime createdAt) {
        this.id          = id;
        this.clientId    = clientId;
        this.serviceId   = serviceId;
        this.employeeId  = employeeId;
        this.scheduledAt = validateScheduledAt(scheduledAt);
        this.date        = scheduledAt.toLocalDate();
        this.status      = status;
        this.createdAt   = createdAt;
    }

    public static Booking create(UUID clientId, UUID serviceId, UUID employeeId, LocalDateTime scheduledAt) {
        return new Booking(UUID.randomUUID(), clientId, serviceId, employeeId, scheduledAt, BookingStatus.PENDING, LocalDateTime.now());
    }

    public static Booking reconstitute(UUID id, UUID clientId, UUID serviceId, UUID employeeId, LocalDateTime scheduledAt, BookingStatus status, LocalDateTime createdAt) {
        return new Booking(id, clientId, serviceId, employeeId, scheduledAt, status, createdAt);
    }

    public void assignEmployee(UUID employeeId) { this.employeeId = employeeId; }

    public void reschedule(LocalDateTime newScheduledAt) {
        if (this.status != BookingStatus.PENDING && this.status != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only pending or confirmed bookings can be rescheduled");
        }
        this.scheduledAt = validateScheduledAt(newScheduledAt);
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