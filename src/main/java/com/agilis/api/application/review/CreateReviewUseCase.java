package com.agilis.api.application.review;

import com.agilis.api.domain.booking.Booking;
import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.booking.BookingStatus;
import com.agilis.api.domain.review.Review;
import com.agilis.api.domain.review.ReviewRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public class CreateReviewUseCase {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;

    public CreateReviewUseCase(
            ReviewRepository reviewRepository,
            BookingRepository bookingRepository
    ) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
    }

    public Output execute(Input input) {
        UUID bookingId   = UUID.fromString(input.bookingId());
        UUID reviewerId  = UUID.fromString(input.reviewerId());
        UUID reviewedId  = UUID.fromString(input.reviewedId());

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new IllegalStateException("Only completed bookings can be reviewed.");
        }

        if (!booking.getClientId().equals(reviewerId)) {
            throw new IllegalStateException("Only the client can review the service.");
        }

        if (reviewRepository.existsByBookingId(bookingId)) {
            throw new IllegalStateException("This booking has already been reviewed.");
        }

        Review review = Review.create(bookingId, reviewerId, reviewedId, input.rating(), input.comment());
        reviewRepository.save(review);

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

    public record Input(
            String bookingId,
            String reviewerId,
            String reviewedId,
            int rating,
            String comment
    ) {}

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