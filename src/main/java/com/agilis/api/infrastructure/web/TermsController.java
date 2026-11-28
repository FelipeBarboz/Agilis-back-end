package com.agilis.api.infrastructure.web;

import com.agilis.api.application.user.AcceptTermsUseCase;
import com.agilis.api.application.user.GetTermsStatusUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Termos e Privacidade", description = "Registro e consulta de aceite dos Termos de Uso e Política de Privacidade")
@RestController
@RequestMapping("/api/v1/terms")
public class TermsController {

    private final AcceptTermsUseCase acceptTermsUseCase;
    private final GetTermsStatusUseCase getTermsStatusUseCase;

    public TermsController(
            AcceptTermsUseCase acceptTermsUseCase,
            GetTermsStatusUseCase getTermsStatusUseCase
    ) {
        this.acceptTermsUseCase = acceptTermsUseCase;
        this.getTermsStatusUseCase = getTermsStatusUseCase;
    }

    @PostMapping("/accept")
    @Operation(summary = "Registra o aceite dos Termos de Uso e Política de Privacidade pelo usuário autenticado")
    public ResponseEntity<AcceptTermsUseCase.Output> accept(
            @RequestBody AcceptTermsRequest request,
            HttpServletRequest httpRequest
    ) {
        String userId = getCurrentUserId();
        String ipAddress = extractClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        AcceptTermsUseCase.Input input = new AcceptTermsUseCase.Input(
                userId,
                request.termsAccepted(),
                request.privacyAccepted(),
                request.termsVersion(),
                request.privacyVersion(),
                ipAddress,
                userAgent
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(acceptTermsUseCase.execute(input));
    }

    @GetMapping("/status")
    @Operation(summary = "Verifica se o usuário autenticado já aceitou os Termos de Uso e Política de Privacidade")
    public ResponseEntity<GetTermsStatusUseCase.Output> status() {
        String userId = getCurrentUserId();
        return ResponseEntity.ok(getTermsStatusUseCase.execute(userId));
    }

    private String getCurrentUserId() {
        return (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        String ip = (forwardedFor != null && !forwardedFor.isBlank())
                ? forwardedFor.split(",")[0].trim()
                : request.getRemoteAddr();
        if (ip != null && ip.length() > 45) {
            return ip.substring(0, 45);
        }
        return ip;
    }

    @Schema(name = "AcceptTermsRequest")
    public record AcceptTermsRequest(
            Boolean termsAccepted,
            Boolean privacyAccepted,
            String termsVersion,
            String privacyVersion
    ) {}
}
