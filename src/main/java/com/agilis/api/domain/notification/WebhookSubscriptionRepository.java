package com.agilis.api.domain.notification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebhookSubscriptionRepository {

    WebhookSubscription save(WebhookSubscription subscription);
    Optional<WebhookSubscription> findById(UUID id);
    List<WebhookSubscription> findAllByStoreId(UUID storeId);
    void deleteById(UUID id);
}
