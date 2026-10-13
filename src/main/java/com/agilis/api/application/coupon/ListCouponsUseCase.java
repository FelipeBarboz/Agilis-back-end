package com.agilis.api.application.coupon;

import com.agilis.api.domain.coupon.CouponRepository;
import com.agilis.api.domain.coupon.PlatformAdminRepository;

import java.util.List;
import java.util.UUID;

public class ListCouponsUseCase {

    private final CouponRepository couponRepository;
    private final PlatformAdminRepository platformAdminRepository;

    public ListCouponsUseCase(CouponRepository couponRepository, PlatformAdminRepository platformAdminRepository) {
        this.couponRepository        = couponRepository;
        this.platformAdminRepository = platformAdminRepository;
    }

    public List<CreateCouponUseCase.Output> execute(String requesterId) {
        if (!platformAdminRepository.existsByUserId(UUID.fromString(requesterId))) {
            throw new IllegalStateException("Only platform administrators can list coupons");
        }
        return couponRepository.findAll().stream().map(CreateCouponUseCase::toOutput).toList();
    }
}