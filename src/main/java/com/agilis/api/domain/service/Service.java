package com.agilis.api.domain.service;

import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Service {

    private final UUID id;
    private final UUID storeId;
    private UUID unitId;
    private String title;
    private String description;
    private BigDecimal price;
    private PriceType priceType;
    private int durationMinutes;
    private final LocalDateTime createdAt;

    public Service(UUID id, UUID storeId, UUID unitId, String title, String description, BigDecimal price, PriceType priceType, int durationMinutes, LocalDateTime createdAt) {
        this.id = id;
        this.storeId = storeId;
        this.unitId = unitId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.priceType = priceType;
        this.durationMinutes = durationMinutes;
        this.createdAt = createdAt;
    }

    public static Service create(UUID storeId, UUID unitId, String title, String description, BigDecimal price, PriceType priceType, int durationMinutes) {
        return new Service(
                UUID.randomUUID(), storeId, unitId, title, description, price, priceType, durationMinutes, LocalDateTime.now()
        );
    }

    public static Service reconstitute(UUID id, UUID storeId, UUID unitId, String title, String description,
                                       BigDecimal price, PriceType priceType, int durationMinutes, LocalDateTime createdAt) {
        return new Service(id, storeId, unitId, title, description, price, priceType, durationMinutes, createdAt);
    }

    public void changeUnit(UUID unitId) {
        this.unitId = unitId;
    }

    public void changeTitle(String title) {
        this.title = validateTitle(title);
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changePrice(BigDecimal price, PriceType priceType) {
        this.price     = validatePrice(price);
        this.priceType = validatePriceType(priceType);
    }

    public void changeDuration(int durationMinutes) {
        this.durationMinutes = validateDuration(durationMinutes);
    }

    private String validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("The service title cannot be empty.");
        }
        return title;
    }

    private BigDecimal validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("The price cannot be negative.");
        }
        return price;
    }

    private PriceType validatePriceType(PriceType priceType) {
        if (priceType == null) {
            throw new IllegalArgumentException("The price type cannot be null.");
        }
        return priceType;
    }

    private int validateDuration(int durationMinutes) {
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("The duration must be greater than zero.");
        }
        return durationMinutes;
    }
}