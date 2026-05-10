package com.agilis.api.application.booking;

import com.agilis.api.domain.booking.Booking;
import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.client.ClientRepository;
import com.agilis.api.domain.service.ServiceRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public class CreateBookingUseCase {

    private final BookingRepository bookingRepository;
    private final ClientRepository clientRepository;
    private final ServiceRepository serviceRepository;

    public CreateBookingUseCase(
            BookingRepository bookingRepository,
            ClientRepository clientRepository,
            ServiceRepository serviceRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.clientRepository  = clientRepository;
        this.serviceRepository = serviceRepository;
    }

    public Output execute(Input input) {
        UUID clientId  = UUID.fromString(input.clientId());
        UUID serviceId = UUID.fromString(input.serviceId());

        if (!clientRepository.existsByUserId(clientId)) {
            throw new IllegalArgumentException("Client not found.");
        }

        serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found."));

        if (bookingRepository.existsConflict(serviceId, input.scheduledAt())) {
            throw new IllegalStateException("There is already a booking at this time.");
        }

        Booking booking = Booking.create(clientId, serviceId, input.scheduledAt());
        bookingRepository.save(booking);

        return new Output(
                booking.getId().toString(),
                booking.getClientId().toString(),
                booking.getServiceId().toString(),
                booking.getScheduledAt(),
                booking.getStatus().name()
        );
    }

    public record Input(String clientId, String serviceId, LocalDateTime scheduledAt) {}

    public record Output(String bookingId, String clientId, String serviceId, LocalDateTime scheduledAt, String status) {}
}