package com.agilis.api.infrastructure.web;

import com.agilis.api.application.provider.GetMyStoresUseCase;
import com.agilis.api.application.provider.GetProviderProfileBySlugUseCase;
import com.agilis.api.application.provider.GetProviderProfileUseCase;
import com.agilis.api.application.provider.UpdateProviderProfileUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stores")
@Tag(name = "Store Profile", description = "Store profile management")
public class ProviderProfileController {

    private final GetProviderProfileUseCase getProviderProfileUseCase;
    private final GetProviderProfileBySlugUseCase getBySlugUseCase;
    private final UpdateProviderProfileUseCase updateProfileUseCase;
    private final GetMyStoresUseCase getMyStoresUseCase;

    public ProviderProfileController(GetProviderProfileUseCase getProviderProfileUseCase,
                                     GetProviderProfileBySlugUseCase getBySlugUseCase,
                                     UpdateProviderProfileUseCase updateProfileUseCase,
                                     GetMyStoresUseCase getMyStoresUseCase) {
        this.getProviderProfileUseCase = getProviderProfileUseCase;
        this.getBySlugUseCase = getBySlugUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
        this.getMyStoresUseCase = getMyStoresUseCase;
    }

    // ── DTOs ──────────────────────────────────────────────────────────────────

    public record StoreProfileResponse(String storeId, String storeName, String slug,
                                       String description, String profileImgUrl) {}

    public record UpdateStoreProfileRequest(String storeName, String description, String profileImgUrl) {}

    public record MyStoreItem(String storeId, String storeName, String slug,
                              String profileImgUrl, String role) {}

    public record MyStoresResponse(List<MyStoreItem> stores) {}

    // ── Endpoints ─────────────────────────────────────────────────────────────

    @GetMapping("/{storeId}")
    @Operation(summary = "Get store profile by ID (public)")
    public ResponseEntity<StoreProfileResponse> getById(@PathVariable String storeId) {
        var out = getProviderProfileUseCase.execute(new GetProviderProfileUseCase.Input(storeId));
        return ResponseEntity.ok(new StoreProfileResponse(
                out.storeId(), out.storeName(), out.slug(),
                out.description(), out.profileImgUrl()));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get store profile by slug (public)")
    public ResponseEntity<StoreProfileResponse> getBySlug(@PathVariable String slug) {
        var out = getBySlugUseCase.execute(new GetProviderProfileBySlugUseCase.Input(slug));
        return ResponseEntity.ok(new StoreProfileResponse(
                out.storeId(), out.storeName(), out.slug(),
                out.description(), out.profileImgUrl()));
    }

    @PutMapping("/{storeId}")
    @Operation(summary = "Update store profile", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<StoreProfileResponse> update(@PathVariable String storeId,
                                                       @RequestBody UpdateStoreProfileRequest body) {
        String userId = getPrincipal();
        var input = new UpdateProviderProfileUseCase.Input(
                userId, storeId, body.storeName(), body.description(), body.profileImgUrl());
        var out = updateProfileUseCase.execute(input);
        return ResponseEntity.ok(new StoreProfileResponse(
                out.storeId(), out.storeName(), out.slug(),
                out.description(), out.profileImgUrl()));
    }

    @GetMapping("/my")
    @Operation(summary = "Get all stores for authenticated provider", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<MyStoresResponse> myStores() {
        String userId = getPrincipal();
        var out = getMyStoresUseCase.execute(new GetMyStoresUseCase.Input(userId));
        List<MyStoreItem> items = out.stores().stream()
                .map(s -> new MyStoreItem(s.storeId(), s.storeName(), s.slug(),
                        s.profileImgUrl(), s.role().name()))
                .toList();
        return ResponseEntity.ok(new MyStoresResponse(items));
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private String getPrincipal() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}