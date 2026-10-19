package com.agilis.api.domain.provider;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class StoreInvite {

    private final UUID id;
    private final UUID storeId;
    private final String code;
    private final StoreRole role;
    private final UUID createdBy;
    private final LocalDateTime expiresAt;
    private final Integer maxUses;
    private int usedCount;
    private StoreInviteStatus status;
    private final LocalDateTime createdAt;

    private StoreInvite(
            UUID id,
            UUID storeId,
            String code,
            StoreRole role,
            UUID createdBy,
            LocalDateTime expiresAt,
            Integer maxUses,
            int usedCount,
            StoreInviteStatus status,
            LocalDateTime createdAt
    ) {
        this.id        = id;
        this.storeId   = validateStoreId(storeId);
        this.code      = validateCode(code);
        this.role      = role != null ? role : StoreRole.EMPLOYEE;
        this.createdBy = createdBy;
        this.expiresAt = validateExpiresAt(expiresAt);
        this.maxUses   = maxUses;
        this.usedCount = usedCount;
        this.status    = status != null ? status : StoreInviteStatus.ACTIVE;
        this.createdAt = createdAt;
    }

    public static StoreInvite create(
            UUID storeId,
            String code,
            StoreRole role,
            UUID createdBy,
            LocalDateTime expiresAt,
            Integer maxUses
    ) {
        return new StoreInvite(
                UUID.randomUUID(),
                storeId,
                code,
                role,
                createdBy,
                expiresAt,
                maxUses,
                0,
                StoreInviteStatus.ACTIVE,
                LocalDateTime.now()
        );
    }

    public static StoreInvite reconstitute(
            UUID id,
            UUID storeId,
            String code,
            StoreRole role,
            UUID createdBy,
            LocalDateTime expiresAt,
            Integer maxUses,
            int usedCount,
            StoreInviteStatus status,
            LocalDateTime createdAt
    ) {
        return new StoreInvite(
                id,
                storeId,
                code,
                role,
                createdBy,
                expiresAt,
                maxUses,
                usedCount,
                status,
                createdAt
        );
    }

    public boolean isValid() {
        if (status != StoreInviteStatus.ACTIVE) {
            return false;
        }
        if (expiresAt != null && LocalDateTime.now().isAfter(expiresAt)) {
            return false;
        }
        if (maxUses != null && usedCount >= maxUses) {
            return false;
        }
        return true;
    }

    public void consume() {
        if (!isValid()) {
            throw new IllegalStateException("This invite is no longer valid or has expired.");
        }
        this.usedCount++;
        if (maxUses != null && this.usedCount >= maxUses) {
            this.status = StoreInviteStatus.CONSUMED;
        }
    }

    public void revoke() {
        this.status = StoreInviteStatus.REVOKED;
    }

    private UUID validateStoreId(UUID storeId) {
        if (storeId == null) {
            throw new IllegalArgumentException("Store ID cannot be null.");
        }
        return storeId;
    }

    private String validateCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Invite code cannot be empty.");
        }
        return code.trim();
    }

    private LocalDateTime validateExpiresAt(LocalDateTime expiresAt) {
        if (expiresAt != null && expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Expiration date must be in the future.");
        }
        return expiresAt;
    }
}
