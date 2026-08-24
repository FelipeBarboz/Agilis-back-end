package com.agilis.api.application.service;

import com.agilis.api.infrastructure.persistence.service.ServiceJpaRepository;
import com.agilis.api.infrastructure.persistence.service.ServiceSearchProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;

public class SearchServicesUseCase {

    private final ServiceJpaRepository serviceJpaRepository;

    public SearchServicesUseCase(ServiceJpaRepository serviceJpaRepository) {
        this.serviceJpaRepository = serviceJpaRepository;
    }

    public Output execute(Input input) {
        var page = serviceJpaRepository.search(
                blankToNull(input.city()),
                blankToNull(input.state()),
                input.minPrice(),
                input.maxPrice(),
                input.category() != null && !input.category().equalsIgnoreCase("TODOS") ? input.category().toUpperCase() : null,
                input.minRating(),
                PageRequest.of(input.page(), input.size())
        );

        List<Item> items = page.getContent().stream().map(this::toItem).toList();

        return new Output(items, page.getTotalElements(), page.getTotalPages());
    }

    private Item toItem(ServiceSearchProjection p) {
        return new Item(
                p.getId().toString(),
                p.getStoreId().toString(),
                p.getUnitId() != null ? p.getUnitId().toString() : null,
                p.getTitle(),
                p.getDescription(),
                p.getPrice(),
                p.getPriceType(),
                p.getDurationMinutes(),
                p.getCategory(),
                Math.round(p.getAvgRating() * 10.0) / 10.0, // arredonda pra 1 casa decimal
                p.getReviewCount(),
                p.getCity(),
                p.getState(),
                p.getThumbnailUrl()
        );
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    @Schema(name = "SearchServicesInput")
    public record Input(
            String city, String state, BigDecimal minPrice, BigDecimal maxPrice,
            String category, Double minRating, int page, int size
    ) {}

    @Schema(name = "SearchServicesOutput")
    public record Item(
            String serviceId, String storeId, String unitId, String title, String description,
            BigDecimal price, String priceType, Integer durationMinutes, String category,
            double avgRating, long reviewCount, String city, String state, String thumbnailUrl
    ) {}

    public record Output(List<Item> items, long totalElements, int totalPages) {}
}