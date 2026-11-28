package com.agilis.api.application.user;

import com.agilis.api.domain.user.TermsAcceptance;
import com.agilis.api.domain.user.TermsAcceptanceRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public class AcceptTermsUseCase {

    private final TermsAcceptanceRepository termsAcceptanceRepository;

    public AcceptTermsUseCase(TermsAcceptanceRepository termsAcceptanceRepository) {
        this.termsAcceptanceRepository = termsAcceptanceRepository;
    }

    public Output execute(Input input) {
        UUID userId;
        try {
            userId = UUID.fromString(input.userId());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("ID de usuário inválido.");
        }

        TermsAcceptance acceptance = TermsAcceptance.create(
                userId,
                input.termsAccepted(),
                input.privacyAccepted(),
                input.termsVersion(),
                input.privacyVersion(),
                input.ipAddress(),
                input.userAgent()
        );

        TermsAcceptance saved = termsAcceptanceRepository.save(acceptance);

        return new Output(
                saved.getId().toString(),
                saved.getUserId().toString(),
                saved.isTermsAccepted(),
                saved.isPrivacyAccepted(),
                saved.getTermsVersion(),
                saved.getPrivacyVersion(),
                saved.getAcceptedAt()
        );
    }

    @Schema(name = "AcceptTermsInput")
    public record Input(
            String userId,
            Boolean termsAccepted,
            Boolean privacyAccepted,
            String termsVersion,
            String privacyVersion,
            String ipAddress,
            String userAgent
    ) {}

    @Schema(name = "AcceptTermsOutput")
    public record Output(
            String id,
            String userId,
            boolean termsAccepted,
            boolean privacyAccepted,
            String termsVersion,
            String privacyVersion,
            LocalDateTime acceptedAt
    ) {}
}
