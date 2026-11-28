package com.agilis.api.domain.user;

import java.util.Optional;
import java.util.UUID;

public interface TermsAcceptanceRepository {

    TermsAcceptance save(TermsAcceptance termsAcceptance);

    Optional<TermsAcceptance> findLatestByUserId(UUID userId);
}
