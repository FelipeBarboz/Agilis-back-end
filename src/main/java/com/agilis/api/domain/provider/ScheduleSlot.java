package com.agilis.api.domain.provider;

import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
public class ScheduleSlot {

    private final UUID id;
    private final UUID employeeScheduleId;
    private final Integer dayOfWeek;   // usado em FIXED/STORE_HOURS — recorrente
    private final LocalDate specificDate; // usado em FLEXIBLE — pontual
    private LocalTime startTime;
    private LocalTime endTime;

    private ScheduleSlot(UUID id, UUID employeeScheduleId, Integer dayOfWeek, LocalDate specificDate, LocalTime startTime, LocalTime endTime) {
        if ((dayOfWeek == null) == (specificDate == null)) {
            throw new IllegalArgumentException("Provide exactly one of dayOfWeek (recurring) or specificDate (one-time)");
        }
        this.id                 = id;
        this.employeeScheduleId = employeeScheduleId;
        this.dayOfWeek          = dayOfWeek;
        this.specificDate       = specificDate;
        this.startTime          = startTime;
        this.endTime            = validateInterval(startTime, endTime);
    }

    public static ScheduleSlot createRecurring(UUID employeeScheduleId, int dayOfWeek, LocalTime startTime, LocalTime endTime) {
        return new ScheduleSlot(UUID.randomUUID(), employeeScheduleId, dayOfWeek, null, startTime, endTime);
    }

    public static ScheduleSlot createSpecific(UUID employeeScheduleId, LocalDate specificDate, LocalTime startTime, LocalTime endTime) {
        return new ScheduleSlot(UUID.randomUUID(), employeeScheduleId, null, specificDate, startTime, endTime);
    }

    public static ScheduleSlot reconstitute(UUID id, UUID employeeScheduleId, Integer dayOfWeek, LocalDate specificDate, LocalTime startTime, LocalTime endTime) {
        return new ScheduleSlot(id, employeeScheduleId, dayOfWeek, specificDate, startTime, endTime);
    }

    public void changeTimes(LocalTime startTime, LocalTime endTime) {
        this.endTime   = validateInterval(startTime, endTime);
        this.startTime = startTime;
    }

    private LocalTime validateInterval(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null || !endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        return endTime;
    }
}