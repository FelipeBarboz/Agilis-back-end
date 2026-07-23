package com.agilis.api.infrastructure.persistence.provider;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "store_units")
public class StoreUnitEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "provider_profile_id", nullable = false)
    private UUID providerProfileId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false, length = 20)
    private String number;

    @Column(length = 100)
    private String complement;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 2)
    private String state;

    @Column(nullable = false, length = 9)
    private String cep;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}