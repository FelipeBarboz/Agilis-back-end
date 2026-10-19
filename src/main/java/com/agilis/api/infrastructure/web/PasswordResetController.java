package com.agilis.api.infrastructure.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class PasswordResetController {

    private final RestTemplate restTemplate;

    @Value("${supabase.project.url}")
    private String supabaseUrl;

    @Value("${supabase.anon.key}")
    private String anonKey;

    public PasswordResetController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", anonKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of("email", request.email());
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(supabaseUrl + "/auth/v1/recover", entity, String.class);
            return ResponseEntity.ok(Map.of("message", "If the email exists, a code has been sent"));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("message", "If the email exists, a code has been sent"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        HttpHeaders verifyHeaders = new HttpHeaders();
        verifyHeaders.set("apikey", anonKey);
        verifyHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> verifyBody = Map.of(
                "type", "recovery",
                "email", request.email(),
                "token", request.token()
        );

        try {
            ResponseEntity<Map> verifyResponse = restTemplate.postForEntity(
                    supabaseUrl + "/auth/v1/verify",
                    new HttpEntity<>(verifyBody, verifyHeaders),
                    Map.class
            );

            String recoveryAccessToken = (String) verifyResponse.getBody().get("access_token");
            if (recoveryAccessToken == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid or expired code"));
            }

            HttpHeaders updateHeaders = new HttpHeaders();
            updateHeaders.set("apikey", anonKey);
            updateHeaders.setBearerAuth(recoveryAccessToken);
            updateHeaders.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> updateBody = Map.of("password", request.newPassword());

            restTemplate.exchange(
                    supabaseUrl + "/auth/v1/user",
                    HttpMethod.PUT,
                    new HttpEntity<>(updateBody, updateHeaders),
                    String.class
            );

            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid or expired code"));
        }
    }

    public record ForgotPasswordRequest(String email) {}
    public record ResetPasswordRequest(String email, String token, String newPassword) {}
}