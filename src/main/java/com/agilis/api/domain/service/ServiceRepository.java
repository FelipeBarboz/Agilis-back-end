package com.agilis.api.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceRepository {

    Service save(Service service);
    Optional<Service> findById(UUID id);
    List<Service> findAllByStoreId(UUID storeId);
    void deleteById(UUID id);
}