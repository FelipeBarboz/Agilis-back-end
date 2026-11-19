package com.agilis.api.infrastructure.web;

import com.agilis.api.application.payment.CreatePaymentPreferenceUseCase;
import com.agilis.api.application.payment.GetPaymentStatusUseCase;
import com.agilis.api.application.payment.ProcessPaymentWebhookUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments", description = "Endpoints de gerenciamento e checkout de pagamentos")
public class PaymentController {

    private final CreatePaymentPreferenceUseCase createPaymentPreferenceUseCase;
    private final ProcessPaymentWebhookUseCase processPaymentWebhookUseCase;
    private final GetPaymentStatusUseCase getPaymentStatusUseCase;

    public PaymentController(
            CreatePaymentPreferenceUseCase createPaymentPreferenceUseCase,
            ProcessPaymentWebhookUseCase processPaymentWebhookUseCase,
            GetPaymentStatusUseCase getPaymentStatusUseCase
    ) {
        this.createPaymentPreferenceUseCase = createPaymentPreferenceUseCase;
        this.processPaymentWebhookUseCase = processPaymentWebhookUseCase;
        this.getPaymentStatusUseCase = getPaymentStatusUseCase;
    }

    @PostMapping("/preference")
    @Operation(summary = "Gera uma preferência de pagamento no Mercado Pago Checkout Pro")
    public ResponseEntity<CreatePaymentPreferenceUseCase.Output> createPreference(
            @RequestBody CreatePreferenceRequest request
    ) {
        String requesterId = getCurrentUserId();
        CreatePaymentPreferenceUseCase.Input input = new CreatePaymentPreferenceUseCase.Input(
                request.bookingId(),
                requesterId,
                request.frontendBaseUrl(),
                request.customSuccessUrl(),
                request.customFailureUrl(),
                request.customPendingUrl()
        );
        return ResponseEntity.ok(createPaymentPreferenceUseCase.execute(input));
    }

    @GetMapping("/booking/{bookingId}")
    @Operation(summary = "Consulta o status do pagamento de um agendamento")
    public ResponseEntity<GetPaymentStatusUseCase.Output> getPaymentStatus(@PathVariable String bookingId) {
        String requesterId = getCurrentUserId();
        return ResponseEntity.ok(getPaymentStatusUseCase.execute(
                new GetPaymentStatusUseCase.Input(bookingId, requesterId)
        ));
    }

    @PostMapping("/mercadopago/webhook")
    @Operation(summary = "Webhook do Mercado Pago para notificação de atualizações de pagamento")
    public ResponseEntity<ProcessPaymentWebhookUseCase.Output> handleWebhook(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String action,
            @RequestParam(required = false, name = "data.id") String queryDataId,
            @RequestParam(required = false) String id,
            @RequestBody(required = false) Map<String, Object> body
    ) {
        String effectiveType = type != null ? type : topic;
        String effectiveDataId = queryDataId;
        String effectiveAction = action;

        if (body != null) {
            if (effectiveType == null && body.get("type") != null) {
                effectiveType = String.valueOf(body.get("type"));
            }
            if (effectiveAction == null && body.get("action") != null) {
                effectiveAction = String.valueOf(body.get("action"));
            }
            if (effectiveDataId == null && body.get("data") instanceof Map<?, ?> dataMap) {
                Object dataIdObj = dataMap.get("id");
                if (dataIdObj != null) {
                    effectiveDataId = String.valueOf(dataIdObj);
                }
            }
        }

        ProcessPaymentWebhookUseCase.Input input = new ProcessPaymentWebhookUseCase.Input(
                topic,
                effectiveType,
                effectiveAction,
                effectiveDataId,
                id
        );

        return ResponseEntity.ok(processPaymentWebhookUseCase.execute(input));
    }

    @GetMapping("/mercadopago/webhook")
    public ResponseEntity<String> verifyWebhook() {
        return ResponseEntity.ok("Mercado Pago Webhook endpoint active");
    }

    private String getCurrentUserId() {
        return (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    public record CreatePreferenceRequest(
            String bookingId,
            String frontendBaseUrl,
            String customSuccessUrl,
            String customFailureUrl,
            String customPendingUrl
    ) {}
}
