package com.agilis.api.domain.provider;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class StoreMembership {

    private final UUID id;
    private final UUID storeId;
    private final UUID providerId;
    private StoreRole role;
    private UUID positionId;
    private final UUID invitedBy;
    private final LocalDateTime createdAt;

    private StoreMembership(UUID id, UUID storeId, UUID providerId, StoreRole role, UUID positionId, UUID invitedBy, LocalDateTime createdAt) {
        this.id         = id;
        this.storeId    = storeId;
        this.providerId = providerId;
        this.role       = validateRole(role);
        this.positionId = positionId;
        this.invitedBy  = invitedBy;
        this.createdAt  = createdAt;
    }

    public static StoreMembership create(UUID storeId, UUID providerId, StoreRole role, UUID invitedBy) {
        return new StoreMembership(UUID.randomUUID(), storeId, providerId, role, null, invitedBy, LocalDateTime.now());
    }

    public static StoreMembership reconstitute(UUID id, UUID storeId, UUID providerId, StoreRole role, UUID positionId, UUID invitedBy, LocalDateTime createdAt) {
        return new StoreMembership(id, storeId, providerId, role, positionId, invitedBy, createdAt);
    }

    public void assignPosition(UUID positionId) {
        if (this.role == StoreRole.OWNER) {
            throw new IllegalStateException("Owner does not receive a role — they already have full access");
        }
        this.positionId = positionId;
    }

    public void promoteToAdmin() {
        if (this.role == StoreRole.OWNER) {
            throw new IllegalStateException("Owner cannot be changed");
        }
        this.role = StoreRole.ADMIN;
    }

    public void demoteToEmployee() {
        if (this.role == StoreRole.OWNER) {
            throw new IllegalStateException("Owner cannot be changed");
        }
        this.role = StoreRole.EMPLOYEE;
    }

    private StoreRole validateRole(StoreRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        return role;
    }
}