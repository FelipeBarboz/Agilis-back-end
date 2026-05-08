package com.agilis.api.domain.negotiation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NegotiationRepository {

    Negotiation save(Negotiation negotiation);
    Optional<Negotiation> findById(UUID id);
    List<Negotiation> findAllByBookingId(UUID bookingId);
    Optional<Negotiation> findPendingByBookingId(UUID bookingId);
}