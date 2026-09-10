package com.agilis.api.infrastructure.persistence.provider;

import com.agilis.api.domain.provider.StoreServiceArea;
import com.agilis.api.domain.provider.StoreServiceAreaRepository;
import java.util.Optional;
import java.util.UUID;

public class StoreServiceAreaRepositoryAdapter implements StoreServiceAreaRepository {

    private final StoreServiceAreaJpaRepository jpaRepository;

    public StoreServiceAreaRepositoryAdapter(StoreServiceAreaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public StoreServiceArea save(StoreServiceArea area) {
        jpaRepository.save(toEntity(area));
        return area;
    }

    @Override
    public Optional<StoreServiceArea> findByStoreId(UUID storeId) {
        return jpaRepository.findByStoreId(storeId).map(this::toDomain);
    }

    private StoreServiceAreaEntity toEntity(StoreServiceArea area) {
        StoreServiceAreaEntity entity = new StoreServiceAreaEntity();
        entity.setId(area.getId());
        entity.setStoreId(area.getStoreId());
        entity.setAttendanceType(area.getAttendanceType());
        entity.setRadiusKm(area.getRadiusKm());
        entity.setReferenceAddress(area.getReferenceAddress());
        entity.setCreatedAt(area.getCreatedAt());
        return entity;
    }

    private StoreServiceArea toDomain(StoreServiceAreaEntity entity) {
        return StoreServiceArea.reconstitute(
                entity.getId(), entity.getStoreId(), entity.getAttendanceType(),
                entity.getRadiusKm(), entity.getReferenceAddress(), entity.getCreatedAt()
        );
    }
}