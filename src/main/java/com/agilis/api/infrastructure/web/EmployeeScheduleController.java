package com.agilis.api.infrastructure.web;

import com.agilis.api.application.provider.AddScheduleSlotUseCase;
import com.agilis.api.application.provider.CreateEmployeeScheduleUseCase;
import com.agilis.api.application.provider.RemoveScheduleSlotUseCase;
import com.agilis.api.domain.provider.ScheduleSlot;
import com.agilis.api.domain.provider.ScheduleSlotRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stores/{storeId}/schedules")
public class EmployeeScheduleController {

    private final CreateEmployeeScheduleUseCase createEmployeeScheduleUseCase;
    private final AddScheduleSlotUseCase addScheduleSlotUseCase;
    private final RemoveScheduleSlotUseCase removeScheduleSlotUseCase;
    private final ScheduleSlotRepository scheduleSlotRepository;

    public EmployeeScheduleController(
            CreateEmployeeScheduleUseCase createEmployeeScheduleUseCase,
            AddScheduleSlotUseCase addScheduleSlotUseCase,
            RemoveScheduleSlotUseCase removeScheduleSlotUseCase,
            ScheduleSlotRepository scheduleSlotRepository
    ) {
        this.createEmployeeScheduleUseCase = createEmployeeScheduleUseCase;
        this.addScheduleSlotUseCase        = addScheduleSlotUseCase;
        this.removeScheduleSlotUseCase     = removeScheduleSlotUseCase;
        this.scheduleSlotRepository        = scheduleSlotRepository;
    }

    @PostMapping
    public ResponseEntity<CreateEmployeeScheduleUseCase.Output> create(
            @PathVariable String storeId,
            @RequestBody CreateEmployeeScheduleUseCase.Input input
    ) {
        String requesterId = getCurrentUserId();
        var inputWithRequester = new CreateEmployeeScheduleUseCase.Input(
                requesterId, storeId, input.employeeId(), input.scheduleType(), input.monthlyHoursQuota(), input.monthlyDaysQuota()
        );
        return ResponseEntity.ok(createEmployeeScheduleUseCase.execute(inputWithRequester));
    }

    @PostMapping("/{scheduleId}/slots")
    public ResponseEntity<AddScheduleSlotUseCase.Output> addSlot(
            @PathVariable String scheduleId,
            @RequestBody AddScheduleSlotUseCase.Input input
    ) {
        String requesterId = getCurrentUserId();
        var inputWithRequester = new AddScheduleSlotUseCase.Input(
                requesterId, scheduleId, input.dayOfWeek(), input.specificDate(), input.startTime(), input.endTime()
        );
        return ResponseEntity.ok(addScheduleSlotUseCase.execute(inputWithRequester));
    }

    @GetMapping("/{scheduleId}/slots")
    public ResponseEntity<List<ScheduleSlot>> listSlots(@PathVariable String scheduleId) {
        return ResponseEntity.ok(scheduleSlotRepository.findAllByEmployeeScheduleId(UUID.fromString(scheduleId)));
    }

    @DeleteMapping("/slots/{slotId}")
    public ResponseEntity<Void> removeSlot(@PathVariable String slotId) {
        String requesterId = getCurrentUserId();
        removeScheduleSlotUseCase.execute(requesterId, slotId);
        return ResponseEntity.noContent().build();
    }

    private String getCurrentUserId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}