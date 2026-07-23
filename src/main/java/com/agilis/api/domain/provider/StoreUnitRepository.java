package com.agilis.api.domain.provider;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreUnitRepository {

    StoreUnit save(StoreUnit unit);
    Optional<StoreUnit> findById(UUID id);
    List<StoreUnit> findAllByProviderProfileId(UUID providerProfileId);
    void deleteById(UUID id);
}