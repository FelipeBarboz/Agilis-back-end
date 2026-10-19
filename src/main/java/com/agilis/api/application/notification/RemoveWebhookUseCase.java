package com.agilis.api.application.notification;

import com.agilis.api.domain.notification.WebhookSubscription;
import com.agilis.api.domain.notification.WebhookSubscriptionRepository;
import com.agilis.api.domain.provider.StoreMembershipRepository;

import java.util.UUID;

public class RemoveWebhookUseCase {

    private final WebhookSubscriptionRepository webhookSubscriptionRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public RemoveWebhookUseCase(
            WebhookSubscriptionRepository webhookSubscriptionRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.webhookSubscriptionRepository = webhookSubscriptionRepository;
        this.storeMembershipRepository     = storeMembershipRepository;
    }

    public void execute(String requesterId, String webhookId) {
        UUID requester = UUID.fromString(requesterId);
        UUID id        = UUID.fromString(webhookId);

        WebhookSubscription subscription = webhookSubscriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Webhook not found"));

        var membership = storeMembershipRepository
                .findByProviderIdAndStoreId(requester, subscription.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store"));

        if (!membership.getRole().canManageStore()) {
            throw new IllegalStateException("Insufficient permissions to remove webhooks");
        }

        webhookSubscriptionRepository.deleteById(id);
    }
}