package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.EmployeeSchedule;
import com.agilis.api.domain.provider.EmployeeScheduleRepository;
import com.agilis.api.domain.provider.ScheduleType;
import com.agilis.api.domain.provider.StoreMembershipRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public class CreateEmployeeScheduleUseCase {

    private final EmployeeScheduleRepository employeeScheduleRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public CreateEmployeeScheduleUseCase(EmployeeScheduleRepository employeeScheduleRepository, StoreMembershipRepository storeMembershipRepository) {
        this.employeeScheduleRepository = employeeScheduleRepository;
        this.storeMembershipRepository  = storeMembershipRepository;
    }

    public Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID storeId      = UUID.fromString(input.storeId());
        UUID employeeId    = UUID.fromString(input.employeeId());

        var requesterMembership = storeMembershipRepository.findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store"));

        // o próprio funcionário pode configurar sua escala se for do tipo FLEXIBLE;
        // owner/admin configuram qualquer tipo pra qualquer membro
        boolean isSelf = requesterId.equals(employeeId);
        if (!isSelf && !requesterMembership.getRole().canManageStore()) {
            throw new IllegalStateException("You do not have permission to configure another employee's schedule");
        }

        // confirma que o employeeId é membro da loja
        storeMembershipRepository.findByProviderIdAndStoreId(employeeId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee is not a member of this store"));

        ScheduleType type = ScheduleType.valueOf(input.scheduleType());

        EmployeeSchedule schedule = EmployeeSchedule.create(employeeId, storeId, type, input.monthlyHoursQuota(), input.monthlyDaysQuota());
        employeeScheduleRepository.save(schedule);

        return toOutput(schedule);
    }

    private Output toOutput(EmployeeSchedule s) {
        return new Output(s.getId().toString(), s.getProviderId().toString(), s.getStoreId().toString(),
                s.getScheduleType().name(), s.getMonthlyHoursQuota(), s.getMonthlyDaysQuota());
    }

    @Schema(name = "CreateEmployeeScheduleInput")
    public record Input(String requesterId, String storeId, String employeeId, String scheduleType, Integer monthlyHoursQuota, Integer monthlyDaysQuota) {}
    @Schema(name = "CreateEmployeeScheduleOutput")
    public record Output(String id, String employeeId, String storeId, String scheduleType, Integer monthlyHoursQuota, Integer monthlyDaysQuota) {}
}