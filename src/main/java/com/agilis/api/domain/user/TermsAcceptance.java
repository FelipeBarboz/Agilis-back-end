package com.agilis.api.domain.user;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class TermsAcceptance {

    private static final String DEFAULT_VERSION = "1.0";

    private final UUID id;
    private final UUID userId;
    private final boolean termsAccepted;
    private final boolean privacyAccepted;
    private final String termsVersion;
    private final String privacyVersion;
    private final String ipAddress;
    private final String userAgent;
    private final LocalDateTime acceptedAt;

    private TermsAcceptance(
            UUID id,
            UUID userId,
            boolean termsAccepted,
            boolean privacyAccepted,
            String termsVersion,
            String privacyVersion,
            String ipAddress,
            String userAgent,
            LocalDateTime acceptedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.termsAccepted = termsAccepted;
        this.privacyAccepted = privacyAccepted;
        this.termsVersion = termsVersion;
        this.privacyVersion = privacyVersion;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.acceptedAt = acceptedAt;
    }

    public static TermsAcceptance create(
            UUID userId,
            Boolean termsAccepted,
            Boolean privacyAccepted,
            String termsVersion,
            String privacyVersion,
            String ipAddress,
            String userAgent
    ) {
        if (userId == null) {
            throw new IllegalArgumentException("Usuário não identificado.");
        }
        if (!Boolean.TRUE.equals(termsAccepted)) {
            throw new IllegalArgumentException("É obrigatório aceitar os Termos de Uso.");
        }
        if (!Boolean.TRUE.equals(privacyAccepted)) {
            throw new IllegalArgumentException("É obrigatório aceitar a Política de Privacidade.");
        }

        String normalizedTermsVersion = normalizeVersion(termsVersion);
        String normalizedPrivacyVersion = normalizeVersion(privacyVersion);

        return new TermsAcceptance(
                UUID.randomUUID(),
                userId,
                true,
                true,
                normalizedTermsVersion,
                normalizedPrivacyVersion,
                ipAddress,
                userAgent,
                LocalDateTime.now()
        );
    }

    public static TermsAcceptance reconstitute(
            UUID id,
            UUID userId,
            boolean termsAccepted,
            boolean privacyAccepted,
            String termsVersion,
            String privacyVersion,
            String ipAddress,
            String userAgent,
            LocalDateTime acceptedAt
    ) {
        return new TermsAcceptance(
                id,
                userId,
                termsAccepted,
                privacyAccepted,
                termsVersion,
                privacyVersion,
                ipAddress,
                userAgent,
                acceptedAt
        );
    }

    private static String normalizeVersion(String version) {
        return (version == null || version.isBlank()) ? DEFAULT_VERSION : version.trim();
    }
}
