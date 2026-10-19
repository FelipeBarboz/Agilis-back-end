package com.agilis.api.domain.client;

import java.util.Optional;
import java.util.UUID;

public interface PriorityRebookingRepository {

    PriorityRebooking save(PriorityRebooking priority);
    Optional<PriorityRebooking> findValidByClientAndService(UUID clientId, UUID serviceId);
}