package com.agilis.api.domain.booking;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class BookingDelay {

    private final UUID id;
    private final UUID bookingId;
    private final LocalDateTime originalScheduledAt;
    private final LocalDateTime newScheduledAt;
    private final int delayMinutes;
    private final String reason;
    private DelayResponse response;
    private LocalDateTime respondedAt;
    private final LocalDateTime createdAt;

    private BookingDelay(UUID id, UUID bookingId, LocalDateTime originalScheduledAt, LocalDateTime newScheduledAt,
                         int delayMinutes, String reason, DelayResponse response, LocalDateTime respondedAt, LocalDateTime createdAt) {
        this.id                  = id;
        this.bookingId           = bookingId;
        this.originalScheduledAt = originalScheduledAt;
        this.newScheduledAt      = newScheduledAt;
        this.delayMinutes        = delayMinutes;
        this.reason              = reason;
        this.response            = response;
        this.respondedAt         = respondedAt;
        this.createdAt           = createdAt;
    }

    public static BookingDelay create(UUID bookingId, LocalDateTime originalScheduledAt, int delayMinutes, String reason) {
        return new BookingDelay(
                UUID.randomUUID(),
                bookingId,
                originalScheduledAt,
                originalScheduledAt.plusMinutes(delayMinutes),
                delayMinutes,
                reason,
                DelayResponse.PENDING,
                null,
                LocalDateTime.now()
        );
    }

    public static BookingDelay reconstitute(UUID id, UUID bookingId, LocalDateTime originalScheduledAt, LocalDateTime newScheduledAt,
                                            int delayMinutes, String reason, DelayResponse response, LocalDateTime respondedAt, LocalDateTime createdAt) {
        return new BookingDelay(id, bookingId, originalScheduledAt, newScheduledAt, delayMinutes, reason, response, respondedAt, createdAt);
    }

    public void respond(DelayResponse response) {
        if (this.response != DelayResponse.PENDING) {
            throw new IllegalStateException("This delay notice has already been addressed");
        }
        if (response == DelayResponse.PENDING) {
            throw new IllegalArgumentException("Invalid response");
        }
        this.response    = response;
        this.respondedAt = LocalDateTime.now();
    }
}