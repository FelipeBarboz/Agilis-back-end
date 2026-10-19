package com.agilis.api.application.booking;

import com.agilis.api.domain.booking.Booking;
import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.provider.StoreMembershipRepository;
import com.agilis.api.domain.service.ServiceRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class GetStoreBookingsUseCase {

    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public GetStoreBookingsUseCase(
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.bookingRepository         = bookingRepository;
        this.serviceRepository         = serviceRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public List<Output> execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID storeId     = UUID.fromString(input.storeId());

        storeMembershipRepository
                .findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalStateException("No permission to view this store’s schedule."));

        return serviceRepository.findAllByStoreId(storeId)
                .stream()
                .flatMap(service -> {
                    List<Booking> bookings = input.date() != null
                            ? bookingRepository.findAllByServiceIdAndDate(service.getId(), input.date())
                            : bookingRepository.findAllByServiceId(service.getId());

                    return bookings.stream().map(booking -> new Output(
                            booking.getId().toString(),
                            booking.getClientId().toString(),
                            service.getId().toString(),
                            service.getTitle(),
                            booking.getScheduledAt(),
                            booking.getStatus().name()
                    ));
                })
                .sorted((a, b) -> a.scheduledAt().compareTo(b.scheduledAt()))
                .toList();
    }

    @Schema(name = "GetStoreBookingInput")
    public record Input(String requesterId, String storeId, LocalDate date) {}

    @Schema(name = "GetStoreBookingOutput")
    public record Output(String bookingId, String clientId, String serviceId, String serviceTitle, LocalDateTime scheduledAt, String status) {}
}