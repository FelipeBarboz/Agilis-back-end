package com.agilis.api.infrastructure.web;

import com.agilis.api.application.negotiation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Negociações", description = "Proposta e resposta de negociações de preço")
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
    @Operation(summary = "Inicia uma proposta de negociação de valor para um agendamento")
    public ResponseEntity<CreateNegotiationUseCase.Output> create(
            @RequestBody CreateNegotiationUseCase.Input input
    ) {
        return ResponseEntity.ok(createNegotiationUseCase.execute(input));
    }

    @PatchMapping("/{id}/respond")
    @Operation(summary = "Responde a uma proposta de negociação (aceitar, recusar ou contraproposta)")
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
    @Operation(summary = "Lista as propostas de negociação de um agendamento")
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