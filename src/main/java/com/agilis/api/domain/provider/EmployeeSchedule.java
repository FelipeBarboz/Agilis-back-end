package com.agilis.api.domain.provider;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class EmployeeSchedule {

    private final UUID id;
    private final UUID providerId;
    private final UUID storeId;
    private ScheduleType scheduleType;
    private Integer monthlyHoursQuota; // só usado em STORE_HOURS
    private Integer monthlyDaysQuota;  // só usado em STORE_HOURS
    private final LocalDateTime createdAt;

    private EmployeeSchedule(UUID id, UUID providerId, UUID storeId, ScheduleType scheduleType,
                             Integer monthlyHoursQuota, Integer monthlyDaysQuota, LocalDateTime createdAt) {
        this.id                = id;
        this.providerId         = providerId;
        this.storeId            = storeId;
        this.scheduleType       = validateType(scheduleType);
        this.monthlyHoursQuota  = monthlyHoursQuota;
        this.monthlyDaysQuota   = monthlyDaysQuota;
        this.createdAt          = createdAt;
    }

    public static EmployeeSchedule create(UUID providerId, UUID storeId, ScheduleType type, Integer monthlyHoursQuota, Integer monthlyDaysQuota) {
        return new EmployeeSchedule(UUID.randomUUID(), providerId, storeId, type, monthlyHoursQuota, monthlyDaysQuota, LocalDateTime.now());
    }

    public static EmployeeSchedule reconstitute(UUID id, UUID providerId, UUID storeId, ScheduleType type,
                                                Integer monthlyHoursQuota, Integer monthlyDaysQuota, LocalDateTime createdAt) {
        return new EmployeeSchedule(id, providerId, storeId, type, monthlyHoursQuota, monthlyDaysQuota, createdAt);
    }

    public void changeQuota(Integer monthlyHoursQuota, Integer monthlyDaysQuota) {
        if (scheduleType != ScheduleType.STORE_HOURS) {
            throw new IllegalStateException("Monthly quota only applies to the STORE_HOURS type");
        }
        this.monthlyHoursQuota = monthlyHoursQuota;
        this.monthlyDaysQuota  = monthlyDaysQuota;
    }

    private ScheduleType validateType(ScheduleType type) {
        if (type == null) {
            throw new IllegalArgumentException("Scale type cannot be null");
        }
        return type;
    }
}