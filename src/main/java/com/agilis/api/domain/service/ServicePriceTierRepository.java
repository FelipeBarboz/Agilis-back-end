package com.agilis.api.domain.service;

import java.util.List;
import java.util.UUID;

public interface ServicePriceTierRepository {

    ServicePriceTier save(ServicePriceTier tier);
    List<ServicePriceTier> findAllByServiceIdOrdered(UUID serviceId);
    void deleteAllByServiceId(UUID serviceId);
}