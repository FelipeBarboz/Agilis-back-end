package com.agilis.api.infrastructure.persistence.coupon;

import com.agilis.api.domain.coupon.CouponRedemption;
import com.agilis.api.domain.coupon.CouponRedemptionRepository;
import java.util.UUID;

public class CouponRedemptionRepositoryAdapter implements CouponRedemptionRepository {

    private final CouponRedemptionJpaRepository jpaRepository;

    public CouponRedemptionRepositoryAdapter(CouponRedemptionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CouponRedemption save(CouponRedemption redemption) {
        CouponRedemptionEntity entity = new CouponRedemptionEntity();
        entity.setId(redemption.getId());
        entity.setCouponId(redemption.getCouponId());
        entity.setClientId(redemption.getClientId());
        entity.setBookingId(redemption.getBookingId());
        entity.setRedeemedAt(redemption.getRedeemedAt());
        jpaRepository.save(entity);
        return redemption;
    }

    @Override
    public boolean existsByCouponIdAndClientId(UUID couponId, UUID clientId) {
        return jpaRepository.existsByCouponIdAndClientId(couponId, clientId);
    }
}