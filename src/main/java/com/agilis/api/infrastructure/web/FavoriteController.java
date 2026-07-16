package com.agilis.api.infrastructure.web;

import com.agilis.api.application.favorite.AddFavoriteUseCase;
import com.agilis.api.application.favorite.GetFavoritesUseCase;
import com.agilis.api.application.favorite.RemoveFavoriteUseCase;
import com.agilis.api.domain.service.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorites")
public class FavoriteController {

    private final AddFavoriteUseCase addFavoriteUseCase;
    private final RemoveFavoriteUseCase removeFavoriteUseCase;
    private final GetFavoritesUseCase getFavoritesUseCase;

    public FavoriteController(
            AddFavoriteUseCase addFavoriteUseCase,
            RemoveFavoriteUseCase removeFavoriteUseCase,
            GetFavoritesUseCase getFavoritesUseCase
    ) {
        this.addFavoriteUseCase    = addFavoriteUseCase;
        this.removeFavoriteUseCase = removeFavoriteUseCase;
        this.getFavoritesUseCase   = getFavoritesUseCase;
    }

    @GetMapping
    public ResponseEntity<List<Service>> getMyFavorites() {
        String userId = getCurrentUserId();
        return ResponseEntity.ok(getFavoritesUseCase.execute(userId));
    }

    @PostMapping("/{serviceId}")
    public ResponseEntity<AddFavoriteUseCase.Output> add(@PathVariable String serviceId) {
        String userId = getCurrentUserId();
        return ResponseEntity.ok(addFavoriteUseCase.execute(
                new AddFavoriteUseCase.Input(userId, serviceId)
        ));
    }

    @DeleteMapping("/{serviceId}")
    public ResponseEntity<Void> remove(@PathVariable String serviceId) {
        String userId = getCurrentUserId();
        removeFavoriteUseCase.execute(new RemoveFavoriteUseCase.Input(userId, serviceId));
        return ResponseEntity.noContent().build();
    }

    private String getCurrentUserId() {
        return (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}