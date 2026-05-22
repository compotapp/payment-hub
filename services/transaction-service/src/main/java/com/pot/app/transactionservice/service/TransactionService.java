package com.pot.app.transactionservice.service;

import com.pot.app.transactionservice.dto.PaymentRequest;
import com.pot.app.transactionservice.entity.Transaction;

public interface TransactionService {

    Transaction create(PaymentRequest request);

    Transaction update(Transaction transaction);
}
