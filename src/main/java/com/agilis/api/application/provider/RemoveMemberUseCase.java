package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.StoreMembership;
import com.agilis.api.domain.provider.StoreMembershipRepository;
import com.agilis.api.domain.provider.StoreRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public class RemoveMemberUseCase {

    private final StoreMembershipRepository storeMembershipRepository;

    public RemoveMemberUseCase(StoreMembershipRepository storeMembershipRepository) {
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public void execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID storeId     = UUID.fromString(input.storeId());
        UUID memberId    = UUID.fromString(input.memberId());

        StoreMembership requesterMembership = storeMembershipRepository
                .findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store."));

        if (!requesterMembership.getRole().canManageStore()) {
            throw new IllegalStateException("No permission to remove members.");
        }
        StoreMembership targetMembership = storeMembershipRepository
                .findByProviderIdAndStoreId(memberId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found in the store."));

        if (targetMembership.getRole() == StoreRole.OWNER) {
            throw new IllegalStateException("The owner cannot be removed from the store.");
        }

        if (targetMembership.getRole() == StoreRole.ADMIN &&
                requesterMembership.getRole() != StoreRole.OWNER) {
            throw new IllegalStateException("Only the owner can remove admins.");
        }

        storeMembershipRepository.deleteById(targetMembership.getId());
    }

    @Schema(name = "RemoveMemberInput")
    public record Input(String requesterId, String storeId, String memberId) {}
}