package com.agilis.api.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceImageRepository {

    ServiceImage save(ServiceImage image);
    Optional<ServiceImage> findById(UUID id);
    List<ServiceImage> findAllByServiceId(UUID serviceId);
    int countByServiceId(UUID serviceId);
    void deleteById(UUID id);
}