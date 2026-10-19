package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SaveEmployeeWeeklyScheduleUseCase {

    private final EmployeeScheduleRepository employeeScheduleRepository;
    private final ScheduleSlotRepository scheduleSlotRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public SaveEmployeeWeeklyScheduleUseCase(
            EmployeeScheduleRepository employeeScheduleRepository,
            ScheduleSlotRepository scheduleSlotRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.employeeScheduleRepository = employeeScheduleRepository;
        this.scheduleSlotRepository     = scheduleSlotRepository;
        this.storeMembershipRepository  = storeMembershipRepository;
    }

    public Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID storeId     = UUID.fromString(input.storeId());
        UUID employeeId  = UUID.fromString(input.employeeId());

        var requesterMembership = storeMembershipRepository.findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store"));

        boolean isSelf = requesterId.equals(employeeId);
        if (!isSelf && !requesterMembership.getRole().canManageStore()) {
            throw new IllegalStateException("You do not have permission to configure another employee's schedule");
        }

        storeMembershipRepository.findByProviderIdAndStoreId(employeeId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee is not a member of this store"));

        // Obtém ou cria a escala para o colaborador
        EmployeeSchedule schedule = employeeScheduleRepository.findByProviderIdAndStoreId(employeeId, storeId)
                .orElseGet(() -> {
                    EmployeeSchedule newSchedule = EmployeeSchedule.create(
                            employeeId,
                            storeId,
                            ScheduleType.FIXED,
                            null,
                            null
                    );
                    return employeeScheduleRepository.save(newSchedule);
                });

        // Limpa slots anteriores para substituir pela nova grade semanal
        scheduleSlotRepository.deleteAllByEmployeeScheduleId(schedule.getId());

        List<SavedSlotOutput> savedSlots = new ArrayList<>();
        double totalHours = 0;
        int daysOffCount = 0;

        if (input.shifts() != null) {
            for (ShiftInput shift : input.shifts()) {
                if (shift.isDayOff() || shift.startTime() == null || shift.endTime() == null) {
                    daysOffCount++;
                    continue;
                }

                ScheduleSlot slot = ScheduleSlot.createRecurring(
                        schedule.getId(),
                        shift.dayOfWeek(),
                        shift.startTime(),
                        shift.endTime()
                );
                scheduleSlotRepository.save(slot);

                long minutes = Duration.between(shift.startTime(), shift.endTime()).toMinutes();
                if (minutes < 0) minutes += 24 * 60;
                double h = minutes / 60.0;
                totalHours += h;

                savedSlots.add(new SavedSlotOutput(
                        slot.getId().toString(),
                        slot.getDayOfWeek(),
                        slot.getStartTime(),
                        slot.getEndTime(),
                        h
                ));
            }
        }

        return new Output(
                schedule.getId().toString(),
                employeeId.toString(),
                storeId.toString(),
                Math.round(totalHours * 10.0) / 10.0,
                daysOffCount,
                savedSlots
        );
    }

    @Schema(name = "ShiftInput")
    public record ShiftInput(
            int dayOfWeek,
            boolean isDayOff,
            LocalTime startTime,
            LocalTime endTime
    ) {}

    @Schema(name = "SaveEmployeeWeeklyScheduleInput")
    public record Input(
            String requesterId,
            String storeId,
            String employeeId,
            List<ShiftInput> shifts
    ) {}

    @Schema(name = "SavedSlotOutput")
    public record SavedSlotOutput(
            String slotId,
            int dayOfWeek,
            LocalTime startTime,
            LocalTime endTime,
            double hours
    ) {}

    @Schema(name = "SaveEmployeeWeeklyScheduleOutput")
    public record Output(
            String scheduleId,
            String employeeId,
            String storeId,
            double weeklyTotalHours,
            int daysOffCount,
            List<SavedSlotOutput> activeSlots
    ) {}
}
