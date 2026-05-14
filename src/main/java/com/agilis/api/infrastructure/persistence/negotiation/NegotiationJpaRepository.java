package com.agilis.api.infrastructure.persistence.negotiation;

import com.agilis.api.domain.negotiation.NegotiationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NegotiationJpaRepository extends JpaRepository<NegotiationEntity, UUID> {

    List<NegotiationEntity> findAllByBookingId(UUID bookingId);
    Optional<NegotiationEntity> findByBookingIdAndStatus(UUID bookingId, NegotiationStatus status);
}