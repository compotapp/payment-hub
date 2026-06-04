package com.pot.app.sagaorchestrator.repository;

import com.pot.app.sagaorchestrator.entity.SagaStepLog;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SagaStepLogRepository extends GeneralRepository<SagaStepLog, UUID> {
}
