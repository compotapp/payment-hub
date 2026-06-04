package com.pot.app.shared.dto.event;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;

@Getter @Setter
public class SagaEvent {
    private String sagaId;
    private String eventType;     // FUNDS_RESERVED, PAYMENT_PROCESSED, PAYMENT_FAILED, etc
    private String transactionId;
    private Map<String, Object> payload;  // reservationId, paymentId, errorMessage
    private boolean success;
    private Instant timestamp;
    private String source;        // account-service, transaction-service
}