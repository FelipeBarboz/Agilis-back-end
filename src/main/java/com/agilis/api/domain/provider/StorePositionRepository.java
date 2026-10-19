package com.agilis.api.domain.provider;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StorePositionRepository {

    StorePosition save(StorePosition position);
    Optional<StorePosition> findById(UUID id);
    List<StorePosition> findAllByStoreId(UUID storeId);
    void deleteById(UUID id);
}