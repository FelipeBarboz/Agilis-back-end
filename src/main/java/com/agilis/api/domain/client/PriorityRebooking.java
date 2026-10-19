package com.agilis.api.domain.client;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class PriorityRebooking {

    private final UUID id;
    private final UUID clientId;
    private final UUID serviceId;
    private final String reason;
    private final LocalDateTime expiresAt;
    private boolean used;
    private final LocalDateTime createdAt;

    private PriorityRebooking(UUID id, UUID clientId, UUID serviceId, String reason, LocalDateTime expiresAt, boolean used, LocalDateTime createdAt) {
        this.id        = id;
        this.clientId  = clientId;
        this.serviceId = serviceId;
        this.reason    = reason;
        this.expiresAt = expiresAt;
        this.used      = used;
        this.createdAt = createdAt;
    }

    public static PriorityRebooking create(UUID clientId, UUID serviceId, String reason) {
        return new PriorityRebooking(UUID.randomUUID(), clientId, serviceId, reason, LocalDateTime.now().plusDays(30), false, LocalDateTime.now());
    }

    public static PriorityRebooking reconstitute(UUID id, UUID clientId, UUID serviceId, String reason, LocalDateTime expiresAt, boolean used, LocalDateTime createdAt) {
        return new PriorityRebooking(id, clientId, serviceId, reason, expiresAt, used, createdAt);
    }

    public boolean isValid() {
        return !used && LocalDateTime.now().isBefore(expiresAt);
    }

    public void markUsed() {
        if (!isValid()) {
            throw new IllegalStateException("Invalid or already used priority");
        }
        this.used = true;
    }
}