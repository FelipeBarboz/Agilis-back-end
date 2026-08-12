package com.agilis.api.application.provider;

import com.agilis.api.domain.notification.WebhookDispatcher;
import com.agilis.api.domain.notification.WebhookEventType;
import com.agilis.api.domain.provider.*;
import com.agilis.api.domain.user.UserRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;
import java.util.UUID;

public class InviteMemberUseCase {

    private final StoreMembershipRepository storeMembershipRepository;
    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;
    private final WebhookDispatcher webhookDispatcher;

    public InviteMemberUseCase(
            StoreMembershipRepository storeMembershipRepository,
            ProviderRepository providerRepository,
            UserRepository userRepository,
            WebhookDispatcher webhookDispatcher
    ) {
        this.storeMembershipRepository = storeMembershipRepository;
        this.providerRepository        = providerRepository;
        this.userRepository            = userRepository;
        this.webhookDispatcher = webhookDispatcher;
    }

    public Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID storeId     = UUID.fromString(input.storeId());

        StoreMembership requesterMembership = storeMembershipRepository
                .findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store."));

        if (!requesterMembership.getRole().canManageMembers() &&
                !requesterMembership.getRole().canManageStore()) {
            throw new IllegalStateException("No permission to invite members.");
        }

        var user = userRepository.findByEmail(input.providerEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        providerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("This user does not have a provider account."));

        if (storeMembershipRepository.existsByProviderIdAndStoreId(user.getId(), storeId)) {
            throw new IllegalStateException("This provider is already a member of the store.");
        }

        if (input.role() == StoreRole.OWNER) {
            throw new IllegalStateException("It is not possible to invite someone as an owner.");
        }

        if (input.role() == StoreRole.ADMIN &&
                requesterMembership.getRole() != StoreRole.OWNER) {
            throw new IllegalStateException("Only the owner can invite admins.");
        }

        StoreMembership membership = StoreMembership.create(
                storeId,
                user.getId(),
                input.role(),
                requesterId
        );
        storeMembershipRepository.save(membership);

        webhookDispatcher.dispatch(
                storeId,
                WebhookEventType.STORE_MEMBER_INVITED,
                Map.of(
                        "membershipId", membership.getId().toString(),
                        "providerId", user.getId().toString(),
                        "role", membership.getRole().name()
                )
        );

        return new Output(
                membership.getId().toString(),
                user.getId().toString(),
                user.getName(),
                user.getEmail(),
                membership.getRole().name()
        );
    }

    @Schema(name = "InviteMemberInput")
    public record Input(String requesterId, String storeId, String providerEmail, StoreRole role) {}

    @Schema(name = "InviteMemberOutput")
    public record Output(String membershipId, String providerId, String name, String email, String role) {}
}