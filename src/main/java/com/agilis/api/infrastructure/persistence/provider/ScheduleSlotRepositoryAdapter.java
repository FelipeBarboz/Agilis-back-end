package com.agilis.api.infrastructure.persistence.provider;

import com.agilis.api.domain.provider.ScheduleSlot;
import com.agilis.api.domain.provider.ScheduleSlotRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ScheduleSlotRepositoryAdapter implements ScheduleSlotRepository {

    private final ScheduleSlotJpaRepository jpaRepository;

    public ScheduleSlotRepositoryAdapter(ScheduleSlotJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ScheduleSlot save(ScheduleSlot slot) {
        jpaRepository.save(toEntity(slot));
        return slot;
    }

    @Override
    public Optional<ScheduleSlot> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<ScheduleSlot> findAllByEmployeeScheduleId(UUID employeeScheduleId) {
        return jpaRepository.findAllByEmployeeScheduleId(employeeScheduleId).stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private ScheduleSlotEntity toEntity(ScheduleSlot slot) {
        ScheduleSlotEntity entity = new ScheduleSlotEntity();
        entity.setId(slot.getId());
        entity.setEmployeeScheduleId(slot.getEmployeeScheduleId());
        entity.setDayOfWeek(slot.getDayOfWeek());
        entity.setSpecificDate(slot.getSpecificDate());
        entity.setStartTime(slot.getStartTime());
        entity.setEndTime(slot.getEndTime());
        return entity;
    }

    private ScheduleSlot toDomain(ScheduleSlotEntity entity) {
        return ScheduleSlot.reconstitute(
                entity.getId(), entity.getEmployeeScheduleId(), entity.getDayOfWeek(),
                entity.getSpecificDate(), entity.getStartTime(), entity.getEndTime()
        );
    }
}