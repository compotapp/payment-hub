package com.pot.app.transactionservice.service;

import com.pot.app.transactionservice.entity.Outbox;
import com.pot.app.transactionservice.entity.Transaction;

import java.util.List;

public interface OutboxService {

    void create(Transaction transaction);

    void save(Outbox event);

    List<Outbox> findPendingEvents();

    void delete(Outbox event);
}
