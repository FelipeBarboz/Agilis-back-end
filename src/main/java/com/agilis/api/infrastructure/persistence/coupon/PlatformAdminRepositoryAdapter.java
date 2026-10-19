package com.agilis.api.infrastructure.persistence.coupon;

import com.agilis.api.domain.coupon.PlatformAdminRepository;
import java.util.UUID;

public class PlatformAdminRepositoryAdapter implements PlatformAdminRepository {

    private final PlatformAdminJpaRepository jpaRepository;

    public PlatformAdminRepositoryAdapter(PlatformAdminJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return jpaRepository.existsByUserId(userId);
    }
}