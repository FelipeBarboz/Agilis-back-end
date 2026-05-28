package com.agilis.api.infrastructure.persistence.service;

import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServiceRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ServiceRepositoryAdapter implements ServiceRepository {

    private final ServiceJpaRepository jpaRepository;

    public ServiceRepositoryAdapter(ServiceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Service save(Service service) {
        jpaRepository.save(toEntity(service));
        return service;
    }

    @Override
    public Optional<Service> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Service> findAllByStoreId(UUID storeId) {
        return jpaRepository.findAllByStoreId(storeId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private ServiceEntity toEntity(Service service) {
        ServiceEntity entity = new ServiceEntity();
        entity.setId(service.getId());
        entity.setStoreId(service.getStoreId());
        entity.setTitle(service.getTitle());
        entity.setDescription(service.getDescription());
        entity.setPrice(service.getPrice());
        entity.setPriceType(service.getPriceType());
        entity.setDurationMinutes(service.getDurationMinutes());
        entity.setCreatedAt(service.getCreatedAt());
        return entity;
    }

    private Service toDomain(ServiceEntity entity) {
        return Service.reconstitute(
                entity.getId(),
                entity.getStoreId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getPriceType(),
                entity.getDurationMinutes(),
                entity.getCreatedAt()
        );
    }
}