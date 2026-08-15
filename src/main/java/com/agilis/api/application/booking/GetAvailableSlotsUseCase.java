package com.agilis.api.application.booking;

import com.agilis.api.domain.booking.Booking;
import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.booking.BookingStatus;
import com.agilis.api.domain.provider.*;
import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServiceRepository;
import org.jspecify.annotations.NonNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class GetAvailableSlotsUseCase {

    private static final int SLOT_STEP_MINUTES = 30;

    private final ServiceRepository serviceRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final BusinessHoursRepository businessHoursRepository;
    private final StoreMembershipRepository storeMembershipRepository;
    private final EmployeeScheduleRepository employeeScheduleRepository;
    private final ScheduleSlotRepository scheduleSlotRepository;
    private final BookingRepository bookingRepository;

    public GetAvailableSlotsUseCase(
            ServiceRepository serviceRepository,
            ProviderProfileRepository providerProfileRepository,
            BusinessHoursRepository businessHoursRepository,
            StoreMembershipRepository storeMembershipRepository,
            EmployeeScheduleRepository employeeScheduleRepository,
            ScheduleSlotRepository scheduleSlotRepository,
            BookingRepository bookingRepository
    ) {
        this.serviceRepository          = serviceRepository;
        this.providerProfileRepository  = providerProfileRepository;
        this.businessHoursRepository    = businessHoursRepository;
        this.storeMembershipRepository  = storeMembershipRepository;
        this.employeeScheduleRepository = employeeScheduleRepository;
        this.scheduleSlotRepository     = scheduleSlotRepository;
        this.bookingRepository          = bookingRepository;
    }

    public Output execute(Input input) {
        UUID serviceId = UUID.fromString(input.serviceId());
        LocalDate date = input.date();

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));

        ProviderProfile store = providerProfileRepository.findById(service.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("Loja não encontrada"));

        UUID employeeId = store.isAllowEmployeeSelection() && input.employeeId() != null
                ? UUID.fromString(input.employeeId())
                : null;

        List<UUID> candidateEmployees = employeeId != null
                ? List.of(employeeId)
                : storeMembershipRepository.findAllByStoreId(service.getStoreId())
                .stream()
                .map(StoreMembership::getProviderId)
                .toList();

        List<LocalTime> allFreeSlots = new ArrayList<>();
        for (UUID candidate : candidateEmployees) {
            allFreeSlots.addAll(computeFreeSlotsForEmployee(candidate, service, date));
        }

        List<LocalTime> distinctSorted = allFreeSlots.stream().distinct().sorted().toList();

        return new Output(distinctSorted, store.isAllowEmployeeSelection());
    }

    private List<LocalTime> computeFreeSlotsForEmployee(UUID employeeId, Service service, LocalDate date) {
        List<TimeWindow> workWindows = getWorkWindows(employeeId, service.getStoreId(), date);
        if (workWindows.isEmpty()) return List.of();

        List<Booking> existingBookings = bookingRepository.findAllByEmployeeAndDate(employeeId, date)
                .stream()
                .filter(b -> b.getStatus() == BookingStatus.PENDING || b.getStatus() == BookingStatus.CONFIRMED)
                .toList();

        List<TimeWindow> occupiedBlocks = getWorkWindows(service, existingBookings);

        List<LocalTime> free = new ArrayList<>();
        int newServiceDuration = service.getDurationMinutes();

        for (TimeWindow window : workWindows) {
            LocalTime cursor = roundUp(window.start(), SLOT_STEP_MINUTES);
            while (!cursor.plusMinutes(newServiceDuration).isAfter(window.end())) {
                final LocalTime candidateStart = cursor;
                final LocalTime candidateEnd   = cursor.plusMinutes(newServiceDuration);

                boolean conflicts = occupiedBlocks.stream().anyMatch(block ->
                        candidateStart.isBefore(block.end()) && candidateEnd.isAfter(block.start())
                );

                if (!conflicts && !isPast(date, candidateStart)) {
                    free.add(candidateStart);
                }
                cursor = cursor.plusMinutes(SLOT_STEP_MINUTES);
            }
        }
        return free;
    }

    private @NonNull List<TimeWindow> getWorkWindows(Service service, List<Booking> existingBookings) {
        Map<UUID, Integer> durationCache = new HashMap<>();
        durationCache.put(service.getId(), service.getDurationMinutes());

        List<TimeWindow> occupiedBlocks = new ArrayList<>();
        for (Booking booking : existingBookings) {
            int duration = durationCache.computeIfAbsent(booking.getServiceId(), sid ->
                    serviceRepository.findById(sid)
                            .map(Service::getDurationMinutes)
                            .orElse(0)
            );

            if (duration <= 0) continue;

            LocalTime start = booking.getScheduledAt().toLocalTime();
            LocalTime end   = start.plusMinutes(duration);
            occupiedBlocks.add(new TimeWindow(start, end));
        }
        return occupiedBlocks;
    }

    private List<TimeWindow> getWorkWindows(UUID employeeId, UUID storeId, LocalDate date) {
        Optional<EmployeeSchedule> scheduleOpt = employeeScheduleRepository.findByProviderIdAndStoreId(employeeId, storeId);
        if (scheduleOpt.isEmpty()) return List.of();

        EmployeeSchedule schedule = scheduleOpt.get();
        int dayOfWeek = date.getDayOfWeek() == DayOfWeek.SUNDAY ? 0 : date.getDayOfWeek().getValue();

        return switch (schedule.getScheduleType()) {
            case STORE_HOURS -> businessHoursRepository.findByStoreIdAndDayOfWeek(storeId, dayOfWeek)
                    .map(h -> List.of(new TimeWindow(h.getOpensAt(), h.getClosesAt())))
                    .orElse(List.of());

            case FIXED -> scheduleSlotRepository.findAllByEmployeeScheduleId(schedule.getId())
                    .stream()
                    .filter(s -> s.getDayOfWeek() != null && s.getDayOfWeek() == dayOfWeek)
                    .map(s -> new TimeWindow(s.getStartTime(), s.getEndTime()))
                    .toList();

            case FLEXIBLE -> scheduleSlotRepository.findAllByEmployeeScheduleId(schedule.getId())
                    .stream()
                    .filter(s -> date.equals(s.getSpecificDate()))
                    .map(s -> new TimeWindow(s.getStartTime(), s.getEndTime()))
                    .toList();
        };
    }

    private LocalTime roundUp(LocalTime time, int stepMinutes) {
        int minute = time.getMinute();
        int remainder = minute % stepMinutes;
        return remainder == 0 ? time : time.plusMinutes(stepMinutes - remainder).withSecond(0).withNano(0);
    }

    private boolean isPast(LocalDate date, LocalTime time) {
        return LocalDateTime.of(date, time).isBefore(LocalDateTime.now());
    }

    private record TimeWindow(LocalTime start, LocalTime end) {}

    public record Input(String serviceId, LocalDate date, String employeeId) {}
    public record Output(List<LocalTime> availableSlots, boolean canChooseEmployee) {}
}