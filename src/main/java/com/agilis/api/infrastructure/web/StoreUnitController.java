package com.agilis.api.infrastructure.web;

import com.agilis.api.application.provider.CreateStoreUnitUseCase;
import com.agilis.api.application.provider.DeleteStoreUnitUseCase;
import com.agilis.api.application.provider.UpdateStoreUnitUseCase;
import com.agilis.api.domain.provider.StoreUnit;
import com.agilis.api.domain.provider.StoreUnitRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stores/{storeId}/units")
public class StoreUnitController {

    private final CreateStoreUnitUseCase createStoreUnitUseCase;
    private final UpdateStoreUnitUseCase updateStoreUnitUseCase;
    private final DeleteStoreUnitUseCase deleteStoreUnitUseCase;
    private final StoreUnitRepository storeUnitRepository;

    public StoreUnitController(
            CreateStoreUnitUseCase createStoreUnitUseCase,
            UpdateStoreUnitUseCase updateStoreUnitUseCase,
            DeleteStoreUnitUseCase deleteStoreUnitUseCase,
            StoreUnitRepository storeUnitRepository
    ) {
        this.createStoreUnitUseCase = createStoreUnitUseCase;
        this.updateStoreUnitUseCase = updateStoreUnitUseCase;
        this.deleteStoreUnitUseCase = deleteStoreUnitUseCase;
        this.storeUnitRepository    = storeUnitRepository;
    }

    @GetMapping
    public ResponseEntity<List<StoreUnit>> list(@PathVariable String storeId) {
        return ResponseEntity.ok(
                storeUnitRepository.findAllByProviderProfileId(UUID.fromString(storeId))
        );
    }

    @PostMapping
    public ResponseEntity<CreateStoreUnitUseCase.Output> create(
            @PathVariable String storeId,
            @RequestBody CreateStoreUnitUseCase.Input input
    ) {
        String requesterId = getCurrentUserId();
        CreateStoreUnitUseCase.Input inputWithRequester = new CreateStoreUnitUseCase.Input(
                requesterId, storeId, input.name(), input.street(), input.number(),
                input.complement(), input.city(), input.state(), input.cep()
        );
        return ResponseEntity.ok(createStoreUnitUseCase.execute(inputWithRequester));
    }

    @PutMapping("/{unitId}")
    public ResponseEntity<UpdateStoreUnitUseCase.Output> update(
            @PathVariable String unitId,
            @RequestBody UpdateStoreUnitUseCase.Input input
    ) {
        String requesterId = getCurrentUserId();
        UpdateStoreUnitUseCase.Input inputWithRequester = new UpdateStoreUnitUseCase.Input(
                requesterId, unitId, input.name(), input.street(), input.number(),
                input.complement(), input.city(), input.state(), input.cep()
        );
        return ResponseEntity.ok(updateStoreUnitUseCase.execute(inputWithRequester));
    }

    @DeleteMapping("/{unitId}")
    public ResponseEntity<Void> delete(@PathVariable String unitId) {
        String requesterId = getCurrentUserId();
        deleteStoreUnitUseCase.execute(new DeleteStoreUnitUseCase.Input(requesterId, unitId));
        return ResponseEntity.noContent().build();
    }

    private String getCurrentUserId() {
        return (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}