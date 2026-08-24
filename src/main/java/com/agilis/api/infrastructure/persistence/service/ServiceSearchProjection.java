package com.agilis.api.infrastructure.persistence.service;

import java.math.BigDecimal;
import java.util.UUID;

public interface ServiceSearchProjection {

    UUID getId();
    UUID getStoreId();
    UUID getUnitId();
    String getTitle();
    String getDescription();
    BigDecimal getPrice();
    String getPriceType();
    Integer getDurationMinutes();
    String getCategory();
    Double getAvgRating();
    Long getReviewCount();
    String getCity();
    String getState();
    String getThumbnailUrl();
}