package com.agilis.api.infrastructure.persistence.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookingJpaRepository extends JpaRepository<BookingEntity, UUID> {

}
