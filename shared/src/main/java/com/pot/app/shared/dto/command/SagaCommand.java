package com.pot.app.shared.dto.command;

import java.time.Instant;
import java.util.Map;

public record SagaCommand(
    String sagaId,
    String commandType,   // RESERVE_FUNDS, PROCESS_PAYMENT, COMMIT_FUNDS, CANCEL_RESERVATION
    String transactionId,
    Map<String, Object> payload,
    Instant timestamp,
    String source        // saga-orchestrator
) {}