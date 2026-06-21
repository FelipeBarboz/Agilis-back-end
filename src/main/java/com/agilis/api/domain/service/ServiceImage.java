package com.agilis.api.domain.service;

import lombok.Getter;
import java.util.UUID;

@Getter
public class ServiceImage {

    private final UUID id;
    private final UUID serviceId;
    private final String url;

    private ServiceImage(UUID id, UUID serviceId, String url) {
        this.id        = id;
        this.serviceId = serviceId;
        this.url       = validateUrl(url);
    }

    public static ServiceImage create(UUID serviceId, String url) {
        return new ServiceImage(UUID.randomUUID(), serviceId, url);
    }

    public static ServiceImage reconstitute(UUID id, UUID serviceId, String url) {
        return new ServiceImage(id, serviceId, url);
    }

    private String validateUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL da imagem não pode ser vazia");
        }
        return url;
    }
}