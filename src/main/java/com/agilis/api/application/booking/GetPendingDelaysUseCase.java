package com.agilis.api.application.booking;

import com.agilis.api.domain.booking.BookingDelayRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class GetPendingDelaysUseCase {

    private final BookingDelayRepository bookingDelayRepository;

    public GetPendingDelaysUseCase(BookingDelayRepository bookingDelayRepository) {
        this.bookingDelayRepository = bookingDelayRepository;
    }

    public List<Output> execute(String clientId) {
        return bookingDelayRepository.findAllPendingByClientId(UUID.fromString(clientId))
                .stream()
                .map(d -> new Output(
                        d.getId().toString(),
                        d.getBookingId().toString(),
                        d.getOriginalScheduledAt(),
                        d.getNewScheduledAt(),
                        d.getDelayMinutes(),
                        d.getReason()
                ))
                .toList();
    }

    public record Output(String delayId, String bookingId, LocalDateTime originalScheduledAt, LocalDateTime newScheduledAt, int delayMinutes, String reason) {}
}