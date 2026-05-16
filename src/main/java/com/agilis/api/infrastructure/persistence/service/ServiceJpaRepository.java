package com.agilis.api.infrastructure.persistence.service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServiceJpaRepository extends JpaRepository<ServiceEntity, UUID> {

}
