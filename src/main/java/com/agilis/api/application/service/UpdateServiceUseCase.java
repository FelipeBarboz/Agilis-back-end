package com.agilis.api.application.service;

import com.agilis.api.domain.provider.StoreMembershipRepository;
import com.agilis.api.domain.service.PriceType;
import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServiceRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class UpdateServiceUseCase {

    private final ServiceRepository serviceRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public UpdateServiceUseCase(
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.serviceRepository         = serviceRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID serviceId   = UUID.fromString(input.serviceId());

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));

        var membership = storeMembershipRepository
                .findByProviderIdAndStoreId(requesterId, service.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("Você não é membro desta loja"));

        if (!membership.getRole().canManageServices()) {
            throw new IllegalStateException("Sem permissão para editar serviços");
        }

        service.changeTitle(input.title());
        service.changeDescription(input.description());
        service.changePrice(input.price(), input.priceType());
        service.changeDuration(input.durationMinutes());

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

    @Schema(name = "UpdateServiceInput")
    public record Input(
            String requesterId,
            String serviceId,
            String title,
            String description,
            BigDecimal price,
            PriceType priceType,
            int durationMinutes
    ) {}

    @Schema(name = "UpdateServiceOutput")
    public record Output(
            String serviceId,
            String storeId,
            String title,
            String description,
            BigDecimal price,
            String priceType,
            int durationMinutes,
            LocalDateTime createdAt
    ) {}
}