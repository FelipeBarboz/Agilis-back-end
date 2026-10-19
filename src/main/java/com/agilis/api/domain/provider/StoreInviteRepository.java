package com.agilis.api.domain.provider;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreInviteRepository {

    StoreInvite save(StoreInvite invite);

    Optional<StoreInvite> findById(UUID id);

    Optional<StoreInvite> findByCode(String code);

    List<StoreInvite> findAllByStoreId(UUID storeId);
}
