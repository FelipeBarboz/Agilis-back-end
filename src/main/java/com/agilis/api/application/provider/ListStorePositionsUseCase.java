package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.StoreMembershipRepository;
import com.agilis.api.domain.provider.StorePositionRepository;

import java.util.List;
import java.util.UUID;

public class ListStorePositionsUseCase {

    private final StorePositionRepository storePositionRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public ListStorePositionsUseCase(StorePositionRepository storePositionRepository, StoreMembershipRepository storeMembershipRepository) {
        this.storePositionRepository   = storePositionRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public List<CreateStorePositionUseCase.Output> execute(String storeId) {
        UUID storeUuid = UUID.fromString(storeId);

        return storePositionRepository.findAllByStoreId(storeUuid).stream()
                .map(p -> {
                    int employeeCount = storeMembershipRepository.findAllByPositionId(p.getId()).size();
                    return CreateStorePositionUseCase.toOutput(p, employeeCount);
                })
                .toList();
    }
}