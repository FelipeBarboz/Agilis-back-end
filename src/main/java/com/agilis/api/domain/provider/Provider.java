package com.agilis.api.domain.provider;

import lombok.Getter;
import java.util.UUID;

@Getter
public class Provider {

    private final UUID userId;
    private String cnpj;
    private final UUID profileId;

    private Provider(UUID userId, String cnpj, UUID profileId) {
        this.userId = userId;
        this.cnpj   = validateCnpj(cnpj);
        this.profileId = profileId;
    }

    public static Provider create(UUID userId, String cnpj, UUID profileId) {
        return new Provider(userId, cnpj, profileId);
    }

    public static Provider reconstitute(UUID userId, String cnpj, UUID profileId) {
        return new Provider(userId, cnpj, profileId);
    }

    private String validateCnpj(String cnpj) {
        if (cnpj == null || !cnpj.matches("^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$")) {
            throw new IllegalArgumentException("Invalid CNPJ. Format expected: 00.000.000/0000-00");
        }
        return cnpj;
    }
}