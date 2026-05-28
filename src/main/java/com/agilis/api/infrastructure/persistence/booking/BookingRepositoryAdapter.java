package com.agilis.api.infrastructure.persistence.booking;

import com.agilis.api.domain.booking.Booking;
import com.agilis.api.domain.booking.BookingRepository;
import com.agilis.api.domain.booking.BookingStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BookingRepositoryAdapter implements BookingRepository {

    private final BookingJpaRepository jpaRepository;

    public BookingRepositoryAdapter(BookingJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Booking save(Booking booking) {
        jpaRepository.save(toEntity(booking));
        return booking;
    }

    @Override
    public Optional<Booking> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Booking> findAllByClientId(UUID clientId) {
        return jpaRepository.findAllByClientId(clientId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Booking> findAllByServiceId(UUID serviceId) {
        return jpaRepository.findAllByServiceId(serviceId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Booking> findAllByServiceIdAndDate(UUID serviceId, LocalDate date) {
        return jpaRepository.findAllByServiceId(serviceId)
                .stream()
                .filter(b -> b.getDate().equals(date))
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsConflict(UUID serviceId, LocalDateTime scheduledAt) {
        return jpaRepository.existsConflict(
                serviceId,
                scheduledAt,
                List.of(BookingStatus.CANCELLED)
        );
    }

    private BookingEntity toEntity(Booking booking) {
        BookingEntity entity = new BookingEntity();
        entity.setId(booking.getId());
        entity.setClientId(booking.getClientId());
        entity.setServiceId(booking.getServiceId());
        entity.setScheduledAt(booking.getScheduledAt());
        entity.setStatus(booking.getStatus());
        entity.setCreatedAt(booking.getCreatedAt());
        return entity;
    }

    private Booking toDomain(BookingEntity entity) {
        return Booking.reconstitute(
                entity.getId(),
                entity.getClientId(),
                entity.getServiceId(),
                entity.getScheduledAt(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}