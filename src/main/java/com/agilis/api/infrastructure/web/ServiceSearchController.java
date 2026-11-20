package com.agilis.api.infrastructure.web;

import com.agilis.api.application.service.SearchServicesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Tag(name = "Busca de Serviços", description = "Busca e filtragem de serviços disponíveis")
@RestController
@RequestMapping("/api/v1/services")
public class ServiceSearchController {

    private final SearchServicesUseCase searchServicesUseCase;

    public ServiceSearchController(SearchServicesUseCase searchServicesUseCase) {
        this.searchServicesUseCase = searchServicesUseCase;
    }

    @GetMapping("/search")
    @Operation(summary = "Pesquisa e filtra serviços por cidade, estado, preço, categoria ou nota")
    public ResponseEntity<SearchServicesUseCase.Output> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var input = new SearchServicesUseCase.Input(city, state, minPrice, maxPrice, category, minRating, page, size);
        return ResponseEntity.ok(searchServicesUseCase.execute(input));
    }
}