package com.agilis.api.infrastructure.web;

import com.agilis.api.application.booking.GetAvailableSlotsUseCase;
import com.agilis.api.application.provider.ToggleEmployeeSelectionUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
public class AvailabilityController {

    private final GetAvailableSlotsUseCase getAvailableSlotsUseCase;
    private final ToggleEmployeeSelectionUseCase toggleEmployeeSelectionUseCase;

    public AvailabilityController(GetAvailableSlotsUseCase getAvailableSlotsUseCase, ToggleEmployeeSelectionUseCase toggleEmployeeSelectionUseCase) {
        this.getAvailableSlotsUseCase = getAvailableSlotsUseCase;
        this.toggleEmployeeSelectionUseCase = toggleEmployeeSelectionUseCase;
    }

    @GetMapping("/api/v1/services/{serviceId}/availability")
    public ResponseEntity<GetAvailableSlotsUseCase.Output> getAvailability(
            @PathVariable String serviceId,
            @RequestParam LocalDate date,
            @RequestParam(required = false) String employeeId
    ) {
        return ResponseEntity.ok(getAvailableSlotsUseCase.execute(new GetAvailableSlotsUseCase.Input(serviceId, date, employeeId)));
    }

    // dono/admin liga ou desliga a escolha de funcionário
    @PatchMapping("/api/v1/stores/{storeId}/settings/employee-selection")
    public ResponseEntity<Void> toggleEmployeeSelection(
            @PathVariable String storeId,
            @RequestParam boolean allow
    ) {
        String requesterId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        toggleEmployeeSelectionUseCase.execute(requesterId, storeId, allow);
        return ResponseEntity.noContent().build();
    }
}