package com.agilis.api.infrastructure.persistence.booking;

import com.agilis.api.domain.booking.DelayResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "booking_delays")
public class BookingDelayEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "booking_id", nullable = false)
    private UUID bookingId;

    @Column(name = "original_scheduled_at", nullable = false)
    private LocalDateTime originalScheduledAt;

    @Column(name = "new_scheduled_at", nullable = false)
    private LocalDateTime newScheduledAt;

    @Column(name = "delay_minutes", nullable = false)
    private int delayMinutes;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DelayResponse response;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}