package com.agilis.api.infrastructure.persistence.service;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ServiceCoverageAreaJpaRepository extends JpaRepository<ServiceCoverageAreaEntity, UUID> {

    List<ServiceCoverageAreaEntity> findAllByServiceId(UUID serviceId);
    void deleteAllByServiceId(UUID serviceId);
}