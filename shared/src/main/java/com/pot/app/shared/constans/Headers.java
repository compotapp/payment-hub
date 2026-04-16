package com.pot.app.shared.constans;

/**
 * Названия HTTP и Kafka заголовков для сквозной трассировки.
 */
public final class Headers {

    // Сквозной ID для трассировки запроса через все сервисы
    public static final String CORRELATION_ID = "X-Correlation-Id";

    // ID пользователя (для контекста)
    public static final String USER_ID = "X-User-Id";

    // ID запроса (для дебага)
    public static final String REQUEST_ID = "X-Request-Id";

    private Headers() {
        // Запрещаем создание экземпляров
    }
}
