package com.agilis.api.domain.service;

import lombok.Getter;
import java.util.UUID;

@Getter
public class ServiceThumbnail {

    private final UUID id;
    private final UUID serviceId;
    private String url;

    private ServiceThumbnail(UUID id, UUID serviceId, String url) {
        this.id        = id;
        this.serviceId = serviceId;
        this.url       = validateUrl(url);
    }

    public static ServiceThumbnail create(UUID serviceId, String url) {
        return new ServiceThumbnail(UUID.randomUUID(), serviceId, url);
    }

    public static ServiceThumbnail reconstitute(UUID id, UUID serviceId, String url) {
        return new ServiceThumbnail(id, serviceId, url);
    }

    public void changeUrl(String url) {
        this.url = validateUrl(url);
    }

    private String validateUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL da thumbnail não pode ser vazia");
        }
        return url;
    }
}