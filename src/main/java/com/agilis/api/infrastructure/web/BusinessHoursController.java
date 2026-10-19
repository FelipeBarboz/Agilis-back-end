package com.agilis.api.infrastructure.web;

import com.agilis.api.application.provider.SetBusinessHoursUseCase;
import com.agilis.api.domain.provider.BusinessHours;
import com.agilis.api.domain.provider.BusinessHoursRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stores/{storeId}/business-hours")
public class BusinessHoursController {

    private final SetBusinessHoursUseCase setBusinessHoursUseCase;
    private final BusinessHoursRepository businessHoursRepository;

    public BusinessHoursController(SetBusinessHoursUseCase setBusinessHoursUseCase, BusinessHoursRepository businessHoursRepository) {
        this.setBusinessHoursUseCase = setBusinessHoursUseCase;
        this.businessHoursRepository = businessHoursRepository;
    }

    @GetMapping
    public ResponseEntity<List<BusinessHours>> list(@PathVariable String storeId) {
        return ResponseEntity.ok(businessHoursRepository.findAllByStoreId(UUID.fromString(storeId)));
    }

    @PutMapping
    public ResponseEntity<SetBusinessHoursUseCase.Output> set(
            @PathVariable String storeId,
            @RequestBody SetBusinessHoursUseCase.Input input
    ) {
        String requesterId = getCurrentUserId();
        var inputWithRequester = new SetBusinessHoursUseCase.Input(requesterId, storeId, input.dayOfWeek(), input.opensAt(), input.closesAt());
        return ResponseEntity.ok(setBusinessHoursUseCase.execute(inputWithRequester));
    }

    private String getCurrentUserId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}