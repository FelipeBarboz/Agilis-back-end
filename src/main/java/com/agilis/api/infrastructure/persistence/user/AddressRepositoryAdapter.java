package com.agilis.api.infrastructure.persistence.user;

import com.agilis.api.domain.user.Address;
import com.agilis.api.domain.user.AddressRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AddressRepositoryAdapter implements AddressRepository {

    private final AddressJpaRepository jpaRepository;

    public AddressRepositoryAdapter(AddressJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Address save(Address address) {
        jpaRepository.save(toEntity(address));
        return address;
    }

    @Override
    public Optional<Address> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Address> findAllByUserId(UUID userId) {
        return jpaRepository.findAllByUserId(userId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private AddressEntity toEntity(Address address) {
        AddressEntity entity = new AddressEntity();
        entity.setId(address.getId());
        entity.setUserId(address.getUserId());
        entity.setStreet(address.getStreet());
        entity.setNumber(address.getNumber());
        entity.setComplement(address.getComplement());
        entity.setCity(address.getCity());
        entity.setState(address.getState());
        entity.setCep(address.getCep());
        return entity;
    }

    private Address toDomain(AddressEntity entity) {
        return Address.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.getStreet(),
                entity.getNumber(),
                entity.getComplement(),
                entity.getCity(),
                entity.getState(),
                entity.getCep()
        );
    }
}