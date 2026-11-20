package com.agilis.api.infrastructure.web;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.agilis.api.application.provider.GetMyStoresUseCase;
import com.agilis.api.application.provider.UpdateProviderProfileUseCase;
import com.agilis.api.domain.provider.ProviderProfile;
import com.agilis.api.domain.provider.ProviderProfileRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Perfil da Loja", description = "Consulta e atualização do perfil do prestador / loja")
@RestController
@RequestMapping("/api/v1/stores")
public class ProviderProfileController {

    private final ProviderProfileRepository providerProfileRepository;
    private final GetMyStoresUseCase getMyStoresUseCase;
    private final UpdateProviderProfileUseCase updateProviderProfileUseCase;

    public ProviderProfileController(
            ProviderProfileRepository providerProfileRepository,
            GetMyStoresUseCase getMyStoresUseCase,
            UpdateProviderProfileUseCase updateProviderProfileUseCase
    ) {
        this.providerProfileRepository   = providerProfileRepository;
        this.getMyStoresUseCase          = getMyStoresUseCase;
        this.updateProviderProfileUseCase = updateProviderProfileUseCase;
    }

    @GetMapping("/{storeId}")
    @Operation(summary = "Consulta o perfil da loja pelo ID")
    public ResponseEntity<ProviderProfile> getById(@PathVariable String storeId) {
        return providerProfileRepository.findById(UUID.fromString(storeId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Consulta o perfil público da loja pelo slug")
    public ResponseEntity<ProviderProfile> getBySlug(@PathVariable String slug) {
        return providerProfileRepository.findBySlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/my")
    @Operation(summary = "Lista as lojas associadas ao prestador logado")
    public ResponseEntity<List<GetMyStoresUseCase.Output>> getMyStores() {
        String providerId = getCurrentUserId();
        return ResponseEntity.ok(getMyStoresUseCase.execute(providerId));
    }

    @PutMapping("/{storeId}")
    @Operation(summary = "Atualiza as informações do perfil da loja")
    public ResponseEntity<UpdateProviderProfileUseCase.Output> update(
            @PathVariable String storeId,
            @RequestBody UpdateProviderProfileUseCase.Input input
    ) {
        String requesterId = getCurrentUserId();
        UpdateProviderProfileUseCase.Input inputWithRequester = new UpdateProviderProfileUseCase.Input(
                requesterId,
                storeId,
                input.storeName(),
                input.description(),
                input.slug(),
                input.profileImgUrl()
        );
        return ResponseEntity.ok(updateProviderProfileUseCase.execute(inputWithRequester));
    }

    private String getCurrentUserId() {
        return (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}