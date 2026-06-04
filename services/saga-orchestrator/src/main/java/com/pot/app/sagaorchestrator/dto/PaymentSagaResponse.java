package com.pot.app.sagaorchestrator.dto;

public record PaymentSagaResponse(
        String sagaId,
        String status,
        String message,
        boolean success
) {
}
