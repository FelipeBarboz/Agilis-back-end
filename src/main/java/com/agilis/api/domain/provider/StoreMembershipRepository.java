package com.agilis.api.domain.provider;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreMembershipRepository {

    StoreMembership save(StoreMembership membership);
    Optional<StoreMembership> findByProviderIdAndStoreId(UUID providerId, UUID storeId);
    List<StoreMembership> findAllByStoreId(UUID storeId);
    List<StoreMembership> findAllByProviderId(UUID providerId);
    boolean existsByProviderIdAndStoreId(UUID providerId, UUID storeId);
    void deleteById(UUID id);
}