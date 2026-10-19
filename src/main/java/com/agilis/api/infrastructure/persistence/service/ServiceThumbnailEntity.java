package com.agilis.api.infrastructure.persistence.service;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "service_thumbnail")
public class ServiceThumbnailEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "service_id", nullable = false, unique = true)
    private UUID serviceId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;
}