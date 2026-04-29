package com.agilis.api.domain.client;

import lombok.Getter;
import java.util.UUID;

@Getter
public class Client {

    private final UUID userId;
    private String cpf;

    private Client(UUID userId, String cpf) {
        this.userId = userId;
        this.cpf    = validateCpf(cpf);
    }

    public static Client create(UUID userId, String cpf) {
        return new Client(userId, cpf);
    }

    public static Client reconstitute(UUID userId, String cpf) {
        return new Client(userId, cpf);
    }

    private String validateCpf(String cpf) {
        if (cpf == null || !cpf.matches("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$")) {
            throw new IllegalArgumentException("Invalid CPF, format expected: 000.000.000-00");
        }
        return cpf;
    }
}