package com.agilis.api.infrastructure.persistence.provider;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "provider_profiles")
public class ProviderProfileEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "profile_img_url", columnDefinition = "TEXT")
    private String profileImgUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}