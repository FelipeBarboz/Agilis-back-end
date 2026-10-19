package com.agilis.api.infrastructure.persistence.provider;

import com.agilis.api.domain.provider.BusinessHours;
import com.agilis.api.domain.provider.BusinessHoursRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BusinessHoursRepositoryAdapter implements BusinessHoursRepository {

    private final BusinessHoursJpaRepository jpaRepository;

    public BusinessHoursRepositoryAdapter(BusinessHoursJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public BusinessHours save(BusinessHours hours) {
        jpaRepository.save(toEntity(hours));
        return hours;
    }

    @Override
    public Optional<BusinessHours> findByStoreIdAndDayOfWeek(UUID storeId, int dayOfWeek) {
        return jpaRepository.findByStoreIdAndDayOfWeek(storeId, dayOfWeek).map(this::toDomain);
    }

    @Override
    public List<BusinessHours> findAllByStoreId(UUID storeId) {
        return jpaRepository.findAllByStoreId(storeId).stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private BusinessHoursEntity toEntity(BusinessHours hours) {
        BusinessHoursEntity entity = new BusinessHoursEntity();
        entity.setId(hours.getId());
        entity.setStoreId(hours.getStoreId());
        entity.setDayOfWeek(hours.getDayOfWeek());
        entity.setOpensAt(hours.getOpensAt());
        entity.setClosesAt(hours.getClosesAt());
        return entity;
    }

    private BusinessHours toDomain(BusinessHoursEntity entity) {
        return BusinessHours.reconstitute(
                entity.getId(), entity.getStoreId(), entity.getDayOfWeek(), entity.getOpensAt(), entity.getClosesAt()
        );
    }
}