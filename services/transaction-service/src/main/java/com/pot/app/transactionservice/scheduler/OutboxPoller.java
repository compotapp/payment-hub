package com.pot.app.transactionservice.scheduler;

import com.pot.app.transactionservice.entity.Outbox;
import com.pot.app.transactionservice.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxPoller {

    private final OutboxService outboxService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    
    // Запускается каждые 5 секунд
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processOutbox() {
        log.debug("Checking outbox for pending events");
        
        List<Outbox> pendingEvents = outboxService.findPendingEvents();
        
        for (Outbox event : pendingEvents) {
            try {
                // Отправляем в Kafka
                kafkaTemplate.send("payment-events", event.getTransactionId(), event.getPayload())
                    .get(5, SECONDS);  // ждём подтверждения
                
                // Успех → удаляем из outbox
                outboxService.delete(event);
                log.info("Outbox event sent and deleted: id={}, type={}", 
                         event.getId(), event.getEventType());
                
            } catch (Exception e) {
                log.warn("Failed to send outbox event: id={}, attempt={}", 
                         event.getId(), event.getRetryCount(), e);
                
                // Обновляем для повторной попытки
                event.setRetryCount(event.getRetryCount() + 1);
                
                // Exponential backoff: 1, 2, 4, 8, 16 секунд...
                long delaySeconds = (long) Math.pow(2, event.getRetryCount());
                event.setNextAttemptAt(LocalDateTime.now().plusSeconds(delaySeconds));
                
                // После 10 попыток помечаем как FAILED (чтобы не забивать)
                if (event.getRetryCount() >= 10) {
                    event.setStatus("FAILED");
                    log.error("Outbox event permanently failed: id={}", event.getId());
                }
                
                outboxService.save(event);
            }
        }
    }
}