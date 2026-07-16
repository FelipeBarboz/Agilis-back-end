package com.agilis.api.domain.favorite;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Favorite {

    private final UUID id;
    private final UUID userId;
    private final UUID serviceId;
    private final LocalDateTime createdAt;

    private Favorite(UUID id, UUID userId, UUID serviceId, LocalDateTime createdAt) {
        this.id        = id;
        this.userId    = userId;
        this.serviceId = serviceId;
        this.createdAt = createdAt;
    }

    public static Favorite create(UUID userId, UUID serviceId) {
        return new Favorite(UUID.randomUUID(), userId, serviceId, LocalDateTime.now());
    }

    public static Favorite reconstitute(UUID id, UUID userId, UUID serviceId, LocalDateTime createdAt) {
        return new Favorite(id, userId, serviceId, createdAt);
    }
}