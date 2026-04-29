package com.agilis.api.domain.provider;

import java.util.Optional;
import java.util.UUID;

public interface ProviderRepository {

    Provider save(Provider provider);
    Optional<Provider> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
    boolean existsByCnpj(String cnpj);
}