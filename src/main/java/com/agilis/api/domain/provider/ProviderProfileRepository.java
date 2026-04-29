package com.agilis.api.domain.provider;

import java.util.Optional;
import java.util.UUID;

public interface ProviderProfileRepository {

    ProviderProfile save(ProviderProfile providerProfile);
    Optional<ProviderProfile> findById(UUID id);
    Optional<ProviderProfile> findBySlug(String slug);
    boolean existsBySlug(String slug);
    void deleteById(UUID id);
}