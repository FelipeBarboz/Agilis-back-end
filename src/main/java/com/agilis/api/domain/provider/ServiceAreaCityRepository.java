package com.agilis.api.domain.provider;

import java.util.List;
import java.util.UUID;

public interface ServiceAreaCityRepository {

    ServiceAreaCity save(ServiceAreaCity city);
    List<ServiceAreaCity> findAllByStoreServiceAreaId(UUID storeServiceAreaId);
    void deleteAllByStoreServiceAreaId(UUID storeServiceAreaId);
}