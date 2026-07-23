package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.StoreMembershipRepository;
import com.agilis.api.domain.provider.StoreUnit;
import com.agilis.api.domain.provider.StoreUnitRepository;

import java.util.UUID;

public class UpdateStoreUnitUseCase {

    private final StoreUnitRepository storeUnitRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public UpdateStoreUnitUseCase(
            StoreUnitRepository storeUnitRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.storeUnitRepository       = storeUnitRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID unitId      = UUID.fromString(input.unitId());

        StoreUnit unit = storeUnitRepository.findById(unitId)
                .orElseThrow(() -> new IllegalArgumentException("Unidade não encontrada"));

        var membership = storeMembershipRepository
                .findByProviderIdAndStoreId(requesterId, unit.getProviderProfileId())
                .orElseThrow(() -> new IllegalArgumentException("Você não é membro desta loja"));

        if (!membership.getRole().canManageStore()) {
            throw new IllegalStateException("Sem permissão para editar unidades");
        }

        unit.changeName(input.name());
        unit.changeStreet(input.street());
        unit.changeNumber(input.number());
        unit.changeComplement(input.complement());
        unit.changeCity(input.city());
        unit.changeState(input.state());
        unit.changeCep(input.cep());

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

    public record Input(
            String requesterId, String unitId, String name, String street, String number,
            String complement, String city, String state, String cep
    ) {}

    public record Output(
            String unitId, String storeId, String name, String street, String number,
            String complement, String city, String state, String cep
    ) {}
}