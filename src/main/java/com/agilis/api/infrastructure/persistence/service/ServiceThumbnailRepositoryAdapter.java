package com.agilis.api.infrastructure.persistence.service;

import com.agilis.api.domain.service.ServiceThumbnail;
import com.agilis.api.domain.service.ServiceThumbnailRepository;
import java.util.Optional;
import java.util.UUID;

public class ServiceThumbnailRepositoryAdapter implements ServiceThumbnailRepository {

    private final ServiceThumbnailJpaRepository jpaRepository;

    public ServiceThumbnailRepositoryAdapter(ServiceThumbnailJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ServiceThumbnail save(ServiceThumbnail thumbnail) {
        jpaRepository.save(toEntity(thumbnail));
        return thumbnail;
    }

    @Override
    public Optional<ServiceThumbnail> findByServiceId(UUID serviceId) {
        return jpaRepository.findByServiceId(serviceId).map(this::toDomain);
    }

    @Override
    public void deleteByServiceId(UUID serviceId) {
        jpaRepository.deleteByServiceId(serviceId);
    }

    private ServiceThumbnailEntity toEntity(ServiceThumbnail thumbnail) {
        ServiceThumbnailEntity entity = new ServiceThumbnailEntity();
        entity.setId(thumbnail.getId());
        entity.setServiceId(thumbnail.getServiceId());
        entity.setUrl(thumbnail.getUrl());
        return entity;
    }

    private ServiceThumbnail toDomain(ServiceThumbnailEntity entity) {
        return ServiceThumbnail.reconstitute(entity.getId(), entity.getServiceId(), entity.getUrl());
    }
}