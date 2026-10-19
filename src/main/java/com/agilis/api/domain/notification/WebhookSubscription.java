package com.agilis.api.domain.notification;

import lombok.Getter;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class WebhookSubscription {

    private final UUID id;
    private final UUID storeId;
    private String url;
    private final String secret;
    private Set<WebhookEventType> events;
    private boolean active;
    private final LocalDateTime createdAt;

    private WebhookSubscription(UUID id, UUID storeId, String url, String secret,
                                Set<WebhookEventType> events, boolean active, LocalDateTime createdAt) {
        this.id        = id;
        this.storeId   = storeId;
        this.url       = validateUrl(url);
        this.secret    = secret;
        this.events    = validateEvents(events);
        this.active    = active;
        this.createdAt = createdAt;
    }

    public static WebhookSubscription create(UUID storeId, String url, Set<WebhookEventType> events) {
        return new WebhookSubscription(
                UUID.randomUUID(), storeId, url, generateSecret(), events, true, LocalDateTime.now()
        );
    }

    public static WebhookSubscription reconstitute(UUID id, UUID storeId, String url, String secret,
                                                   Set<WebhookEventType> events, boolean active, LocalDateTime createdAt) {
        return new WebhookSubscription(id, storeId, url, secret, events, active, createdAt);
    }

    public void changeUrl(String url)               { this.url    = validateUrl(url); }
    public void changeEvents(Set<WebhookEventType> events) { this.events = validateEvents(events); }
    public void activate()                            { this.active = true; }
    public void deactivate()                           { this.active = false; }

    public boolean subscribesTo(WebhookEventType type) {
        return active && events.contains(type);
    }

    private String validateUrl(String url) {
        if (url == null || !url.matches("^https?://.+")) {
            throw new IllegalArgumentException("Invalid webhook URL. Must start with http:// ou https://");
        }
        return url;
    }

    private Set<WebhookEventType> validateEvents(Set<WebhookEventType> events) {
        if (events == null || events.isEmpty()) {
            throw new IllegalArgumentException("Select at least one event to the webhook");
        }
        return EnumSet.copyOf(events);
    }

    private static String generateSecret() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}