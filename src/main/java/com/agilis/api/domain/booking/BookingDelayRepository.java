package com.agilis.api.domain.booking;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingDelayRepository {

    BookingDelay save(BookingDelay delay);
    Optional<BookingDelay> findById(UUID id);
    List<BookingDelay> findAllPendingByClientId(UUID clientId);
}