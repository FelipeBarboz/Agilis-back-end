package com.agilis.api.infrastructure.persistence.provider;

import com.agilis.api.domain.provider.StoreInvite;
import com.agilis.api.domain.provider.StoreInviteRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class StoreInviteRepositoryAdapter implements StoreInviteRepository {

    private final StoreInviteJpaRepository jpaRepository;

    public StoreInviteRepositoryAdapter(StoreInviteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public StoreInvite save(StoreInvite invite) {
        jpaRepository.save(toEntity(invite));
        return invite;
    }

    @Override
    public Optional<StoreInvite> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<StoreInvite> findByCode(String code) {
        return jpaRepository.findByCode(code).map(this::toDomain);
    }

    @Override
    public List<StoreInvite> findAllByStoreId(UUID storeId) {
        return jpaRepository.findAllByStoreId(storeId).stream().map(this::toDomain).toList();
    }

    private StoreInviteEntity toEntity(StoreInvite invite) {
        StoreInviteEntity entity = new StoreInviteEntity();
        entity.setId(invite.getId());
        entity.setStoreId(invite.getStoreId());
        entity.setCode(invite.getCode());
        entity.setRole(invite.getRole());
        entity.setCreatedBy(invite.getCreatedBy());
        entity.setExpiresAt(invite.getExpiresAt());
        entity.setMaxUses(invite.getMaxUses());
        entity.setUsedCount(invite.getUsedCount());
        entity.setStatus(invite.getStatus());
        entity.setCreatedAt(invite.getCreatedAt());
        return entity;
    }

    private StoreInvite toDomain(StoreInviteEntity entity) {
        return StoreInvite.reconstitute(
                entity.getId(),
                entity.getStoreId(),
                entity.getCode(),
                entity.getRole(),
                entity.getCreatedBy(),
                entity.getExpiresAt(),
                entity.getMaxUses(),
                entity.getUsedCount(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
