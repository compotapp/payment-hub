package com.pot.app.analyticsservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hibernate.type.SqlTypes.JSON;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "raw_events")
public class RawEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "event_type", length = 64)
    private String eventType;

    @Column(name = "transaction_id", length = 64)
    private String transactionId;

    @Column(name = "user_id", length = 64)
    private String userId;

    @Column(columnDefinition = "payload")
    @JdbcTypeCode(value = JSON)
    private Map<String, Object> payload = new HashMap<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
