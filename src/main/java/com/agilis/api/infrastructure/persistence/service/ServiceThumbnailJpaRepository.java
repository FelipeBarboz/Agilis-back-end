package com.agilis.api.infrastructure.persistence.service;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ServiceThumbnailJpaRepository extends JpaRepository<ServiceThumbnailEntity, UUID> {

    Optional<ServiceThumbnailEntity> findByServiceId(UUID serviceId);
    void deleteByServiceId(UUID serviceId);
}