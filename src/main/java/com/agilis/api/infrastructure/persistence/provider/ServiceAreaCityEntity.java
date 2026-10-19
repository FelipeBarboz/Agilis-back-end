package com.agilis.api.infrastructure.persistence.provider;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "store_service_area_cities")
public class ServiceAreaCityEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "store_service_area_id", nullable = false)
    private UUID storeServiceAreaId;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 2)
    private String state;
}