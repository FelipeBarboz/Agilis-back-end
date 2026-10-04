package com.agilis.api.domain.coupon;

import java.util.UUID;

public interface PlatformAdminRepository {

    boolean existsByUserId(UUID userId);
}