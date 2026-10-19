package com.agilis.api.domain.provider;

import java.util.Optional;
import java.util.UUID;

public interface StoreServiceAreaRepository {

    StoreServiceArea save(StoreServiceArea area);
    Optional<StoreServiceArea> findByStoreId(UUID storeId);
}