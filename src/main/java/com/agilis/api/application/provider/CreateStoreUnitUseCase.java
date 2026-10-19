package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.*;

import java.util.UUID;

public class CreateStoreUnitUseCase {

    private final StoreUnitRepository storeUnitRepository;
    private final StoreMembershipRepository storeMembershipRepository;
    private final ProviderProfileRepository providerProfileRepository;

    public CreateStoreUnitUseCase(
            StoreUnitRepository storeUnitRepository,
            StoreMembershipRepository storeMembershipRepository,
            ProviderProfileRepository providerProfileRepository
    ) {
        this.storeUnitRepository       = storeUnitRepository;
        this.storeMembershipRepository = storeMembershipRepository;
        this.providerProfileRepository = providerProfileRepository;
    }

    public Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID storeId     = UUID.fromString(input.storeId());

        var membership = storeMembershipRepository
                .findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store."));

        if (!membership.getRole().canManageStore()) {
            throw new IllegalStateException("No permission to create units.");
        }

        CnpjValidator.validate(input.cnpj());

        var storeWithCnpj = providerProfileRepository.findByCnpj(input.cnpj());
        if (storeWithCnpj.isPresent() && !storeWithCnpj.get().getId().equals(storeId)) {
            throw new IllegalStateException("CNPJ already registered by another store.");
        }

        if (storeUnitRepository.findByCnpj(input.cnpj()).isPresent()) {
            throw new IllegalStateException("CNPJ already registered by another unit.");
        }

        StoreUnit unit = StoreUnit.create(
                storeId, input.cnpj(), input.name(), input.street(), input.number(),
                input.complement(), input.city(), input.state(), input.cep()
        );
        storeUnitRepository.save(unit);

        return new Output(
                unit.getId().toString(), unit.getProviderProfileId().toString(), unit.getCnpj(), unit.getName(),
                unit.getStreet(), unit.getNumber(), unit.getComplement(), unit.getCity(), unit.getState(), unit.getCep()
        );
    }

    public record Input(
            String requesterId, String storeId, String cnpj, String name, String street, String number,
            String complement, String city, String state, String cep
    ) {}

    public record Output(
            String unitId, String storeId, String cnpj, String name, String street, String number,
            String complement, String city, String state, String cep
    ) {}
}