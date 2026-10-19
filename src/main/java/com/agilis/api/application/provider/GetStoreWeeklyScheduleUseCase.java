package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.*;
import com.agilis.api.domain.user.User;
import com.agilis.api.domain.user.UserRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GetStoreWeeklyScheduleUseCase {

    private final StoreMembershipRepository storeMembershipRepository;
    private final EmployeeScheduleRepository employeeScheduleRepository;
    private final ScheduleSlotRepository scheduleSlotRepository;
    private final UserRepository userRepository;
    private final StorePositionRepository storePositionRepository;

    public GetStoreWeeklyScheduleUseCase(
            StoreMembershipRepository storeMembershipRepository,
            EmployeeScheduleRepository employeeScheduleRepository,
            ScheduleSlotRepository scheduleSlotRepository,
            UserRepository userRepository,
            StorePositionRepository storePositionRepository
    ) {
        this.storeMembershipRepository     = storeMembershipRepository;
        this.employeeScheduleRepository    = employeeScheduleRepository;
        this.scheduleSlotRepository        = scheduleSlotRepository;
        this.userRepository                = userRepository;
        this.storePositionRepository       = storePositionRepository;
    }

    public Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID storeId     = UUID.fromString(input.storeId());

        storeMembershipRepository.findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store"));

        LocalDate anchorDate = input.startDate() != null ? input.startDate() : LocalDate.now();
        // Começa sempre na Segunda-feira daquela semana
        LocalDate monday = anchorDate.with(DayOfWeek.MONDAY);

        List<LocalDate> weekDates = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weekDates.add(monday.plusDays(i));
        }

        List<StoreMembership> memberships = storeMembershipRepository.findAllByStoreId(storeId);
        List<EmployeeWeeklyScheduleItem> employeeItems = new ArrayList<>();
        Set<String> positionsSet = new LinkedHashSet<>();

        int[] dailyWorkingCount = new int[7];
        double totalWeeklyHoursSum = 0;
        int totalMembersWithShift = 0;

        LocalDate today = LocalDate.now();
        int todayWorkingCount = 0;

        for (StoreMembership membership : memberships) {
            UUID memberId = membership.getProviderId();
            User user = userRepository.findById(memberId).orElse(null);
            String name = user != null ? user.getName() : "Colaborador";

            String positionTitle = "Colaborador";
            if (membership.getRole() == StoreRole.OWNER) {
                positionTitle = "Proprietário";
            } else if (membership.getPositionId() != null) {
                positionTitle = storePositionRepository.findById(membership.getPositionId())
                        .map(StorePosition::getTitle)
                        .orElse("Colaborador");
            }
            positionsSet.add(positionTitle);

            boolean isCurrentUser = memberId.equals(requesterId);

            Optional<EmployeeSchedule> scheduleOpt = employeeScheduleRepository.findByProviderIdAndStoreId(memberId, storeId);
            List<ScheduleSlot> slots = scheduleOpt
                    .map(s -> scheduleSlotRepository.findAllByEmployeeScheduleId(s.getId()))
                    .orElse(List.of());

            List<DaySlotItem> daySlots = new ArrayList<>();
            double memberTotalHours = 0;
            int memberDaysOff = 0;

            for (int i = 0; i < 7; i++) {
                LocalDate date = weekDates.get(i);
                int dayOfWeekNum = toDayOfWeekNum(date.getDayOfWeek()); // 0 = Domingo, 1 = Segunda...

                ScheduleSlot slotMatch = findSlotForDate(slots, scheduleOpt.map(EmployeeSchedule::getScheduleType).orElse(null), dayOfWeekNum, date);

                if (slotMatch != null && slotMatch.getStartTime() != null && slotMatch.getEndTime() != null) {
                    double hours = calculateHours(slotMatch.getStartTime(), slotMatch.getEndTime());
                    memberTotalHours += hours;
                    dailyWorkingCount[i]++;

                    if (date.equals(today)) {
                        todayWorkingCount++;
                    }

                    daySlots.add(new DaySlotItem(
                            dayOfWeekNum,
                            date,
                            date.getDayOfMonth(),
                            getDayAbbreviation(date.getDayOfWeek()),
                            false,
                            formatHoursDisplay(slotMatch.getStartTime(), slotMatch.getEndTime()),
                            slotMatch.getStartTime(),
                            slotMatch.getEndTime(),
                            hours
                    ));
                } else {
                    memberDaysOff++;
                    daySlots.add(new DaySlotItem(
                            dayOfWeekNum,
                            date,
                            date.getDayOfMonth(),
                            getDayAbbreviation(date.getDayOfWeek()),
                            true,
                            "Folga",
                            null,
                            null,
                            0.0
                    ));
                }
            }

            if (memberTotalHours > 0) {
                totalMembersWithShift++;
            }
            totalWeeklyHoursSum += memberTotalHours;

            employeeItems.add(new EmployeeWeeklyScheduleItem(
                    memberId.toString(),
                    name,
                    positionTitle,
                    isCurrentUser,
                    daySlots,
                    Math.round(memberTotalHours * 10.0) / 10.0,
                    memberDaysOff
            ));
        }

        List<DayHeaderItem> dayHeaders = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate d = weekDates.get(i);
            dayHeaders.add(new DayHeaderItem(
                    toDayOfWeekNum(d.getDayOfWeek()),
                    d,
                    d.getDayOfMonth(),
                    getDayAbbreviation(d.getDayOfWeek()),
                    dailyWorkingCount[i],
                    memberships.size()
            ));
        }

        int totalMembers = memberships.size();
        int activePercentage = totalMembers > 0 ? (int) Math.round(((double) totalMembersWithShift / totalMembers) * 100) : 0;
        double dailyAverageCoverage = Math.round((Arrays.stream(dailyWorkingCount).average().orElse(0.0)) * 10.0) / 10.0;

        MetricsSummary metrics = new MetricsSummary(
                totalMembers,
                activePercentage,
                Math.round(totalWeeklyHoursSum * 10.0) / 10.0,
                dailyAverageCoverage,
                todayWorkingCount,
                totalMembers,
                totalMembers - todayWorkingCount
        );

        return new Output(
                monday,
                monday.plusDays(6),
                metrics,
                dayHeaders,
                employeeItems,
                new ArrayList<>(positionsSet)
        );
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

    private double calculateHours(LocalTime start, LocalTime end) {
        if (start == null || end == null) return 0;
        long minutes = Duration.between(start, end).toMinutes();
        if (minutes < 0) minutes += 24 * 60;
        return minutes / 60.0;
    }

    private String formatHoursDisplay(LocalTime start, LocalTime end) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH'h'");
        DateTimeFormatter fmtMin = DateTimeFormatter.ofPattern("HH'h'mm");
        String s = start.getMinute() == 0 ? start.format(fmt) : start.format(fmtMin);
        String e = end.getMinute() == 0 ? end.format(fmt) : end.format(fmtMin);
        return s + "–" + e;
    }

    @Schema(name = "GetStoreWeeklyScheduleInput")
    public record Input(String requesterId, String storeId, LocalDate startDate) {}

    @Schema(name = "MetricsSummary")
    public record MetricsSummary(
            int totalMembersInSchedule,
            int activePercentage,
            double weeklyTotalHours,
            double dailyAverageCoverage,
            int todayInStoreCount,
            int todayTotalCount,
            int todayOffCount
    ) {}

    @Schema(name = "DayHeaderItem")
    public record DayHeaderItem(
            int dayOfWeek,
            LocalDate date,
            int dayOfMonth,
            String dayLabel,
            int activeCount,
            int totalCount
    ) {}

    @Schema(name = "DaySlotItem")
    public record DaySlotItem(
            int dayOfWeek,
            LocalDate date,
            int dayOfMonth,
            String dayLabel,
            boolean isDayOff,
            String formattedHours,
            LocalTime startTime,
            LocalTime endTime,
            double hours
    ) {}

    @Schema(name = "EmployeeWeeklyScheduleItem")
    public record EmployeeWeeklyScheduleItem(
            String employeeId,
            String name,
            String positionTitle,
            boolean isCurrentUser,
            List<DaySlotItem> days,
            double weeklyHours,
            int daysOffCount
    ) {}

    @Schema(name = "GetStoreWeeklyScheduleOutput")
    public record Output(
            LocalDate weekStartDate,
            LocalDate weekEndDate,
            MetricsSummary metrics,
            List<DayHeaderItem> dayHeaders,
            List<EmployeeWeeklyScheduleItem> employees,
            List<String> availablePositions
    ) {}
}
