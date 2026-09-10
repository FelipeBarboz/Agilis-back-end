package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.ServiceAreaCityRepository;
import com.agilis.api.domain.provider.StoreServiceArea;
import com.agilis.api.domain.provider.StoreServiceAreaRepository;

import java.util.List;
import java.util.UUID;

public class GetServiceAreaUseCase {

    private final StoreServiceAreaRepository storeServiceAreaRepository;
    private final ServiceAreaCityRepository serviceAreaCityRepository;

    public GetServiceAreaUseCase(StoreServiceAreaRepository storeServiceAreaRepository, ServiceAreaCityRepository serviceAreaCityRepository) {
        this.storeServiceAreaRepository = storeServiceAreaRepository;
        this.serviceAreaCityRepository  = serviceAreaCityRepository;
    }

    public SetServiceAreaUseCase.Output execute(String storeId) {
        StoreServiceArea area = storeServiceAreaRepository.findByStoreId(UUID.fromString(storeId))
                .orElseThrow(() -> new IllegalArgumentException("Área de atendimento não configurada para esta loja"));

        List<SetServiceAreaUseCase.CityOutput> cities = serviceAreaCityRepository.findAllByStoreServiceAreaId(area.getId())
                .stream()
                .map(c -> new SetServiceAreaUseCase.CityOutput(c.getCity(), c.getState()))
                .toList();

        return new SetServiceAreaUseCase.Output(
                area.getId().toString(), area.getAttendanceType().name(), area.getRadiusKm(), area.getReferenceAddress(), cities
        );
    }
}