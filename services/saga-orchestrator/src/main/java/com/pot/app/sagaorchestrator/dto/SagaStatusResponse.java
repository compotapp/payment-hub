package com.pot.app.sagaorchestrator.dto;

public record SagaStatusResponse(
        String sagaId,
        String status
) {}
