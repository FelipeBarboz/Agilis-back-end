package com.agilis.api.infrastructure.persistence.provider;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreMembershipJpaRepository extends JpaRepository<StoreMembershipEntity, UUID> {

    Optional<StoreMembershipEntity> findByProviderIdAndStoreId(UUID providerId, UUID storeId);
    List<StoreMembershipEntity> findAllByStoreId(UUID storeId);
    List<StoreMembershipEntity> findAllByProviderId(UUID providerId);
    boolean existsByProviderIdAndStoreId(UUID providerId, UUID storeId);
}