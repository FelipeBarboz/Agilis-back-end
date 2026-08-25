package com.agilis.api.infrastructure.persistence.provider;

import java.util.UUID;

public interface StoreSearchProjection {

    UUID getId();
    String getStoreName();
    String getSlug();
    String getProfileImgUrl();
    String getDescription();
    String getPrimaryCategory();
    Double getAvgRating();
    Long getReviewCount();
    String getCity();
    String getState();
    Boolean getIsOpenNow();
}