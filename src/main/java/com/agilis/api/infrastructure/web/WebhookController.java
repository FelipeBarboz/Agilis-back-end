package com.agilis.api.infrastructure.web;

import com.agilis.api.application.notification.ListWebhooksUseCase;
import com.agilis.api.application.notification.RegisterWebhookUseCase;
import com.agilis.api.application.notification.RemoveWebhookUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Webhooks", description = "Registro e gerenciamento de webhooks de notificação")
@RestController
@RequestMapping("/api/v1/stores/{storeId}/webhooks")
public class WebhookController {

    private final RegisterWebhookUseCase registerWebhookUseCase;
    private final ListWebhooksUseCase listWebhooksUseCase;
    private final RemoveWebhookUseCase removeWebhookUseCase;

    public WebhookController(
            RegisterWebhookUseCase registerWebhookUseCase,
            ListWebhooksUseCase listWebhooksUseCase,
            RemoveWebhookUseCase removeWebhookUseCase
    ) {
        this.registerWebhookUseCase = registerWebhookUseCase;
        this.listWebhooksUseCase    = listWebhooksUseCase;
        this.removeWebhookUseCase   = removeWebhookUseCase;
    }

    @PostMapping
    @Operation(summary = "Cadastra um webhook para receber eventos da loja")
    public ResponseEntity<RegisterWebhookUseCase.Output> register(
            @PathVariable String storeId,
            @RequestBody RegisterWebhookUseCase.Input input
    ) {
        String requesterId = getCurrentUserId();
        RegisterWebhookUseCase.Input inputWithRequester = new RegisterWebhookUseCase.Input(
                requesterId, storeId, input.url(), input.events()
        );
        return ResponseEntity.ok(registerWebhookUseCase.execute(inputWithRequester));
    }

    @GetMapping
    @Operation(summary = "Lista todos os webhooks cadastrados para a loja")
    public ResponseEntity<List<ListWebhooksUseCase.Output>> list(@PathVariable String storeId) {
        String requesterId = getCurrentUserId();
        return ResponseEntity.ok(listWebhooksUseCase.execute(requesterId, storeId));
    }

    @DeleteMapping("/{webhookId}")
    @Operation(summary = "Remove um webhook cadastrado")
    public ResponseEntity<Void> remove(@PathVariable String webhookId) {
        String requesterId = getCurrentUserId();
        removeWebhookUseCase.execute(requesterId, webhookId);
        return ResponseEntity.noContent().build();
    }

    private String getCurrentUserId() {
        return (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}