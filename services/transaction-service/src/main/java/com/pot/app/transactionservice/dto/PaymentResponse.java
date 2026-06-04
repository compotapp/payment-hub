package com.pot.app.transactionservice.dto;

public record PaymentResponse(
        String transactionId,
        String sagaId,
        String status,
        String message
) {
}
