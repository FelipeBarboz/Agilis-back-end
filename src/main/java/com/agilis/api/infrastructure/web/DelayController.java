package com.agilis.api.infrastructure.web;

import com.agilis.api.application.booking.DeclareDelayUseCase;
import com.agilis.api.application.booking.GetPendingDelaysUseCase;
import com.agilis.api.application.booking.RespondToDelayUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Atrasos", description = "Registro e consulta de atrasos em agendamentos")
@RestController
public class DelayController {

    private final DeclareDelayUseCase declareDelayUseCase;
    private final GetPendingDelaysUseCase getPendingDelaysUseCase;
    private final RespondToDelayUseCase respondToDelayUseCase;

    public DelayController(
            DeclareDelayUseCase declareDelayUseCase,
            GetPendingDelaysUseCase getPendingDelaysUseCase,
            RespondToDelayUseCase respondToDelayUseCase
    ) {
        this.declareDelayUseCase     = declareDelayUseCase;
        this.getPendingDelaysUseCase = getPendingDelaysUseCase;
        this.respondToDelayUseCase   = respondToDelayUseCase;
    }

    @PostMapping("/api/v1/stores/{storeId}/delays")
    @Operation(summary = "Registra ou declara um atraso em um agendamento")
    public ResponseEntity<DeclareDelayUseCase.Output> declare(
            @PathVariable String storeId,
            @RequestBody DeclareDelayUseCase.Input input
    ) {
        String requesterId = getCurrentUserId();
        DeclareDelayUseCase.Input inputWithRequester = new DeclareDelayUseCase.Input(
                requesterId, storeId, input.employeeId(), input.date(), input.delayMinutes(), input.reason()
        );
        return ResponseEntity.ok(declareDelayUseCase.execute(inputWithRequester));
    }

    @GetMapping("/api/v1/bookings/delays/pending")
    @Operation(summary = "Consulta atrasos pendentes de resposta do cliente")
    public ResponseEntity<List<GetPendingDelaysUseCase.Output>> getPending() {
        String clientId = getCurrentUserId();
        return ResponseEntity.ok(getPendingDelaysUseCase.execute(clientId));
    }

    @PatchMapping("/api/v1/bookings/delays/{delayId}/respond")
    @Operation(summary = "Responde a um atraso (aceitar ou reagendar)")
    public ResponseEntity<Void> respond(
            @PathVariable String delayId,
            @RequestBody RespondToDelayUseCase.Input input
    ) {
        String requesterId = getCurrentUserId();
        respondToDelayUseCase.execute(new RespondToDelayUseCase.Input(requesterId, delayId, input.action()));
        return ResponseEntity.noContent().build();
    }

    private String getCurrentUserId() {
        return (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}