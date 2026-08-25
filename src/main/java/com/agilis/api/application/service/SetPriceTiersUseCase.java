package com.agilis.api.application.service;

import com.agilis.api.domain.provider.StoreMembershipRepository;
import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServicePriceTier;
import com.agilis.api.domain.service.ServicePriceTierRepository;
import com.agilis.api.domain.service.ServiceRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class SetPriceTiersUseCase {

    private final ServicePriceTierRepository priceTierRepository;
    private final ServiceRepository serviceRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public SetPriceTiersUseCase(
            ServicePriceTierRepository priceTierRepository,
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.priceTierRepository       = priceTierRepository;
        this.serviceRepository         = serviceRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public List<Output> execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID serviceId   = UUID.fromString(input.serviceId());

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));

        var membership = storeMembershipRepository.findByProviderIdAndStoreId(requesterId, service.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("Você não é membro desta loja"));

        if (!membership.getRole().canManageServices()) {
            throw new IllegalStateException("Sem permissão para configurar pacotes de preço");
        }

        priceTierRepository.deleteAllByServiceId(serviceId);

        List<Output> outputs = new java.util.ArrayList<>();
        int order = 0;
        for (TierInput t : input.tiers()) {
            ServicePriceTier tier = ServicePriceTier.create(serviceId, t.name(), t.description(), t.price(), order++);
            priceTierRepository.save(tier);
            outputs.add(new Output(tier.getId().toString(), tier.getName(), tier.getDescription(), tier.getPrice(), tier.getDisplayOrder()));
        }

        if (!outputs.isEmpty()) {
            BigDecimal lowest = outputs.stream().map(Output::price).min(BigDecimal::compareTo).orElse(service.getPrice());
            service.changePrice(lowest, service.getPriceType());
            serviceRepository.save(service);
        }

        return outputs;
    }

    @Schema(name = "TierInput")
    public record TierInput(String name, String description, BigDecimal price) {}

    @Schema(name = "SetPriceTiersInput")
    public record Input(String requesterId, String serviceId, List<TierInput> tiers) {}

    @Schema(name = "SetPriceTiersOutput")
    public record Output(String id, String name, String description, BigDecimal price, int displayOrder) {}
}