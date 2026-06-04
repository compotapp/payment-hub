package com.pot.app.sagaorchestrator.service;

import com.pot.app.sagaorchestrator.dto.SagaStatusResponse;
import com.pot.app.sagaorchestrator.entity.SagaInstance;

public interface SagaInstanceService {

    SagaStatusResponse findById(String sagaId);

    SagaInstance create(SagaInstance sagaInstance);
}
