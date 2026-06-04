package com.pot.app.transactionservice.service;

import com.pot.app.transactionservice.dto.PaymentRequest;
import com.pot.app.transactionservice.entity.Transaction;
import com.pot.app.transactionservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.pot.app.transactionservice.constans.TransactionType.PAYMENT;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;

    @Override
    @Transactional
    public Transaction create(PaymentRequest request) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setUserId(request.userId());
        transaction.setAmount(request.amount());
        transaction.setType(PAYMENT);
        return repository.save(transaction);
    }

    @Override
    @Transactional
    public Transaction update(Transaction transaction) {
        return repository.save(transaction);
    }
}
