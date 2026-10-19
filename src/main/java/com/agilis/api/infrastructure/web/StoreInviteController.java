package com.agilis.api.infrastructure.web;

import com.agilis.api.application.provider.*;
import com.agilis.api.domain.provider.StoreInvite;
import com.agilis.api.domain.provider.StoreInviteRepository;
import com.agilis.api.domain.provider.StoreRole;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class StoreInviteController {

    private final GenerateStoreInviteLinkUseCase generateStoreInviteLinkUseCase;
    private final GetStoreInviteInfoUseCase getStoreInviteInfoUseCase;
    private final AcceptStoreInviteUseCase acceptStoreInviteUseCase;
    private final RevokeStoreInviteUseCase revokeStoreInviteUseCase;
    private final StoreInviteRepository storeInviteRepository;

    public StoreInviteController(
            GenerateStoreInviteLinkUseCase generateStoreInviteLinkUseCase,
            GetStoreInviteInfoUseCase getStoreInviteInfoUseCase,
            AcceptStoreInviteUseCase acceptStoreInviteUseCase,
            RevokeStoreInviteUseCase revokeStoreInviteUseCase,
            StoreInviteRepository storeInviteRepository
    ) {
        this.generateStoreInviteLinkUseCase = generateStoreInviteLinkUseCase;
        this.getStoreInviteInfoUseCase     = getStoreInviteInfoUseCase;
        this.acceptStoreInviteUseCase       = acceptStoreInviteUseCase;
        this.revokeStoreInviteUseCase       = revokeStoreInviteUseCase;
        this.storeInviteRepository          = storeInviteRepository;
    }

    @PostMapping("/api/v1/stores/{storeId}/invites")
    public ResponseEntity<GenerateStoreInviteLinkUseCase.Output> generateInvite(
            @PathVariable String storeId,
            @RequestBody(required = false) CreateInviteRequest request
    ) {
        String requesterId = getCurrentUserId();
        StoreRole role = request != null && request.role() != null ? request.role() : StoreRole.EMPLOYEE;
        Integer expirationDays = request != null ? request.expirationDays() : null;
        Integer maxUses = request != null ? request.maxUses() : null;

        var input = new GenerateStoreInviteLinkUseCase.Input(
                requesterId,
                storeId,
                role,
                expirationDays,
                maxUses
        );

        return ResponseEntity.ok(generateStoreInviteLinkUseCase.execute(input));
    }

    @GetMapping("/api/v1/stores/{storeId}/invites")
    public ResponseEntity<List<StoreInvite>> listStoreInvites(@PathVariable String storeId) {
        return ResponseEntity.ok(storeInviteRepository.findAllByStoreId(UUID.fromString(storeId)));
    }

    @DeleteMapping("/api/v1/stores/{storeId}/invites/{inviteId}")
    public ResponseEntity<Void> revokeInvite(
            @PathVariable String storeId,
            @PathVariable String inviteId
    ) {
        String requesterId = getCurrentUserId();
        revokeStoreInviteUseCase.execute(new RevokeStoreInviteUseCase.Input(requesterId, storeId, inviteId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/v1/store-invites/{code}")
    public ResponseEntity<GetStoreInviteInfoUseCase.Output> getInviteInfo(@PathVariable String code) {
        return ResponseEntity.ok(getStoreInviteInfoUseCase.execute(code));
    }

    @PostMapping("/api/v1/store-invites/{code}/accept")
    public ResponseEntity<AcceptStoreInviteUseCase.Output> acceptInvite(@PathVariable String code) {
        String userId = getCurrentUserId();
        return ResponseEntity.ok(acceptStoreInviteUseCase.execute(new AcceptStoreInviteUseCase.Input(userId, code)));
    }

    private String getCurrentUserId() {
        org.springframework.security.core.Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new org.springframework.security.access.AccessDeniedException("Acesso negado: usuário não autenticado");
        }
        return (String) auth.getPrincipal();
    }

    @Schema(name = "CreateInviteRequest")
    public record CreateInviteRequest(
            StoreRole role,
            Integer expirationDays,
            Integer maxUses
    ) {}
}
