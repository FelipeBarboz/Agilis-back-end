package com.agilis.api.domain.review;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Review {

    private final UUID id;
    private final UUID bookingId;
    private final UUID reviewerId;
    private final UUID reviewedId;
    private final int rating;
    private final String comment;
    private final LocalDateTime createdAt;

    private Review(UUID id, UUID bookingId, UUID reviewerId, UUID reviewedId, int rating, String comment, LocalDateTime createdAt) {
        this.id         = id;
        this.bookingId  = bookingId;
        this.reviewerId = reviewerId;
        this.reviewedId = reviewedId;
        this.rating     = validateRating(rating);
        this.comment    = validateComment(comment);
        this.createdAt  = createdAt;
    }

    public static Review create(UUID bookingId, UUID reviewerId, UUID reviewedId, int rating, String comment) {
        return new Review(
                UUID.randomUUID(),
                bookingId,
                reviewerId,
                reviewedId,
                rating,
                comment,
                LocalDateTime.now()
        );
    }

    public static Review reconstitute(UUID id, UUID bookingId, UUID reviewerId, UUID reviewedId, int rating, String comment, LocalDateTime createdAt) {
        return new Review(id, bookingId, reviewerId, reviewedId, rating, comment, createdAt);
    }

    private int validateRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        return rating;
    }

    private String validateComment(String comment) {
        if (comment != null && comment.length() > 500) {
            throw new IllegalArgumentException("The comment cannot be longer than 500 characters.");
        }
        return comment;
    }
}