package com.agilis.api.infrastructure.web;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.agilis.api.application.provider.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cargos da Loja", description = "Gerenciamento de cargos e permissões da equipe")
@RestController
@RequestMapping("/api/v1/stores/{storeId}/positions")
public class StorePositionController {

    private final CreateStorePositionUseCase createStorePositionUseCase;
    private final UpdateStorePositionUseCase updateStorePositionUseCase;
    private final DeleteStorePositionUseCase deleteStorePositionUseCase;
    private final ListStorePositionsUseCase listStorePositionsUseCase;
    private final AssignPositionUseCase assignPositionUseCase;

    public StorePositionController(
            CreateStorePositionUseCase createStorePositionUseCase,
            UpdateStorePositionUseCase updateStorePositionUseCase,
            DeleteStorePositionUseCase deleteStorePositionUseCase,
            ListStorePositionsUseCase listStorePositionsUseCase,
            AssignPositionUseCase assignPositionUseCase
    ) {
        this.createStorePositionUseCase = createStorePositionUseCase;
        this.updateStorePositionUseCase = updateStorePositionUseCase;
        this.deleteStorePositionUseCase = deleteStorePositionUseCase;
        this.listStorePositionsUseCase  = listStorePositionsUseCase;
        this.assignPositionUseCase      = assignPositionUseCase;
    }

    @GetMapping
    @Operation(summary = "Lista todos os cargos cadastrados na loja")
    public ResponseEntity<List<CreateStorePositionUseCase.Output>> list(@PathVariable String storeId) {
        return ResponseEntity.ok(listStorePositionsUseCase.execute(storeId));
    }

    @PostMapping
    @Operation(summary = "Cria um novo cargo com permissões específicas na loja")
    public ResponseEntity<CreateStorePositionUseCase.Output> create(
            @PathVariable String storeId,
            @RequestBody CreateStorePositionUseCase.Input input
    ) {
        String requesterId = getCurrentUserId();
        var inputWithRequester = new CreateStorePositionUseCase.Input(
                requesterId, storeId, input.title(), input.description(),
                input.canManageBookings(), input.canAccessChats(), input.canManageStoreSettings(), input.canViewReports()
        );
        return ResponseEntity.ok(createStorePositionUseCase.execute(inputWithRequester));
    }

    @PutMapping("/{positionId}")
    @Operation(summary = "Atualiza os dados e permissões de um cargo existente")
    public ResponseEntity<CreateStorePositionUseCase.Output> update(
            @PathVariable String positionId,
            @RequestBody UpdateStorePositionUseCase.Input input
    ) {
        String requesterId = getCurrentUserId();
        var inputWithRequester = new UpdateStorePositionUseCase.Input(
                requesterId, positionId, input.title(), input.description(),
                input.canManageBookings(), input.canAccessChats(), input.canManageStoreSettings(), input.canViewReports()
        );
        return ResponseEntity.ok(updateStorePositionUseCase.execute(inputWithRequester));
    }

    @DeleteMapping("/{positionId}")
    @Operation(summary = "Remove um cargo da loja")
    public ResponseEntity<Void> delete(@PathVariable String positionId) {
        String requesterId = getCurrentUserId();
        deleteStorePositionUseCase.execute(requesterId, positionId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/members/{memberId}")
    @Operation(summary = "Atribui um cargo a um membro da equipe da loja")
    public ResponseEntity<Void> assign(
            @PathVariable String storeId,
            @PathVariable String memberId,
            @RequestBody AssignRequest request
    ) {
        String requesterId = getCurrentUserId();
        assignPositionUseCase.execute(new AssignPositionUseCase.Input(requesterId, storeId, memberId, request.positionId()));
        return ResponseEntity.noContent().build();
    }

    public record AssignRequest(String positionId) {}

    private String getCurrentUserId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}