package com.pot.app.accountservice.service;

import com.pot.app.accountservice.entity.Reservation;
import com.pot.app.accountservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository repository;

    @Override
    @Transactional
    public Optional<Reservation> findByTransactionId(String transactionId) {
        return repository.findByTransactionId(transactionId);
    }

    @Override
    @Transactional
    public Reservation save(Reservation reservation) {
        return repository.save(reservation);
    }

    @Override
    @Transactional
    public Optional<Reservation> findByIdAndTransactionId(UUID id, String transactionId) {
        return repository.findByIdAndTransactionId(id, transactionId);
    }
}
