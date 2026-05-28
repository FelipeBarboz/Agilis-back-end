package com.agilis.api.application.service;

import com.agilis.api.domain.provider.StoreMembershipRepository;
import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServiceRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public class DeleteServiceUseCase {

    private final ServiceRepository serviceRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public DeleteServiceUseCase(
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.serviceRepository         = serviceRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public void execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID serviceId   = UUID.fromString(input.serviceId());

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found."));

        var membership = storeMembershipRepository
                .findByProviderIdAndStoreId(requesterId, service.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store."));

        if (!membership.getRole().canManageServices()) {
            throw new IllegalStateException("No permission to delete services.");
        }

        serviceRepository.deleteById(serviceId);
    }

    @Schema(name = "DeleteServiceInput")
    public record Input(String requesterId, String serviceId) {}
}