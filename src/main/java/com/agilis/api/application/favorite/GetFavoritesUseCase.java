package com.agilis.api.application.favorite;

import com.agilis.api.domain.favorite.FavoriteRepository;
import com.agilis.api.domain.service.Service;
import com.agilis.api.domain.service.ServiceRepository;

import java.util.List;
import java.util.UUID;

public class GetFavoritesUseCase {

    private final FavoriteRepository favoriteRepository;
    private final ServiceRepository serviceRepository;

    public GetFavoritesUseCase(FavoriteRepository favoriteRepository, ServiceRepository serviceRepository) {
        this.favoriteRepository = favoriteRepository;
        this.serviceRepository  = serviceRepository;
    }

    public List<Service> execute(String userId) {
        return favoriteRepository.findAllByUserId(UUID.fromString(userId))
                .stream()
                .map(favorite -> serviceRepository.findById(favorite.getServiceId()))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .toList();
    }
}