package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.*;
import com.agilis.api.domain.user.UserRepository;

import java.util.UUID;

public class RegisterProviderUseCase {

    private final UserRepository userRepository;
    private final ProviderRepository providerRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public RegisterProviderUseCase(
            UserRepository userRepository,
            ProviderRepository providerRepository,
            ProviderProfileRepository providerProfileRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.userRepository            = userRepository;
        this.providerRepository        = providerRepository;
        this.providerProfileRepository = providerProfileRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public Output execute(Input input) {
        UUID userId = UUID.fromString(input.userId());

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found.");
        }
        if (providerRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("User already has a provider account.");
        }
        if (providerRepository.existsByCnpj(input.cnpj())) {
            throw new IllegalArgumentException("CNPJ already registered.");
        }
        if (providerProfileRepository.existsBySlug(input.slug())) {
            throw new IllegalArgumentException("Slug is already in use.");
        }

        ProviderProfile profile = ProviderProfile.create(
                input.storeName(),
                input.slug(),
                input.description(),
                input.profileImgUrl()
        );
        providerProfileRepository.save(profile);

        Provider provider = Provider.create(userId, input.cnpj(), profile.getId());
        providerRepository.save(provider);

        StoreMembership membership = StoreMembership.create(
                profile.getId(),
                userId,
                StoreRole.OWNER,
                null
        );
        storeMembershipRepository.save(membership);

        return new Output(
                profile.getId().toString(),
                profile.getStoreName(),
                profile.getSlug()
        );
    }

    public record Input(
            String userId,
            String cnpj,
            String storeName,
            String slug,
            String description,
            String profileImgUrl
    ) {}

    public record Output(
            String profileId,
            String storeName,
            String slug
    ) {}
}