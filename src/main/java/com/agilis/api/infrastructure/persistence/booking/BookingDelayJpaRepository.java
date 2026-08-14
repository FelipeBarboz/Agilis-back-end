package com.agilis.api.infrastructure.persistence.booking;

import com.agilis.api.domain.booking.DelayResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface BookingDelayJpaRepository extends JpaRepository<BookingDelayEntity, UUID> {

    @Query("""
        SELECT d FROM BookingDelayEntity d
        JOIN BookingEntity b ON b.id = d.bookingId
        WHERE b.clientId = :clientId
          AND d.response = :response
    """)
    List<BookingDelayEntity> findAllPendingByClientId(
            @Param("clientId") UUID clientId,
            @Param("response") DelayResponse response
    );
}