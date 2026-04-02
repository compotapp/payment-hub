-- =====================================================
-- Миграция V1: Создание таблицы raw_events (гипертаблица)
-- Сервис: Analytics Service
-- Описание: Хранит все события для аналитики с TimescaleDB
-- =====================================================

-- Включаем расширение TimescaleDB (если не включено)
CREATE EXTENSION IF NOT EXISTS timescaledb;

-- Создание таблицы событий
-- ВАЖНО: Первичный ключ должен включать колонку created_at для TimescaleDB
CREATE TABLE IF NOT EXISTS raw_events (
    -- Уникальный идентификатор события
                                          id UUID NOT NULL DEFAULT gen_random_uuid(),

    -- Тип события:
    -- PAYMENT_CREATED    - платеж создан
    -- PAYMENT_COMPLETED  - платеж завершен
    -- PAYMENT_FAILED     - платеж неудачен
    -- BALANCE_CHANGED    - изменен баланс
    -- RESERVATION_CREATED - создано резервирование
    -- RESERVATION_COMMITTED - подтверждено резервирование
    -- RESERVATION_CANCELLED - отменено резервирование
                                          event_type VARCHAR(64) NOT NULL,

    -- Ссылка на транзакцию (из Transaction Service)
                                          transaction_id VARCHAR(64),

    -- ID пользователя
                                          user_id VARCHAR(64),

    -- Полное событие в JSON формате (для детального анализа)
                                          payload JSONB NOT NULL,

    -- Временная метка события (колонка времени для TimescaleDB)
                                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Составной первичный ключ: id + created_at
    -- Это необходимо для TimescaleDB гипертаблицы
                                          PRIMARY KEY (id, created_at)
);

-- Комментарии
COMMENT ON TABLE raw_events IS 'Сырые события для аналитики (гипертаблица TimescaleDB)';
COMMENT ON COLUMN raw_events.id IS 'Уникальный идентификатор события';
COMMENT ON COLUMN raw_events.event_type IS 'Тип события для аналитики';
COMMENT ON COLUMN raw_events.transaction_id IS 'ID транзакции для связывания';
COMMENT ON COLUMN raw_events.user_id IS 'ID пользователя';
COMMENT ON COLUMN raw_events.payload IS 'Полное событие в JSON формате';
COMMENT ON COLUMN raw_events.created_at IS 'Время создания события (используется для партиционирования)';

-- Преобразуем таблицу в гипертаблицу TimescaleDB
-- Партиционирование по интервалу 7 дней
SELECT create_hypertable('raw_events', 'created_at',
                         chunk_time_interval => INTERVAL '7 days',
                         if_not_exists => TRUE,
                         migrate_data => TRUE
       );

-- Индексы для производительности
-- Индекс для фильтрации по типу события
CREATE INDEX IF NOT EXISTS idx_raw_events_event_type ON raw_events(event_type, created_at DESC);
COMMENT ON INDEX idx_raw_events_event_type IS 'Индекс для фильтрации по типу события';

-- Индекс для поиска событий пользователя
CREATE INDEX IF NOT EXISTS idx_raw_events_user_id ON raw_events(user_id, created_at DESC);
COMMENT ON INDEX idx_raw_events_user_id IS 'Индекс для поиска событий пользователя';

-- Индекс для поиска по транзакции
CREATE INDEX IF NOT EXISTS idx_raw_events_transaction_id ON raw_events(transaction_id, created_at DESC);
COMMENT ON INDEX idx_raw_events_transaction_id IS 'Индекс для поиска по транзакции';

-- GIN индекс для JSONB поля payload
CREATE INDEX IF NOT EXISTS idx_raw_events_payload ON raw_events USING GIN(payload);
COMMENT ON INDEX idx_raw_events_payload IS 'GIN индекс для поиска по JSONB полю payload';

-- Индекс для created_at (уже есть в гипертаблице, но добавим для полноты)
CREATE INDEX IF NOT EXISTS idx_raw_events_created_at ON raw_events(created_at DESC);
COMMENT ON INDEX idx_raw_events_created_at IS 'Индекс для поиска по времени создания';

-- Создаем политику автоматического удаления старых данных (через 90 дней)
SELECT add_retention_policy('raw_events', INTERVAL '90 days', if_not_exists => TRUE);
COMMENT ON TABLE raw_events IS 'События хранятся 90 дней, старые автоматически удаляются';