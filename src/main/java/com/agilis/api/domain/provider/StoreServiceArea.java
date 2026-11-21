package com.agilis.api.domain.provider;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class StoreServiceArea {

    private static final int[] VALID_RADIUS_KM = {5, 10, 20, 50};

    private final UUID id;
    private final UUID storeId;
    private AttendanceType attendanceType;
    private Integer radiusKm;
    private final LocalDateTime createdAt;

    private StoreServiceArea(UUID id, UUID storeId, AttendanceType attendanceType, Integer radiusKm, LocalDateTime createdAt) {
        this.id             = id;
        this.storeId        = storeId;
        this.attendanceType = validateType(attendanceType);
        this.radiusKm       = validateRadius(attendanceType, radiusKm);
        this.createdAt      = createdAt;
    }

    public static StoreServiceArea create(UUID storeId, AttendanceType attendanceType, Integer radiusKm) {
        return new StoreServiceArea(UUID.randomUUID(), storeId, attendanceType, radiusKm, LocalDateTime.now());
    }

    public static StoreServiceArea reconstitute(UUID id, UUID storeId, AttendanceType attendanceType, Integer radiusKm, LocalDateTime createdAt) {
        return new StoreServiceArea(id, storeId, attendanceType, radiusKm, createdAt);
    }

    public void update(AttendanceType attendanceType, Integer radiusKm) {
        this.attendanceType = validateType(attendanceType);
        this.radiusKm       = validateRadius(attendanceType, radiusKm);
    }

    private AttendanceType validateType(AttendanceType type) {
        if (type == null) {
            throw new IllegalArgumentException("Service type cannot be null");
        }
        return type;
    }

    // raio só faz sentido quando a loja vai até o cliente (ON_CLIENT ou BOTH)
    private Integer validateRadius(AttendanceType type, Integer radiusKm) {
        if (type == AttendanceType.FIXED_LOCATION) {
            return null;
        }
        if (radiusKm == null) {
            throw new IllegalArgumentException("Coverage radius is required for this service type");
        }
        boolean valid = false;
        for (int v : VALID_RADIUS_KM) {
            if (v == radiusKm) { valid = true; break; }
        }
        if (!valid) {
            throw new IllegalArgumentException("Invalid radius. Accepted values: 5, 10, 20, or 50 km");
        }
        return radiusKm;
    }
}