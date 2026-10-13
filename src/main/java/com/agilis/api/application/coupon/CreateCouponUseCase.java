package com.agilis.api.application.coupon;

import com.agilis.api.domain.coupon.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class CreateCouponUseCase {

    private final CouponRepository couponRepository;
    private final PlatformAdminRepository platformAdminRepository;

    public CreateCouponUseCase(CouponRepository couponRepository, PlatformAdminRepository platformAdminRepository) {
        this.couponRepository         = couponRepository;
        this.platformAdminRepository  = platformAdminRepository;
    }

    public Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());

        if (!platformAdminRepository.existsByUserId(requesterId)) {
            throw new IllegalStateException("Only platform administrators can create coupons");
        }

        if (couponRepository.findByCode(input.code()).isPresent()) {
            throw new IllegalArgumentException("A coupon with this code already exists");
        }

        Coupon coupon = Coupon.create(
                input.code().toUpperCase(), input.description(), input.discountType(), input.discountValue(),
                input.maxDiscountAmount(), input.minOrderValue(), input.validFrom(), input.validUntil()
        );
        couponRepository.save(coupon);

        return toOutput(coupon);
    }

    static Output toOutput(Coupon c) {
        return new Output(
                c.getId().toString(), c.getCode(), c.getDescription(), c.getDiscountType().name(), c.getDiscountValue(),
                c.getMaxDiscountAmount(), c.getMinOrderValue(), c.getValidFrom(), c.getValidUntil(), c.isActive()
        );
    }

    public record Input(String requesterId, String code, String description, DiscountType discountType,
                        BigDecimal discountValue, BigDecimal maxDiscountAmount, BigDecimal minOrderValue,
                        LocalDateTime validFrom, LocalDateTime validUntil) {}

    public record Output(String id, String code, String description, String discountType, BigDecimal discountValue,
                         BigDecimal maxDiscountAmount, BigDecimal minOrderValue, LocalDateTime validFrom,
                         LocalDateTime validUntil, boolean active) {}
}