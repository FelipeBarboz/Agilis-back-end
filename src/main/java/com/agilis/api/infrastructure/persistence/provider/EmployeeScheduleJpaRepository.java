package com.agilis.api.infrastructure.persistence.provider;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeScheduleJpaRepository extends JpaRepository<EmployeeScheduleEntity, UUID> {

    Optional<EmployeeScheduleEntity> findByProviderIdAndStoreId(UUID providerId, UUID storeId);
    List<EmployeeScheduleEntity> findAllByStoreId(UUID storeId);
}