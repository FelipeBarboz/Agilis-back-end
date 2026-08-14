package com.agilis.api.infrastructure.persistence.provider;

import com.agilis.api.domain.provider.EmployeeSchedule;
import com.agilis.api.domain.provider.EmployeeScheduleRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EmployeeScheduleRepositoryAdapter implements EmployeeScheduleRepository {

    private final EmployeeScheduleJpaRepository jpaRepository;

    public EmployeeScheduleRepositoryAdapter(EmployeeScheduleJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public EmployeeSchedule save(EmployeeSchedule schedule) {
        jpaRepository.save(toEntity(schedule));
        return schedule;
    }

    @Override
    public Optional<EmployeeSchedule> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<EmployeeSchedule> findByProviderIdAndStoreId(UUID providerId, UUID storeId) {
        return jpaRepository.findByProviderIdAndStoreId(providerId, storeId).map(this::toDomain);
    }

    @Override
    public List<EmployeeSchedule> findAllByStoreId(UUID storeId) {
        return jpaRepository.findAllByStoreId(storeId).stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private EmployeeScheduleEntity toEntity(EmployeeSchedule schedule) {
        EmployeeScheduleEntity entity = new EmployeeScheduleEntity();
        entity.setId(schedule.getId());
        entity.setProviderId(schedule.getProviderId());
        entity.setStoreId(schedule.getStoreId());
        entity.setScheduleType(schedule.getScheduleType());
        entity.setMonthlyHoursQuota(schedule.getMonthlyHoursQuota());
        entity.setMonthlyDaysQuota(schedule.getMonthlyDaysQuota());
        entity.setCreatedAt(schedule.getCreatedAt());
        return entity;
    }

    private EmployeeSchedule toDomain(EmployeeScheduleEntity entity) {
        return EmployeeSchedule.reconstitute(
                entity.getId(), entity.getProviderId(), entity.getStoreId(), entity.getScheduleType(),
                entity.getMonthlyHoursQuota(), entity.getMonthlyDaysQuota(), entity.getCreatedAt()
        );
    }
}