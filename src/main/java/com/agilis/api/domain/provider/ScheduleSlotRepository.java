package com.agilis.api.domain.provider;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleSlotRepository {

    ScheduleSlot save(ScheduleSlot slot);
    Optional<ScheduleSlot> findById(UUID id);
    List<ScheduleSlot> findAllByEmployeeScheduleId(UUID employeeScheduleId);
    void deleteById(UUID id);
}