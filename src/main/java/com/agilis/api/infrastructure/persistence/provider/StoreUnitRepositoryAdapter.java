package com.agilis.api.infrastructure.persistence.provider;

import com.agilis.api.domain.provider.StoreUnit;
import com.agilis.api.domain.provider.StoreUnitRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class StoreUnitRepositoryAdapter implements StoreUnitRepository {

    private final StoreUnitJpaRepository jpaRepository;

    public StoreUnitRepositoryAdapter(StoreUnitJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public StoreUnit save(StoreUnit unit) {
        jpaRepository.save(toEntity(unit));
        return unit;
    }

    @Override
    public Optional<StoreUnit> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<StoreUnit> findAllByProviderProfileId(UUID providerProfileId) {
        return jpaRepository.findAllByProviderProfileId(providerProfileId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private StoreUnitEntity toEntity(StoreUnit unit) {
        StoreUnitEntity entity = new StoreUnitEntity();
        entity.setId(unit.getId());
        entity.setProviderProfileId(unit.getProviderProfileId());
        entity.setName(unit.getName());
        entity.setStreet(unit.getStreet());
        entity.setNumber(unit.getNumber());
        entity.setComplement(unit.getComplement());
        entity.setCity(unit.getCity());
        entity.setState(unit.getState());
        entity.setCep(unit.getCep());
        entity.setCreatedAt(unit.getCreatedAt());
        return entity;
    }

    private StoreUnit toDomain(StoreUnitEntity entity) {
        return StoreUnit.reconstitute(
                entity.getId(),
                entity.getProviderProfileId(),
                entity.getName(),
                entity.getStreet(),
                entity.getNumber(),
                entity.getComplement(),
                entity.getCity(),
                entity.getState(),
                entity.getCep(),
                entity.getCreatedAt()
        );
    }
}