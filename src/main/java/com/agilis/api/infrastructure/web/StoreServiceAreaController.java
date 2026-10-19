package com.agilis.api.infrastructure.web;

import com.agilis.api.application.provider.GetServiceAreaUseCase;
import com.agilis.api.application.provider.SetServiceAreaUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stores/{storeId}/service-area")
public class StoreServiceAreaController {

    private final SetServiceAreaUseCase setServiceAreaUseCase;
    private final GetServiceAreaUseCase getServiceAreaUseCase;

    public StoreServiceAreaController(SetServiceAreaUseCase setServiceAreaUseCase, GetServiceAreaUseCase getServiceAreaUseCase) {
        this.setServiceAreaUseCase = setServiceAreaUseCase;
        this.getServiceAreaUseCase = getServiceAreaUseCase;
    }

    @GetMapping
    public ResponseEntity<SetServiceAreaUseCase.Output> get(@PathVariable String storeId) {
        return ResponseEntity.ok(getServiceAreaUseCase.execute(storeId));
    }

    @PutMapping
    public ResponseEntity<SetServiceAreaUseCase.Output> set(
            @PathVariable String storeId,
            @RequestBody SetServiceAreaUseCase.Input input
    ) {
        String requesterId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var inputWithRequester = new SetServiceAreaUseCase.Input(
                requesterId, storeId, input.attendanceType(), input.radiusKm(), input.referenceAddress(), input.cities()
        );
        return ResponseEntity.ok(setServiceAreaUseCase.execute(inputWithRequester));
    }
}