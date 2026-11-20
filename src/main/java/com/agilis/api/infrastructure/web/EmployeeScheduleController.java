package com.agilis.api.infrastructure.web;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.agilis.api.application.provider.*;
import com.agilis.api.domain.provider.ScheduleSlot;
import com.agilis.api.domain.provider.ScheduleSlotRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Agenda de Funcionários", description = "Gerenciamento de escalas e horários dos funcionários")
@RestController
@RequestMapping("/api/v1/stores/{storeId}/schedules")
public class EmployeeScheduleController {

    private final CreateEmployeeScheduleUseCase createEmployeeScheduleUseCase;
    private final AddScheduleSlotUseCase addScheduleSlotUseCase;
    private final RemoveScheduleSlotUseCase removeScheduleSlotUseCase;
    private final ScheduleSlotRepository scheduleSlotRepository;
    private final GetStoreWeeklyScheduleUseCase getStoreWeeklyScheduleUseCase;
    private final GetStoreDailyScheduleUseCase getStoreDailyScheduleUseCase;
    private final SaveEmployeeWeeklyScheduleUseCase saveEmployeeWeeklyScheduleUseCase;
    private final RemoveEmployeeFromScheduleUseCase removeEmployeeFromScheduleUseCase;

    public EmployeeScheduleController(
            CreateEmployeeScheduleUseCase createEmployeeScheduleUseCase,
            AddScheduleSlotUseCase addScheduleSlotUseCase,
            RemoveScheduleSlotUseCase removeScheduleSlotUseCase,
            ScheduleSlotRepository scheduleSlotRepository,
            GetStoreWeeklyScheduleUseCase getStoreWeeklyScheduleUseCase,
            GetStoreDailyScheduleUseCase getStoreDailyScheduleUseCase,
            SaveEmployeeWeeklyScheduleUseCase saveEmployeeWeeklyScheduleUseCase,
            RemoveEmployeeFromScheduleUseCase removeEmployeeFromScheduleUseCase
    ) {
        this.createEmployeeScheduleUseCase       = createEmployeeScheduleUseCase;
        this.addScheduleSlotUseCase              = addScheduleSlotUseCase;
        this.removeScheduleSlotUseCase           = removeScheduleSlotUseCase;
        this.scheduleSlotRepository              = scheduleSlotRepository;
        this.getStoreWeeklyScheduleUseCase       = getStoreWeeklyScheduleUseCase;
        this.getStoreDailyScheduleUseCase        = getStoreDailyScheduleUseCase;
        this.saveEmployeeWeeklyScheduleUseCase   = saveEmployeeWeeklyScheduleUseCase;
        this.removeEmployeeFromScheduleUseCase   = removeEmployeeFromScheduleUseCase;
    }

    @PostMapping
    @Operation(summary = "Cria uma escala de trabalho para um funcionário")
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
    @Operation(summary = "Adiciona um intervalo de horário na escala do funcionário")
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
    @Operation(summary = "Lista os horários da escala do funcionário")
    public ResponseEntity<List<ScheduleSlot>> listSlots(@PathVariable String scheduleId) {
        return ResponseEntity.ok(scheduleSlotRepository.findAllByEmployeeScheduleId(UUID.fromString(scheduleId)));
    }

    @DeleteMapping("/slots/{slotId}")
    @Operation(summary = "Remove um intervalo de horário da escala")
    public ResponseEntity<Void> removeSlot(@PathVariable String slotId) {
        String requesterId = getCurrentUserId();
        removeScheduleSlotUseCase.execute(requesterId, slotId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/weekly")
    @Operation(summary = "Consulta a escala semanal dos funcionários da loja")
    public ResponseEntity<GetStoreWeeklyScheduleUseCase.Output> getWeekly(
            @PathVariable String storeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate
    ) {
        String requesterId = getCurrentUserId();
        return ResponseEntity.ok(getStoreWeeklyScheduleUseCase.execute(
                new GetStoreWeeklyScheduleUseCase.Input(requesterId, storeId, startDate)
        ));
    }

    @GetMapping("/daily")
    @Operation(summary = "Consulta a escala diária dos funcionários da loja")
    public ResponseEntity<GetStoreDailyScheduleUseCase.Output> getDaily(
            @PathVariable String storeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        String requesterId = getCurrentUserId();
        return ResponseEntity.ok(getStoreDailyScheduleUseCase.execute(
                new GetStoreDailyScheduleUseCase.Input(requesterId, storeId, date)
        ));
    }

    @PutMapping("/employees/{employeeId}/weekly")
    @Operation(summary = "Salva a escala semanal completa de um funcionário")
    public ResponseEntity<SaveEmployeeWeeklyScheduleUseCase.Output> saveEmployeeWeekly(
            @PathVariable String storeId,
            @PathVariable String employeeId,
            @RequestBody List<SaveEmployeeWeeklyScheduleUseCase.ShiftInput> shifts
    ) {
        String requesterId = getCurrentUserId();
        return ResponseEntity.ok(saveEmployeeWeeklyScheduleUseCase.execute(
                new SaveEmployeeWeeklyScheduleUseCase.Input(requesterId, storeId, employeeId, shifts)
        ));
    }

    @DeleteMapping("/employees/{employeeId}")
    @Operation(summary = "Remove um funcionário da escala da loja")
    public ResponseEntity<Void> removeEmployeeFromSchedule(
            @PathVariable String storeId,
            @PathVariable String employeeId
    ) {
        String requesterId = getCurrentUserId();
        removeEmployeeFromScheduleUseCase.execute(
                new RemoveEmployeeFromScheduleUseCase.Input(requesterId, storeId, employeeId)
        );
        return ResponseEntity.noContent().build();
    }

    private String getCurrentUserId() {
        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new org.springframework.security.access.AccessDeniedException("Acesso negado: usuário não autenticado");
        }
        return (String) auth.getPrincipal();
    }
}