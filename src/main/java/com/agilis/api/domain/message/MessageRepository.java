package com.agilis.api.domain.message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {

    Message save(Message message);
    List<Message> findAllByBookingId(UUID bookingId);
}