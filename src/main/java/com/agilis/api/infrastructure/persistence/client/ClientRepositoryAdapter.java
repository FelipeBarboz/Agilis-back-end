package com.agilis.api.infrastructure.persistence.client;

import com.agilis.api.domain.client.Client;
import com.agilis.api.domain.client.ClientRepository;
import java.util.Optional;
import java.util.UUID;

public class ClientRepositoryAdapter implements ClientRepository {

    private final ClientJpaRepository jpaRepository;

    public ClientRepositoryAdapter(ClientJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Client save(Client client) {
        ClientEntity entity = toEntity(client);
        jpaRepository.save(entity);
        return client;
    }

    @Override
    public Optional<Client> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return jpaRepository.existsByUserId(userId);
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return jpaRepository.existsByCpf(cpf);
    }

    private ClientEntity toEntity(Client client) {
        ClientEntity entity = new ClientEntity();
        entity.setUserId(client.getUserId());
        entity.setCpf(client.getCpf());
        return entity;
    }

    private Client toDomain(ClientEntity entity) {
        return Client.reconstitute(entity.getUserId(), entity.getCpf());
    }
}