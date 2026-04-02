package com.pot.app.notificationservice.dto;

public record EmailRequest(
        String to,
        String subject,
        String body,
        String transactionId
) {}
