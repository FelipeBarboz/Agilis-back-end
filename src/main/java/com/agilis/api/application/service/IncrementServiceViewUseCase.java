package com.agilis.api.application.service;

import com.agilis.api.infrastructure.persistence.service.ServiceJpaRepository;

import java.util.UUID;

public class IncrementServiceViewUseCase {

    private final ServiceJpaRepository serviceJpaRepository;

    public IncrementServiceViewUseCase(ServiceJpaRepository serviceJpaRepository) {
        this.serviceJpaRepository = serviceJpaRepository;
    }

    public void execute(String serviceId) {
        try {
            serviceJpaRepository.incrementViewCount(UUID.fromString(serviceId));
        } catch (Exception e) {
            System.out.println("Failed to increment service view count " + serviceId + ": " + e.getMessage());
        }
    }
}