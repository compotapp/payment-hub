package com.pot.app.sagaorchestrator.service;

import com.pot.app.sagaorchestrator.entity.Outbox;

import java.util.List;
import java.util.UUID;

public interface OutboxService {

    Outbox create(Outbox outbox);

    void saveAll(List<Outbox> outboxes);

    List<Outbox> findAllByStatus(String status);

    void deleteByIds(List<UUID> ids);
}
