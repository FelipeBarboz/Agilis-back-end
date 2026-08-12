package com.agilis.api.application.booking;

import com.agilis.api.domain.booking.Booking;
import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.client.ClientRepository;
import com.agilis.api.domain.client.PriorityRebookingRepository;
import com.agilis.api.domain.notification.WebhookDispatcher;
import com.agilis.api.domain.notification.WebhookEventType;
import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServiceRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class CreateBookingUseCase {

    private final BookingRepository bookingRepository;
    private final ClientRepository clientRepository;
    private final ServiceRepository serviceRepository;
    private final PriorityRebookingRepository priorityRebookingRepository;
    private final WebhookDispatcher webhookDispatcher;

    public CreateBookingUseCase(
            BookingRepository bookingRepository,
            ClientRepository clientRepository,
            ServiceRepository serviceRepository,
            PriorityRebookingRepository priorityRebookingRepository,
            WebhookDispatcher webhookDispatcher
    ) {
        this.bookingRepository           = bookingRepository;
        this.clientRepository            = clientRepository;
        this.serviceRepository           = serviceRepository;
        this.priorityRebookingRepository = priorityRebookingRepository;
        this.webhookDispatcher           = webhookDispatcher;
    }

    public Output execute(Input input) {
        UUID clientId  = UUID.fromString(input.clientId());
        UUID serviceId = UUID.fromString(input.serviceId());
        UUID employeeId = input.employeeId() != null ? UUID.fromString(input.employeeId()) : null;

        if (!clientRepository.existsByUserId(clientId)) {
            throw new IllegalArgumentException("Client not found");
        }

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        boolean hasPriority = priorityRebookingRepository
                .findValidByClientAndService(clientId, serviceId)
                .map(priority -> {
                    priority.markUsed();
                    priorityRebookingRepository.save(priority);
                    return true;
                })
                .orElse(false);

        if (!hasPriority && bookingRepository.existsConflict(serviceId, input.scheduledAt())) {
            throw new IllegalStateException("There is already a booking at this time.");
        }

        Booking booking = Booking.create(clientId, serviceId, employeeId, input.scheduledAt());
        bookingRepository.save(booking);

        webhookDispatcher.dispatch(
                service.getStoreId(),
                WebhookEventType.BOOKING_CREATED,
                Map.of(
                        "bookingId", booking.getId().toString(),
                        "clientId", booking.getClientId().toString(),
                        "serviceId", booking.getServiceId().toString(),
                        "scheduledAt", booking.getScheduledAt().toString()
                )
        );

        return new Output(
                booking.getId().toString(),
                booking.getClientId().toString(),
                booking.getServiceId().toString(),
                booking.getScheduledAt(),
                booking.getStatus().name()
        );
    }

    @Schema(name = "CreateBookingInput")
    public record Input(String clientId, String serviceId, String employeeId, LocalDateTime scheduledAt) {}

    @Schema(name = "CreateBookingOutput")
    public record Output(String bookingId, String clientId, String serviceId, LocalDateTime scheduledAt, String status) {}
}