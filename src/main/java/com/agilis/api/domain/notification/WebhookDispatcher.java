package com.agilis.api.domain.notification;

import java.util.Map;
import java.util.UUID;

public interface WebhookDispatcher {

    void dispatch(UUID storeId, WebhookEventType eventType, Map<String, Object> payload);
}