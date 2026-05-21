package com.agilis.api.infrastructure.web;

import com.agilis.api.application.provider.InviteMemberUseCase;
import com.agilis.api.application.provider.RemoveMemberUseCase;
import com.agilis.api.domain.provider.StoreMembership;
import com.agilis.api.domain.provider.StoreMembershipRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stores")
public class StoreController {

    private final InviteMemberUseCase inviteMemberUseCase;
    private final RemoveMemberUseCase removeMemberUseCase;
    private final StoreMembershipRepository storeMembershipRepository;

    public StoreController(
            InviteMemberUseCase inviteMemberUseCase,
            RemoveMemberUseCase removeMemberUseCase,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.inviteMemberUseCase       = inviteMemberUseCase;
        this.removeMemberUseCase       = removeMemberUseCase;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    @PostMapping("/{storeId}/members")
    public ResponseEntity<InviteMemberUseCase.Output> invite(
            @PathVariable String storeId,
            @RequestBody InviteMemberUseCase.Input input
    ) {
        String requesterId = getCurrentUserId();
        InviteMemberUseCase.Input inputWithRequester = new InviteMemberUseCase.Input(
                requesterId,
                storeId,
                input.providerEmail(),
                input.role()
        );
        return ResponseEntity.ok(inviteMemberUseCase.execute(inputWithRequester));
    }

    @DeleteMapping("/{storeId}/members/{memberId}")
    public ResponseEntity<Void> remove(
            @PathVariable String storeId,
            @PathVariable String memberId
    ) {
        String requesterId = getCurrentUserId();
        removeMemberUseCase.execute(new RemoveMemberUseCase.Input(
                requesterId,
                storeId,
                memberId
        ));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{storeId}/members")
    public ResponseEntity<List<StoreMembership>> getMembers(@PathVariable String storeId) {
        return ResponseEntity.ok(
                storeMembershipRepository.findAllByStoreId(UUID.fromString(storeId))
        );
    }

    private String getCurrentUserId() {
        return (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}