package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.*;
import com.agilis.api.domain.user.User;
import com.agilis.api.domain.user.UserRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GetStoreDailyScheduleUseCase {

    private final StoreMembershipRepository storeMembershipRepository;
    private final EmployeeScheduleRepository employeeScheduleRepository;
    private final ScheduleSlotRepository scheduleSlotRepository;
    private final UserRepository userRepository;
    private final StorePositionRepository storePositionRepository;

    public GetStoreDailyScheduleUseCase(
            StoreMembershipRepository storeMembershipRepository,
            EmployeeScheduleRepository employeeScheduleRepository,
            ScheduleSlotRepository scheduleSlotRepository,
            UserRepository userRepository,
            StorePositionRepository storePositionRepository
    ) {
        this.storeMembershipRepository  = storeMembershipRepository;
        this.employeeScheduleRepository = employeeScheduleRepository;
        this.scheduleSlotRepository     = scheduleSlotRepository;
        this.userRepository             = userRepository;
        this.storePositionRepository    = storePositionRepository;
    }

    public Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID storeId     = UUID.fromString(input.storeId());

        storeMembershipRepository.findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store"));

        LocalDate selectedDate = input.date() != null ? input.date() : LocalDate.now();
        int dayOfWeekNum = toDayOfWeekNum(selectedDate.getDayOfWeek());

        List<StoreMembership> memberships = storeMembershipRepository.findAllByStoreId(storeId);

        // Prepara weekStrip (segunda a domingo da semana do dia selecionado)
        LocalDate monday = selectedDate.with(DayOfWeek.MONDAY);
        List<WeekDaySummary> weekStrip = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate d = monday.plusDays(i);
            int dNum = toDayOfWeekNum(d.getDayOfWeek());
            int countScheduled = 0;
            for (StoreMembership m : memberships) {
                if (isMemberWorkingOn(m.getProviderId(), storeId, dNum, d)) {
                    countScheduled++;
                }
            }
            weekStrip.add(new WeekDaySummary(
                    d,
                    d.getDayOfMonth(),
                    getDayAbbreviation(d.getDayOfWeek()),
                    countScheduled,
                    d.equals(selectedDate)
            ));
        }

        // Informações dos funcionários no dia selecionado
        List<EmployeeDailyItem> employees = new ArrayList<>();
        LocalTime minStart = null;
        LocalTime maxEnd = null;
        int scheduledCount = 0;
        int daysOffCount = 0;

        for (StoreMembership m : memberships) {
            UUID memberId = m.getProviderId();
            User user = userRepository.findById(memberId).orElse(null);
            String name = user != null ? user.getName() : "Colaborador";

            String positionTitle = "Colaborador";
            if (m.getRole() == StoreRole.OWNER) {
                positionTitle = "Proprietário";
            } else if (m.getPositionId() != null) {
                positionTitle = storePositionRepository.findById(m.getPositionId())
                        .map(StorePosition::getTitle)
                        .orElse("Colaborador");
            }

            boolean isCurrentUser = memberId.equals(requesterId);

            Optional<EmployeeSchedule> scheduleOpt = employeeScheduleRepository.findByProviderIdAndStoreId(memberId, storeId);
            List<ScheduleSlot> slots = scheduleOpt
                    .map(s -> scheduleSlotRepository.findAllByEmployeeScheduleId(s.getId()))
                    .orElse(List.of());

            ScheduleSlot slot = findSlotForDate(slots, scheduleOpt.map(EmployeeSchedule::getScheduleType).orElse(null), dayOfWeekNum, selectedDate);

            if (slot != null && slot.getStartTime() != null && slot.getEndTime() != null) {
                scheduledCount++;
                if (minStart == null || slot.getStartTime().isBefore(minStart)) {
                    minStart = slot.getStartTime();
                }
                if (maxEnd == null || slot.getEndTime().isAfter(maxEnd)) {
                    maxEnd = slot.getEndTime();
                }

                employees.add(new EmployeeDailyItem(
                        memberId.toString(),
                        name,
                        positionTitle,
                        isCurrentUser,
                        false,
                        formatTime(slot.getStartTime()) + " – " + formatTime(slot.getEndTime()),
                        slot.getStartTime(),
                        slot.getEndTime()
                ));
            } else {
                daysOffCount++;
                employees.add(new EmployeeDailyItem(
                        memberId.toString(),
                        name,
                        positionTitle,
                        isCurrentUser,
                        true,
                        "Folga",
                        null,
                        null
                ));
            }
        }

        // Timeline horária: das 06:00 às 20:00 (ou expandido conforme minStart e maxEnd)
        int timelineStartHour = Math.min(6, minStart != null ? minStart.getHour() : 6);
        int timelineEndHour = Math.max(20, maxEnd != null ? (maxEnd.getMinute() > 0 ? maxEnd.getHour() + 1 : maxEnd.getHour()) : 20);

        List<HourlyCoverageItem> hourlyCoverage = new ArrayList<>();
        for (int h = timelineStartHour; h <= timelineEndHour; h++) {
            LocalTime hourMark = LocalTime.of(h % 24, 0);
            int workingAtHour = 0;
            for (EmployeeDailyItem emp : employees) {
                if (!emp.isDayOff() && emp.startTime() != null && emp.endTime() != null) {
                    if (!hourMark.isBefore(emp.startTime()) && hourMark.isBefore(emp.endTime())) {
                        workingAtHour++;
                    }
                }
            }
            hourlyCoverage.add(new HourlyCoverageItem(
                    h,
                    String.format("%02dh", h),
                    workingAtHour
            ));
        }

        String coverageInterval = (minStart != null && maxEnd != null)
                ? formatTime(minStart) + " – " + formatTime(maxEnd)
                : "Sem cobertura";

        return new Output(
                selectedDate,
                scheduledCount,
                daysOffCount,
                coverageInterval,
                weekStrip,
                hourlyCoverage,
                employees
        );
    }

    private boolean isMemberWorkingOn(UUID memberId, UUID storeId, int dayOfWeekNum, LocalDate date) {
        Optional<EmployeeSchedule> scheduleOpt = employeeScheduleRepository.findByProviderIdAndStoreId(memberId, storeId);
        if (scheduleOpt.isEmpty()) return false;
        List<ScheduleSlot> slots = scheduleSlotRepository.findAllByEmployeeScheduleId(scheduleOpt.get().getId());
        return findSlotForDate(slots, scheduleOpt.get().getScheduleType(), dayOfWeekNum, date) != null;
    }

    private ScheduleSlot findSlotForDate(List<ScheduleSlot> slots, ScheduleType type, int dayOfWeekNum, LocalDate date) {
        if (type == ScheduleType.FLEXIBLE) {
            return slots.stream()
                    .filter(s -> date.equals(s.getSpecificDate()))
                    .findFirst()
                    .orElse(null);
        }
        return slots.stream()
                .filter(s -> s.getDayOfWeek() != null && s.getDayOfWeek() == dayOfWeekNum)
                .findFirst()
                .orElse(null);
    }

    private int toDayOfWeekNum(DayOfWeek dow) {
        return dow == DayOfWeek.SUNDAY ? 0 : dow.getValue();
    }

    private String getDayAbbreviation(DayOfWeek dow) {
        return switch (dow) {
            case MONDAY    -> "SEG";
            case TUESDAY   -> "TER";
            case WEDNESDAY -> "QUA";
            case THURSDAY  -> "QUI";
            case FRIDAY    -> "SEX";
            case SATURDAY  -> "SÁB";
            case SUNDAY    -> "DOM";
        };
    }

    private String formatTime(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    @Schema(name = "GetStoreDailyScheduleInput")
    public record Input(String requesterId, String storeId, LocalDate date) {}

    @Schema(name = "WeekDaySummary")
    public record WeekDaySummary(
            LocalDate date,
            int dayOfMonth,
            String dayLabel,
            int scheduledCount,
            boolean isSelected
    ) {}

    @Schema(name = "HourlyCoverageItem")
    public record HourlyCoverageItem(
            int hour,
            String hourLabel,
            int workingCount
    ) {}

    @Schema(name = "EmployeeDailyItem")
    public record EmployeeDailyItem(
            String employeeId,
            String name,
            String positionTitle,
            boolean isCurrentUser,
            boolean isDayOff,
            String formattedHours,
            LocalTime startTime,
            LocalTime endTime
    ) {}

    @Schema(name = "GetStoreDailyScheduleOutput")
    public record Output(
            LocalDate date,
            int scheduledCount,
            int daysOffCount,
            String coverageInterval,
            List<WeekDaySummary> weekStrip,
            List<HourlyCoverageItem> hourlyCoverage,
            List<EmployeeDailyItem> employees
    ) {}
}
