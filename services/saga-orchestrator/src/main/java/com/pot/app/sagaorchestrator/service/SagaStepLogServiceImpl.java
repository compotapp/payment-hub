package com.pot.app.sagaorchestrator.service;

import com.pot.app.sagaorchestrator.repository.SagaStepLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SagaStepLogServiceImpl implements SagaStepLogService {

    private final SagaStepLogRepository repository;
}
