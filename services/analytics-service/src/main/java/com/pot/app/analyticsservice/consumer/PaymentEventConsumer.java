package com.pot.app.analyticsservice.consumer;

import com.pot.app.analyticsservice.entity.RawEvent;
import com.pot.app.analyticsservice.service.AnalyticsService;
import com.pot.app.shared.dto.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

import static com.pot.app.shared.constans.KafkaTopics.PAYMENT_EVENTS;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final AnalyticsService service;

    @KafkaListener(topics = PAYMENT_EVENTS)
    public void consumePaymentEvent(PaymentEvent event, Acknowledgment ack) {
        try {
            if (event == null) {
                log.error("Failed to parse base event");
                return;
            }

            // Шаг 2: создаём RawEvent
            RawEvent entity = new RawEvent();
            entity.setEventType(event.getEventType());
            entity.setTransactionId(event.getTransactionId());
            entity.setPayload(Map.of());  // сохраняем полное сообщение
            entity.setCreatedAt(LocalDateTime.now());

            // Шаг 3: извлекаем userId из конкретного типа
            if (PaymentEvent.TYPE_CREATED.equals(event.getEventType()) ||
                    PaymentEvent.TYPE_SUCCESS.equals(event.getEventType())) {
                entity.setUserId(event.getUserId());
            } else {
                entity.setUserId(null);  // или пропускаем
            }

            // Шаг 4: сохраняем в БД
            service.save(entity);
            log.info("Saved payment event: id={}, type={}", entity.getId(), entity.getEventType());

            // Шаг 5: подтверждаем обработку
            ack.acknowledge();

        } catch (Exception e) {
            log.error("Error processing payment event", e);
            // Не коммитим — Kafka переотправит
        }
    }
}
