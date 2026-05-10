package com.agilis.api.application.booking;

import com.agilis.api.domain.booking.Booking;
import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.provider.StoreMembershipRepository;
import com.agilis.api.domain.service.ServiceRepository;

import java.util.UUID;

public class CompleteBookingUseCase {

    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public CompleteBookingUseCase(
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.bookingRepository         = bookingRepository;
        this.serviceRepository         = serviceRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public void execute(Input input) {
        UUID bookingId   = UUID.fromString(input.bookingId());
        UUID requesterId = UUID.fromString(input.requesterId());

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        var service = serviceRepository.findById(booking.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Service not found."));

        storeMembershipRepository
                .findByProviderIdAndStoreId(requesterId, service.getStoreId())
                .orElseThrow(() -> new IllegalStateException("You do not have permission to complete this booking."));

        booking.complete();
        bookingRepository.save(booking);
    }

    public record Input(String bookingId, String requesterId) {}
}