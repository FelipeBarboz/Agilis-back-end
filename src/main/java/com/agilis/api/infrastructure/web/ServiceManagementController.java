package com.agilis.api.infrastructure.web;

import com.agilis.api.application.service.CreateServiceUseCase;
import com.agilis.api.application.service.DeleteServiceUseCase;
import com.agilis.api.application.service.UpdateServiceUseCase;
import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServiceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stores/{storeId}/services")
public class ServiceManagementController {

    private final CreateServiceUseCase createServiceUseCase;
    private final UpdateServiceUseCase updateServiceUseCase;
    private final DeleteServiceUseCase deleteServiceUseCase;
    private final ServiceRepository serviceRepository;

    public ServiceManagementController(
            CreateServiceUseCase createServiceUseCase,
            UpdateServiceUseCase updateServiceUseCase,
            DeleteServiceUseCase deleteServiceUseCase,
            ServiceRepository serviceRepository
    ) {
        this.createServiceUseCase = createServiceUseCase;
        this.updateServiceUseCase = updateServiceUseCase;
        this.deleteServiceUseCase = deleteServiceUseCase;
        this.serviceRepository    = serviceRepository;
    }

    @GetMapping
    public ResponseEntity<List<Service>> listByStore(@PathVariable String storeId) {
        return ResponseEntity.ok(
                serviceRepository.findAllByStoreId(UUID.fromString(storeId))
        );
    }

    @GetMapping("/{serviceId}")
    public ResponseEntity<Service> getById(
            @PathVariable String storeId,
            @PathVariable String serviceId
    ) {
        return serviceRepository.findById(UUID.fromString(serviceId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CreateServiceUseCase.Output> create(
            @PathVariable String storeId,
            @RequestBody CreateServiceUseCase.Input input
    ) {
        String requesterId = getCurrentUserId();
        CreateServiceUseCase.Input inputWithRequester = new CreateServiceUseCase.Input(
                requesterId,
                storeId,
                input.title(),
                input.description(),
                input.price(),
                input.priceType(),
                input.durationMinutes()
        );
        return ResponseEntity.ok(createServiceUseCase.execute(inputWithRequester));
    }

    @PutMapping("/{serviceId}")
    public ResponseEntity<UpdateServiceUseCase.Output> update(
            @PathVariable String storeId,
            @PathVariable String serviceId,
            @RequestBody UpdateServiceUseCase.Input input
    ) {
        String requesterId = getCurrentUserId();
        UpdateServiceUseCase.Input inputWithRequester = new UpdateServiceUseCase.Input(
                requesterId,
                serviceId,
                input.title(),
                input.description(),
                input.price(),
                input.priceType(),
                input.durationMinutes()
        );
        return ResponseEntity.ok(updateServiceUseCase.execute(inputWithRequester));
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<Void> delete(
            @PathVariable String storeId,
            @PathVariable String serviceId
    ) {
        String requesterId = getCurrentUserId();
        deleteServiceUseCase.execute(new DeleteServiceUseCase.Input(requesterId, serviceId));
        return ResponseEntity.noContent().build();
    }

    private String getCurrentUserId() {
        return (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}