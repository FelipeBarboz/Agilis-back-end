package com.agilis.api.application.review;

import com.agilis.api.domain.review.Review;
import com.agilis.api.domain.review.ReviewRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class GetReviewUseCase {

    private final ReviewRepository reviewRepository;

    public GetReviewUseCase(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Output executeByBooking(String bookingId) {
        return reviewRepository.findByBookingId(UUID.fromString(bookingId))
                .map(this::toOutput)
                .orElseThrow(() -> new IllegalArgumentException("Review not found."));
    }

    public List<Output> executeByReviewed(String reviewedId) {
        return reviewRepository.findAllByReviewedId(UUID.fromString(reviewedId))
                .stream()
                .map(this::toOutput)
                .toList();
    }

    private Output toOutput(Review review) {
        return new Output(
                review.getId().toString(),
                review.getBookingId().toString(),
                review.getReviewerId().toString(),
                review.getReviewedId().toString(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }

    @Schema(name = "GetReviewOutput")
    public record Output(
            String reviewId,
            String bookingId,
            String reviewerId,
            String reviewedId,
            int rating,
            String comment,
            LocalDateTime createdAt
    ) {}
}