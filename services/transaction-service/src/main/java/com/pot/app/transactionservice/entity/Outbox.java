package com.pot.app.transactionservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.pot.app.transactionservice.constans.TransactionStatus.PENDING_STATUS;

@Data
@Entity
@Table(name = "outbox")
public class Outbox {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "event_type", nullable = false, length = 64)
    private String eventType;

    @Column(name = "transaction_id", nullable = false, length = 64)
    private String transactionId;

    @Column(name = "payload", nullable = false)
    private String payload;  // JSON

    @Column(name = "status", nullable = false, length = 32)
    private String status = PENDING_STATUS; // PENDING, SENT, FAILED

    @Column(name = "retryCount", nullable = false)
    private int retryCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "next_attempt_at", nullable = false)
    private LocalDateTime nextAttemptAt;
}