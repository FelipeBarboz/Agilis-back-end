package com.agilis.api.infrastructure.persistence.provider;

import com.agilis.api.domain.provider.ServiceAreaCity;
import com.agilis.api.domain.provider.ServiceAreaCityRepository;
import java.util.List;
import java.util.UUID;

public class ServiceAreaCityRepositoryAdapter implements ServiceAreaCityRepository {

    private final ServiceAreaCityJpaRepository jpaRepository;

    public ServiceAreaCityRepositoryAdapter(ServiceAreaCityJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ServiceAreaCity save(ServiceAreaCity city) {
        jpaRepository.save(toEntity(city));
        return city;
    }

    @Override
    public List<ServiceAreaCity> findAllByStoreServiceAreaId(UUID storeServiceAreaId) {
        return jpaRepository.findAllByStoreServiceAreaId(storeServiceAreaId).stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteAllByStoreServiceAreaId(UUID storeServiceAreaId) {
        jpaRepository.deleteAllByStoreServiceAreaId(storeServiceAreaId);
    }

    private ServiceAreaCityEntity toEntity(ServiceAreaCity c) {
        ServiceAreaCityEntity entity = new ServiceAreaCityEntity();
        entity.setId(c.getId());
        entity.setStoreServiceAreaId(c.getStoreServiceAreaId());
        entity.setCity(c.getCity());
        entity.setState(c.getState());
        return entity;
    }

    private ServiceAreaCity toDomain(ServiceAreaCityEntity entity) {
        return ServiceAreaCity.reconstitute(entity.getId(), entity.getStoreServiceAreaId(), entity.getCity(), entity.getState());
    }
}