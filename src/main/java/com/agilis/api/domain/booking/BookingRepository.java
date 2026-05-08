package com.agilis.api.domain.booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository {

    Booking save(Booking booking);
    Optional<Booking> findById(UUID id);
    List<Booking> findAllByClientId(UUID clientId);
    List<Booking> findAllByServiceId(UUID serviceId);
    List<Booking> findAllByServiceIdAndDate(UUID serviceId, LocalDate date);
    boolean existsConflict(UUID serviceId, LocalDateTime scheduledAt);
}