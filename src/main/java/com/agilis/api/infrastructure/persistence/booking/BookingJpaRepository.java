package com.agilis.api.infrastructure.persistence.booking;

import com.agilis.api.domain.booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface BookingJpaRepository extends JpaRepository<BookingEntity, UUID> {

    List<BookingEntity> findAllByClientId(UUID clientId);
    List<BookingEntity> findAllByServiceId(UUID serviceId);
    List<BookingEntity> findAllByEmployeeId(UUID employeeId);

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

    @Query("""
        SELECT b FROM BookingEntity b
        JOIN ServiceEntity s ON s.id = b.serviceId
        WHERE s.storeId = :storeId
          AND b.date = :date
    """)
    List<BookingEntity> findAllByStoreAndDate(@Param("storeId") UUID storeId, @Param("date") LocalDate date);

    @Query("""
        SELECT b FROM BookingEntity b
        WHERE b.employeeId = :employeeId
          AND b.date = :date
    """)
    List<BookingEntity> findAllByEmployeeAndDate(@Param("employeeId") UUID employeeId, @Param("date") LocalDate date);
}