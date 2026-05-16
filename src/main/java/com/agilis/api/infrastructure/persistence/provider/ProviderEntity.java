package com.agilis.api.infrastructure.persistence.provider;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "providers")
public class ProviderEntity {

    @Id
    @Column(name = "fk_user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(nullable = false, unique = true, columnDefinition = "CHAR(18)")
    private String cnpj;
}