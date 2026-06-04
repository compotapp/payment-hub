package com.pot.app.sagaorchestrator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static com.pot.app.shared.constans.OutboxConstants.OutboxStatus.PENDING;
import static org.hibernate.type.SqlTypes.JSON;

@Data
@Builder
@Entity
@Table(name = "outbox")
@NoArgsConstructor
@AllArgsConstructor
public class Outbox {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "message_type", nullable = false, length = 64)
    private String messageType;

    @Column(name = "saga_id", nullable = false, length = 64)
    private String sagaId;

    @Column(name = "transaction_id", nullable = false, length = 64)
    private String transactionId;//для ключа кафки, для трассировки, дедупликации и группировки событий

    @Column(name = "payload", nullable = false)
    @JdbcTypeCode(value = JSON)
    private Map<String, Object> payload; // JSON

    @Builder.Default
    @Column(name = "status", nullable = false, length = 32)
    private String status = PENDING; // PENDING, PROCESSING, COMPLETED, FAILED

    @Builder.Default
    @Column(name = "retryCount", nullable = false)
    private int retryCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "next_attempt_at", nullable = false)
    private LocalDateTime nextAttemptAt;
}