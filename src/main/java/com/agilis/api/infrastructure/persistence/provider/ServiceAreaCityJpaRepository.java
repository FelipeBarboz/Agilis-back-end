package com.agilis.api.infrastructure.persistence.provider;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ServiceAreaCityJpaRepository extends JpaRepository<ServiceAreaCityEntity, UUID> {

    List<ServiceAreaCityEntity> findAllByStoreServiceAreaId(UUID storeServiceAreaId);
    void deleteAllByStoreServiceAreaId(UUID storeServiceAreaId);
}