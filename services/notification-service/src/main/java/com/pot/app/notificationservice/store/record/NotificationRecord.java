package com.pot.app.notificationservice.store.record;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class NotificationRecord {
    private String notificationId;
    private String type;
    private String transactionId;

    @Setter
    private String status;  // PENDING, SENT, FAILED

    @Setter
    private String details;
    private Instant createdAt;

    @Setter
    private Instant updatedAt;
    private int attempts;

}
