package com.agilis.api.infrastructure.persistence.client;

import com.agilis.api.domain.client.PriorityRebooking;
import com.agilis.api.domain.client.PriorityRebookingRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class PriorityRebookingRepositoryAdapter implements PriorityRebookingRepository {

    private final PriorityRebookingJpaRepository jpaRepository;

    public PriorityRebookingRepositoryAdapter(PriorityRebookingJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PriorityRebooking save(PriorityRebooking priority) {
        jpaRepository.save(toEntity(priority));
        return priority;
    }

    @Override
    public Optional<PriorityRebooking> findValidByClientAndService(UUID clientId, UUID serviceId) {
        return jpaRepository.findValid(clientId, serviceId, LocalDateTime.now())
                .stream()
                .findFirst()
                .map(this::toDomain);
    }

    private PriorityRebookingEntity toEntity(PriorityRebooking priority) {
        PriorityRebookingEntity entity = new PriorityRebookingEntity();
        entity.setId(priority.getId());
        entity.setClientId(priority.getClientId());
        entity.setServiceId(priority.getServiceId());
        entity.setReason(priority.getReason());
        entity.setExpiresAt(priority.getExpiresAt());
        entity.setUsed(priority.isUsed());
        entity.setCreatedAt(priority.getCreatedAt());
        return entity;
    }

    private PriorityRebooking toDomain(PriorityRebookingEntity entity) {
        return PriorityRebooking.reconstitute(
                entity.getId(),
                entity.getClientId(),
                entity.getServiceId(),
                entity.getReason(),
                entity.getExpiresAt(),
                entity.isUsed(),
                entity.getCreatedAt()
        );
    }
}