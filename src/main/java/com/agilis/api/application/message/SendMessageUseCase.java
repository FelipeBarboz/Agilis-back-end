package com.agilis.api.application.message;

import com.agilis.api.domain.booking.Booking;
import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.booking.BookingStatus;
import com.agilis.api.domain.message.Message;
import com.agilis.api.domain.message.MessageRepository;
import com.agilis.api.domain.service.ServiceRepository;
import com.agilis.api.domain.provider.StoreMembershipRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public class SendMessageUseCase {

    private final MessageRepository messageRepository;
    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public SendMessageUseCase(
            MessageRepository messageRepository,
            BookingRepository bookingRepository,
            ServiceRepository serviceRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.messageRepository         = messageRepository;
        this.bookingRepository         = bookingRepository;
        this.serviceRepository         = serviceRepository;
        this.storeMembershipRepository = storeMembershipRepository;
    }

    public Output execute(Input input) {
        UUID bookingId  = UUID.fromString(input.bookingId());
        UUID senderId   = UUID.fromString(input.senderId());
        UUID receiverId = UUID.fromString(input.receiverId());

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        if (booking.getStatus() == BookingStatus.CANCELLED ||
                booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException("It is not possible to send messages in completed bookings.");
        }

        validateParticipant(senderId, booking);
        validateParticipant(receiverId, booking);

        Message message = Message.create(bookingId, senderId, receiverId, input.content());
        messageRepository.save(message);

        return new Output(
                message.getId().toString(),
                message.getBookingId().toString(),
                message.getSenderId().toString(),
                message.getReceiverId().toString(),
                message.getContent(),
                message.getSentAt()
        );
    }

    private void validateParticipant(UUID userId, Booking booking) {
        boolean isClient = booking.getClientId().equals(userId);
        if (isClient) return;

        var service = serviceRepository.findById(booking.getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Service not found."));

        boolean isMember = storeMembershipRepository
                .findByProviderIdAndStoreId(userId, service.getStoreId())
                .isPresent();

        if (!isMember) {
            throw new IllegalStateException("User is not a participant in this booking.");
        }
    }

    public record Input(
            String bookingId,
            String senderId,
            String receiverId,
            String content
    ) {}

    public record Output(
            String messageId,
            String bookingId,
            String senderId,
            String receiverId,
            String content,
            LocalDateTime sentAt
    ) {}
}