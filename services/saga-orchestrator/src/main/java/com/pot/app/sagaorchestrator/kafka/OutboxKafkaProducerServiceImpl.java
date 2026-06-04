package com.pot.app.sagaorchestrator.kafka;

import com.pot.app.shared.dto.command.SagaCommand;
import com.pot.app.shared.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.pot.app.shared.constans.KafkaTopics.SAGA_COMMANDS;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxKafkaProducerServiceImpl implements OutboxKafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendCommand(SagaCommand command) throws ExecutionException, InterruptedException, TimeoutException {
        kafkaTemplate.send(SAGA_COMMANDS, command.transactionId(), JsonUtils.toJson(command))
                .get(5, TimeUnit.SECONDS);
    }
}
