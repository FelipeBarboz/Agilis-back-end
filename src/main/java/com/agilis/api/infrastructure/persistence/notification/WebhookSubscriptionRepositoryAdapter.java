package com.agilis.api.infrastructure.persistence.notification;

import com.agilis.api.domain.notification.WebhookEventType;
import com.agilis.api.domain.notification.WebhookSubscription;
import com.agilis.api.domain.notification.WebhookSubscriptionRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class WebhookSubscriptionRepositoryAdapter implements WebhookSubscriptionRepository {

    private final WebhookSubscriptionJpaRepository jpaRepository;

    public WebhookSubscriptionRepositoryAdapter(WebhookSubscriptionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public WebhookSubscription save(WebhookSubscription subscription) {
        jpaRepository.save(toEntity(subscription));
        return subscription;
    }

    @Override
    public Optional<WebhookSubscription> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<WebhookSubscription> findAllByStoreId(UUID storeId) {
        return jpaRepository.findAllByStoreId(storeId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private WebhookSubscriptionEntity toEntity(WebhookSubscription subscription) {
        WebhookSubscriptionEntity entity = new WebhookSubscriptionEntity();
        entity.setId(subscription.getId());
        entity.setStoreId(subscription.getStoreId());
        entity.setUrl(subscription.getUrl());
        entity.setSecret(subscription.getSecret());
        entity.setEvents(subscription.getEvents().stream().map(Enum::name).collect(Collectors.joining(",")));
        entity.setActive(subscription.isActive());
        entity.setCreatedAt(subscription.getCreatedAt());
        return entity;
    }

    private WebhookSubscription toDomain(WebhookSubscriptionEntity entity) {
        Set<WebhookEventType> events = Arrays.stream(entity.getEvents().split(","))
                .map(WebhookEventType::valueOf)
                .collect(Collectors.toSet());

        return WebhookSubscription.reconstitute(
                entity.getId(),
                entity.getStoreId(),
                entity.getUrl(),
                entity.getSecret(),
                events,
                entity.isActive(),
                entity.getCreatedAt()
        );
    }
}