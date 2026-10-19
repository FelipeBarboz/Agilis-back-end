package com.agilis.api.domain.service;

import java.util.Optional;
import java.util.UUID;

public interface ServiceThumbnailRepository {

    ServiceThumbnail save(ServiceThumbnail thumbnail);
    Optional<ServiceThumbnail> findByServiceId(UUID serviceId);
    void deleteByServiceId(UUID serviceId);
}