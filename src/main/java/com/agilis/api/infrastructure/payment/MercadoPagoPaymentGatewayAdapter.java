package com.agilis.api.infrastructure.payment;

import com.agilis.api.domain.payment.PaymentGateway;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public class MercadoPagoPaymentGatewayAdapter implements PaymentGateway {

    private final RestClient restClient;

    public MercadoPagoPaymentGatewayAdapter(String accessToken) {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.mercadopago.com")
                .defaultHeader("Authorization", "Bearer " + accessToken)
                .build();
    }

    public MercadoPagoPaymentGatewayAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public PreferenceResult createPreference(CreatePreferenceCommand command) {
        Map<String, Object> payload = Map.of(
                "items", List.of(Map.of(
                        "title", command.title(),
                        "quantity", 1,
                        "currency_id", "BRL",
                        "unit_price", command.amount()
                )),
                "payer", Map.of(
                        "name", command.payerName() != null ? command.payerName() : "Cliente",
                        "email", command.payerEmail() != null ? command.payerEmail() : "cliente@agilis.com"
                ),
                "back_urls", Map.of(
                        "success", command.backUrlSuccess(),
                        "failure", command.backUrlFailure(),
                        "pending", command.backUrlPending()
                ),
                "auto_return", "approved",
                "notification_url", command.notificationUrl(),
                "external_reference", command.externalReference()
        );

        MpPreferenceResponse response = restClient.post()
                .uri("/checkout/preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .body(MpPreferenceResponse.class);

        if (response == null || response.id() == null) {
            throw new IllegalStateException("Falha ao criar preferência de pagamento no Mercado Pago");
        }

        return new PreferenceResult(response.id(), response.initPoint(), response.sandboxInitPoint());
    }

    @Override
    public PaymentDetails getPaymentDetails(String paymentId) {
        MpPaymentResponse response = restClient.get()
                .uri("/v1/payments/{id}", paymentId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(MpPaymentResponse.class);

        if (response == null) {
            throw new IllegalStateException("Falha ao consultar pagamento no Mercado Pago com ID: " + paymentId);
        }

        LocalDateTime dateApproved = null;
        if (response.dateApproved() != null) {
            try {
                dateApproved = OffsetDateTime.parse(response.dateApproved()).toLocalDateTime();
            } catch (Exception ignored) {}
        }

        return new PaymentDetails(
                String.valueOf(response.id()),
                response.status(),
                response.statusDetail(),
                response.transactionAmount(),
                response.externalReference(),
                response.paymentMethodId(),
                response.paymentTypeId(),
                dateApproved
        );
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MpPreferenceResponse(
            String id,
            @JsonProperty("init_point") String initPoint,
            @JsonProperty("sandbox_init_point") String sandboxInitPoint
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MpPaymentResponse(
            Long id,
            String status,
            @JsonProperty("status_detail") String statusDetail,
            @JsonProperty("transaction_amount") BigDecimal transactionAmount,
            @JsonProperty("external_reference") String externalReference,
            @JsonProperty("payment_method_id") String paymentMethodId,
            @JsonProperty("payment_type_id") String paymentTypeId,
            @JsonProperty("date_approved") String dateApproved
    ) {}
}
