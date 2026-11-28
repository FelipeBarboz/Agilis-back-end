package com.agilis.api.infrastructure.persistence.user;

import com.agilis.api.domain.user.TermsAcceptance;
import com.agilis.api.domain.user.TermsAcceptanceRepository;

import java.util.Optional;
import java.util.UUID;

public class TermsAcceptanceRepositoryAdapter implements TermsAcceptanceRepository {

    private final TermsAcceptanceJpaRepository jpaRepository;

    public TermsAcceptanceRepositoryAdapter(TermsAcceptanceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TermsAcceptance save(TermsAcceptance acceptance) {
        TermsAcceptanceEntity entity = new TermsAcceptanceEntity();
        entity.setId(acceptance.getId());
        entity.setUserId(acceptance.getUserId());
        entity.setTermsAccepted(acceptance.isTermsAccepted());
        entity.setPrivacyAccepted(acceptance.isPrivacyAccepted());
        entity.setTermsVersion(acceptance.getTermsVersion());
        entity.setPrivacyVersion(acceptance.getPrivacyVersion());
        entity.setIpAddress(acceptance.getIpAddress());
        entity.setUserAgent(acceptance.getUserAgent());
        entity.setAcceptedAt(acceptance.getAcceptedAt());

        TermsAcceptanceEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<TermsAcceptance> findLatestByUserId(UUID userId) {
        return jpaRepository.findTopByUserIdOrderByAcceptedAtDesc(userId)
                .map(this::toDomain);
    }

    private TermsAcceptance toDomain(TermsAcceptanceEntity entity) {
        return TermsAcceptance.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.isTermsAccepted(),
                entity.isPrivacyAccepted(),
                entity.getTermsVersion(),
                entity.getPrivacyVersion(),
                entity.getIpAddress(),
                entity.getUserAgent(),
                entity.getAcceptedAt()
        );
    }
}
