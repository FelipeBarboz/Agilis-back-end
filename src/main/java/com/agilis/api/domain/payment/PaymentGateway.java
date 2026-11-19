package com.agilis.api.domain.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PaymentGateway {

    record CreatePreferenceCommand(
            String externalReference,
            String title,
            BigDecimal amount,
            String payerName,
            String payerEmail,
            String backUrlSuccess,
            String backUrlFailure,
            String backUrlPending,
            String notificationUrl
    ) {}

    record PreferenceResult(
            String preferenceId,
            String initPoint,
            String sandboxInitPoint
    ) {}

    record PaymentDetails(
            String paymentId,
            String status,
            String statusDetail,
            BigDecimal amount,
            String externalReference,
            String paymentMethodId,
            String paymentTypeId,
            LocalDateTime dateApproved
    ) {}

    PreferenceResult createPreference(CreatePreferenceCommand command);

    PaymentDetails getPaymentDetails(String paymentId);
}
