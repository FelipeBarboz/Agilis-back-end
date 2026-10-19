package com.agilis.api.infrastructure.notification;

import com.agilis.api.domain.notification.WebhookDispatcher;
import com.agilis.api.domain.notification.WebhookEventType;
import com.agilis.api.domain.notification.WebhookSubscription;
import com.agilis.api.domain.notification.WebhookSubscriptionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class WebhookDispatcherAdapter implements WebhookDispatcher {

    private final WebhookSubscriptionRepository webhookSubscriptionRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WebhookDispatcherAdapter(
            WebhookSubscriptionRepository webhookSubscriptionRepository,
            RestTemplate restTemplate,
            ObjectMapper objectMapper
    ) {
        this.webhookSubscriptionRepository = webhookSubscriptionRepository;
        this.restTemplate                  = restTemplate;
        this.objectMapper                  = objectMapper;
    }

    @Override
    public void dispatch(UUID storeId, WebhookEventType eventType, Map<String, Object> payload) {
        var subscriptions = webhookSubscriptionRepository.findAllByStoreId(storeId)
                .stream()
                .filter(sub -> sub.subscribesTo(eventType))
                .toList();

        if (subscriptions.isEmpty()) return;

        Map<String, Object> body = Map.of(
                "event", eventType.name().toLowerCase().replace('_', '.'),
                "timestamp", Instant.now().toString(),
                "data", payload
        );

        for (WebhookSubscription subscription : subscriptions) {
            CompletableFuture.runAsync(() -> send(subscription, body));
        }
    }

    private void send(WebhookSubscription subscription, Map<String, Object> body) {
        try {
            String json = objectMapper.writeValueAsString(body);
            String signature = sign(json, subscription.getSecret());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("X-Agilis-Signature", signature);
            headers.set("X-Agilis-Event", body.get("event").toString());

            HttpEntity<String> request = new HttpEntity<>(json, headers);
            restTemplate.postForEntity(subscription.getUrl(), request, String.class);
        } catch (Exception e) {
            System.out.println("Failed to send webhook to " + subscription.getUrl() + ": " + e.getMessage());
        }
    }

    private String sign(String payload, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return "sha256=" + HexFormat.of().formatHex(hash);
    }
}