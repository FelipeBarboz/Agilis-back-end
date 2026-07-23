package com.agilis.api.application.service;

import com.agilis.api.domain.provider.StoreMembershipRepository;
import com.agilis.api.domain.service.PriceType;
import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServiceRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class CreateServiceUseCase {

    private final ServiceRepository serviceRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public CreateServiceUseCase(
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.serviceRepository         = serviceRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID storeId     = UUID.fromString(input.storeId());
        UUID unitId = input.unitId() != null ? UUID.fromString(input.unitId()) : null;

        var membership = storeMembershipRepository
                .findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store."));

        if (!membership.getRole().canManageServices()) {
            throw new IllegalStateException("No permission to create services.");
        }

        Service service = Service.create(
                storeId,
                unitId,
                input.title(),
                input.description(),
                input.price(),
                input.priceType(),
                input.durationMinutes()
        );

        serviceRepository.save(service);
        return toOutput(service);
    }

    private Output toOutput(Service service) {
        return new Output(
                service.getId().toString(),
                service.getStoreId().toString(),
                service.getTitle(),
                service.getDescription(),
                service.getPrice(),
                service.getPriceType().name(),
                service.getDurationMinutes(),
                service.getCreatedAt()
        );
    }

    @Schema(name = "CreateServiceInput")
    public record Input(String requesterId, String storeId, String unitId, String title, String description, BigDecimal price, PriceType priceType, int durationMinutes) {}

    @Schema(name = "CreateServiceOutput")
    public record Output(String serviceId, String storeId, String title, String description, BigDecimal price, String priceType, int durationMinutes, LocalDateTime createdAt) {}
}