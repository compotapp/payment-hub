package com.pot.app.notificationservice.dto;

import jakarta.validation.constraints.NotBlank;

public record EmailRequest(

        @NotBlank
        String to,

        @NotBlank
        String subject,

        @NotBlank
        String body,

        String transactionId
) {}
