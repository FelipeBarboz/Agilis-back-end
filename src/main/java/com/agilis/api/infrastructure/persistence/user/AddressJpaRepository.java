package com.agilis.api.infrastructure.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AddressJpaRepository extends JpaRepository<AddressEntity, UUID> {

    List<AddressEntity> findAllByUserId(UUID userId);
}