package com.agilis.api.infrastructure.persistence.provider;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ProviderJpaRepository extends JpaRepository<ProviderEntity, UUID> {

    Optional<ProviderEntity> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
    boolean existsByCnpj(String cnpj);
}