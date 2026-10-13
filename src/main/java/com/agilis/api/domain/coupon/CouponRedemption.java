package com.agilis.api.domain.coupon;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class CouponRedemption {

    private final UUID id;
    private final UUID couponId;
    private final UUID clientId;
    private final UUID bookingId;
    private final LocalDateTime redeemedAt;

    private CouponRedemption(UUID id, UUID couponId, UUID clientId, UUID bookingId, LocalDateTime redeemedAt) {
        this.id         = id;
        this.couponId   = couponId;
        this.clientId   = clientId;
        this.bookingId  = bookingId;
        this.redeemedAt = redeemedAt;
    }

    public static CouponRedemption create(UUID couponId, UUID clientId, UUID bookingId) {
        return new CouponRedemption(UUID.randomUUID(), couponId, clientId, bookingId, LocalDateTime.now());
    }

    public static CouponRedemption reconstitute(UUID id, UUID couponId, UUID clientId, UUID bookingId, LocalDateTime redeemedAt) {
        return new CouponRedemption(id, couponId, clientId, bookingId, redeemedAt);
    }
}