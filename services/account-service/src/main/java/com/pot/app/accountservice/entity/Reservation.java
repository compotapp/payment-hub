package com.pot.app.accountservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.pot.app.accountservice.entity.Reservation.ReservationStatus.*;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "transaction_id", nullable = false, unique = true, length = 64)
    private String transactionId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "amount", nullable = false, precision = 20, scale = 2)
    private BigDecimal amount;

    @Enumerated(STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    @Builder.Default
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Enum для статуса
    public enum ReservationStatus {
        ACTIVE, COMMITTED, CANCELLED
    }

    public static Reservation active(String transactionId, Account account, BigDecimal amount) {
        return Reservation.builder()
                .transactionId(transactionId)
                .account(account)
                .amount(amount)
                .status(ACTIVE)
                .build();
    }

    // Вспомогательные методы
    public boolean isActive() {
        return status == ACTIVE;
    }

    public boolean isCommited() {
        return status == COMMITTED;
    }

    public boolean isCancelled() {
        return status == CANCELLED;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void commit() {
        if (!isActive()) {
            throw new IllegalStateException("Reservation is not active");
        }
        this.status = COMMITTED;
    }

    public void cancel() {
        if (!isActive()) {
            throw new IllegalStateException("Reservation is not active");
        }
        this.status = CANCELLED;
    }
}


