package com.agilis.api.application.service;

import com.agilis.api.domain.provider.StoreMembershipRepository;
import com.agilis.api.domain.service.ServiceImage;
import com.agilis.api.domain.service.ServiceImageRepository;
import com.agilis.api.domain.service.ServiceRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public class AddServiceImageUseCase {

    private final ServiceImageRepository serviceImageRepository;
    private final ServiceRepository serviceRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public AddServiceImageUseCase(
            ServiceImageRepository serviceImageRepository,
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.serviceImageRepository    = serviceImageRepository;
        this.serviceRepository         = serviceRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID serviceId   = UUID.fromString(input.serviceId());

        var service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));

        storeMembershipRepository
                .findByProviderIdAndStoreId(requesterId, service.getStoreId())
                .orElseThrow(() -> new IllegalStateException("Sem permissão para adicionar imagens"));

        int count = serviceImageRepository.countByServiceId(serviceId);
        if (count >= 5) {
            throw new IllegalStateException("Limite de 5 imagens por serviço atingido");
        }

        ServiceImage image = ServiceImage.create(serviceId, input.url());
        serviceImageRepository.save(image);

        return new Output(
                image.getId().toString(),
                image.getServiceId().toString(),
                image.getUrl()
        );
    }

    @Schema(name = "AddServiceImageInput")
    public record Input(String requesterId, String serviceId, String url) {}

    @Schema(name = "AddServiceImageOutput")
    public record Output(String imageId, String serviceId, String url) {}
}