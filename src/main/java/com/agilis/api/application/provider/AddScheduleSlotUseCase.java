package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class AddScheduleSlotUseCase {

    private final ScheduleSlotRepository scheduleSlotRepository;
    private final EmployeeScheduleRepository employeeScheduleRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public AddScheduleSlotUseCase(
            ScheduleSlotRepository scheduleSlotRepository,
            EmployeeScheduleRepository employeeScheduleRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.scheduleSlotRepository     = scheduleSlotRepository;
        this.employeeScheduleRepository = employeeScheduleRepository;
        this.storeMembershipRepository  = storeMembershipRepository;
    }

    public Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID scheduleId   = UUID.fromString(input.employeeScheduleId());

        EmployeeSchedule schedule = employeeScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));

        boolean isSelf = schedule.getProviderId().equals(requesterId);
        if (!isSelf) {
            var membership = storeMembershipRepository.findByProviderIdAndStoreId(requesterId, schedule.getStoreId())
                    .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store"));
            if (!membership.getRole().canManageStore()) {
                throw new IllegalStateException("You do not have permission to edit this employee's schedule");
            }
        }

        // regra pra cada um dos ENUM:
        // FLEXIBLE -> exige specificDate
        // FIXED / STORE_HOURS -> exige dayOfWeek (recorrente)
        ScheduleSlot slot = switch (schedule.getScheduleType()) {
            case FLEXIBLE -> {
                if (input.specificDate() == null) {
                    throw new IllegalArgumentException("Escala flexível exige uma data específica");
                }
                yield ScheduleSlot.createSpecific(scheduleId, input.specificDate(), input.startTime(), input.endTime());
            }
            case FIXED, STORE_HOURS -> {
                if (input.dayOfWeek() == null) {
                    throw new IllegalArgumentException("Escala fixa exige o dia da semana");
                }
                yield ScheduleSlot.createRecurring(scheduleId, input.dayOfWeek(), input.startTime(), input.endTime());
            }
        };

        scheduleSlotRepository.save(slot);

        return new Output(slot.getId().toString(), scheduleId.toString(), slot.getDayOfWeek(), slot.getSpecificDate(), slot.getStartTime(), slot.getEndTime());
    }

    public record Input(String requesterId, String employeeScheduleId, Integer dayOfWeek, LocalDate specificDate, LocalTime startTime, LocalTime endTime) {}
    public record Output(String id, String employeeScheduleId, Integer dayOfWeek, LocalDate specificDate, LocalTime startTime, LocalTime endTime) {}
}