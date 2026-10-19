package com.agilis.api.infrastructure.web;

import com.agilis.api.application.provider.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<CreateStorePositionUseCase.Output>> list(@PathVariable String storeId) {
        return ResponseEntity.ok(listStorePositionsUseCase.execute(storeId));
    }

    @PostMapping
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
    public ResponseEntity<Void> delete(@PathVariable String positionId) {
        String requesterId = getCurrentUserId();
        deleteStorePositionUseCase.execute(requesterId, positionId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/members/{memberId}")
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