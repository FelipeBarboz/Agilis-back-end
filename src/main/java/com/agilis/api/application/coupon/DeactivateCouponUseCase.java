package com.agilis.api.application.coupon;

import com.agilis.api.domain.coupon.Coupon;
import com.agilis.api.domain.coupon.CouponRepository;
import com.agilis.api.domain.coupon.PlatformAdminRepository;

import java.util.UUID;

public class DeactivateCouponUseCase {

    private final CouponRepository couponRepository;
    private final PlatformAdminRepository platformAdminRepository;

    public DeactivateCouponUseCase(CouponRepository couponRepository, PlatformAdminRepository platformAdminRepository) {
        this.couponRepository        = couponRepository;
        this.platformAdminRepository = platformAdminRepository;
    }

    public void execute(String requesterIdStr, String couponIdStr) {
        UUID requesterId = UUID.fromString(requesterIdStr);

        if (!platformAdminRepository.existsByUserId(requesterId)) {
            throw new IllegalStateException("Only platform administrators can deactivate coupons");
        }

        Coupon coupon = couponRepository.findById(UUID.fromString(couponIdStr))
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));

        coupon.deactivate();
        couponRepository.save(coupon);
    }
}