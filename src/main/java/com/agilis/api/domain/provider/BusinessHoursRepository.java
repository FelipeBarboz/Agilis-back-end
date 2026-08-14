package com.agilis.api.domain.provider;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusinessHoursRepository {

    BusinessHours save(BusinessHours hours);
    Optional<BusinessHours> findByStoreIdAndDayOfWeek(UUID storeId, int dayOfWeek);
    List<BusinessHours> findAllByStoreId(UUID storeId);
    void deleteById(UUID id);
}