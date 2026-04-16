package com.pot.app.shared.constans;

/**
 * Названия Kafka топиков.
 * Все сервисы используют эти константы, чтобы не разбегались названия.
 */
public final class KafkaTopics {

    // Transaction Service → событие о платеже
    public static final String PAYMENT_EVENTS = "payment-events";

    // Account Service → событие об изменении баланса
    public static final String BALANCE_EVENTS = "balance-events";

    // Transaction Service → команда на отправку уведомления
    public static final String NOTIFICATION_COMMANDS = "notification-commands";

    // Saga Orchestrator → команда участникам саги
    public static final String SAGA_COMMANDS = "saga-commands";

    // Участники саги → событие для оркестратора
    public static final String SAGA_EVENTS = "saga-events";

    private KafkaTopics() {
        // Запрещаем создание экземпляров
    }
}
