package com.agilis.api.application.notification;

import com.agilis.api.domain.notification.WebhookSubscription;
import com.agilis.api.domain.notification.WebhookSubscriptionRepository;
import com.agilis.api.domain.provider.StoreMembershipRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ListWebhooksUseCase {

    private final WebhookSubscriptionRepository webhookSubscriptionRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public ListWebhooksUseCase(
            WebhookSubscriptionRepository webhookSubscriptionRepository,
            StoreMembershipRepository storeMembershipRepository
    ) {
        this.webhookSubscriptionRepository = webhookSubscriptionRepository;
        this.storeMembershipRepository     = storeMembershipRepository;
    }

    public List<Output> execute(String requesterId, String storeId) {
        UUID requester = UUID.fromString(requesterId);
        UUID store     = UUID.fromString(storeId);

        storeMembershipRepository
                .findByProviderIdAndStoreId(requester, store)
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store"));

        return webhookSubscriptionRepository.findAllByStoreId(store)
                .stream()
                .map(sub -> new Output(
                        sub.getId().toString(),
                        sub.getStoreId().toString(),
                        sub.getUrl(),
                        sub.getEvents().stream().map(Enum::name).collect(Collectors.toSet()),
                        sub.isActive()
                ))
                .toList();
    }

    public record Output(String webhookId, String storeId, String url, java.util.Set<String> events, boolean active) {}
}