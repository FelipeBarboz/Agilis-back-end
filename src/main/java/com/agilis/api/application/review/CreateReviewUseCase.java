package com.agilis.api.application.review;

import com.agilis.api.domain.booking.Booking;
import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.booking.BookingStatus;
import com.agilis.api.domain.notification.WebhookDispatcher;
import com.agilis.api.domain.notification.WebhookEventType;
import com.agilis.api.domain.review.Review;
import com.agilis.api.domain.review.ReviewRepository;
import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServiceRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class CreateReviewUseCase {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final WebhookDispatcher webhookDispatcher;

    public CreateReviewUseCase(
            ReviewRepository reviewRepository,
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            WebhookDispatcher webhookDispatcher
    ) {
        this.reviewRepository  = reviewRepository;
        this.bookingRepository = bookingRepository;
        this.serviceRepository = serviceRepository;
        this.webhookDispatcher = webhookDispatcher;
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

        Service service = serviceRepository.findById(booking.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Service not found."));

        webhookDispatcher.dispatch(
                service.getStoreId(),
                WebhookEventType.REVIEW_CREATED,
                Map.of(
                        "reviewId", review.getId().toString(),
                        "bookingId", review.getBookingId().toString(),
                        "rating", review.getRating(),
                        "reviewedId", review.getReviewedId().toString()
                )
        );

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

    @Schema(name = "CreateReviewInput")
    public record Input(String bookingId, String reviewerId, String reviewedId, int rating, String comment) {}

    @Schema(name = "CreateReviewOutput")
    public record Output(String reviewId, String bookingId, String reviewerId, String reviewedId, int rating, String comment, LocalDateTime createdAt) {}
}