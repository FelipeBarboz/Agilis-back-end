package com.agilis.api.domain.provider;

import lombok.Getter;
import java.time.LocalTime;
import java.util.UUID;

@Getter
public class BusinessHours {

    private final UUID id;
    private final UUID storeId;
    private final int dayOfWeek; // 0=domingo ... 6=sábado
    private LocalTime opensAt;
    private LocalTime closesAt;

    private BusinessHours(UUID id, UUID storeId, int dayOfWeek, LocalTime opensAt, LocalTime closesAt) {
        this.id        = id;
        this.storeId   = storeId;
        this.dayOfWeek = validateDayOfWeek(dayOfWeek);
        this.opensAt   = opensAt;
        this.closesAt  = validateInterval(opensAt, closesAt);
    }

    public static BusinessHours create(UUID storeId, int dayOfWeek, LocalTime opensAt, LocalTime closesAt) {
        return new BusinessHours(UUID.randomUUID(), storeId, dayOfWeek, opensAt, closesAt);
    }

    public static BusinessHours reconstitute(UUID id, UUID storeId, int dayOfWeek, LocalTime opensAt, LocalTime closesAt) {
        return new BusinessHours(id, storeId, dayOfWeek, opensAt, closesAt);
    }

    public void changeHours(LocalTime opensAt, LocalTime closesAt) {
        this.closesAt = validateInterval(opensAt, closesAt);
        this.opensAt  = opensAt;
    }

    private int validateDayOfWeek(int dayOfWeek) {
        if (dayOfWeek < 0 || dayOfWeek > 6) {
            throw new IllegalArgumentException("Day of the week must be between 0 (Sunday) and 6 (Saturday)");
        }
        return dayOfWeek;
    }

    private LocalTime validateInterval(LocalTime opensAt, LocalTime closesAt) {
        if (opensAt == null || closesAt == null || !closesAt.isAfter(opensAt)) {
            throw new IllegalArgumentException("Closing time must be after opening time");
        }
        return closesAt;
    }
}