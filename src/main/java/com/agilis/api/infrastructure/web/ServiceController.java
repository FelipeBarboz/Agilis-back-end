package com.agilis.api.infrastructure.web;

import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServiceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceController {

    private final ServiceRepository serviceRepository;

    public ServiceController(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<Service>> getByStore(@PathVariable String storeId) {
        return ResponseEntity.ok(serviceRepository.findAllByStoreId(UUID.fromString(storeId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Service> getById(@PathVariable String id) {
        return serviceRepository.findById(UUID.fromString(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}