package com.agilis.api.infrastructure.persistence.provider;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ProviderProfileJpaRepository extends JpaRepository<ProviderProfileEntity, UUID> {

    Optional<ProviderProfileEntity> findBySlug(String slug);
    boolean existsBySlug(String slug);
}