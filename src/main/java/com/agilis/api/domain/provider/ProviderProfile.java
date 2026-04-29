package com.agilis.api.domain.provider;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ProviderProfile {

    private final UUID id;
    private String storeName;
    private String slug;
    private String description;
    private String profileImgUrl;
    private final LocalDateTime createdAt;

    private ProviderProfile(UUID id, String storeName, String slug, String description, String profileImgUrl, LocalDateTime createdAt) {
        this.id            = id;
        this.storeName     = validateStoreName(storeName);
        this.slug          = validateSlug(slug);
        this.description   = description;
        this.profileImgUrl = profileImgUrl;
        this.createdAt     = createdAt;
    }

    public static ProviderProfile create(String storeName, String slug, String description, String profileImgUrl) {
        return new ProviderProfile(
                UUID.randomUUID(),
                storeName,
                slug,
                description,
                profileImgUrl,
                LocalDateTime.now()
        );
    }

    public static ProviderProfile reconstitute(UUID id, String storeName, String slug, String description, String profileImgUrl, LocalDateTime createdAt) {
        return new ProviderProfile(id, storeName, slug, description, profileImgUrl, createdAt);
    }

    public void changeStoreName(String storeName) {
        this.storeName = validateStoreName(storeName);
    }

    public void changeSlug(String slug) {
        this.slug = validateSlug(slug);
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changeProfileImg(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    private String validateStoreName(String storeName) {
        if (storeName == null || storeName.isBlank()) {
            throw new IllegalArgumentException("The store cannot be empty");
        }
        return storeName;
    }

    private String validateSlug(String slug) {
        if (slug == null || !slug.matches("^[a-z0-9]+(?:-[a-z0-9]+)*$")) {
            throw new IllegalArgumentException("Invalid slug. Use only lowercase letters, numbers, and hyphens. Example: minha-loja.");
        }
        return slug;
    }
}