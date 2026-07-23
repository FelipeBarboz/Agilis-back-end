package com.agilis.api.infrastructure.persistence.provider;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface StoreUnitJpaRepository extends JpaRepository<StoreUnitEntity, UUID> {

    List<StoreUnitEntity> findAllByProviderProfileId(UUID providerProfileId);
}