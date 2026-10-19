package com.agilis.api.application.booking;

import com.agilis.api.domain.booking.Booking;
import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.booking.BookingStatus;
import com.agilis.api.domain.provider.StoreMembershipRepository;
import com.agilis.api.domain.provider.StoreServiceArea;
import com.agilis.api.domain.provider.StoreServiceAreaRepository;
import com.agilis.api.domain.provider.StoreUnitRepository;
import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServiceRepository;
import com.agilis.api.domain.user.Address;
import com.agilis.api.domain.user.AddressRepository;
import com.agilis.api.domain.user.User;
import com.agilis.api.domain.user.UserRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class GetStoreDailyBookingsOverviewUseCase {

    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final StoreMembershipRepository storeMembershipRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final StoreUnitRepository storeUnitRepository;
    private final StoreServiceAreaRepository storeServiceAreaRepository;

    public GetStoreDailyBookingsOverviewUseCase(
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository,
            UserRepository userRepository,
            AddressRepository addressRepository,
            StoreUnitRepository storeUnitRepository,
            StoreServiceAreaRepository storeServiceAreaRepository
    ) {
        this.bookingRepository          = bookingRepository;
        this.serviceRepository          = serviceRepository;
        this.storeMembershipRepository  = storeMembershipRepository;
        this.userRepository             = userRepository;
        this.addressRepository          = addressRepository;
        this.storeUnitRepository        = storeUnitRepository;
        this.storeServiceAreaRepository = storeServiceAreaRepository;
    }

    public Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID storeId     = UUID.fromString(input.storeId());
        LocalDate date   = input.date() != null ? input.date() : LocalDate.now();

        storeMembershipRepository.findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalStateException("No permission to view this store’s bookings."));

        List<Booking> bookings = bookingRepository.findAllByStoreAndDate(storeId, date);

        int totalToday = bookings.size();
        int pendingCount = 0;
        int confirmedCount = 0;
        int cancelledCount = 0;

        List<DailyBookingItem> items = new ArrayList<>();

        for (Booking b : bookings) {
            if (b.getStatus() == BookingStatus.PENDING) pendingCount++;
            else if (b.getStatus() == BookingStatus.CONFIRMED) confirmedCount++;
            else if (b.getStatus() == BookingStatus.CANCELLED) cancelledCount++;

            Service service = serviceRepository.findById(b.getServiceId()).orElse(null);
            String serviceTitle = service != null ? service.getTitle() : "Serviço";
            int durationMinutes = service != null ? service.getDurationMinutes() : 60;

            LocalTime startTime = b.getScheduledAt().toLocalTime();
            LocalTime endTime = startTime.plusMinutes(durationMinutes);

            User client = userRepository.findById(b.getClientId()).orElse(null);
            String clientName = client != null ? client.getName() : "Cliente";

            String employeeName = "Não atribuído";
            if (b.getEmployeeId() != null) {
                employeeName = userRepository.findById(b.getEmployeeId())
                        .map(User::getName)
                        .orElse("Colaborador");
            }

            String address = resolveAddress(b.getClientId(), service);

            items.add(new DailyBookingItem(
                    b.getId().toString(),
                    b.getClientId().toString(),
                    clientName,
                    b.getEmployeeId() != null ? b.getEmployeeId().toString() : null,
                    employeeName,
                    b.getServiceId().toString(),
                    serviceTitle,
                    b.getScheduledAt(),
                    startTime,
                    endTime,
                    durationMinutes,
                    address,
                    b.getStatus().name()
            ));
        }

        items.sort(Comparator.comparing(DailyBookingItem::scheduledAt));

        DailyMetrics metrics = new DailyMetrics(totalToday, pendingCount, confirmedCount, cancelledCount);

        return new Output(date, metrics, items);
    }

    private String resolveAddress(UUID clientId, Service service) {
        // Tenta pegar primeiro o endereço cadastrado do cliente (caso de serviço a domicílio)
        List<Address> addresses = addressRepository.findAllByUserId(clientId);
        if (!addresses.isEmpty()) {
            Address a = addresses.getFirst();
            return a.getStreet() + ", " + a.getNumber() + " – " + a.getCity();
        }

        if (service != null && service.getUnitId() != null) {
            return storeUnitRepository.findById(service.getUnitId())
                    .map(u -> u.getStreet() + ", " + u.getNumber() + " – " + u.getCity())
                    .orElse(null);
        }

        if (service != null) {
            return storeServiceAreaRepository.findByStoreId(service.getStoreId())
                    .map(StoreServiceArea::getReferenceAddress)
                    .orElse(null);
        }

        return null;
    }

    @Schema(name = "GetStoreDailyBookingsOverviewInput")
    public record Input(String requesterId, String storeId, LocalDate date) {}

    @Schema(name = "DailyBookingMetrics")
    public record DailyMetrics(int totalToday, int pendingCount, int confirmedCount, int cancelledCount) {}

    @Schema(name = "DailyBookingItem")
    public record DailyBookingItem(
            String bookingId,
            String clientId,
            String clientName,
            String employeeId,
            String employeeName,
            String serviceId,
            String serviceTitle,
            LocalDateTime scheduledAt,
            LocalTime startTime,
            LocalTime endTime,
            int durationMinutes,
            String address,
            String status
    ) {}

    @Schema(name = "GetStoreDailyBookingsOverviewOutput")
    public record Output(
            LocalDate date,
            DailyMetrics metrics,
            List<DailyBookingItem> bookings
    ) {}
}
