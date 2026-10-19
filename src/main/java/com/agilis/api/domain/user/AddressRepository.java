package com.agilis.api.domain.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository {

    Address save(Address address);
    Optional<Address> findById(UUID id);
    List<Address> findAllByUserId(UUID userId);
    void deleteById(UUID id);
}