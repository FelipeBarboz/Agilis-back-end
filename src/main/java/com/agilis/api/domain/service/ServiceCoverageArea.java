package com.agilis.api.domain.service;

import lombok.Getter;
import java.util.UUID;

@Getter
public class ServiceCoverageArea {

    private final UUID id;
    private final UUID serviceId;
    private final String city;
    private final String state;

    private ServiceCoverageArea(UUID id, UUID serviceId, String city, String state) {
        this.id        = id;
        this.serviceId = serviceId;
        this.city      = validateField(city, "Cidade");
        this.state     = validateState(state);
    }

    public static ServiceCoverageArea create(UUID serviceId, String city, String state) {
        return new ServiceCoverageArea(UUID.randomUUID(), serviceId, city, state);
    }

    public static ServiceCoverageArea reconstitute(UUID id, UUID serviceId, String city, String state) {
        return new ServiceCoverageArea(id, serviceId, city, state);
    }

    private String validateField(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value;
    }

    private String validateState(String state) {
        if (state == null || state.length() != 2) {
            throw new IllegalArgumentException("State must be 2 characters long. E.g.: SP");
        }
        return state.toUpperCase();
    }
}