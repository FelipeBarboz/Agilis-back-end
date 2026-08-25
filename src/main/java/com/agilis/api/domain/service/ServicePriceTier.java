package com.agilis.api.domain.service;

import lombok.Getter;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class ServicePriceTier {

    private final UUID id;
    private final UUID serviceId;
    private String name;
    private String description;
    private BigDecimal price;
    private int displayOrder;

    private ServicePriceTier(UUID id, UUID serviceId, String name, String description, BigDecimal price, int displayOrder) {
        this.id           = id;
        this.serviceId    = serviceId;
        this.name         = validateName(name);
        this.description  = description;
        this.price        = validatePrice(price);
        this.displayOrder = displayOrder;
    }

    public static ServicePriceTier create(UUID serviceId, String name, String description, BigDecimal price, int displayOrder) {
        return new ServicePriceTier(UUID.randomUUID(), serviceId, name, description, price, displayOrder);
    }

    public static ServicePriceTier reconstitute(UUID id, UUID serviceId, String name, String description, BigDecimal price, int displayOrder) {
        return new ServicePriceTier(id, serviceId, name, description, price, displayOrder);
    }

    private String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Package name cannot be null");
        }
        return name;
    }

    private BigDecimal validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("The package price cannot be negative");
        }
        return price;
    }
}