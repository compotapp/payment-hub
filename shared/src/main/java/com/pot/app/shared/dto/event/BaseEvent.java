package com.pot.app.shared.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Базовый класс для всех событий, отправляемых в Kafka.
 * Все конкретные события наследуются от этого класса.
 */
@Getter @Setter
public abstract class BaseEvent {

    // Уникальный ID события (генерируется при создании)
    private String eventId;

    // Тип события (PAYMENT_CREATED, BALANCE_CHANGED и т.д.)
    private String eventType;

    // Сквозной ID транзакции для идемпотентности
    private String transactionId;

    // Время создания события
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant timestamp;

    // Имя сервиса-отправителя (transaction-service, account-service)
    private String source;

    // Версия схемы события (для обратной совместимости)
    private String version = "1.0";

    public BaseEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
    }

    // Конструктор с параметрами для удобства
    public BaseEvent(String eventType, String transactionId, String source) {
        this();
        this.eventType = eventType;
        this.transactionId = transactionId;
        this.source = source;
    }
}
