package com.agilis.api.domain.user;

import lombok.Getter;
import java.util.UUID;

@Getter
public class Address {

    private final UUID id;
    private final UUID userId;
    private String street;
    private String number;
    private String complement;
    private String city;
    private String state;
    private String cep;

    private Address(UUID id, UUID userId, String street, String number, String complement, String city, String state, String cep) {
        this.id         = id;
        this.userId     = userId;
        this.street     = validateField(street, "Rua");
        this.number     = validateField(number, "Número");
        this.complement = complement;
        this.city       = validateField(city, "Cidade");
        this.state      = validateState(state);
        this.cep        = validateCep(cep);
    }

    public static Address create(UUID userId, String street, String number, String complement, String city, String state, String cep) {
        return new Address(UUID.randomUUID(), userId, street, number, complement, city, state, cep);
    }

    public static Address reconstitute(UUID id, UUID userId, String street, String number, String complement, String city, String state, String cep) {
        return new Address(id, userId, street, number, complement, city, state, cep);
    }

    public void changeStreet(String street)         { this.street     = validateField(street, "Rua"); }
    public void changeNumber(String number)         { this.number     = validateField(number, "Número"); }
    public void changeComplement(String complement) { this.complement = complement; }
    public void changeCity(String city)             { this.city       = validateField(city, "Cidade"); }
    public void changeState(String state)           { this.state      = validateState(state); }
    public void changeCep(String cep)               { this.cep        = validateCep(cep); }

    private String validateField(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty!");
        }
        return value;
    }

    private String validateState(String state) {
        if (state == null || state.length() != 2) {
            throw new IllegalArgumentException("State must have 2 characters. Example: SP.");
        }
        return state.toUpperCase();
    }

    private String validateCep(String cep) {
        if (cep == null || !cep.matches("^\\d{5}-\\d{3}$")) {
            throw new IllegalArgumentException("Invalid CEP. Expected format: 00000-000.");
        }
        return cep;
    }
}