package com.pot.app.accountservice.repository;

import com.pot.app.accountservice.entity.Reservation;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository extends GeneralRepository<Reservation, UUID>{

    Optional<Reservation> findByTransactionId(String transactionId);

    Optional<Reservation> findByIdAndTransactionId(UUID id, String transactionId);
}
