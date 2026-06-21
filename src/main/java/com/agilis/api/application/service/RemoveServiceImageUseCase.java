package com.agilis.api.application.service;

import com.agilis.api.domain.provider.StoreMembershipRepository;
import com.agilis.api.domain.service.ServiceImageRepository;
import com.agilis.api.domain.service.ServiceRepository;

import java.util.UUID;

public class RemoveServiceImageUseCase {

    private final ServiceImageRepository serviceImageRepository;
    private final ServiceRepository serviceRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public RemoveServiceImageUseCase(
            ServiceImageRepository serviceImageRepository,
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.serviceImageRepository    = serviceImageRepository;
        this.serviceRepository         = serviceRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public void execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID imageId     = UUID.fromString(input.imageId());

        var image = serviceImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Imagem não encontrada"));

        var service = serviceRepository.findById(image.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));

        storeMembershipRepository
                .findByProviderIdAndStoreId(requesterId, service.getStoreId())
                .orElseThrow(() -> new IllegalStateException("Sem permissão para remover imagens"));

        serviceImageRepository.deleteById(imageId);
    }

    public record Input(String requesterId, String imageId) {}
}