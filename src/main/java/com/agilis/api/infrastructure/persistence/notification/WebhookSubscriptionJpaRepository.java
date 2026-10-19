package com.agilis.api.infrastructure.persistence.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface WebhookSubscriptionJpaRepository extends JpaRepository<WebhookSubscriptionEntity, UUID> {

    List<WebhookSubscriptionEntity> findAllByStoreId(UUID storeId);
}