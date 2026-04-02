package com.pot.app.notificationservice.dto;

public record PushRequest(
        String userId,
        String title,
        String body,
        String transactionId
) {}
