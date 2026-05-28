package com.agilis.api.infrastructure.persistence.message;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface MessageJpaRepository extends JpaRepository<MessageEntity, UUID> {

    List<MessageEntity> findAllByBookingIdOrderBySentAtAsc(UUID bookingId);
}