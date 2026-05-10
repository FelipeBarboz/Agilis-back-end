package com.agilis.api.application.booking;

import com.agilis.api.domain.booking.Booking;
import com.agilis.api.domain.booking.BookingRepository;

import java.util.UUID;

public class CancelBookingUseCase {

    private final BookingRepository bookingRepository;

    public CancelBookingUseCase(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public void execute(Input input) {
        UUID bookingId = UUID.fromString(input.bookingId());
        UUID requesterId = UUID.fromString(input.requesterId());

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        if (!booking.getClientId().equals(requesterId)) {
            throw new IllegalStateException("You do not have permission to cancel this booking.");
        }

        booking.cancel();
        bookingRepository.save(booking);
    }

    public record Input(String bookingId, String requesterId) {}
}