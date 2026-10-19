package com.agilis.api.infrastructure.persistence.provider;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreInviteJpaRepository extends JpaRepository<StoreInviteEntity, UUID> {

    Optional<StoreInviteEntity> findByCode(String code);

    List<StoreInviteEntity> findAllByStoreId(UUID storeId);
}
