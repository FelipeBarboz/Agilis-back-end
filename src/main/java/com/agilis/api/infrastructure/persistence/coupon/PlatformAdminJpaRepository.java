package com.agilis.api.infrastructure.persistence.coupon;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PlatformAdminJpaRepository extends JpaRepository<PlatformAdminEntity, UUID> {

    boolean existsByUserId(UUID userId);
}