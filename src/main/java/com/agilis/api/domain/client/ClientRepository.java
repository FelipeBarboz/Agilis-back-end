package com.agilis.api.domain.client;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository {

    Client save(Client client);
    Optional<Client> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
    boolean existsByCpf(String cpf);
}