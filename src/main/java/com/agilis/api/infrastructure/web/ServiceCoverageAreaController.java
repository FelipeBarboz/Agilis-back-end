package com.agilis.api.infrastructure.web;

import com.agilis.api.application.service.SetCoverageAreasUseCase;
import com.agilis.api.domain.service.ServiceCoverageArea;
import com.agilis.api.domain.service.ServiceCoverageAreaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Área de Cobertura", description = "Configuração das áreas de cobertura de um serviço")
@RestController
@RequestMapping("/api/v1/services/{serviceId}/coverage-areas")
public class ServiceCoverageAreaController {

    private final SetCoverageAreasUseCase setCoverageAreasUseCase;
    private final ServiceCoverageAreaRepository coverageAreaRepository;

    public ServiceCoverageAreaController(SetCoverageAreasUseCase setCoverageAreasUseCase, ServiceCoverageAreaRepository coverageAreaRepository) {
        this.setCoverageAreasUseCase  = setCoverageAreasUseCase;
        this.coverageAreaRepository   = coverageAreaRepository;
    }

    @GetMapping
    @Operation(summary = "Lista as cidades e bairros atendidos pelo serviço")
    public ResponseEntity<List<ServiceCoverageArea>> list(@PathVariable String serviceId) {
        return ResponseEntity.ok(coverageAreaRepository.findAllByServiceId(UUID.fromString(serviceId)));
    }

    @PutMapping
    @Operation(summary = "Define ou atualiza as áreas de cobertura do serviço")
    public ResponseEntity<List<SetCoverageAreasUseCase.Output>> set(
            @PathVariable String serviceId,
            @RequestBody List<SetCoverageAreasUseCase.AreaInput> areas
    ) {
        String requesterId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(setCoverageAreasUseCase.execute(new SetCoverageAreasUseCase.Input(requesterId, serviceId, areas)));
    }
}