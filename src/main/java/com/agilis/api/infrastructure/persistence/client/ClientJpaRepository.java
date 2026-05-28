package com.agilis.api.infrastructure.persistence.client;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ClientJpaRepository extends JpaRepository<ClientEntity, UUID> {

    Optional<ClientEntity> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
    boolean existsByCpf(String cpf);
}