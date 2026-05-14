package com.agilis.api.infrastructure.persistence.provider;

import com.agilis.api.domain.provider.Provider;
import com.agilis.api.domain.provider.ProviderRepository;
import java.util.Optional;
import java.util.UUID;

public class ProviderRepositoryAdapter implements ProviderRepository {

    private final ProviderJpaRepository jpaRepository;

    public ProviderRepositoryAdapter(ProviderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Provider save(Provider provider) {
        jpaRepository.save(toEntity(provider));
        return provider;
    }

    @Override
    public Optional<Provider> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return jpaRepository.existsByUserId(userId);
    }

    @Override
    public boolean existsByCnpj(String cnpj) {
        return jpaRepository.existsByCnpj(cnpj);
    }

    private ProviderEntity toEntity(Provider provider) {
        ProviderEntity entity = new ProviderEntity();
        entity.setUserId(provider.getUserId());
        entity.setCnpj(provider.getCnpj());
        return entity;
    }

    private Provider toDomain(ProviderEntity entity) {
        return Provider.reconstitute(
                entity.getUserId(),
                entity.getCnpj()
        );
    }
}