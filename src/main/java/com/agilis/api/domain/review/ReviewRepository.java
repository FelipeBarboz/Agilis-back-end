package com.agilis.api.domain.review;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository {

    Review save(Review review);
    Optional<Review> findByBookingId(UUID bookingId);
    List<Review> findAllByReviewedId(UUID reviewedId);
    boolean existsByBookingId(UUID bookingId);
}