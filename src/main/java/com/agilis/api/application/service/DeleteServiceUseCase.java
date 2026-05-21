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
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));

        var membership = storeMembershipRepository
                .findByProviderIdAndStoreId(requesterId, service.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("Você não é membro desta loja"));

        if (!membership.getRole().canManageServices()) {
            throw new IllegalStateException("Sem permissão para deletar serviços");
        }

        serviceRepository.deleteById(serviceId);
    }

    @Schema(name = "DeleteServiceInput")
    public record Input(String requesterId, String serviceId) {}
}