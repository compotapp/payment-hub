package com.pot.app.analyticsservice.consumer;

import com.pot.app.analyticsservice.entity.RawEvent;
import com.pot.app.analyticsservice.service.AnalyticsService;
import com.pot.app.shared.dto.event.BalanceEvent;
import com.pot.app.shared.dto.event.BaseEvent;
import com.pot.app.shared.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static com.pot.app.shared.constans.KafkaTopics.BALANCE_EVENTS;

@Component
@Slf4j
@RequiredArgsConstructor
public class BalanceEventConsumer {

    private final AnalyticsService service;

    @KafkaListener(topics = BALANCE_EVENTS)
    public void consumeBalanceEvent(String message, Acknowledgment ack) {
        try {
            BaseEvent base = JsonUtils.fromJson(message, BaseEvent.class);

            RawEvent entity = new RawEvent();
            entity.setId(UUID.randomUUID());
            entity.setEventType(base.getEventType());
            entity.setTransactionId(base.getTransactionId());
            entity.setPayload(Map.of());
            entity.setCreatedAt(LocalDateTime.now());

            // Для BalanceEvent userId есть в самом событии
            BalanceEvent balanceEvent = JsonUtils.fromJson(message, BalanceEvent.class);
            entity.setUserId(balanceEvent.getUserId());

            service.save(entity);
            log.info("Saved balance event: id={}", entity.getId());
            ack.acknowledge();

        } catch (Exception e) {
            log.error("Error processing balance event", e);
        }
    }
}
