package com.pot.app.sagaorchestrator.service;

import com.pot.app.sagaorchestrator.dto.PaymentSagaRequest;
import com.pot.app.sagaorchestrator.dto.PaymentSagaResponse;

public interface SagaOrchestrator {

    PaymentSagaResponse startPaymentSaga(PaymentSagaRequest request);
}
