package com.agilis.api.infrastructure.persistence.review;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewJpaRepository extends JpaRepository<ReviewEntity, UUID> {

    Optional<ReviewEntity> findByBookingId(UUID bookingId);
    List<ReviewEntity> findAllByReviewedId(UUID reviewedId);
    boolean existsByBookingId(UUID bookingId);
}