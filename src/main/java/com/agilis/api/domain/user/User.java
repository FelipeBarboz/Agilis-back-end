package com.agilis.api.domain.user;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class User {

    private final UUID id;
    private String name;
    private String email;
    private String phone;
    private final LocalDateTime createdAt;

    private User(UUID id, String name, String email, String phone, LocalDateTime createdAt) {
        this.id = id;
        this.name = validateName(name);
        this.email = validateEmail(email);
        this.phone = phone;
        this.createdAt = createdAt;
    }

    public static User create(String name, String email, String phone) {
        return new User(
                UUID.randomUUID(),
                name,
                email,
                phone,
                LocalDateTime.now()
        );
    }

    public static User reconstitute(UUID id, String name, String email, String phone, LocalDateTime createdAt) {
        return new User(id, name, email, phone, createdAt);
    }

    public void changeName(String name) {
        this.name = validateName(name);
    }

    public void changeEmail(String email) {
        this.email = validateEmail(email);
    }

    public void changePhone(String phone) {
        this.phone = phone;
    }

    private String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        return name;
    }

    private String validateEmail(String email) {
        if (email == null || !email.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[a-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email");
        }
        return email;
    }
}
