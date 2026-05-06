package com.agilis.api.domain.message;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Message {

    private final UUID id;
    private final UUID bookingId;
    private final UUID senderId;
    private final UUID receiverId;
    private final String content;
    private final LocalDateTime sentAt;

    private Message(UUID id, UUID bookingId, UUID senderId, UUID receiverId, String content, LocalDateTime sentAt) {
        this.id         = id;
        this.bookingId  = bookingId;
        this.senderId   = senderId;
        this.receiverId = receiverId;
        this.content    = validateContent(content);
        this.sentAt     = sentAt;
    }

    public static Message create(UUID bookingId, UUID senderId, UUID receiverId, String content) {
        return new Message(
                UUID.randomUUID(),
                bookingId,
                senderId,
                receiverId,
                content,
                LocalDateTime.now()
        );
    }

    public static Message reconstitute(UUID id, UUID bookingId, UUID senderId, UUID receiverId, String content, LocalDateTime sentAt) {
        return new Message(id, bookingId, senderId, receiverId, content, sentAt);
    }

    private String validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message cannot be empty.");
        }
        if (content.length() > 2000) {
            throw new IllegalArgumentException("Message must not exceed 2000 characters.");
        }
        return content;
    }
}