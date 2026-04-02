package com.pot.app.notificationservice.dto;

public record NotificationStatusResponse(
        String notificationId,
        String status,
        String details
) {}
