package com.agilis.api.infrastructure.persistence.support;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SupportMessageJpaRepository extends JpaRepository<SupportMessageEntity, UUID> {
}