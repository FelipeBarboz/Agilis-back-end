package com.agilis.api.infrastructure.persistence.support;

import com.agilis.api.domain.support.SupportMessage;
import com.agilis.api.domain.support.SupportMessageRepository;

public class SupportMessageRepositoryAdapter implements SupportMessageRepository {

    private final SupportMessageJpaRepository jpaRepository;

    public SupportMessageRepositoryAdapter(SupportMessageJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public SupportMessage save(SupportMessage message) {
        SupportMessageEntity entity = new SupportMessageEntity();
        entity.setId(message.getId());
        entity.setUserId(message.getUserId());
        entity.setName(message.getName());
        entity.setEmail(message.getEmail());
        entity.setSubject(message.getSubject());
        entity.setMessage(message.getMessage());
        entity.setCreatedAt(message.getCreatedAt());
        jpaRepository.save(entity);
        return message;
    }
}