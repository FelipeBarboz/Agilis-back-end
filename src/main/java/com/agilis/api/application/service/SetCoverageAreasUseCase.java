package com.agilis.api.application.service;

import com.agilis.api.domain.provider.StoreMembershipRepository;
import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServiceCoverageArea;
import com.agilis.api.domain.service.ServiceCoverageAreaRepository;
import com.agilis.api.domain.service.ServiceRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

public class SetCoverageAreasUseCase {

    private final ServiceCoverageAreaRepository coverageAreaRepository;
    private final ServiceRepository serviceRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public SetCoverageAreasUseCase(
            ServiceCoverageAreaRepository coverageAreaRepository,
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.coverageAreaRepository    = coverageAreaRepository;
        this.serviceRepository         = serviceRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public List<Output> execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID serviceId   = UUID.fromString(input.serviceId());

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        var membership = storeMembershipRepository.findByProviderIdAndStoreId(requesterId, service.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store"));

        if (!membership.getRole().canManageServices()) {
            throw new IllegalStateException("You do not have permission to configure coverage areas");
        }

        coverageAreaRepository.deleteAllByServiceId(serviceId);

        return input.areas().stream()
                .map(a -> {
                    ServiceCoverageArea area = ServiceCoverageArea.create(serviceId, a.city(), a.state());
                    coverageAreaRepository.save(area);
                    return new Output(area.getId().toString(), area.getCity(), area.getState());
                })
                .toList();
    }

    @Schema(name = "AreaInput")
    public record AreaInput(String city, String state) {}

    @Schema(name = "SetCoverageAreasInput")
    public record Input(String requesterId, String serviceId, List<AreaInput> areas) {}

    @Schema(name = "SetCoverageAreasOutput")
    public record Output(String id, String city, String state) {}
}