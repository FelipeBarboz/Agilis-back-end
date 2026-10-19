package com.agilis.api.infrastructure.persistence.provider;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface StoreServiceAreaJpaRepository extends JpaRepository<StoreServiceAreaEntity, UUID> {

    Optional<StoreServiceAreaEntity> findByStoreId(UUID storeId);
}