package com.agilis.api.application.provider;

import com.agilis.api.infrastructure.persistence.provider.StoreSearchProjection;
import com.agilis.api.infrastructure.persistence.provider.ProviderProfileJpaRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

public class SearchStoresUseCase {

    private final ProviderProfileJpaRepository providerProfileJpaRepository;

    public SearchStoresUseCase(ProviderProfileJpaRepository providerProfileJpaRepository) {
        this.providerProfileJpaRepository = providerProfileJpaRepository;
    }

    public List<Output> execute(Input input) {
        LocalDateTime now = LocalDateTime.now();
        int dayOfWeek = now.getDayOfWeek() == DayOfWeek.SUNDAY ? 0 : now.getDayOfWeek().getValue();

        List<StoreSearchProjection> results = providerProfileJpaRepository.search(
                blankToNull(input.city()),
                blankToNull(input.state()),
                blankToNull(input.category()),
                dayOfWeek,
                now.toLocalTime()
        );

        return results.stream().map(this::toOutput).toList();
    }

    private Output toOutput(StoreSearchProjection p) {
        return new Output(
                p.getId().toString(),
                p.getStoreName(),
                p.getSlug(),
                p.getProfileImgUrl(),
                p.getDescription(),
                p.getPrimaryCategory(),
                Math.round(p.getAvgRating() * 10.0) / 10.0,
                p.getReviewCount(),
                p.getCity(),
                p.getState(),
                Boolean.TRUE.equals(p.getIsOpenNow())
        );
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    public record Input(String city, String state, String category) {}

    @Schema(name = "")
    public record Output(
            String storeId,
            String storeName,
            String slug,
            String profileImgUrl,
            String description,
            String primaryCategory,
            double avgRating,
            long reviewCount,
            String city,
            String state,
            boolean isOpenNow
    ) {}
}