package com.agilis.api.infrastructure.persistence.booking;

import com.agilis.api.domain.booking.BookingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "bookings")

public class BookingEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "booking_id", nullable = false)
    private UUID bookingId;

    @Column(name = "service_id", nullable = false)
    private UUID serviceId;

    @Column(name = "scheduled_at", nullable = false, updatable = false)
    private LocalDateTime scheduledAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "date", nullable = false, updatable = false)
    private LocalDate date;

    @Column(name = "status", nullable = false)
    private BookingStatus status;

}
