package com.agilis.api.infrastructure.web;

import com.agilis.api.application.review.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final CreateReviewUseCase createReviewUseCase;
    private final GetReviewUseCase getReviewUseCase;

    public ReviewController(
            CreateReviewUseCase createReviewUseCase,
            GetReviewUseCase getReviewUseCase
    ) {
        this.createReviewUseCase = createReviewUseCase;
        this.getReviewUseCase    = getReviewUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateReviewUseCase.Output> create(
            @RequestBody CreateReviewUseCase.Input input
    ) {
        String reviewerId = getCurrentUserId();
        CreateReviewUseCase.Input inputWithReviewer = new CreateReviewUseCase.Input(
                input.bookingId(),
                reviewerId,
                input.reviewedId(),
                input.rating(),
                input.comment()
        );
        return ResponseEntity.ok(createReviewUseCase.execute(inputWithReviewer));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<GetReviewUseCase.Output> getByBooking(
            @PathVariable String bookingId
    ) {
        return ResponseEntity.ok(getReviewUseCase.executeByBooking(bookingId));
    }

    @GetMapping("/reviewed/{reviewedId}")
    public ResponseEntity<List<GetReviewUseCase.Output>> getByReviewed(
            @PathVariable String reviewedId
    ) {
        return ResponseEntity.ok(getReviewUseCase.executeByReviewed(reviewedId));
    }

    private String getCurrentUserId() {
        return (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}