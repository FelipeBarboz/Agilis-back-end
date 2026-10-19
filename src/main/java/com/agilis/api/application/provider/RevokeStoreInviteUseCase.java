package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.StoreInvite;
import com.agilis.api.domain.provider.StoreInviteRepository;
import com.agilis.api.domain.provider.StoreMembership;
import com.agilis.api.domain.provider.StoreMembershipRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public class RevokeStoreInviteUseCase {

    private final StoreInviteRepository storeInviteRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public RevokeStoreInviteUseCase(
            StoreInviteRepository storeInviteRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.storeInviteRepository      = storeInviteRepository;
        this.storeMembershipRepository  = storeMembershipRepository;
    }

    public void execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID storeId     = UUID.fromString(input.storeId());
        UUID inviteId    = UUID.fromString(input.inviteId());

        StoreMembership requesterMembership = storeMembershipRepository
                .findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store."));

        if (!requesterMembership.getRole().canManageMembers() && !requesterMembership.getRole().canManageStore()) {
            throw new IllegalStateException("No permission to revoke invite links.");
        }

        StoreInvite invite = storeInviteRepository.findById(inviteId)
                .orElseThrow(() -> new IllegalArgumentException("Convite não encontrado."));

        if (!invite.getStoreId().equals(storeId)) {
            throw new IllegalArgumentException("Convite não pertence a esta loja.");
        }

        invite.revoke();
        storeInviteRepository.save(invite);
    }

    @Schema(name = "RevokeStoreInviteInput")
    public record Input(String requesterId, String storeId, String inviteId) {}
}
