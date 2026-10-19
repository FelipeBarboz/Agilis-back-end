package com.agilis.api.domain.favorite;

import java.util.List;
import java.util.UUID;

public interface FavoriteRepository {

    Favorite save(Favorite favorite);
    List<Favorite> findAllByUserId(UUID userId);
    boolean existsByUserIdAndServiceId(UUID userId, UUID serviceId);
    void deleteByUserIdAndServiceId(UUID userId, UUID serviceId);
}