package com.agilis.api.infrastructure.persistence.service;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ServiceJpaRepository extends JpaRepository<ServiceEntity, UUID> {

    List<ServiceEntity> findAllByStoreId(UUID storeId);

    @Query(
            value = """
        SELECT
            s.id                AS id,
            s.store_id          AS storeId,
            s.unit_id           AS unitId,
            s.title             AS title,
            s.description       AS description,
            s.price             AS price,
            s.price_type        AS priceType,
            s.duration_minutes  AS durationMinutes,
            s.category          AS category,
            EXISTS (SELECT 1 FROM service_price_tiers spt WHERE spt.service_id = s.id) AS hasPriceTiers,
            COALESCE((SELECT AVG(r.rating) FROM reviews r JOIN bookings b ON b.id = r.booking_id WHERE b.service_id = s.id), 0) AS avgRating,
            (SELECT COUNT(r.id) FROM reviews r JOIN bookings b ON b.id = r.booking_id WHERE b.service_id = s.id) AS reviewCount,
            su.city             AS city,
            su.state            AS state,
            st.url              AS thumbnailUrl,
            pp.store_name       AS storeName,
            pp.profile_img_url  AS storeProfileImgUrl
        FROM services s
        LEFT JOIN store_units su ON su.id = s.unit_id
        LEFT JOIN service_thumbnail st ON st.service_id = s.id
        LEFT JOIN provider_profiles pp ON pp.id = s.store_id
        WHERE (
            :city IS NULL
            OR su.city ILIKE CONCAT('%', CAST(:city AS text), '%')
            OR EXISTS (
                SELECT 1 FROM service_coverage_areas sca
                WHERE sca.service_id = s.id
                  AND sca.city ILIKE CONCAT('%', CAST(:city AS text), '%')
                  AND (:state IS NULL OR sca.state = :state)
            )
          )
          AND (:state IS NULL OR su.state = :state OR EXISTS (
                SELECT 1 FROM service_coverage_areas sca WHERE sca.service_id = s.id AND sca.state = :state
              ))
          AND (:minPrice IS NULL OR s.price >= :minPrice)
          AND (:maxPrice IS NULL OR s.price <= :maxPrice)
          AND (:category IS NULL OR s.category = :category)
          AND (:minRating IS NULL OR COALESCE((SELECT AVG(r.rating) FROM reviews r JOIN bookings b ON b.id = r.booking_id WHERE b.service_id = s.id), 0) >= :minRating)
        ORDER BY avgRating DESC
        """,
            countQuery = """
        SELECT COUNT(s.id)
        FROM services s
        LEFT JOIN store_units su ON su.id = s.unit_id
        WHERE (
            :city IS NULL
            OR su.city ILIKE CONCAT('%', CAST(:city AS text), '%')
            OR EXISTS (
                SELECT 1 FROM service_coverage_areas sca
                WHERE sca.service_id = s.id
                  AND sca.city ILIKE CONCAT('%', CAST(:city AS text), '%')
                  AND (:state IS NULL OR sca.state = :state)
            )
          )
          AND (:state IS NULL OR su.state = :state OR EXISTS (
                SELECT 1 FROM service_coverage_areas sca WHERE sca.service_id = s.id AND sca.state = :state
              ))
          AND (:minPrice IS NULL OR s.price >= :minPrice)
          AND (:maxPrice IS NULL OR s.price <= :maxPrice)
          AND (:category IS NULL OR s.category = :category)
          AND (:minRating IS NULL OR COALESCE((SELECT AVG(r.rating) FROM reviews r JOIN bookings b ON b.id = r.booking_id WHERE b.service_id = s.id), 0) >= :minRating)
        """,
            nativeQuery = true
    )
    Page<ServiceSearchProjection> search(
            @Param("city") String city,
            @Param("state") String state,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("category") String category,
            @Param("minRating") Double minRating,
            Pageable pageable
    );
}