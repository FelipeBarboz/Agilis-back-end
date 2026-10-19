package com.agilis.api.infrastructure.web;

import com.agilis.api.application.service.GetHomeHighlightsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/services")
public class HomeHighlightsController {

    private final GetHomeHighlightsUseCase getHomeHighlightsUseCase;

    public HomeHighlightsController(GetHomeHighlightsUseCase getHomeHighlightsUseCase) {
        this.getHomeHighlightsUseCase = getHomeHighlightsUseCase;
    }

    @GetMapping("/trending")
    public ResponseEntity<GetHomeHighlightsUseCase.Output> trending(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(getHomeHighlightsUseCase.execute(GetHomeHighlightsUseCase.Section.MOST_VISITED, page, size));
    }

    @GetMapping("/top-rated")
    public ResponseEntity<GetHomeHighlightsUseCase.Output> topRated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(getHomeHighlightsUseCase.execute(GetHomeHighlightsUseCase.Section.TOP_RATED, page, size));
    }

    @GetMapping("/most-hired")
    public ResponseEntity<GetHomeHighlightsUseCase.Output> mostHired(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(getHomeHighlightsUseCase.execute(GetHomeHighlightsUseCase.Section.MOST_HIRED, page, size));
    }
}