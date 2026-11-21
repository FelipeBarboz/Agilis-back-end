package com.agilis.api.infrastructure.web;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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

@Tag(name = "Unidades da Loja", description = "Gerenciamento das unidades/filiais da loja")
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
    @Operation(summary = "Lista todas as unidades físicas/filiais da loja")
    public ResponseEntity<List<StoreUnit>> list(@PathVariable String storeId) {
        return ResponseEntity.ok(
                storeUnitRepository.findAllByProviderProfileId(UUID.fromString(storeId))
        );
    }

    @PostMapping
    @Operation(summary = "Cadastra uma nova unidade ou filial para a loja")
    public ResponseEntity<CreateStoreUnitUseCase.Output> create(
            @PathVariable String storeId,
            @RequestBody CreateStoreUnitUseCase.Input input
    ) {
        String requesterId = getCurrentUserId();
        CreateStoreUnitUseCase.Input inputWithRequester = new CreateStoreUnitUseCase.Input(
                requesterId, storeId, input.cnpj(), input.name(), input.attendanceType(),
                input.street(), input.number(), input.complement(), input.city(), input.state(), input.cep()
        );
        return ResponseEntity.ok(createStoreUnitUseCase.execute(inputWithRequester));
    }

    @PutMapping("/{unitId}")
    @Operation(summary = "Atualiza os dados de endereço de uma unidade da loja")
    public ResponseEntity<UpdateStoreUnitUseCase.Output> update(
            @PathVariable String unitId,
            @RequestBody UpdateStoreUnitUseCase.Input input
    ) {
        String requesterId = getCurrentUserId();
        UpdateStoreUnitUseCase.Input inputWithRequester = new UpdateStoreUnitUseCase.Input(
                requesterId, unitId, input.name(), input.attendanceType(),
                input.street(), input.number(), input.complement(), input.city(), input.state(), input.cep()
        );
        return ResponseEntity.ok(updateStoreUnitUseCase.execute(inputWithRequester));
    }

    @DeleteMapping("/{unitId}")
    @Operation(summary = "Remove uma unidade ou filial da loja")
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