package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.StoreMembershipRepository;
import com.agilis.api.domain.provider.StorePosition;
import com.agilis.api.domain.provider.StorePositionRepository;

import java.util.UUID;

public class DeleteStorePositionUseCase {

    private final StorePositionRepository storePositionRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public DeleteStorePositionUseCase(StorePositionRepository storePositionRepository, StoreMembershipRepository storeMembershipRepository) {
        this.storePositionRepository   = storePositionRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public void execute(String requesterIdStr, String positionIdStr) {
        UUID requesterId = UUID.fromString(requesterIdStr);
        UUID positionId  = UUID.fromString(positionIdStr);

        StorePosition position = storePositionRepository.findById(positionId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        var membership = storeMembershipRepository.findByProviderIdAndStoreId(requesterId, position.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store"));

        if (!membership.getRole().canManageStore()) {
            throw new IllegalStateException("You do not have permission to delete roles");
        }

        storePositionRepository.deleteById(positionId);
    }
}