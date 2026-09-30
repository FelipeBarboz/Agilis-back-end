package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.StoreMembershipRepository;
import com.agilis.api.domain.provider.StorePosition;
import com.agilis.api.domain.provider.StorePositionRepository;

import java.util.UUID;

public class UpdateStorePositionUseCase {

    private final StorePositionRepository storePositionRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public UpdateStorePositionUseCase(StorePositionRepository storePositionRepository, StoreMembershipRepository storeMembershipRepository) {
        this.storePositionRepository   = storePositionRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public CreateStorePositionUseCase.Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID positionId  = UUID.fromString(input.positionId());

        StorePosition position = storePositionRepository.findById(positionId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        var membership = storeMembershipRepository.findByProviderIdAndStoreId(requesterId, position.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store"));

        if (!membership.getRole().canManageStore()) {
            throw new IllegalStateException("You do not have permission to manage roles");
        }

        position.update(
                input.title(), input.description(),
                input.canManageBookings(), input.canAccessChats(), input.canManageStoreSettings(), input.canViewReports()
        );
        storePositionRepository.save(position);

        int employeeCount = 0;
        return CreateStorePositionUseCase.toOutput(position, employeeCount);
    }

    public record Input(String requesterId, String positionId, String title, String description,
                        boolean canManageBookings, boolean canAccessChats, boolean canManageStoreSettings, boolean canViewReports) {}
}