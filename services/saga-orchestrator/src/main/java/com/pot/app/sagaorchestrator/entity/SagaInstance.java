package com.pot.app.sagaorchestrator.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.pot.app.shared.constans.SagaConstants.SagaStatus.STARTED;
import static org.hibernate.type.SqlTypes.JSON;

@Data
@Entity
@Table(name = "saga_instance")
public class SagaInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "saga_type", nullable = false, length = 32)
    private String sagaType;

    @Column(name = "status", nullable = false, length = 32)
    private String status = STARTED;

    @Column(name = "current_step", nullable = false)
    private int currentStep = 0;

    @Column(name = "payload", nullable = false)
    @JdbcTypeCode(value = JSON)
    private Map<String, Object> payload;   // JSON

    @Column(name = "context")
    @JdbcTypeCode(value = JSON)
    private Map<String, Object> context = new HashMap<>();   // JSON

//    @OneToMany(mappedBy = "sagaId", cascade = ALL, fetch = EAGER)
//    private List<SagaStepLog> logs;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}