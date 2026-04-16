package com.pot.app.accountservice.service;

import com.pot.app.accountservice.dto.BalanceData;
import com.pot.app.accountservice.dto.ReservationResult;
import com.pot.app.accountservice.entity.Account;
import com.pot.app.accountservice.entity.Reservation;
import com.pot.app.accountservice.exception.AccountNotFoundException;
import com.pot.app.accountservice.exception.ConcurrentModificationException;
import com.pot.app.accountservice.exception.ReservationExpiredException;
import com.pot.app.accountservice.exception.ReservationStatusAlreadyException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static com.pot.app.accountservice.dto.ReservationResult.success;
import static com.pot.app.accountservice.entity.Reservation.ReservationStatus.CANCELLED;
import static com.pot.app.accountservice.entity.Reservation.ReservationStatus.COMMITTED;
import static com.pot.app.accountservice.entity.Reservation.active;
import static com.pot.app.accountservice.util.MoneyConverter.toMajorUnit;
import static com.pot.app.accountservice.util.MoneyConverter.toMinorUnit;
import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountReservationServiceImpl implements AccountReservationService {

    private static final byte MAX_RETRIES = 4;

    private final AccountService accountService;
    private final ReservationService reservationService;

    @Override
    @Transactional
    public ReservationResult reserve(String transactionId, String userId, long amount) {
        return reservationService.findByTransactionId(transactionId)
                .map(this::requireActiveAndGetResult)
                .orElseGet(() -> createReservation(transactionId, userId, amount));
    }

    @Override
    @Transactional
    public boolean commit(UUID reservationId, String transactionId) {
        return reservationService.findByIdAndTransactionId(reservationId, transactionId)
                .map(reservation -> {
                    if (reservation.isActive()) {
                        if (reservation.isExpired()) {
                            throw new ReservationExpiredException(format("Transaction id: %s, expired", transactionId));
                        }
                        Account account = reservation.getAccount();
                        account.commitReservation(reservation.getAmount());
                        reserveFromAccount(account);
                        reservation.commit();
                        reservationService.save(reservation);
                        return true;
                    } else if (reservation.isCommited()) return true;
                    throw new ReservationStatusAlreadyException(
                            CANCELLED,
                            format("Transaction id: %s has already been cancelled", transactionId)
                    );
                })
                .orElse(false);
    }

    @Override
    public boolean cancel(UUID reservationId, String transactionId) {
        return reservationService.findByIdAndTransactionId(reservationId, transactionId)
                .map(reservation -> {
                    if (reservation.isActive()) {
                        Account account = reservation.getAccount();
                        account.cancelReservation(reservation.getAmount());
                        reserveFromAccount(account);
                        reservation.cancel();
                        reservationService.save(reservation);
                        return true;
                    } else if (reservation.isCancelled()) return true;
                    throw new ReservationStatusAlreadyException(
                            COMMITTED,
                            format("Transaction id: %s has already been commited", transactionId)
                    );
                })
                .orElse(false);
    }

    @Override
    @Transactional
    public BalanceData getBalance(String userId) {
        return accountService.findByUserId(userId)
                .map(account -> new BalanceData(
                        toMinorUnit(account.getAvailableBalance()),
                        toMinorUnit(account.getReservedBalance())))
                .orElseGet(BalanceData::zeroBalance);
    }

    private ReservationResult requireActiveAndGetResult(Reservation reservation) {
        String transactionId = reservation.getTransactionId();
        if (reservation.isActive()) {
            return success(reservation.getId(), format("Transaction id: %s exists", transactionId));
        }
        throw new ReservationStatusAlreadyException(
                reservation.getStatus(),
                format("Transaction id: %s has already been completed", transactionId)
        );
    }

    private ReservationResult createReservation(String transactionId, String userId, long amount) {
        return accountService.findByUserId(userId)
                .map(account -> {
                    BigDecimal majorAmount = toMajorUnit(amount);
                    account.reserve(majorAmount);
                    account = reserveFromAccount(account);
                    Reservation reservation = reservationService.save(active(transactionId, account, majorAmount));
                    return success(reservation.getId(), format("Transaction id: %s created", transactionId));
                })
                .orElseThrow(() -> new AccountNotFoundException(format("User id: %s not found", userId)));
    }

    private Account reserveFromAccount(Account account) {
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                return accountService.update(account);
            } catch (OptimisticLockException e) {
                log.info("User id: {}, optimistic lock retry", account.getUserId());
            }
        }
        throw new ConcurrentModificationException(format("User id: %s failed to reserve", account.getUserId()));
    }
}
