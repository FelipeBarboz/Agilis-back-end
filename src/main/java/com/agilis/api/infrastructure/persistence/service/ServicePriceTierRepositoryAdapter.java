package com.agilis.api.infrastructure.persistence.service;

import com.agilis.api.domain.service.ServicePriceTier;
import com.agilis.api.domain.service.ServicePriceTierRepository;
import java.util.List;
import java.util.UUID;

public class ServicePriceTierRepositoryAdapter implements ServicePriceTierRepository {

    private final ServicePriceTierJpaRepository jpaRepository;

    public ServicePriceTierRepositoryAdapter(ServicePriceTierJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ServicePriceTier save(ServicePriceTier tier) {
        jpaRepository.save(toEntity(tier));
        return tier;
    }

    @Override
    public List<ServicePriceTier> findAllByServiceIdOrdered(UUID serviceId) {
        return jpaRepository.findAllByServiceIdOrderByDisplayOrderAsc(serviceId).stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteAllByServiceId(UUID serviceId) {
        jpaRepository.deleteAllByServiceId(serviceId);
    }

    private ServicePriceTierEntity toEntity(ServicePriceTier tier) {
        ServicePriceTierEntity entity = new ServicePriceTierEntity();
        entity.setId(tier.getId());
        entity.setServiceId(tier.getServiceId());
        entity.setName(tier.getName());
        entity.setDescription(tier.getDescription());
        entity.setPrice(tier.getPrice());
        entity.setDisplayOrder(tier.getDisplayOrder());
        return entity;
    }

    private ServicePriceTier toDomain(ServicePriceTierEntity entity) {
        return ServicePriceTier.reconstitute(
                entity.getId(), entity.getServiceId(), entity.getName(), entity.getDescription(), entity.getPrice(), entity.getDisplayOrder()
        );
    }
}