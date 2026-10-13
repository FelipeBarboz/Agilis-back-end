package com.agilis.api.application.coupon;

import com.agilis.api.domain.coupon.Coupon;
import com.agilis.api.domain.coupon.CouponRedemptionRepository;
import com.agilis.api.domain.coupon.CouponRepository;
import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServiceRepository;

import java.math.BigDecimal;
import java.util.UUID;

public class ValidateCouponUseCase {

    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository couponRedemptionRepository;
    private final ServiceRepository serviceRepository;

    public ValidateCouponUseCase(CouponRepository couponRepository, CouponRedemptionRepository couponRedemptionRepository, ServiceRepository serviceRepository) {
        this.couponRepository            = couponRepository;
        this.couponRedemptionRepository  = couponRedemptionRepository;
        this.serviceRepository           = serviceRepository;
    }

    public Output execute(Input input) {
        UUID clientId  = UUID.fromString(input.clientId());
        UUID serviceId = UUID.fromString(input.serviceId());

        Coupon coupon = couponRepository.findByCode(input.code())
                .orElse(null);

        if (coupon == null) return Output.invalid("Coupon not found");
        if (!coupon.isValidNow()) return Output.invalid("Coupon expired or inactive");

        if (couponRedemptionRepository.existsByCouponIdAndClientId(coupon.getId(), clientId)) {
            return Output.invalid("You have already used this coupon");
        }

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        if (!coupon.meetsMinimumOrder(service.getPrice())) {
            return Output.invalid("Service value is below the minimum required for this coupon");
        }

        BigDecimal discount = coupon.calculateDiscount(service.getPrice());
        BigDecimal finalPrice = service.getPrice().subtract(discount);

        return new Output(true, null, discount, finalPrice);
    }

    public record Input(String clientId, String serviceId, String code) {}

    public record Output(boolean valid, String reason, BigDecimal discountAmount, BigDecimal finalPrice) {
        public static Output invalid(String reason) { return new Output(false, reason, null, null); }
    }
}