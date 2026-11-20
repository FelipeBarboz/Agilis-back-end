package com.agilis.api.infrastructure.web;

import com.agilis.api.application.provider.CreateStoreUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Criação de Loja", description = "Cadastro de nova loja/estabelecimento")
@RestController
@RequestMapping("/api/v1/stores")
public class StoreCreationController {

    private final CreateStoreUseCase createStoreUseCase;

    public StoreCreationController(CreateStoreUseCase createStoreUseCase) {
        this.createStoreUseCase = createStoreUseCase;
    }

    @PostMapping
    @Operation(summary = "Cadastra uma nova loja/estabelecimento com CNPJ e slug")
    public ResponseEntity<CreateStoreUseCase.Output> create(@RequestBody CreateStoreUseCase.Input input) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(createStoreUseCase.execute(new CreateStoreUseCase.Input(
                userId, input.cnpj(), input.storeName(), input.slug(), input.description(), input.profileImgUrl()
        )));
    }
}