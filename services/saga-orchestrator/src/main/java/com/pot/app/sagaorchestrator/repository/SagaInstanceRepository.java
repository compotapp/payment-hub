package com.pot.app.sagaorchestrator.repository;

import com.pot.app.sagaorchestrator.entity.SagaInstance;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SagaInstanceRepository extends GeneralRepository<SagaInstance, UUID> {
}
