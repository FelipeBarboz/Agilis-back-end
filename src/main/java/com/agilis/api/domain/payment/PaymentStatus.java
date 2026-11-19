package com.agilis.api.domain.payment;

public enum PaymentStatus {
    PENDING,
    APPROVED,
    AUTHORIZED,
    IN_PROCESS,
    IN_MEDIATION,
    REJECTED,
    CANCELLED,
    REFUNDED,
    CHARGED_BACK;

    public static PaymentStatus fromMercadoPago(String status) {
        if (status == null) return PENDING;
        return switch (status.toLowerCase()) {
            case "approved" -> APPROVED;
            case "authorized" -> AUTHORIZED;
            case "in_process" -> IN_PROCESS;
            case "in_mediation" -> IN_MEDIATION;
            case "rejected" -> REJECTED;
            case "cancelled" -> CANCELLED;
            case "refunded" -> REFUNDED;
            case "charged_back" -> CHARGED_BACK;
            default -> PENDING;
        };
    }
}
