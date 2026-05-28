package com.agilis.api.infrastructure.persistence.provider;

import com.agilis.api.domain.provider.StoreMembership;
import com.agilis.api.domain.provider.StoreMembershipRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class StoreMembershipRepositoryAdapter implements StoreMembershipRepository {

    private final StoreMembershipJpaRepository jpaRepository;

    public StoreMembershipRepositoryAdapter(StoreMembershipJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public StoreMembership save(StoreMembership membership) {
        jpaRepository.save(toEntity(membership));
        return membership;
    }

    @Override
    public Optional<StoreMembership> findByProviderIdAndStoreId(UUID providerId, UUID storeId) {
        return jpaRepository.findByProviderIdAndStoreId(providerId, storeId).map(this::toDomain);
    }

    @Override
    public List<StoreMembership> findAllByStoreId(UUID storeId) {
        return jpaRepository.findAllByStoreId(storeId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<StoreMembership> findAllByProviderId(UUID providerId) {
        return jpaRepository.findAllByProviderId(providerId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByProviderIdAndStoreId(UUID providerId, UUID storeId) {
        return jpaRepository.existsByProviderIdAndStoreId(providerId, storeId);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private StoreMembershipEntity toEntity(StoreMembership membership) {
        StoreMembershipEntity entity = new StoreMembershipEntity();
        entity.setId(membership.getId());
        entity.setStoreId(membership.getStoreId());
        entity.setProviderId(membership.getProviderId());
        entity.setRole(membership.getRole());
        entity.setInvitedBy(membership.getInvitedBy());
        entity.setCreatedAt(membership.getCreatedAt());
        return entity;
    }

    private StoreMembership toDomain(StoreMembershipEntity entity) {
        return StoreMembership.reconstitute(
                entity.getId(),
                entity.getStoreId(),
                entity.getProviderId(),
                entity.getRole(),
                entity.getInvitedBy(),
                entity.getCreatedAt()
        );
    }
}