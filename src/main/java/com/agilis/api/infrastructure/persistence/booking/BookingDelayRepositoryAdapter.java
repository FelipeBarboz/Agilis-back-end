package com.agilis.api.infrastructure.persistence.booking;

import com.agilis.api.domain.booking.BookingDelay;
import com.agilis.api.domain.booking.BookingDelayRepository;
import com.agilis.api.domain.booking.DelayResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BookingDelayRepositoryAdapter implements BookingDelayRepository {

    private final BookingDelayJpaRepository jpaRepository;

    public BookingDelayRepositoryAdapter(BookingDelayJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public BookingDelay save(BookingDelay delay) {
        jpaRepository.save(toEntity(delay));
        return delay;
    }

    @Override
    public Optional<BookingDelay> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<BookingDelay> findAllPendingByClientId(UUID clientId) {
        return jpaRepository.findAllPendingByClientId(clientId, DelayResponse.PENDING)
                .stream().map(this::toDomain).toList();
    }

    private BookingDelayEntity toEntity(BookingDelay delay) {
        BookingDelayEntity entity = new BookingDelayEntity();
        entity.setId(delay.getId());
        entity.setBookingId(delay.getBookingId());
        entity.setOriginalScheduledAt(delay.getOriginalScheduledAt());
        entity.setNewScheduledAt(delay.getNewScheduledAt());
        entity.setDelayMinutes(delay.getDelayMinutes());
        entity.setReason(delay.getReason());
        entity.setResponse(delay.getResponse());
        entity.setRespondedAt(delay.getRespondedAt());
        entity.setCreatedAt(delay.getCreatedAt());
        return entity;
    }

    private BookingDelay toDomain(BookingDelayEntity entity) {
        return BookingDelay.reconstitute(
                entity.getId(),
                entity.getBookingId(),
                entity.getOriginalScheduledAt(),
                entity.getNewScheduledAt(),
                entity.getDelayMinutes(),
                entity.getReason(),
                entity.getResponse(),
                entity.getRespondedAt(),
                entity.getCreatedAt()
        );
    }
}