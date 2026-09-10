package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SetServiceAreaUseCase {

    private final StoreServiceAreaRepository storeServiceAreaRepository;
    private final ServiceAreaCityRepository serviceAreaCityRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public SetServiceAreaUseCase(
            StoreServiceAreaRepository storeServiceAreaRepository,
            ServiceAreaCityRepository serviceAreaCityRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.storeServiceAreaRepository = storeServiceAreaRepository;
        this.serviceAreaCityRepository  = serviceAreaCityRepository;
        this.storeMembershipRepository  = storeMembershipRepository;
    }

    public Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID storeId      = UUID.fromString(input.storeId());

        var membership = storeMembershipRepository.findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store"));

        if (!membership.getRole().canManageStore()) {
            throw new IllegalStateException("You do not have permission to configure the service area");
        }

        AttendanceType type = AttendanceType.valueOf(input.attendanceType());

        // upsert — se já existe, atualiza; senão cria
        StoreServiceArea area = storeServiceAreaRepository.findByStoreId(storeId)
                .map(existing -> {
                    existing.update(type, input.radiusKm(), input.referenceAddress());
                    return existing;
                })
                .orElse(StoreServiceArea.create(storeId, type, input.radiusKm(), input.referenceAddress()));

        storeServiceAreaRepository.save(area);

        // substitui a lista de cidades por completo
        serviceAreaCityRepository.deleteAllByStoreServiceAreaId(area.getId());

        List<CityOutput> cityOutputs = new ArrayList<>();
        for (CityInput c : input.cities()) {
            ServiceAreaCity city = ServiceAreaCity.create(area.getId(), c.city(), c.state());
            serviceAreaCityRepository.save(city);
            cityOutputs.add(new CityOutput(city.getCity(), city.getState()));
        }

        return new Output(
                area.getId().toString(),
                area.getAttendanceType().name(),
                area.getRadiusKm(),
                area.getReferenceAddress(),
                cityOutputs
        );
    }

    public record CityInput(String city, String state) {}
    public record Input(String requesterId, String storeId, String attendanceType, Integer radiusKm, String referenceAddress, List<CityInput> cities) {}

    public record CityOutput(String city, String state) {}
    public record Output(String id, String attendanceType, Integer radiusKm, String referenceAddress, List<CityOutput> cities) {}
}