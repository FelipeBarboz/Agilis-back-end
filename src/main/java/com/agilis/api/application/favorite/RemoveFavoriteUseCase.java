package com.agilis.api.application.favorite;

import com.agilis.api.domain.favorite.FavoriteRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public class RemoveFavoriteUseCase {

    private final FavoriteRepository favoriteRepository;

    public RemoveFavoriteUseCase(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    public void execute(Input input) {
        UUID userId    = UUID.fromString(input.userId());
        UUID serviceId = UUID.fromString(input.serviceId());

        if (!favoriteRepository.existsByUserIdAndServiceId(userId, serviceId)) {
            throw new IllegalArgumentException("Serviço não está favoritado");
        }

        favoriteRepository.deleteByUserIdAndServiceId(userId, serviceId);
    }

    @Schema(name= "RemoveFavoriteInput")
    public record Input(String userId, String serviceId) {}
}