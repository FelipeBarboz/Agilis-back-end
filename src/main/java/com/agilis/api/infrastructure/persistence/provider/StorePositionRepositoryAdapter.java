package com.agilis.api.infrastructure.persistence.provider;

import com.agilis.api.domain.provider.StorePosition;
import com.agilis.api.domain.provider.StorePositionRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class StorePositionRepositoryAdapter implements StorePositionRepository {

    private final StorePositionJpaRepository jpaRepository;

    public StorePositionRepositoryAdapter(StorePositionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public StorePosition save(StorePosition position) {
        jpaRepository.save(toEntity(position));
        return position;
    }

    @Override
    public Optional<StorePosition> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<StorePosition> findAllByStoreId(UUID storeId) {
        return jpaRepository.findAllByStoreId(storeId).stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private StorePositionEntity toEntity(StorePosition p) {
        StorePositionEntity entity = new StorePositionEntity();
        entity.setId(p.getId());
        entity.setStoreId(p.getStoreId());
        entity.setTitle(p.getTitle());
        entity.setDescription(p.getDescription());
        entity.setCanManageBookings(p.isCanManageBookings());
        entity.setCanAccessChats(p.isCanAccessChats());
        entity.setCanManageStoreSettings(p.isCanManageStoreSettings());
        entity.setCanViewReports(p.isCanViewReports());
        entity.setCreatedAt(p.getCreatedAt());
        return entity;
    }

    private StorePosition toDomain(StorePositionEntity entity) {
        return StorePosition.reconstitute(
                entity.getId(), entity.getStoreId(), entity.getTitle(), entity.getDescription(),
                entity.isCanManageBookings(), entity.isCanAccessChats(),
                entity.isCanManageStoreSettings(), entity.isCanViewReports(), entity.getCreatedAt()
        );
    }
}