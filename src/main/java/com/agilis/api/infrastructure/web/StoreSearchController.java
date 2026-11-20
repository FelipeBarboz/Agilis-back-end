package com.agilis.api.infrastructure.web;

import com.agilis.api.application.provider.SearchStoresUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Busca de Lojas", description = "Busca e filtragem de lojas disponíveis")
@RestController
@RequestMapping("/api/v1/stores")
public class StoreSearchController {

    private final SearchStoresUseCase searchStoresUseCase;

    public StoreSearchController(SearchStoresUseCase searchStoresUseCase) {
        this.searchStoresUseCase = searchStoresUseCase;
    }

    @GetMapping("/search")
    @Operation(summary = "Pesquisa lojas por cidade, estado ou categoria")
    public ResponseEntity<List<SearchStoresUseCase.Output>> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String category
    ) {
        return ResponseEntity.ok(searchStoresUseCase.execute(new SearchStoresUseCase.Input(city, state, category)));
    }
}