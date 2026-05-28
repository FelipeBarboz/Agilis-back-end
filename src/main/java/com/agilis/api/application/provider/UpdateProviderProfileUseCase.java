package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.ProviderProfileRepository;
import com.agilis.api.domain.provider.StoreMembershipRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public class UpdateProviderProfileUseCase {

    private final ProviderProfileRepository providerProfileRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public UpdateProviderProfileUseCase(
            ProviderProfileRepository providerProfileRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.providerProfileRepository = providerProfileRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID storeId     = UUID.fromString(input.storeId());

        var membership = storeMembershipRepository
                .findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store."));

        if (!membership.getRole().canManageStore()) {
            throw new IllegalStateException("No permission to edit the store profile.");
        }

        var profile = providerProfileRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found."));

        profile.changeStoreName(input.storeName());
        profile.changeDescription(input.description());
        profile.changeProfileImg(input.profileImgUrl());

        providerProfileRepository.save(profile);

        return new Output(
                profile.getId().toString(),
                profile.getStoreName(),
                profile.getSlug(),
                profile.getDescription(),
                profile.getProfileImgUrl()
        );
    }

    @Schema(name = "UpdateProviderInput")
    public record Input(
            String requesterId,
            String storeId,
            String storeName,
            String description,
            String profileImgUrl
    ) {}

    @Schema(name = "UpdateProviderOutput")
    public record Output(
            String storeId,
            String storeName,
            String slug,
            String description,
            String profileImgUrl
    ) {}
}