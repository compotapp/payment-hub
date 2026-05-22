package com.pot.app.transactionservice.dto;

public record PaymentResponse(
        String paymentId,
        String status,
        String message
) {
}
