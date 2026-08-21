package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.StoreMembershipRepository;
import com.agilis.api.domain.provider.StoreUnit;
import com.agilis.api.domain.provider.StoreUnitRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public class CreateStoreUnitUseCase {

    private final StoreUnitRepository storeUnitRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public CreateStoreUnitUseCase(
            StoreUnitRepository storeUnitRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.storeUnitRepository       = storeUnitRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID storeId     = UUID.fromString(input.storeId());

        var membership = storeMembershipRepository
                .findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("Você não é membro desta loja"));

        if (!membership.getRole().canManageStore()) {
            throw new IllegalStateException("Sem permissão para criar unidades");
        }

        StoreUnit unit = StoreUnit.create(
                storeId,
                input.name(),
                input.street(),
                input.number(),
                input.complement(),
                input.city(),
                input.state(),
                input.cep()
        );
        storeUnitRepository.save(unit);

        return toOutput(unit);
    }

    private Output toOutput(StoreUnit unit) {
        return new Output(
                unit.getId().toString(),
                unit.getProviderProfileId().toString(),
                unit.getName(),
                unit.getStreet(),
                unit.getNumber(),
                unit.getComplement(),
                unit.getCity(),
                unit.getState(),
                unit.getCep()
        );
    }

    @Schema(name = "CreateStoreUnitInput")
    public record Input(
            String requesterId, String storeId, String name, String street, String number,
            String complement, String city, String state, String cep
    ) {}

    @Schema(name = "CreateStoreUnitOutput")
    public record Output(
            String unitId, String storeId, String name, String street, String number,
            String complement, String city, String state, String cep
    ) {}
}