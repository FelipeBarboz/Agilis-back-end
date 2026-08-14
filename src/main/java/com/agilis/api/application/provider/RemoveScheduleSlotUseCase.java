package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.EmployeeSchedule;
import com.agilis.api.domain.provider.EmployeeScheduleRepository;
import com.agilis.api.domain.provider.ScheduleSlot;
import com.agilis.api.domain.provider.ScheduleSlotRepository;
import com.agilis.api.domain.provider.StoreMembershipRepository;

import java.util.UUID;

public class RemoveScheduleSlotUseCase {

    private final ScheduleSlotRepository scheduleSlotRepository;
    private final EmployeeScheduleRepository employeeScheduleRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public RemoveScheduleSlotUseCase(
            ScheduleSlotRepository scheduleSlotRepository,
            EmployeeScheduleRepository employeeScheduleRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.scheduleSlotRepository     = scheduleSlotRepository;
        this.employeeScheduleRepository = employeeScheduleRepository;
        this.storeMembershipRepository  = storeMembershipRepository;
    }

    public void execute(String requesterIdStr, String slotIdStr) {
        UUID requesterId = UUID.fromString(requesterIdStr);
        UUID slotId       = UUID.fromString(slotIdStr);

        ScheduleSlot slot = scheduleSlotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found"));

        EmployeeSchedule schedule = employeeScheduleRepository.findById(slot.getEmployeeScheduleId())
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));

        boolean isSelf = schedule.getProviderId().equals(requesterId);
        if (!isSelf) {
            var membership = storeMembershipRepository.findByProviderIdAndStoreId(requesterId, schedule.getStoreId())
                    .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store"));
            if (!membership.getRole().canManageStore()) {
                throw new IllegalStateException("You do not have permission to remove this schedule");
            }
        }

        scheduleSlotRepository.deleteById(slotId);
    }
}