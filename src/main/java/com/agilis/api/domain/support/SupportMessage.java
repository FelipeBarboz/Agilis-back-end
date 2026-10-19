package com.agilis.api.domain.support;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class SupportMessage {

    private final UUID id;
    private final UUID userId; // pode ser null — pessoa nao logada
    private final String name;
    private final String email;
    private final SupportSubject subject;
    private final String message;
    private final LocalDateTime createdAt;

    private SupportMessage(UUID id, UUID userId, String name, String email, SupportSubject subject, String message, LocalDateTime createdAt) {
        this.id        = id;
        this.userId    = userId;
        this.name      = validateField(name, "Name");
        this.email     = validateEmail(email);
        this.subject   = validateSubject(subject);
        this.message   = validateField(message, "Message");
        this.createdAt = createdAt;
    }

    public static SupportMessage create(UUID userId, String name, String email, SupportSubject subject, String message) {
        return new SupportMessage(UUID.randomUUID(), userId, name, email, subject, message, LocalDateTime.now());
    }

    public static SupportMessage reconstitute(UUID id, UUID userId, String name, String email, SupportSubject subject, String message, LocalDateTime createdAt) {
        return new SupportMessage(id, userId, name, email, subject, message, createdAt);
    }

    private String validateField(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        return value;
    }

    private String validateEmail(String email) {
        if (email == null || !email.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[a-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email");
        }
        return email;
    }

    private SupportSubject validateSubject(SupportSubject subject) {
        if (subject == null) {
            throw new IllegalArgumentException("Support subject cannot be empty");
        }
        return subject;
    }
}