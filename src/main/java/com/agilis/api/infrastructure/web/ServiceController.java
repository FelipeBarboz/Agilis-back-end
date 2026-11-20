package com.agilis.api.infrastructure.web;

import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServiceRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Serviços", description = "Consulta de serviços por loja")
@RestController
@RequestMapping("/api/v1/services")
public class ServiceController {

    private final ServiceRepository serviceRepository;

    public ServiceController(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @GetMapping("/store/{storeId}")
    @Operation(summary = "Lista todos os serviços ativos de uma loja")
    public ResponseEntity<List<Service>> getByStore(@PathVariable String storeId) {
        return ResponseEntity.ok(serviceRepository.findAllByStoreId(UUID.fromString(storeId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca os detalhes de um serviço pelo ID")
    public ResponseEntity<Service> getById(@PathVariable String id) {
        return serviceRepository.findById(UUID.fromString(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}