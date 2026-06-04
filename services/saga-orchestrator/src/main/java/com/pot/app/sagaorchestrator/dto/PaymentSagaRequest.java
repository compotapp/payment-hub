package com.pot.app.sagaorchestrator.dto;

import java.math.BigDecimal;

public record PaymentSagaRequest(
        String transactionId,
        String userId,
        BigDecimal amount
) {
}
