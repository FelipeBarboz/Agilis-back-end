package com.agilis.api.infrastructure.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TermsAcceptanceJpaRepository extends JpaRepository<TermsAcceptanceEntity, UUID> {

    Optional<TermsAcceptanceEntity> findTopByUserIdOrderByAcceptedAtDesc(UUID userId);
}
