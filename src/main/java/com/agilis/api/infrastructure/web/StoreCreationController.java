package com.agilis.api.infrastructure.web;

import com.agilis.api.application.provider.CreateStoreUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stores")
public class StoreCreationController {

    private final CreateStoreUseCase createStoreUseCase;

    public StoreCreationController(CreateStoreUseCase createStoreUseCase) {
        this.createStoreUseCase = createStoreUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateStoreUseCase.Output> create(@RequestBody CreateStoreUseCase.Input input) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(createStoreUseCase.execute(new CreateStoreUseCase.Input(
                userId, input.cnpj(), input.storeName(), input.slug(), input.description(), input.profileImgUrl()
        )));
    }
}