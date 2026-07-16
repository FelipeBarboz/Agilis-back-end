package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.ProviderProfileRepository;
import com.agilis.api.domain.provider.StoreMembershipRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

public class GetMyStoresUseCase {

    private final StoreMembershipRepository storeMembershipRepository;
    private final ProviderProfileRepository providerProfileRepository;

    public GetMyStoresUseCase(
            StoreMembershipRepository storeMembershipRepository,
            ProviderProfileRepository providerProfileRepository
    ) {
        this.storeMembershipRepository = storeMembershipRepository;
        this.providerProfileRepository = providerProfileRepository;
    }

    public List<Output> execute(String providerId) {
        return storeMembershipRepository
                .findAllByProviderId(UUID.fromString(providerId))
                .stream()
                .map(membership -> {
                    var profile = providerProfileRepository
                            .findById(membership.getStoreId())
                            .orElseThrow(() -> new IllegalArgumentException("Store not found."));

                    return new Output(
                            profile.getId().toString(),
                            profile.getStoreName(),
                            profile.getSlug(),
                            profile.getProfileImgUrl(),
                            membership.getRole().name()
                    );
                })
                .toList();
    }

    @Schema(name = "GetMyStoresOutput")
    public record Output(String storeId, String storeName, String slug, String profileImgUrl, String role) {}
}