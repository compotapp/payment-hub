package com.pot.app.notificationservice.dto;

import java.time.Instant;

public record NotificationStatusResponse(
        String notificationId,
        String status,  // "PENDING", "SENT", "FAILED"
        String details,  // ошибка или информация
        Instant createdAt,
        Instant updatedAt
) {}
