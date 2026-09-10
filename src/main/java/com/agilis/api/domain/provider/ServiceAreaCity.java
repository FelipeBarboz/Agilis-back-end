package com.agilis.api.domain.provider;

import lombok.Getter;
import java.util.UUID;

@Getter
public class ServiceAreaCity {

    private final UUID id;
    private final UUID storeServiceAreaId;
    private final String city;
    private final String state;

    private ServiceAreaCity(UUID id, UUID storeServiceAreaId, String city, String state) {
        this.id                 = id;
        this.storeServiceAreaId = storeServiceAreaId;
        this.city               = validateField(city, "City");
        this.state              = validateState(state);
    }

    public static ServiceAreaCity create(UUID storeServiceAreaId, String city, String state) {
        return new ServiceAreaCity(UUID.randomUUID(), storeServiceAreaId, city, state);
    }

    public static ServiceAreaCity reconstitute(UUID id, UUID storeServiceAreaId, String city, String state) {
        return new ServiceAreaCity(id, storeServiceAreaId, city, state);
    }

    private String validateField(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
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