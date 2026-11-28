package com.agilis.api.infrastructure.persistence.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "terms_acceptances")
public class TermsAcceptanceEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "terms_accepted", nullable = false, updatable = false)
    private boolean termsAccepted;

    @Column(name = "privacy_accepted", nullable = false, updatable = false)
    private boolean privacyAccepted;

    @Column(name = "terms_version", nullable = false, updatable = false, length = 50)
    private String termsVersion;

    @Column(name = "privacy_version", nullable = false, updatable = false, length = 50)
    private String privacyVersion;

    @Column(name = "ip_address", updatable = false, length = 45)
    private String ipAddress;

    @Column(name = "user_agent", updatable = false, columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "accepted_at", nullable = false, updatable = false)
    private LocalDateTime acceptedAt;
}
