package com.agilis.api.infrastructure.web;

import com.agilis.api.application.service.SetPriceTiersUseCase;
import com.agilis.api.domain.service.ServicePriceTier;
import com.agilis.api.domain.service.ServicePriceTierRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/services/{serviceId}/price-tiers")
public class ServicePriceTierController {

    private final SetPriceTiersUseCase setPriceTiersUseCase;
    private final ServicePriceTierRepository priceTierRepository;

    public ServicePriceTierController(SetPriceTiersUseCase setPriceTiersUseCase, ServicePriceTierRepository priceTierRepository) {
        this.setPriceTiersUseCase = setPriceTiersUseCase;
        this.priceTierRepository  = priceTierRepository;
    }

    @GetMapping
    public ResponseEntity<List<ServicePriceTier>> list(@PathVariable String serviceId) {
        return ResponseEntity.ok(priceTierRepository.findAllByServiceIdOrdered(UUID.fromString(serviceId)));
    }

    @PutMapping
    public ResponseEntity<List<SetPriceTiersUseCase.Output>> set(
            @PathVariable String serviceId,
            @RequestBody List<SetPriceTiersUseCase.TierInput> tiers
    ) {
        String requesterId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(setPriceTiersUseCase.execute(new SetPriceTiersUseCase.Input(requesterId, serviceId, tiers)));
    }
}