package com.agilis.api.infrastructure.persistence.coupon;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CouponRedemptionJpaRepository extends JpaRepository<CouponRedemptionEntity, UUID> {

    boolean existsByCouponIdAndClientId(UUID couponId, UUID clientId);
}