package com.agilis.api.infrastructure.persistence.provider;

import com.agilis.api.domain.provider.AttendanceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "store_service_areas")
public class StoreServiceAreaEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "store_id", nullable = false, unique = true)
    private UUID storeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_type", nullable = false)
    private AttendanceType attendanceType;

    @Column(name = "radius_km")
    private Integer radiusKm;

    @Column(name = "reference_address", columnDefinition = "TEXT")
    private String referenceAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}