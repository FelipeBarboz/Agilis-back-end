package com.agilis.api.infrastructure.persistence.review;

import com.agilis.api.domain.review.Review;
import com.agilis.api.domain.review.ReviewRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ReviewRepositoryAdapter implements ReviewRepository {

    private final ReviewJpaRepository jpaRepository;

    public ReviewRepositoryAdapter(ReviewJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Review save(Review review) {
        jpaRepository.save(toEntity(review));
        return review;
    }

    @Override
    public Optional<Review> findByBookingId(UUID bookingId) {
        return jpaRepository.findByBookingId(bookingId).map(this::toDomain);
    }

    @Override
    public List<Review> findAllByReviewedId(UUID reviewedId) {
        return jpaRepository.findAllByReviewedId(reviewedId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByBookingId(UUID bookingId) {
        return jpaRepository.existsByBookingId(bookingId);
    }

    private ReviewEntity toEntity(Review review) {
        ReviewEntity entity = new ReviewEntity();
        entity.setId(review.getId());
        entity.setBookingId(review.getBookingId());
        entity.setReviewerId(review.getReviewerId());
        entity.setReviewedId(review.getReviewedId());
        entity.setRating(review.getRating());
        entity.setComment(review.getComment());
        entity.setCreatedAt(review.getCreatedAt());
        return entity;
    }

    private Review toDomain(ReviewEntity entity) {
        return Review.reconstitute(
                entity.getId(),
                entity.getBookingId(),
                entity.getReviewerId(),
                entity.getReviewedId(),
                entity.getRating(),
                entity.getComment(),
                entity.getCreatedAt()
        );
    }
}