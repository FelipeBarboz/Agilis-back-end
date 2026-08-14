package com.agilis.api.domain.provider;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeScheduleRepository {

    EmployeeSchedule save(EmployeeSchedule schedule);
    Optional<EmployeeSchedule> findById(UUID id);
    Optional<EmployeeSchedule> findByProviderIdAndStoreId(UUID providerId, UUID storeId);
    List<EmployeeSchedule> findAllByStoreId(UUID storeId);
    void deleteById(UUID id);
}