package com.agilis.api.application.booking;

import com.agilis.api.domain.booking.Booking;
import com.agilis.api.domain.booking.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class GetBookingUseCase {

    private final BookingRepository bookingRepository;

    public GetBookingUseCase(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Output execute(String bookingId) {
        Booking booking = bookingRepository.findById(UUID.fromString(bookingId))
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        return toOutput(booking);
    }

    public List<Output> executeByClient(String clientId) {
        return bookingRepository.findAllByClientId(UUID.fromString(clientId))
                .stream()
                .map(this::toOutput)
                .toList();
    }

    public List<Output> executeByService(String serviceId) {
        return bookingRepository.findAllByServiceId(UUID.fromString(serviceId))
                .stream()
                .map(this::toOutput)
                .toList();
    }

    private Output toOutput(Booking booking) {
        return new Output(
                booking.getId().toString(),
                booking.getClientId().toString(),
                booking.getServiceId().toString(),
                booking.getScheduledAt(),
                booking.getStatus().name()
        );
    }

    public record Output(
            String bookingId,
            String clientId,
            String serviceId,
            LocalDateTime scheduledAt,
            String status
    ) {}
}