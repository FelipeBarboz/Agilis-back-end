package com.agilis.api.infrastructure.persistence.negotiation;

import com.agilis.api.domain.negotiation.Negotiation;
import com.agilis.api.domain.negotiation.NegotiationRepository;
import com.agilis.api.domain.negotiation.NegotiationStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class NegotiationRepositoryAdapter implements NegotiationRepository {

    private final NegotiationJpaRepository jpaRepository;

    public NegotiationRepositoryAdapter(NegotiationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Negotiation save(Negotiation negotiation) {
        jpaRepository.save(toEntity(negotiation));
        return negotiation;
    }

    @Override
    public Optional<Negotiation> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Negotiation> findAllByBookingId(UUID bookingId) {
        return jpaRepository.findAllByBookingId(bookingId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Negotiation> findPendingByBookingId(UUID bookingId) {
        return jpaRepository.findByBookingIdAndStatus(bookingId, NegotiationStatus.PENDING)
                .map(this::toDomain);
    }

    private NegotiationEntity toEntity(Negotiation negotiation) {
        NegotiationEntity entity = new NegotiationEntity();
        entity.setId(negotiation.getId());
        entity.setBookingId(negotiation.getBookingId());
        entity.setSenderId(negotiation.getSenderId());
        entity.setReceiverId(negotiation.getReceiverId());
        entity.setAmount(negotiation.getAmount());
        entity.setStatus(negotiation.getStatus());
        entity.setSentAt(negotiation.getSentAt());
        entity.setCreatedAt(negotiation.getCreatedAt());
        return entity;
    }

    private Negotiation toDomain(NegotiationEntity entity) {
        return Negotiation.reconstitute(
                entity.getId(),
                entity.getBookingId(),
                entity.getSenderId(),
                entity.getReceiverId(),
                entity.getAmount(),
                entity.getStatus(),
                entity.getSentAt(),
                entity.getCreatedAt()
        );
    }
}