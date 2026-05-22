package com.pot.app.accountservice.entity;

import com.pot.app.accountservice.exception.InsufficientFundsException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.lang.String.format;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", unique = true, nullable = false, length = 64)
    private String userId;

    @Column(name = "available_balance", nullable = false, precision = 20, scale = 2)
    private BigDecimal availableBalance;

    @Column(name = "reserved_balance", nullable = false, precision = 20, scale = 2)
    private BigDecimal reservedBalance;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Вспомогательные методы
    public boolean hasSufficientFunds(BigDecimal amount) {
        return availableBalance.compareTo(amount) >= 0;
    }

    public void reserve(BigDecimal amount) {
        if (!hasSufficientFunds(amount)) {
            throw new InsufficientFundsException(format("User id: %s insufficient funds", userId));
        }
        this.availableBalance = this.availableBalance.subtract(amount);
        this.reservedBalance = this.reservedBalance.add(amount);
    }

    public void commitReservation(BigDecimal amount) {
        this.reservedBalance = this.reservedBalance.subtract(amount);
    }

    public void cancelReservation(BigDecimal amount) {
        this.availableBalance = this.availableBalance.add(amount);
        this.reservedBalance = this.reservedBalance.subtract(amount);
    }
}
