package com.pot.app.sagaorchestrator.service;

import com.pot.app.sagaorchestrator.dto.SagaStatusResponse;
import com.pot.app.sagaorchestrator.entity.SagaInstance;
import com.pot.app.sagaorchestrator.repository.SagaInstanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SagaInstanceServiceImpl implements SagaInstanceService {

    private final SagaInstanceRepository repository;

    @Override
    @Transactional
    public SagaStatusResponse findById(String sagaId) {
        return repository.findById(UUID.fromString(sagaId))
                .map(saga -> new SagaStatusResponse(saga.getId().toString(), saga.getStatus()))
                .orElseThrow(() -> new RuntimeException("Saga not found by id: " + sagaId));
    }

    @Override
    public SagaInstance create(SagaInstance sagaInstance) {
        return repository.save(sagaInstance);
    }
}
