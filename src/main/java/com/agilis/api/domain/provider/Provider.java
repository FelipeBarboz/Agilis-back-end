package com.agilis.api.domain.provider;

import lombok.Getter;
import java.util.UUID;

@Getter
public class Provider {

    private final UUID userId;
    private String cnpj;

    private Provider(UUID userId, String cnpj) {
        this.userId = userId;
        this.cnpj   = validateCnpj(cnpj);
    }

    public static Provider create(UUID userId, String cnpj) {
        return new Provider(userId, cnpj);
    }

    public static Provider reconstitute(UUID userId, String cnpj) {
        return new Provider(userId, cnpj);
    }

    private String validateCnpj(String cnpj) {
        if (cnpj == null || !cnpj.matches("^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$")) {
            throw new IllegalArgumentException("Invalid CNPJ. Format expected: 00.000.000/0000-00");
        }
        return cnpj;
    }
}