package com.agilis.api.infrastructure.persistence.service;

import com.agilis.api.domain.service.ServiceCoverageArea;
import com.agilis.api.domain.service.ServiceCoverageAreaRepository;
import java.util.List;
import java.util.UUID;

public class ServiceCoverageAreaRepositoryAdapter implements ServiceCoverageAreaRepository {

    private final ServiceCoverageAreaJpaRepository jpaRepository;

    public ServiceCoverageAreaRepositoryAdapter(ServiceCoverageAreaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ServiceCoverageArea save(ServiceCoverageArea area) {
        jpaRepository.save(toEntity(area));
        return area;
    }

    @Override
    public List<ServiceCoverageArea> findAllByServiceId(UUID serviceId) {
        return jpaRepository.findAllByServiceId(serviceId).stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByServiceId(UUID serviceId) {
        jpaRepository.deleteAllByServiceId(serviceId);
    }

    private ServiceCoverageAreaEntity toEntity(ServiceCoverageArea area) {
        ServiceCoverageAreaEntity entity = new ServiceCoverageAreaEntity();
        entity.setId(area.getId());
        entity.setServiceId(area.getServiceId());
        entity.setCity(area.getCity());
        entity.setState(area.getState());
        return entity;
    }

    private ServiceCoverageArea toDomain(ServiceCoverageAreaEntity entity) {
        return ServiceCoverageArea.reconstitute(entity.getId(), entity.getServiceId(), entity.getCity(), entity.getState());
    }
}