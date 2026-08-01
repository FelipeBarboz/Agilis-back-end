package com.agilis.api.application.notification;

import com.agilis.api.domain.notification.WebhookEventType;
import com.agilis.api.domain.notification.WebhookSubscription;
import com.agilis.api.domain.notification.WebhookSubscriptionRepository;
import com.agilis.api.domain.provider.StoreMembershipRepository;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RegisterWebhookUseCase {

    private final WebhookSubscriptionRepository webhookSubscriptionRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public RegisterWebhookUseCase(
            WebhookSubscriptionRepository webhookSubscriptionRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.webhookSubscriptionRepository = webhookSubscriptionRepository;
        this.storeMembershipRepository     = storeMembershipRepository;
    }

    public Output execute(Input input) {
        UUID requesterId = UUID.fromString(input.requesterId());
        UUID storeId     = UUID.fromString(input.storeId());

        var membership = storeMembershipRepository
                .findByProviderIdAndStoreId(requesterId, storeId)
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store"));

        if (!membership.getRole().canManageStore()) {
            throw new IllegalStateException("Insufficient permissions to configure webhooks");
        }

        Set<WebhookEventType> events = input.events().stream()
                .map(WebhookEventType::valueOf)
                .collect(Collectors.toSet());

        WebhookSubscription subscription = WebhookSubscription.create(storeId, input.url(), events);
        webhookSubscriptionRepository.save(subscription);

        return toOutput(subscription);
    }

    private Output toOutput(WebhookSubscription subscription) {
        return new Output(
                subscription.getId().toString(),
                subscription.getStoreId().toString(),
                subscription.getUrl(),
                subscription.getSecret(),
                subscription.getEvents().stream().map(Enum::name).collect(Collectors.toSet()),
                subscription.isActive()
        );
    }

    public record Input(String requesterId, String storeId, String url, Set<String> events) {}

    public record Output(String webhookId, String storeId, String url, String secret, Set<String> events, boolean active) {}
}