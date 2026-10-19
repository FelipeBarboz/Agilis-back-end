package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.StoreMembershipRepository;
import com.agilis.api.domain.provider.StoreUnit;
import com.agilis.api.domain.provider.StoreUnitRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public class DeleteStoreUnitUseCase {

    private final StoreUnitRepository storeUnitRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public DeleteStoreUnitUseCase(
            StoreUnitRepository storeUnitRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.storeUnitRepository       = storeUnitRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public void execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID unitId      = UUID.fromString(input.unitId());

        StoreUnit unit = storeUnitRepository.findById(unitId)
                .orElseThrow(() -> new IllegalArgumentException("Unidade não encontrada"));

        var membership = storeMembershipRepository
                .findByProviderIdAndStoreId(requesterId, unit.getProviderProfileId())
                .orElseThrow(() -> new IllegalArgumentException("Você não é membro desta loja"));

        if (!membership.getRole().canManageStore()) {
            throw new IllegalStateException("Sem permissão para deletar unidades");
        }

        storeUnitRepository.deleteById(unitId);
    }

    @Schema(name = "DeleteStoreUnitOutput")
    public record Input(String requesterId, String unitId) {}
}