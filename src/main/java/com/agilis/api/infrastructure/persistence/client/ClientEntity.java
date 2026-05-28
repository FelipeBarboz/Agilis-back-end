package com.agilis.api.infrastructure.persistence.client;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "clients")
public class ClientEntity {

    @Id
    @Column(name = "fk_user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(nullable = false, unique = true, columnDefinition = "CHAR(14)")
    private String cpf;
}