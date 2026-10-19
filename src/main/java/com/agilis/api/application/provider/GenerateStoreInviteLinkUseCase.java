package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

public class GenerateStoreInviteLinkUseCase {

    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 8;
    private final SecureRandom random = new SecureRandom();

    private final StoreInviteRepository storeInviteRepository;
    private final StoreMembershipRepository storeMembershipRepository;
    private final ProviderProfileRepository providerProfileRepository;

    public GenerateStoreInviteLinkUseCase(
            StoreInviteRepository storeInviteRepository,
            StoreMembershipRepository storeMembershipRepository,
            ProviderProfileRepository providerProfileRepository
    ) {
        this.storeInviteRepository       = storeInviteRepository;
        this.storeMembershipRepository  = storeMembershipRepository;
        this.providerProfileRepository  = providerProfileRepository;
    }

    public Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID storeId     = UUID.fromString(input.storeId());

        if (providerProfileRepository.findById(storeId).isEmpty()) {
            throw new IllegalArgumentException("Store not found.");
        }

        StoreMembership requesterMembership = storeMembershipRepository
                .findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store."));

        if (!requesterMembership.getRole().canManageMembers() && !requesterMembership.getRole().canManageStore()) {
            throw new IllegalStateException("No permission to generate invite links.");
        }

        StoreRole role = input.role() != null ? input.role() : StoreRole.EMPLOYEE;
        if (role == StoreRole.OWNER) {
            throw new IllegalStateException("Cannot invite someone as an owner.");
        }
        if (role == StoreRole.ADMIN && requesterMembership.getRole() != StoreRole.OWNER) {
            throw new IllegalStateException("Only the store owner can generate invite links for admin role.");
        }

        int days = (input.expirationDays() != null && input.expirationDays() > 0) ? input.expirationDays() : 7;
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(days);

        String code = generateUniqueCode();

        StoreInvite invite = StoreInvite.create(
                storeId,
                code,
                role,
                requesterId,
                expiresAt,
                input.maxUses()
        );

        storeInviteRepository.save(invite);

        String invitePath = "/join-store?code=" + code;

        return new Output(
                invite.getId().toString(),
                storeId.toString(),
                code,
                invitePath,
                invite.getRole().name(),
                invite.getExpiresAt(),
                invite.getMaxUses()
        );
    }

    private String generateUniqueCode() {
        String code;
        int attempts = 0;
        do {
            if (attempts++ > 10) {
                code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                break;
            }
            StringBuilder sb = new StringBuilder("AG-");
            for (int i = 0; i < CODE_LENGTH; i++) {
                sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
            code = sb.toString();
        } while (storeInviteRepository.findByCode(code).isPresent());
        return code;
    }

    @Schema(name = "GenerateStoreInviteInput")
    public record Input(
            String requesterId,
            String storeId,
            StoreRole role,
            Integer expirationDays,
            Integer maxUses
    ) {}

    @Schema(name = "GenerateStoreInviteOutput")
    public record Output(
            String inviteId,
            String storeId,
            String code,
            String invitePath,
            String role,
            LocalDateTime expiresAt,
            Integer maxUses
    ) {}
}
