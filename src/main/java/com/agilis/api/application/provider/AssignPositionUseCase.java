package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.StoreMembership;
import com.agilis.api.domain.provider.StoreMembershipRepository;
import com.agilis.api.domain.provider.StorePosition;
import com.agilis.api.domain.provider.StorePositionRepository;

import java.util.UUID;

public class AssignPositionUseCase {

    private final StoreMembershipRepository storeMembershipRepository;
    private final StorePositionRepository storePositionRepository;

    public AssignPositionUseCase(StoreMembershipRepository storeMembershipRepository, StorePositionRepository storePositionRepository) {
        this.storeMembershipRepository = storeMembershipRepository;
        this.storePositionRepository   = storePositionRepository;
    }

    public void execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID storeId     = UUID.fromString(input.storeId());
        UUID memberId    = UUID.fromString(input.memberId());
        UUID positionId  = input.positionId() != null ? UUID.fromString(input.positionId()) : null;

        var requesterMembership = storeMembershipRepository.findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store"));

        if (!requesterMembership.getRole().canManageStore()) {
            throw new IllegalStateException("You do not have permission to assign roles");
        }

        StoreMembership target = storeMembershipRepository.findByProviderIdAndStoreId(memberId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found in this store"));

        if (positionId != null) {
            StorePosition position = storePositionRepository.findById(positionId)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found"));
            if (!position.getStoreId().equals(storeId)) {
                throw new IllegalArgumentException("Role does not belong to this store");
            }
        }

        target.assignPosition(positionId);
        storeMembershipRepository.save(target);
    }

    public record Input(String requesterId, String storeId, String memberId, String positionId) {}
}