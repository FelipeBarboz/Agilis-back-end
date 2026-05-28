package com.agilis.api.infrastructure.persistence.provider;

import com.agilis.api.domain.provider.ProviderProfile;
import com.agilis.api.domain.provider.ProviderProfileRepository;
import java.util.Optional;
import java.util.UUID;

public class ProviderProfileRepositoryAdapter implements ProviderProfileRepository {

    private final ProviderProfileJpaRepository jpaRepository;

    public ProviderProfileRepositoryAdapter(ProviderProfileJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ProviderProfile save(ProviderProfile profile) {
        jpaRepository.save(toEntity(profile));
        return profile;
    }

    @Override
    public Optional<ProviderProfile> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<ProviderProfile> findBySlug(String slug) {
        return jpaRepository.findBySlug(slug).map(this::toDomain);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return jpaRepository.existsBySlug(slug);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private ProviderProfileEntity toEntity(ProviderProfile profile) {
        ProviderProfileEntity entity = new ProviderProfileEntity();
        entity.setId(profile.getId());
        entity.setStoreName(profile.getStoreName());
        entity.setSlug(profile.getSlug());
        entity.setDescription(profile.getDescription());
        entity.setProfileImgUrl(profile.getProfileImgUrl());
        entity.setCreatedAt(profile.getCreatedAt());
        return entity;
    }

    private ProviderProfile toDomain(ProviderProfileEntity entity) {
        return ProviderProfile.reconstitute(
                entity.getId(),
                entity.getStoreName(),
                entity.getSlug(),
                entity.getDescription(),
                entity.getProfileImgUrl(),
                entity.getCreatedAt()
        );
    }
}