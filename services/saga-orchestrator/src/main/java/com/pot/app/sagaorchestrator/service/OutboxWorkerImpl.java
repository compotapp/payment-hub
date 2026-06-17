package com.pot.app.sagaorchestrator.service;

import com.pot.app.sagaorchestrator.entity.Outbox;
import com.pot.app.sagaorchestrator.mapping.OutboxMapper;
import com.pot.app.shared.dto.command.SagaCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.pot.app.shared.constans.KafkaTopics.SAGA_COMMANDS;
import static com.pot.app.shared.constans.OutboxConstants.OutboxStatus.FAILED;
import static com.pot.app.shared.constans.OutboxConstants.OutboxStatus.PENDING;
import static java.time.LocalDateTime.now;
import static java.util.concurrent.TimeUnit.MINUTES;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxWorkerImpl implements OutboxWorker {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OutboxService outboxService;
    private final OutboxMapper outboxMapper;

    @Override
    @Transactional
    public void processBath() {
        // 1. Забираем 20 строк. Другие инстансы эти 20 строк сейчас не увидят
        List<Outbox> outboxes = outboxService.findAllByStatus(PENDING);
        if (outboxes.isEmpty()) return;
        List<Outbox> successful = new ArrayList<>();
        List<Outbox> failed = new ArrayList<>();

        for (Outbox outbox : outboxes) {
            try {
                SagaCommand command = outboxMapper.toCommand(outbox);
                // Синхронная отправка с тайм-аутом
                kafkaTemplate.send(SAGA_COMMANDS, command.transactionId(), command)
                        //тайм-аут приложения, ожидание текущего потока асинхронного ответа, producer продолжит попытки в фоне!
                        .get(2, MINUTES);  // throws ExecutionException
                successful.add(outbox);
            } catch (Exception e) {
                // Ошибка отправки конкретного сообщения
                outbox.setRetryCount(outbox.getRetryCount() + 1);
                // Exponential backoff: 1, 2, 4, 8, 16 секунд...
                long delaySeconds = (long) Math.pow(2, outbox.getRetryCount());
                outbox.setNextAttemptAt(now().plusSeconds(delaySeconds));

                if (outbox.getRetryCount() >= 50) {
                    outbox.setStatus(FAILED);
                }
                failed.add(outbox);
                log.error("Failed to send outbox message: {}", outbox.getId(), e);
            }
        }
        if (!successful.isEmpty()) {
            outboxService.deleteByIds(outboxMapper.getIds(successful));
        }
        if (!failed.isEmpty()) {
            outboxService.saveAll(failed);
        }
    }
}