package com.pot.app.transactionservice.repository;

import com.pot.app.transactionservice.entity.Outbox;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends GeneralRepository<Outbox, UUID> {

    @Query("SELECT o FROM Outbox o WHERE o.status = 'PENDING' AND o.nextAttemptAt <= CURRENT_TIMESTAMP")
    List<Outbox> findPendingEvents();
}