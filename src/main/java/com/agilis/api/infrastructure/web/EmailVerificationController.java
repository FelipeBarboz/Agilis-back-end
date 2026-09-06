package com.agilis.api.infrastructure.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class EmailVerificationController {

    private final RestTemplate restTemplate;

    @Value("${supabase.project.url}")
    private String supabaseUrl;

    @Value("${supabase.anon.key}")
    private String anonKey;

    public EmailVerificationController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody VerifyEmailRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", anonKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
                "type", "signup",
                "email", request.email(),
                "token", request.token()
        );

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    supabaseUrl + "/auth/v1/verify",
                    entity,
                    String.class
            );
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Código inválido ou expirado"));
        }
    }

    public record VerifyEmailRequest(String email, String token) {}
}