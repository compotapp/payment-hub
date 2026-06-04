package com.pot.app.sagaorchestrator.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static jakarta.persistence.FetchType.LAZY;
import static org.hibernate.type.SqlTypes.JSON;

@Data
@Entity
@Table(name = "saga_step_log")
public class SagaStepLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private UUID id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "saga_id", nullable = false)
    private SagaInstance sagaId;

    @Column(name = "step_index", nullable = false)
    private int stepIndex = 0;

    @Column(name = "step_name", nullable = false)
    private String stepName;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "payload")
    @JdbcTypeCode(value = JSON)
    private Map<String, Object> payload;   // JSON

    @Column(name = "error_message")
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
