package com.agilis.api.domain.coupon;

import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Coupon {

    private final UUID id;
    private String code;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal maxDiscountAmount; // nullable, só pra PERCENTAGE
    private BigDecimal minOrderValue;     // nullable
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private boolean active;
    private final LocalDateTime createdAt;

    private Coupon(UUID id, String code, String description, DiscountType discountType, BigDecimal discountValue,
                   BigDecimal maxDiscountAmount, BigDecimal minOrderValue, LocalDateTime validFrom,
                   LocalDateTime validUntil, boolean active, LocalDateTime createdAt) {
        this.id                 = id;
        this.code                = validateCode(code);
        this.description         = description;
        this.discountType        = validateType(discountType);
        this.discountValue       = validateValue(discountType, discountValue);
        this.maxDiscountAmount   = maxDiscountAmount;
        this.minOrderValue       = minOrderValue;
        this.validUntil          = validUntil;
        this.validFrom           = validateInterval(validFrom, validUntil);
        this.active              = active;
        this.createdAt           = createdAt;
    }

    public static Coupon create(String code, String description, DiscountType discountType, BigDecimal discountValue,
                                BigDecimal maxDiscountAmount, BigDecimal minOrderValue,
                                LocalDateTime validFrom, LocalDateTime validUntil) {
        return new Coupon(UUID.randomUUID(), code, description, discountType, discountValue,
                maxDiscountAmount, minOrderValue, validFrom, validUntil, true, LocalDateTime.now());
    }

    public static Coupon reconstitute(UUID id, String code, String description, DiscountType discountType, BigDecimal discountValue,
                                      BigDecimal maxDiscountAmount, BigDecimal minOrderValue,
                                      LocalDateTime validFrom, LocalDateTime validUntil, boolean active, LocalDateTime createdAt) {
        return new Coupon(id, code, description, discountType, discountValue,
                maxDiscountAmount, minOrderValue, validFrom, validUntil, active, createdAt);
    }

    public void deactivate() { this.active = false; }

    public boolean isValidNow() {
        LocalDateTime now = LocalDateTime.now();
        return active && !now.isBefore(validFrom) && !now.isAfter(validUntil);
    }

    public boolean meetsMinimumOrder(BigDecimal orderValue) {
        return minOrderValue == null || orderValue.compareTo(minOrderValue) >= 0;
    }

    public BigDecimal calculateDiscount(BigDecimal orderValue) {
        BigDecimal raw = switch (discountType) {
            case FIXED -> discountValue;
            case PERCENTAGE -> orderValue.multiply(discountValue).divide(new BigDecimal("100"));
        };

        if (discountType == DiscountType.PERCENTAGE && maxDiscountAmount != null && raw.compareTo(maxDiscountAmount) > 0) {
            raw = maxDiscountAmount;
        }

        return raw.compareTo(orderValue) > 0 ? orderValue : raw;
    }

    private String validateCode(String code) {
        if (code == null || !code.matches("^[A-Z0-9\\-]{3,50}$")) {
            throw new IllegalArgumentException("Invalid coupon code. Use uppercase letters, numbers, and hyphens (3 to 50 characters)");
        }
        return code;
    }

    private DiscountType validateType(DiscountType type) {
        if (type == null) throw new IllegalArgumentException("Discount type cannot be null");
        return type;
    }

    private BigDecimal validateValue(DiscountType type, BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Discount value must be greater than zero");
        }
        if (type == DiscountType.PERCENTAGE && value.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Percentage discount cannot exceed 100%");
        }
        return value;
    }

    private LocalDateTime validateInterval(LocalDateTime from, LocalDateTime until) {
        if (from == null || until == null || !until.isAfter(from)) {
            throw new IllegalArgumentException("End date must be after the start date");
        }
        return from;
    }
}