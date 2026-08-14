package com.agilis.api.infrastructure.persistence.provider;

import com.agilis.api.domain.provider.ScheduleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "employee_schedules")
public class EmployeeScheduleEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", nullable = false)
    private ScheduleType scheduleType;

    @Column(name = "monthly_hours_quota")
    private Integer monthlyHoursQuota;

    @Column(name = "monthly_days_quota")
    private Integer monthlyDaysQuota;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}