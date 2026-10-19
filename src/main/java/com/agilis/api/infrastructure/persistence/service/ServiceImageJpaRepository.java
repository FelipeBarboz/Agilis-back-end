package com.agilis.api.infrastructure.persistence.service;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ServiceImageJpaRepository extends JpaRepository<ServiceImageEntity, UUID> {

    List<ServiceImageEntity> findAllByServiceId(UUID serviceId);
    int countByServiceId(UUID serviceId);
}