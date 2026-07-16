package com.agilis.api.application.favorite;

import com.agilis.api.domain.favorite.Favorite;
import com.agilis.api.domain.favorite.FavoriteRepository;
import com.agilis.api.domain.service.ServiceRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public class AddFavoriteUseCase {

    private final FavoriteRepository favoriteRepository;
    private final ServiceRepository serviceRepository;

    public AddFavoriteUseCase(FavoriteRepository favoriteRepository, ServiceRepository serviceRepository) {
        this.favoriteRepository = favoriteRepository;
        this.serviceRepository  = serviceRepository;
    }

    public Output execute(Input input) {
        UUID userId    = UUID.fromString(input.userId());
        UUID serviceId = UUID.fromString(input.serviceId());

        serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));

        if (favoriteRepository.existsByUserIdAndServiceId(userId, serviceId)) {
            throw new IllegalStateException("Serviço já favoritado");
        }

        Favorite favorite = Favorite.create(userId, serviceId);
        favoriteRepository.save(favorite);

        return new Output(
                favorite.getId().toString(),
                favorite.getUserId().toString(),
                favorite.getServiceId().toString(),
                favorite.getCreatedAt()
        );
    }

    @Schema(name = "AddFavoriteInput")
    public record Input(String userId, String serviceId) {}

    @Schema(name = "AddFavoriteOutput")
    public record Output(String favoriteId, String userId, String serviceId, LocalDateTime createdAt) {}
}