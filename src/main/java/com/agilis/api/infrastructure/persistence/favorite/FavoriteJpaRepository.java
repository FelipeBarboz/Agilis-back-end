package com.agilis.api.infrastructure.persistence.favorite;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface FavoriteJpaRepository extends JpaRepository<FavoriteEntity, UUID> {

    List<FavoriteEntity> findAllByUserId(UUID userId);
    boolean existsByUserIdAndServiceId(UUID userId, UUID serviceId);
    void deleteByUserIdAndServiceId(UUID userId, UUID serviceId);
}