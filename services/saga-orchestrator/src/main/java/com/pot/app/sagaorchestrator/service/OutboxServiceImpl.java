package com.pot.app.sagaorchestrator.service;

import com.pot.app.sagaorchestrator.entity.Outbox;
import com.pot.app.sagaorchestrator.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService {

    private final OutboxRepository repository;

    @Override
    @Transactional
    public Outbox create(Outbox outbox) {
        return repository.save(outbox);
    }

    @Override
    public void saveAll(List<Outbox> outboxes) {
        repository.saveAll(outboxes);
    }

    @Override
    @Transactional
    public List<Outbox> findAllByStatus(String status) {
        return repository.findAllByStatus(status, 5);
    }

    @Override
    @Transactional
    public void deleteByIds(List<UUID> ids) {
        repository.deleteByIds(ids);
    }
}
