package com.pot.app.accountservice.repository;

import com.pot.app.accountservice.entity.Account;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static jakarta.persistence.LockModeType.OPTIMISTIC;
import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

@Repository
public interface AccountRepository extends GeneralRepository<Account, UUID> {

    // Поиск по userId
    Optional<Account> findByUserId(String userId);

    // Проверка существования
    boolean existsByUserId(String userId);

    // Поиск с пессимистичной блокировкой (для конкурентных операций)
    @Lock(PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.userId = :userId")
    Optional<Account> findByUserIdWithPessimisticLock(@Param("userId") String userId);

    // Поиск с оптимистичной блокировкой (через version поле)
    @Lock(OPTIMISTIC)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdWithOptimisticLock(@Param("id") UUID id);

    // Обновление баланса в одну операцию (без чтения)
    @Modifying
    @Query("UPDATE Account a SET a.availableBalance = a.availableBalance + :amount WHERE a.userId = :userId")
    int addToBalance(@Param("userId") String userId, @Param("amount") BigDecimal amount);

    // Получение только нужных полей (проекция)
    @Query("SELECT a.id, a.userId, a.availableBalance, a.reservedBalance FROM Account a WHERE a.userId = :userId")
    Object[] findBalanceByUserId(@Param("userId") String userId);

    // Удаление по userId
    void deleteByUserId(String userId);

    // Статистика
    @Query("SELECT COUNT(a) FROM Account a WHERE a.availableBalance > :minBalance")
    long countAccountsWithBalanceAbove(@Param("minBalance") BigDecimal minBalance);
}
