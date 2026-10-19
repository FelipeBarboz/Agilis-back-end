package com.agilis.api.infrastructure.persistence.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PriorityRebookingJpaRepository extends JpaRepository<PriorityRebookingEntity, UUID> {

    @Query("""
        SELECT p FROM PriorityRebookingEntity p
        WHERE p.clientId = :clientId
          AND p.serviceId = :serviceId
          AND p.used = false
          AND p.expiresAt > :now
    """)
    List<PriorityRebookingEntity> findValid(
            @Param("clientId") UUID clientId,
            @Param("serviceId") UUID serviceId,
            @Param("now") LocalDateTime now
    );
}