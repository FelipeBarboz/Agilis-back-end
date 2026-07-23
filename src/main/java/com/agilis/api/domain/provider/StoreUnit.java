package com.agilis.api.domain.provider;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class StoreUnit {

    private final UUID id;
    private final UUID providerProfileId;
    private String name;
    private String street;
    private String number;
    private String complement;
    private String city;
    private String state;
    private String cep;
    private final LocalDateTime createdAt;

    private StoreUnit(UUID id, UUID providerProfileId, String name, String street, String number,
                      String complement, String city, String state, String cep, LocalDateTime createdAt) {
        this.id                = id;
        this.providerProfileId = providerProfileId;
        this.name               = validateField(name, "Unit name");
        this.street              = validateField(street, "Road");
        this.number              = validateField(number, "Number");
        this.complement          = complement;
        this.city                = validateField(city, "City");
        this.state                = validateState(state);
        this.cep                  = validateCep(cep);
        this.createdAt           = createdAt;
    }

    public static StoreUnit create(UUID providerProfileId, String name, String street, String number,
                                   String complement, String city, String state, String cep) {
        return new StoreUnit(
                UUID.randomUUID(), providerProfileId, name, street, number, complement, city, state, cep, LocalDateTime.now()
        );
    }

    public static StoreUnit reconstitute(UUID id, UUID providerProfileId, String name, String street, String number,
                                         String complement, String city, String state, String cep, LocalDateTime createdAt) {
        return new StoreUnit(id, providerProfileId, name, street, number, complement, city, state, cep, createdAt);
    }

    public void changeName(String name)             { this.name       = validateField(name, "Unity name"); }
    public void changeStreet(String street)          { this.street     = validateField(street, "Road"); }
    public void changeNumber(String number)          { this.number     = validateField(number, "Number"); }
    public void changeComplement(String complement)  { this.complement = complement; }
    public void changeCity(String city)              { this.city       = validateField(city, "City"); }
    public void changeState(String state)            { this.state      = validateState(state); }
    public void changeCep(String cep)                { this.cep        = validateCep(cep); }

    private String validateField(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        return value;
    }

    private String validateState(String state) {
        if (state == null || state.length() != 2) {
            throw new IllegalArgumentException("State must contain exactly 2 characters. Ex: SP");
        }
        return state.toUpperCase();
    }

    private String validateCep(String cep) {
        if (cep == null || !cep.matches("^\\d{5}-\\d{3}$")) {
            throw new IllegalArgumentException("Invalid CEP code. Expected format: 00000-000");
        }
        return cep;
    }
}