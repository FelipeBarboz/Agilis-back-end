package com.agilis.api.domain.coupon;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CouponRepository {

    Coupon save(Coupon coupon);
    Optional<Coupon> findById(UUID id);
    Optional<Coupon> findByCode(String code);
    List<Coupon> findAll();
}