package com.pot.app.transactionservice.service;

import com.pot.app.shared.dto.event.PaymentEvent;
import com.pot.app.shared.util.JsonUtils;
import com.pot.app.shared.util.MoneyConverter;
import com.pot.app.transactionservice.entity.Outbox;
import com.pot.app.transactionservice.entity.Transaction;
import com.pot.app.transactionservice.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService {

    private final OutboxRepository repository;

    @Override
    @Transactional
    public void create(Transaction transaction) {
        PaymentEvent event = new PaymentEvent();
        event.setEventType(PaymentEvent.TYPE_CREATED);
        event.setTransactionId(transaction.getTransactionId());
        event.setUserId(transaction.getUserId());
        event.setAmount(MoneyConverter.toMinorUnit(transaction.getAmount()));
        event.setSource("transaction-service");

        Outbox outbox = new Outbox();
        outbox.setEventType("PAYMENT_CREATED");
        outbox.setTransactionId(transaction.getTransactionId());
        outbox.setPayload(JsonUtils.toJson(event));
        repository.save(outbox);
    }

    @Override
    @Transactional
    public void save(Outbox event) {
        repository.save(event);
    }

    @Override
    @Transactional
    public List<Outbox> findPendingEvents() {
        return repository.findPendingEvents();
    }

    @Override
    @Transactional
    public void delete(Outbox event) {
        repository.delete(event);
    }
}
