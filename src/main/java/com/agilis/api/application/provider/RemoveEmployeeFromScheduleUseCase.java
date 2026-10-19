package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.EmployeeSchedule;
import com.agilis.api.domain.provider.EmployeeScheduleRepository;
import com.agilis.api.domain.provider.ScheduleSlotRepository;
import com.agilis.api.domain.provider.StoreMembershipRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public class RemoveEmployeeFromScheduleUseCase {

    private final EmployeeScheduleRepository employeeScheduleRepository;
    private final ScheduleSlotRepository scheduleSlotRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public RemoveEmployeeFromScheduleUseCase(
            EmployeeScheduleRepository employeeScheduleRepository,
            ScheduleSlotRepository scheduleSlotRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.employeeScheduleRepository = employeeScheduleRepository;
        this.scheduleSlotRepository     = scheduleSlotRepository;
        this.storeMembershipRepository  = storeMembershipRepository;
    }

    public void execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID storeId     = UUID.fromString(input.storeId());
        UUID employeeId  = UUID.fromString(input.employeeId());

        var requesterMembership = storeMembershipRepository.findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store"));

        boolean isSelf = requesterId.equals(employeeId);
        if (!isSelf && !requesterMembership.getRole().canManageStore()) {
            throw new IllegalStateException("You do not have permission to remove another employee from schedule");
        }

        employeeScheduleRepository.findByProviderIdAndStoreId(employeeId, storeId).ifPresent(schedule -> {
            scheduleSlotRepository.deleteAllByEmployeeScheduleId(schedule.getId());
            employeeScheduleRepository.deleteById(schedule.getId());
        });
    }

    @Schema(name = "RemoveEmployeeFromScheduleInput")
    public record Input(String requesterId, String storeId, String employeeId) {}
}
