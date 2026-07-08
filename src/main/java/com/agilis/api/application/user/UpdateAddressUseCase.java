package com.agilis.api.application.user;

import com.agilis.api.domain.user.Address;
import com.agilis.api.domain.user.AddressRepository;

import java.util.UUID;

public class UpdateAddressUseCase {

    private final AddressRepository addressRepository;

    public UpdateAddressUseCase(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Output execute(Input input) {
        UUID addressId = UUID.fromString(input.addressId());
        UUID requesterId = UUID.fromString(input.requesterId());

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found."));

        if (!address.getUserId().equals(requesterId)) {
            throw new IllegalStateException("No permission to edit this address.");
        }

        address.changeStreet(input.street());
        address.changeNumber(input.number());
        address.changeComplement(input.complement());
        address.changeCity(input.city());
        address.changeState(input.state());
        address.changeCep(input.cep());

        addressRepository.save(address);
        return toOutput(address);
    }

    private Output toOutput(Address address) {
        return new Output(
                address.getId().toString(),
                address.getUserId().toString(),
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getCity(),
                address.getState(),
                address.getCep()
        );
    }

    public record Input(
            String requesterId,
            String addressId,
            String street,
            String number,
            String complement,
            String city,
            String state,
            String cep
    ) {}

    public record Output(
            String addressId,
            String userId,
            String street,
            String number,
            String complement,
            String city,
            String state,
            String cep
    ) {}
}