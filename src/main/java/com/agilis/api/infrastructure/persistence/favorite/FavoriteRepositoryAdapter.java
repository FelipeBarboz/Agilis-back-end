package com.agilis.api.infrastructure.persistence.favorite;

import com.agilis.api.domain.favorite.Favorite;
import com.agilis.api.domain.favorite.FavoriteRepository;
import java.util.List;
import java.util.UUID;

public class FavoriteRepositoryAdapter implements FavoriteRepository {

    private final FavoriteJpaRepository jpaRepository;

    public FavoriteRepositoryAdapter(FavoriteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Favorite save(Favorite favorite) {
        jpaRepository.save(toEntity(favorite));
        return favorite;
    }

    @Override
    public List<Favorite> findAllByUserId(UUID userId) {
        return jpaRepository.findAllByUserId(userId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByUserIdAndServiceId(UUID userId, UUID serviceId) {
        return jpaRepository.existsByUserIdAndServiceId(userId, serviceId);
    }

    @Override
    public void deleteByUserIdAndServiceId(UUID userId, UUID serviceId) {
        jpaRepository.deleteByUserIdAndServiceId(userId, serviceId);
    }

    private FavoriteEntity toEntity(Favorite favorite) {
        FavoriteEntity entity = new FavoriteEntity();
        entity.setId(favorite.getId());
        entity.setUserId(favorite.getUserId());
        entity.setServiceId(favorite.getServiceId());
        entity.setCreatedAt(favorite.getCreatedAt());
        return entity;
    }

    private Favorite toDomain(FavoriteEntity entity) {
        return Favorite.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.getServiceId(),
                entity.getCreatedAt()
        );
    }
}