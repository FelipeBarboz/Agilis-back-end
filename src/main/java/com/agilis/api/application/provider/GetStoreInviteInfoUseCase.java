package com.agilis.api.application.provider;

import com.agilis.api.domain.provider.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class GetStoreInviteInfoUseCase {

    private final StoreInviteRepository storeInviteRepository;
    private final ProviderProfileRepository providerProfileRepository;

    public GetStoreInviteInfoUseCase(
            StoreInviteRepository storeInviteRepository,
            ProviderProfileRepository providerProfileRepository
    ) {
        this.storeInviteRepository      = storeInviteRepository;
        this.providerProfileRepository = providerProfileRepository;
    }

    public Output execute(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Invite code cannot be empty.");
        }

        StoreInvite invite = storeInviteRepository.findByCode(code.trim())
                .orElseThrow(() -> new IllegalArgumentException("Convite não encontrado."));

        if (!invite.isValid()) {
            throw new IllegalStateException("Este convite expirou ou não está mais ativo.");
        }

        ProviderProfile store = providerProfileRepository.findById(invite.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("Loja não encontrada."));

        return new Output(
                invite.getCode(),
                store.getId().toString(),
                store.getStoreName(),
                store.getSlug(),
                store.getProfileImgUrl(),
                store.getDescription(),
                invite.getRole().name(),
                invite.getExpiresAt()
        );
    }

    @Schema(name = "GetStoreInviteInfoOutput")
    public record Output(
            String code,
            String storeId,
            String storeName,
            String storeSlug,
            String storeProfileImgUrl,
            String storeDescription,
            String role,
            LocalDateTime expiresAt
    ) {}
}
