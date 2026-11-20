package com.agilis.api.infrastructure.web;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health", description = "Endpoint de verificação de disponibilidade da API")
@RestController
public class PingController {

    @GetMapping({"/ping", "/api/v1/ping"})
    @Operation(summary = "Verifica se a API está online e respondendo")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}
