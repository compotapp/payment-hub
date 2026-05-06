package com.pot.app.shared.dto.command;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;

@Getter @Setter
public class NotificationCommand {
    private String notificationId;
    private String type;  // "EMAIL" или "PUSH"
    private String transactionId;
    private Map<String, Object> payload;  // гибкая структура
    private Instant createdAt;
}
