package com.agilis.api.infrastructure.web;

import com.agilis.api.application.review.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Avaliações", description = "Criação e consulta de avaliações de serviços")
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
    @Operation(summary = "Cria uma avaliação para um serviço concluído")
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
    @Operation(summary = "Consulta a avaliação associada a um agendamento")
    public ResponseEntity<GetReviewUseCase.Output> getByBooking(
            @PathVariable String bookingId
    ) {
        return ResponseEntity.ok(getReviewUseCase.executeByBooking(bookingId));
    }

    @GetMapping("/reviewed/{reviewedId}")
    @Operation(summary = "Lista as avaliações recebidas por um prestador ou loja")
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