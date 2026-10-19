package com.agilis.api.domain.service;

import java.util.List;
import java.util.UUID;

public interface ServiceCoverageAreaRepository {

    ServiceCoverageArea save(ServiceCoverageArea area);
    List<ServiceCoverageArea> findAllByServiceId(UUID serviceId);
    void deleteById(UUID id);
    void deleteAllByServiceId(UUID serviceId);
}