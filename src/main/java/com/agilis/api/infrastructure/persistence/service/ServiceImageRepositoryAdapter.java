package com.agilis.api.infrastructure.persistence.service;

import com.agilis.api.domain.service.ServiceImage;
import com.agilis.api.domain.service.ServiceImageRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ServiceImageRepositoryAdapter implements ServiceImageRepository {

    private final ServiceImageJpaRepository jpaRepository;

    public ServiceImageRepositoryAdapter(ServiceImageJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ServiceImage save(ServiceImage image) {
        jpaRepository.save(toEntity(image));
        return image;
    }

    @Override
    public Optional<ServiceImage> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<ServiceImage> findAllByServiceId(UUID serviceId) {
        return jpaRepository.findAllByServiceId(serviceId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public int countByServiceId(UUID serviceId) {
        return jpaRepository.countByServiceId(serviceId);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private ServiceImageEntity toEntity(ServiceImage image) {
        ServiceImageEntity entity = new ServiceImageEntity();
        entity.setId(image.getId());
        entity.setServiceId(image.getServiceId());
        entity.setUrl(image.getUrl());
        return entity;
    }

    private ServiceImage toDomain(ServiceImageEntity entity) {
        return ServiceImage.reconstitute(entity.getId(), entity.getServiceId(), entity.getUrl());
    }
}