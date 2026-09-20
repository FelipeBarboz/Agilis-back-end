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
    private UUID employeeId;
    private LocalDateTime scheduledAt;
    private final LocalDate date;
    private BookingStatus status;
    private final String notes;
    private final PaymentMethod paymentMethod;
    private RefundStatus refundStatus;
    private String issueDescription;
    private final LocalDateTime createdAt;

    private Booking(UUID id, UUID clientId, UUID serviceId, UUID employeeId, LocalDateTime scheduledAt,
                    BookingStatus status, String notes, PaymentMethod paymentMethod,
                    RefundStatus refundStatus, String issueDescription, LocalDateTime createdAt) {
        this.id                = id;
        this.clientId          = clientId;
        this.serviceId         = serviceId;
        this.employeeId        = employeeId;
        this.scheduledAt       = validateScheduledAt(scheduledAt);
        this.date              = scheduledAt.toLocalDate();
        this.status            = status;
        this.notes             = notes;
        this.paymentMethod     = paymentMethod;
        this.refundStatus      = refundStatus;
        this.issueDescription  = issueDescription;
        this.createdAt         = createdAt;
    }

    public static Booking create(UUID clientId, UUID serviceId, UUID employeeId, LocalDateTime scheduledAt,
                                 String notes, PaymentMethod paymentMethod) {
        return new Booking(UUID.randomUUID(), clientId, serviceId, employeeId, scheduledAt,
                BookingStatus.PENDING, notes, paymentMethod, null, null, LocalDateTime.now());
    }

    public static Booking reconstitute(UUID id, UUID clientId, UUID serviceId, UUID employeeId, LocalDateTime scheduledAt,
                                       BookingStatus status, String notes, PaymentMethod paymentMethod,
                                       RefundStatus refundStatus, String issueDescription, LocalDateTime createdAt) {
        return new Booking(id, clientId, serviceId, employeeId, scheduledAt, status, notes, paymentMethod,
                refundStatus, issueDescription, createdAt);
    }

    public void assignEmployee(UUID employeeId) { this.employeeId = employeeId; }

    public void reschedule(LocalDateTime newScheduledAt) {
        if (this.status != BookingStatus.PENDING && this.status != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Only pending or confirmed bookings can be rescheduled");
        }
        this.scheduledAt = validateScheduledAt(newScheduledAt);
    }

    public void confirm() { validateTransition(BookingStatus.CONFIRMED); this.status = BookingStatus.CONFIRMED; }

    public void cancel() {
        validateTransition(BookingStatus.CANCELLED);
        this.status = BookingStatus.CANCELLED;
        // mockado: assume que todo cancelamento dispara reembolso automático em análise
        this.refundStatus = RefundStatus.PROCESSING;
    }

    public void complete() { validateTransition(BookingStatus.COMPLETED); this.status = BookingStatus.COMPLETED; }

    // usado só em booking já CONCLUIDO
    public void requestRefundForIssue(String description) {
        if (this.status != BookingStatus.COMPLETED) {
            throw new IllegalStateException("You can only request a refund for a completed service");
        }
        if (this.refundStatus != null) {
            throw new IllegalStateException("A refund request already exists for this booking");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Describe the issue to request a refund");
        }
        this.issueDescription = description;
        this.refundStatus     = RefundStatus.PROCESSING;
    }

    public boolean isInProgress() {
        return status == BookingStatus.CONFIRMED && scheduledAt.isBefore(LocalDateTime.now());
    }

    private void validateTransition(BookingStatus target) {
        boolean allowed = switch (target) {
            case CONFIRMED  -> this.status == BookingStatus.PENDING;
            case CANCELLED  -> this.status == BookingStatus.PENDING || this.status == BookingStatus.CONFIRMED;
            case COMPLETED  -> this.status == BookingStatus.CONFIRMED;
            default         -> false;
        };
        if (!allowed) {
            throw new IllegalStateException("Invalid transition: " + this.status + " → " + target);
        }
    }

    private LocalDateTime validateScheduledAt(LocalDateTime scheduledAt) {
        if (scheduledAt == null || scheduledAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("The scheduled date must be in the future");
        }
        return scheduledAt;
    }
}