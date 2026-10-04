package com.agilis.api.domain.coupon;

import java.util.UUID;

public interface CouponRedemptionRepository {

    CouponRedemption save(CouponRedemption redemption);
    boolean existsByCouponIdAndClientId(UUID couponId, UUID clientId);
}