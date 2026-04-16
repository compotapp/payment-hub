package com.pot.app.accountservice.service;

import com.pot.app.accountservice.entity.Reservation;

import java.util.Optional;
import java.util.UUID;

public interface ReservationService {

    Optional<Reservation> findByTransactionId(String transactionId);

    Reservation save(Reservation reservation);

    Optional<Reservation> findByIdAndTransactionId(UUID id, String transactionId);
}
