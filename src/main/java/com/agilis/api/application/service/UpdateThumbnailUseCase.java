package com.agilis.api.application.service;

import com.agilis.api.domain.provider.StoreMembershipRepository;
import com.agilis.api.domain.service.ServiceRepository;
import com.agilis.api.domain.service.ServiceThumbnail;
import com.agilis.api.domain.service.ServiceThumbnailRepository;

import java.util.UUID;

public class UpdateThumbnailUseCase {

    private final ServiceThumbnailRepository thumbnailRepository;
    private final ServiceRepository serviceRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public UpdateThumbnailUseCase(
            ServiceThumbnailRepository thumbnailRepository,
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.thumbnailRepository       = thumbnailRepository;
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
                .orElseThrow(() -> new IllegalStateException("Sem permissão para atualizar thumbnail"));

        // se já existe thumbnail atualiza, senão cria
        var thumbnail = thumbnailRepository.findByServiceId(serviceId)
                .map(existing -> {
                    existing.changeUrl(input.url());
                    return existing;
                })
                .orElse(ServiceThumbnail.create(serviceId, input.url()));

        thumbnailRepository.save(thumbnail);

        return new Output(
                thumbnail.getId().toString(),
                thumbnail.getServiceId().toString(),
                thumbnail.getUrl()
        );
    }

    public record Input(String requesterId, String serviceId, String url) {}
    public record Output(String thumbnailId, String serviceId, String url) {}
}