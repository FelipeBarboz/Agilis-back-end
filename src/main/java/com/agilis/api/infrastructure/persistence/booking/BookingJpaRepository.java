package com.agilis.api.infrastructure.persistence.booking;

import com.agilis.api.domain.booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface BookingJpaRepository extends JpaRepository<BookingEntity, UUID> {

    List<BookingEntity> findAllByClientId(UUID clientId);
    List<BookingEntity> findAllByServiceId(UUID serviceId);

    @Query("""
        SELECT COUNT(b) > 0 FROM BookingEntity b
        WHERE b.serviceId = :serviceId
          AND b.scheduledAt = :scheduledAt
          AND b.status NOT IN (:excludedStatuses)
    """)
    boolean existsConflict(
            @Param("serviceId") UUID serviceId,
            @Param("scheduledAt") LocalDateTime scheduledAt,
            @Param("excludedStatuses") List<BookingStatus> excludedStatuses
    );
}