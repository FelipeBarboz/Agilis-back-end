package com.agilis.api.application.message;

import com.agilis.api.domain.booking.Booking;
import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.message.Message;
import com.agilis.api.domain.message.MessageRepository;
import com.agilis.api.domain.service.ServiceRepository;
import com.agilis.api.domain.provider.StoreMembershipRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class GetMessagesUseCase {

    private final MessageRepository messageRepository;
    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public GetMessagesUseCase(
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

    public List<Output> execute(Input input) {
        UUID bookingId   = UUID.fromString(input.bookingId());
        UUID requesterId = UUID.fromString(input.requesterId());

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        validateParticipant(requesterId, booking);

        return messageRepository.findAllByBookingId(bookingId)
                .stream()
                .map(this::toOutput)
                .toList();
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
            throw new IllegalStateException("You do not have permission to view the messages for this booking.");
        }
    }

    private Output toOutput(Message message) {
        return new Output(
                message.getId().toString(),
                message.getBookingId().toString(),
                message.getSenderId().toString(),
                message.getReceiverId().toString(),
                message.getContent(),
                message.getSentAt()
        );
    }

    public record Input(String bookingId, String requesterId) {}

    public record Output(
            String messageId,
            String bookingId,
            String senderId,
            String receiverId,
            String content,
            LocalDateTime sentAt
    ) {}
}