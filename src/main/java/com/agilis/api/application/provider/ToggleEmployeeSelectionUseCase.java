package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.ProviderProfile;
import com.agilis.api.domain.provider.ProviderProfileRepository;
import com.agilis.api.domain.provider.StoreMembershipRepository;

import java.util.UUID;

public class ToggleEmployeeSelectionUseCase {

    private final ProviderProfileRepository providerProfileRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public ToggleEmployeeSelectionUseCase(ProviderProfileRepository providerProfileRepository, StoreMembershipRepository storeMembershipRepository) {
        this.providerProfileRepository = providerProfileRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public void execute(String requesterIdStr, String storeIdStr, boolean allow) {
        UUID requesterId = UUID.fromString(requesterIdStr);
        UUID storeId     = UUID.fromString(storeIdStr);

        var membership = storeMembershipRepository.findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store"));

        if (!membership.getRole().canManageStore()) {
            throw new IllegalStateException("You do not have permission to change this setting");
        }

        ProviderProfile profile = providerProfileRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        profile.toggleEmployeeSelection(allow);
        providerProfileRepository.save(profile);
    }
}