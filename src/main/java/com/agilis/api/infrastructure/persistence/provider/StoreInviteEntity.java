package com.agilis.api.infrastructure.persistence.provider;

import com.agilis.api.domain.provider.StoreInviteStatus;
import com.agilis.api.domain.provider.StoreRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "store_invites")
public class StoreInviteEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StoreRole role;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "max_uses")
    private Integer maxUses;

    @Column(name = "used_count", nullable = false)
    private int usedCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StoreInviteStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
