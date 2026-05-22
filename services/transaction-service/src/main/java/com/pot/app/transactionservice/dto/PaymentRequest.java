package com.pot.app.transactionservice.dto;

import java.math.BigDecimal;

public record PaymentRequest(
        String userId,
        BigDecimal amount
) {
}
