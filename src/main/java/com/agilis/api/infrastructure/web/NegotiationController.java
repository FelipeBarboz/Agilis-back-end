package com.agilis.api.infrastructure.web;

import com.agilis.api.application.negotiation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/negotiations")
public class NegotiationController {

    private final CreateNegotiationUseCase createNegotiationUseCase;
    private final RespondNegotiationUseCase respondNegotiationUseCase;
    private final GetNegotiationUseCase getNegotiationUseCase;

    public NegotiationController(
            CreateNegotiationUseCase createNegotiationUseCase,
            RespondNegotiationUseCase respondNegotiationUseCase,
            GetNegotiationUseCase getNegotiationUseCase
    ) {
        this.createNegotiationUseCase  = createNegotiationUseCase;
        this.respondNegotiationUseCase = respondNegotiationUseCase;
        this.getNegotiationUseCase     = getNegotiationUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateNegotiationUseCase.Output> create(
            @RequestBody CreateNegotiationUseCase.Input input
    ) {
        return ResponseEntity.ok(createNegotiationUseCase.execute(input));
    }

    @PatchMapping("/{id}/respond")
    public ResponseEntity<RespondNegotiationUseCase.Output> respond(
            @PathVariable String id,
            @RequestBody RespondNegotiationUseCase.Input input
    ) {
        String requesterId = getCurrentUserId();
        RespondNegotiationUseCase.Input inputWithRequester = new RespondNegotiationUseCase.Input(
                id,
                requesterId,
                input.action(),
                input.counterAmount()
        );
        return ResponseEntity.ok(respondNegotiationUseCase.execute(inputWithRequester));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<GetNegotiationUseCase.Output>> getByBooking(
            @PathVariable String bookingId
    ) {
        return ResponseEntity.ok(getNegotiationUseCase.executeByBooking(bookingId));
    }

    private String getCurrentUserId() {
        return (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}