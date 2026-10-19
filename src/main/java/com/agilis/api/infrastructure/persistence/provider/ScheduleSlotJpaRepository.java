package com.agilis.api.infrastructure.persistence.provider;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

public interface ScheduleSlotJpaRepository extends JpaRepository<ScheduleSlotEntity, UUID> {

    List<ScheduleSlotEntity> findAllByEmployeeScheduleId(UUID employeeScheduleId);

    @Modifying
    @Transactional
    void deleteAllByEmployeeScheduleId(UUID employeeScheduleId);
}