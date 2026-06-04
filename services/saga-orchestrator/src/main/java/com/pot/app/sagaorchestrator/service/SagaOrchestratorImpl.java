package com.pot.app.sagaorchestrator.service;

import com.pot.app.sagaorchestrator.dto.PaymentSagaRequest;
import com.pot.app.sagaorchestrator.dto.PaymentSagaResponse;
import com.pot.app.sagaorchestrator.entity.SagaInstance;
import com.pot.app.sagaorchestrator.mapping.OutboxMapper;
import com.pot.app.sagaorchestrator.mapping.SagaInstanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SagaOrchestratorImpl implements SagaOrchestrator {

    private final SagaInstanceService sagaInstanceService;
    private final OutboxService outboxService;
    private final SagaInstanceMapper sagaInstanceMapper;
    private final OutboxMapper outboxMapper;

    @Override
    @Transactional
    public PaymentSagaResponse startPaymentSaga(PaymentSagaRequest request) {
        SagaInstance sagaInstance = sagaInstanceService.create(sagaInstanceMapper.toEntity(request));
        createOutbox(sagaInstance, request.transactionId());
        return sagaInstanceMapper.toDto(sagaInstance);
    }

    private void createOutbox(SagaInstance sagaInstance, String transactionId) {
        outboxService.create(outboxMapper.toEntity(sagaInstance, transactionId));
    }
}
