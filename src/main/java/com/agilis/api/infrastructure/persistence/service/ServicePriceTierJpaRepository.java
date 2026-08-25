package com.agilis.api.infrastructure.persistence.service;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ServicePriceTierJpaRepository extends JpaRepository<ServicePriceTierEntity, UUID> {

    List<ServicePriceTierEntity> findAllByServiceIdOrderByDisplayOrderAsc(UUID serviceId);
    void deleteAllByServiceId(UUID serviceId);
}