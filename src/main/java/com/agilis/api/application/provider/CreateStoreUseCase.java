package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.*;
import com.agilis.api.domain.user.UserRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public class CreateStoreUseCase {

    private final UserRepository userRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final StoreUnitRepository storeUnitRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public CreateStoreUseCase(
            UserRepository userRepository,
            ProviderProfileRepository providerProfileRepository,
            StoreUnitRepository storeUnitRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.userRepository            = userRepository;
        this.providerProfileRepository = providerProfileRepository;
        this.storeUnitRepository       = storeUnitRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public Output execute(Input input) {
        UUID userId = UUID.fromString(input.userId());

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found.");
        }

        CnpjValidator.validate(input.cnpj());

        if (providerProfileRepository.findByCnpj(input.cnpj()).isPresent()
                || storeUnitRepository.findByCnpj(input.cnpj()).isPresent()) {
            throw new IllegalStateException("CNPJ already registered.");
        }

        if (providerProfileRepository.existsBySlug(input.slug())) {
            throw new IllegalStateException("Slug is already in use.");
        }

        ProviderProfile profile = ProviderProfile.create(
                input.cnpj(), input.storeName(), input.slug(), input.description(), input.profileImgUrl()
        );
        providerProfileRepository.save(profile);

        StoreMembership membership = StoreMembership.create(profile.getId(), userId, StoreRole.OWNER, null);
        storeMembershipRepository.save(membership);

        return new Output(profile.getId().toString(), profile.getStoreName(), profile.getSlug(), profile.getCnpj());
    }

    @Schema(name = "CreateStoreInput")
    public record Input(String userId, String cnpj, String storeName, String slug, String description, String profileImgUrl) {}

    @Schema(name = "CreateStoreOutput")
    public record Output(String storeId, String storeName, String slug, String cnpj) {}
}