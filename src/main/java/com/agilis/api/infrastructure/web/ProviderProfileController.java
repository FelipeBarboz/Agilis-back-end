package com.agilis.api.infrastructure.web;

import com.agilis.api.application.provider.GetMyStoresUseCase;
import com.agilis.api.application.provider.UpdateProviderProfileUseCase;
import com.agilis.api.domain.provider.ProviderProfile;
import com.agilis.api.domain.provider.ProviderProfileRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<ProviderProfile> getById(@PathVariable String storeId) {
        return providerProfileRepository.findById(UUID.fromString(storeId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProviderProfile> getBySlug(@PathVariable String slug) {
        return providerProfileRepository.findBySlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/my")
    public ResponseEntity<List<GetMyStoresUseCase.Output>> getMyStores() {
        String providerId = getCurrentUserId();
        return ResponseEntity.ok(getMyStoresUseCase.execute(providerId));
    }

    @PutMapping("/{storeId}")
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