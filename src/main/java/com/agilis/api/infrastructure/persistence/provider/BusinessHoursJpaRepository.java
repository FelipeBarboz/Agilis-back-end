package com.agilis.api.infrastructure.persistence.provider;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusinessHoursJpaRepository extends JpaRepository<BusinessHoursEntity, UUID> {

    Optional<BusinessHoursEntity> findByStoreIdAndDayOfWeek(UUID storeId, int dayOfWeek);
    List<BusinessHoursEntity> findAllByStoreId(UUID storeId);
}