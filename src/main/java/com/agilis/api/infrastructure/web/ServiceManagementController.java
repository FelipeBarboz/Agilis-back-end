package com.agilis.api.infrastructure.web;

import com.agilis.api.application.service.*;
import com.agilis.api.domain.service.PriceType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stores/{storeId}/services")
@Tag(name = "Service Management", description = "CRUD of services within a store")
public class ServiceManagementController {

    private final CreateServiceUseCase createServiceUseCase;
    private final UpdateServiceUseCase updateServiceUseCase;
    private final DeleteServiceUseCase deleteServiceUseCase;
    private final GetServiceUseCase getServiceUseCase;
    private final ListServicesUseCase listServicesUseCase;

    public ServiceManagementController(CreateServiceUseCase createServiceUseCase,
                                       UpdateServiceUseCase updateServiceUseCase,
                                       DeleteServiceUseCase deleteServiceUseCase,
                                       GetServiceUseCase getServiceUseCase,
                                       ListServicesUseCase listServicesUseCase) {
        this.createServiceUseCase = createServiceUseCase;
        this.updateServiceUseCase = updateServiceUseCase;
        this.deleteServiceUseCase = deleteServiceUseCase;
        this.getServiceUseCase = getServiceUseCase;
        this.listServicesUseCase = listServicesUseCase;
    }

    // ── DTOs ──────────────────────────────────────────────────────────────────

    public record ServiceRequest(String title, String description,
                                 BigDecimal price, PriceType priceType,
                                 int durationMinutes) {}

    public record ServiceResponse(String serviceId, String storeId, String title,
                                  String description, BigDecimal price, String priceType,
                                  int durationMinutes, LocalDateTime createdAt) {}

    public record ServiceListResponse(List<ServiceResponse> services) {}

    // ── Endpoints ─────────────────────────────────────────────────────────────

    @PostMapping
    @Operation(summary = "Create a service", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResponse> create(@PathVariable String storeId,
                                                  @RequestBody ServiceRequest body) {
        String userId = getPrincipal();
        var input = new CreateServiceUseCase.Input(
                userId, storeId, body.title(), body.description(),
                body.price(), body.priceType(), body.durationMinutes());
        var out = createServiceUseCase.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(out));
    }

    @PutMapping("/{serviceId}")
    @Operation(summary = "Update a service", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServiceResponse> update(@PathVariable String storeId,
                                                  @PathVariable String serviceId,
                                                  @RequestBody ServiceRequest body) {
        String userId = getPrincipal();
        var input = new UpdateServiceUseCase.Input(
                userId, storeId, serviceId, body.title(), body.description(),
                body.price(), body.priceType(), body.durationMinutes());
        var out = updateServiceUseCase.execute(input);
        return ResponseEntity.ok(toResponse(out));
    }

    @DeleteMapping("/{serviceId}")
    @Operation(summary = "Delete a service", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> delete(@PathVariable String storeId,
                                       @PathVariable String serviceId) {
        String userId = getPrincipal();
        deleteServiceUseCase.execute(new DeleteServiceUseCase.Input(userId, storeId, serviceId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "List all services for a store (public)")
    public ResponseEntity<ServiceListResponse> list(@PathVariable String storeId) {
        var out = listServicesUseCase.execute(new ListServicesUseCase.Input(storeId));
        List<ServiceResponse> responses = out.services().stream()
                .map(s -> new ServiceResponse(s.serviceId(), s.storeId(), s.title(),
                        s.description(), s.price(), s.priceType(),
                        s.durationMinutes(), s.createdAt()))
                .toList();
        return ResponseEntity.ok(new ServiceListResponse(responses));
    }

    @GetMapping("/{serviceId}")
    @Operation(summary = "Get a specific service (public)")
    public ResponseEntity<ServiceResponse> getOne(@PathVariable String storeId,
                                                  @PathVariable String serviceId) {
        var out = getServiceUseCase.execute(new GetServiceUseCase.Input(serviceId));
        return ResponseEntity.ok(toResponse(out));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String getPrincipal() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private ServiceResponse toResponse(CreateServiceUseCase.Output out) {
        return new ServiceResponse(out.serviceId(), out.storeId(), out.title(),
                out.description(), out.price(), out.priceType(),
                out.durationMinutes(), out.createdAt());
    }

    private ServiceResponse toResponse(UpdateServiceUseCase.Output out) {
        return new ServiceResponse(out.serviceId(), out.storeId(), out.title(),
                out.description(), out.price(), out.priceType(),
                out.durationMinutes(), out.createdAt());
    }

    private ServiceResponse toResponse(GetServiceUseCase.Output out) {
        return new ServiceResponse(out.serviceId(), out.storeId(), out.title(),
                out.description(), out.price(), out.priceType(),
                out.durationMinutes(), out.createdAt());
    }
}