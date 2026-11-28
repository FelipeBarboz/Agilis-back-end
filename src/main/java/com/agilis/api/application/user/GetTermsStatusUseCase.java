package com.agilis.api.application.user;

import com.agilis.api.domain.user.TermsAcceptanceRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public class GetTermsStatusUseCase {

    private final TermsAcceptanceRepository termsAcceptanceRepository;

    public GetTermsStatusUseCase(TermsAcceptanceRepository termsAcceptanceRepository) {
        this.termsAcceptanceRepository = termsAcceptanceRepository;
    }

    public Output execute(String userIdStr) {
        UUID userId;
        try {
            userId = UUID.fromString(userIdStr);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("ID de usuário inválido.");
        }

        return termsAcceptanceRepository.findLatestByUserId(userId)
                .map(acceptance -> new Output(
                        acceptance.isTermsAccepted() && acceptance.isPrivacyAccepted(),
                        acceptance.isTermsAccepted(),
                        acceptance.isPrivacyAccepted(),
                        acceptance.getTermsVersion(),
                        acceptance.getPrivacyVersion(),
                        acceptance.getAcceptedAt()
                ))
                .orElse(new Output(false, false, false, null, null, null));
    }

    @Schema(name = "TermsStatusOutput")
    public record Output(
            boolean accepted,
            boolean termsAccepted,
            boolean privacyAccepted,
            String termsVersion,
            String privacyVersion,
            LocalDateTime acceptedAt
    ) {}
}
