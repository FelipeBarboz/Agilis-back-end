package com.agilis.api.application.service;

import com.agilis.api.application.booking.FindNextAvailableSlotUseCase;
import com.agilis.api.infrastructure.persistence.service.ServiceJpaRepository;
import com.agilis.api.infrastructure.persistence.service.ServiceSearchProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;

public class GetHomeHighlightsUseCase {

    private final ServiceJpaRepository serviceJpaRepository;
    private final FindNextAvailableSlotUseCase findNextAvailableSlotUseCase;

    public GetHomeHighlightsUseCase(ServiceJpaRepository serviceJpaRepository, FindNextAvailableSlotUseCase findNextAvailableSlotUseCase) {
        this.serviceJpaRepository         = serviceJpaRepository;
        this.findNextAvailableSlotUseCase = findNextAvailableSlotUseCase;
    }

    public Output execute(Section section, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ServiceSearchProjection> result = switch (section) {
            case MOST_VISITED -> serviceJpaRepository.findMostVisited(pageable);
            case TOP_RATED    -> serviceJpaRepository.findTopRated(pageable);
            case MOST_HIRED    -> serviceJpaRepository.findMostHired(pageable);
        };

        List<SearchServicesUseCase.Item> items = result.getContent().stream().map(this::toItem).toList();

        return new Output(items, result.getTotalPages());
    }

    private SearchServicesUseCase.Item toItem(ServiceSearchProjection p) {
        String nextSlot = findNextAvailableSlotUseCase.execute(p.getId().toString())
                .map(dt -> dt.toString())
                .orElse(null);

        return new SearchServicesUseCase.Item(
                p.getId().toString(), p.getStoreId().toString(),
                p.getUnitId() != null ? p.getUnitId().toString() : null,
                p.getTitle(), p.getDescription(), p.getPrice(), p.getPriceType(), p.getDurationMinutes(),
                p.getCategory(), Boolean.TRUE.equals(p.getHasPriceTiers()),
                Math.round(p.getAvgRating() * 10.0) / 10.0, p.getReviewCount(),
                p.getCity(), p.getState(), p.getThumbnailUrl(),
                p.getStoreName(), p.getStoreProfileImgUrl(),
                nextSlot
        );
    }

    public enum Section { MOST_VISITED, TOP_RATED, MOST_HIRED }

    public record Output(List<SearchServicesUseCase.Item> items, int totalPages) {}
}