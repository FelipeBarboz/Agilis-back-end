package com.agilis.api.infrastructure.persistence.provider;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProviderProfileJpaRepository extends JpaRepository<ProviderProfileEntity, UUID> {

    Optional<ProviderProfileEntity> findBySlug(String slug);
    boolean existsBySlug(String slug);

    @Query(
            value = """
        SELECT
            pp.id                AS id,
            pp.store_name         AS storeName,
            pp.slug                AS slug,
            pp.profile_img_url     AS profileImgUrl,
            pp.description         AS description,
            (
                SELECT s.category FROM services s
                WHERE s.store_id = pp.id
                GROUP BY s.category
                ORDER BY COUNT(*) DESC
                LIMIT 1
            )                       AS primaryCategory,
            COALESCE((
                SELECT AVG(r.rating) FROM reviews r
                JOIN bookings b ON b.id = r.booking_id
                JOIN services s2 ON s2.id = b.service_id
                WHERE s2.store_id = pp.id
            ), 0)                   AS avgRating,
            (
                SELECT COUNT(r2.id) FROM reviews r2
                JOIN bookings b2 ON b2.id = r2.booking_id
                JOIN services s3 ON s3.id = b2.service_id
                WHERE s3.store_id = pp.id
            )                       AS reviewCount,
            su.city                 AS city,
            su.state                AS state,
            EXISTS (
                SELECT 1 FROM store_business_hours bh
                WHERE bh.store_id = pp.id
                  AND bh.day_of_week = :dayOfWeek
                  AND :now BETWEEN bh.opens_at AND bh.closes_at
            )                       AS isOpenNow
        FROM provider_profiles pp
        LEFT JOIN LATERAL (
            SELECT city, state FROM store_units WHERE provider_profile_id = pp.id LIMIT 1
        ) su ON true
        WHERE (:city IS NULL OR su.city ILIKE CONCAT('%', CAST(:city AS text), '%'))
          AND (:state IS NULL OR su.state = :state)
          AND (:category IS NULL OR EXISTS (
                SELECT 1 FROM services s4 WHERE s4.store_id = pp.id AND s4.category = :category
              ))
        """,
            nativeQuery = true
    )
    List<StoreSearchProjection> search(
            @Param("city") String city,
            @Param("state") String state,
            @Param("category") String category,
            @Param("dayOfWeek") int dayOfWeek,
            @Param("now") LocalTime now
    );
}