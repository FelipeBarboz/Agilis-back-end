package com.agilis.api.infrastructure.persistence.message;

import com.agilis.api.domain.message.Message;
import com.agilis.api.domain.message.MessageRepository;
import java.util.List;
import java.util.UUID;

public class MessageRepositoryAdapter implements MessageRepository {

    private final MessageJpaRepository jpaRepository;

    public MessageRepositoryAdapter(MessageJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Message save(Message message) {
        jpaRepository.save(toEntity(message));
        return message;
    }

    @Override
    public List<Message> findAllByBookingId(UUID bookingId) {
        return jpaRepository.findAllByBookingIdOrderBySentAtAsc(bookingId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private MessageEntity toEntity(Message message) {
        MessageEntity entity = new MessageEntity();
        entity.setId(message.getId());
        entity.setBookingId(message.getBookingId());
        entity.setSenderId(message.getSenderId());
        entity.setReceiverId(message.getReceiverId());
        entity.setContent(message.getContent());
        entity.setSentAt(message.getSentAt());
        return entity;
    }

    private Message toDomain(MessageEntity entity) {
        return Message.reconstitute(
                entity.getId(),
                entity.getBookingId(),
                entity.getSenderId(),
                entity.getReceiverId(),
                entity.getContent(),
                entity.getSentAt()
        );
    }
}