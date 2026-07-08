package com.agilis.api.application.user;

import com.agilis.api.domain.user.Address;
import com.agilis.api.domain.user.AddressRepository;

import java.util.UUID;

public class AddAddressUseCase {

    private final AddressRepository addressRepository;

    public AddAddressUseCase(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Output execute(Input input) {
        Address address = Address.create(
                UUID.fromString(input.userId()),
                input.street(),
                input.number(),
                input.complement(),
                input.city(),
                input.state(),
                input.cep()
        );
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
            String userId,
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