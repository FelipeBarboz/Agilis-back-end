package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.StoreMembershipRepository;
import com.agilis.api.domain.provider.StorePosition;
import com.agilis.api.domain.provider.StorePositionRepository;

import java.util.UUID;

public class CreateStorePositionUseCase {

    private final StorePositionRepository storePositionRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public CreateStorePositionUseCase(StorePositionRepository storePositionRepository, StoreMembershipRepository storeMembershipRepository) {
        this.storePositionRepository   = storePositionRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID storeId     = UUID.fromString(input.storeId());

        var membership = storeMembershipRepository.findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store"));

        if (!membership.getRole().canManageStore()) {
            throw new IllegalStateException("You do not have permission to edit roles");
        }

        StorePosition position = StorePosition.create(
                storeId, input.title(), input.description(),
                input.canManageBookings(), input.canAccessChats(), input.canManageStoreSettings(), input.canViewReports()
        );
        storePositionRepository.save(position);

        return toOutput(position, 0);
    }

    static Output toOutput(StorePosition p, int employeeCount) {
        return new Output(
                p.getId().toString(), p.getTitle(), p.getDescription(), employeeCount,
                p.isCanManageBookings(), p.isCanAccessChats(), p.isCanManageStoreSettings(), p.isCanViewReports()
        );
    }

    public record Input(String requesterId, String storeId, String title, String description,
                        boolean canManageBookings, boolean canAccessChats, boolean canManageStoreSettings, boolean canViewReports) {}

    public record Output(String id, String title, String description, int employeeCount,
                         boolean canManageBookings, boolean canAccessChats, boolean canManageStoreSettings, boolean canViewReports) {}
}