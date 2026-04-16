package com.pot.app.accountservice.service;

import com.pot.app.accountservice.dto.BalanceData;
import com.pot.app.accountservice.dto.ReservationResult;

import java.util.UUID;

public interface AccountReservationService {

    ReservationResult reserve(String transactionId, String userId, long amount);

    boolean commit(UUID reservationId, String transactionId);

    boolean cancel(UUID reservationId, String transactionId);

    BalanceData getBalance(String userId);
}
