-- =====================================================
-- Миграция V2: Создание таблицы daily_aggregates
-- Сервис: Analytics Service
-- Описание: Агрегированные данные по дням для быстрых отчетов
-- =====================================================

-- Создание таблицы агрегатов
CREATE TABLE IF NOT EXISTS daily_aggregates (
    -- Уникальный идентификатор
                                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Дата агрегации
                                                date DATE NOT NULL,

    -- Тип события (PAYMENT_CREATED, BALANCE_CHANGED и т.д.)
                                                event_type VARCHAR(64) NOT NULL,

    -- Общее количество событий за день
                                                total_count BIGINT NOT NULL DEFAULT 0,

    -- Общая сумма (для событий с суммами)
                                                total_amount DECIMAL(20,2) NOT NULL DEFAULT 0.00,

    -- Время создания записи
                                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Уникальный ключ для upsert операций
                                                CONSTRAINT daily_aggregates_date_event_unique UNIQUE (date, event_type)
);

-- Комментарии
COMMENT ON TABLE daily_aggregates IS 'Агрегированные данные по дням для быстрых отчетов';
COMMENT ON COLUMN daily_aggregates.id IS 'Уникальный идентификатор';
COMMENT ON COLUMN daily_aggregates.date IS 'Дата агрегации';
COMMENT ON COLUMN daily_aggregates.event_type IS 'Тип события';
COMMENT ON COLUMN daily_aggregates.total_count IS 'Общее количество событий за день';
COMMENT ON COLUMN daily_aggregates.total_amount IS 'Общая сумма (для платежных событий)';
COMMENT ON COLUMN daily_aggregates.created_at IS 'Время создания записи';

-- Индексы
CREATE INDEX IF NOT EXISTS idx_daily_aggregates_date ON daily_aggregates(date DESC);
COMMENT ON INDEX idx_daily_aggregates_date IS 'Индекс для поиска по дате';

CREATE INDEX IF NOT EXISTS idx_daily_aggregates_event_type ON daily_aggregates(event_type);
COMMENT ON INDEX idx_daily_aggregates_event_type IS 'Индекс для фильтрации по типу события';

-- Функция для обновления агрегатов (вызывается при вставке событий)
CREATE OR REPLACE FUNCTION update_daily_aggregates()
    RETURNS TRIGGER AS $$
DECLARE
    amount_value DECIMAL(20,2);
BEGIN
    -- Безопасное извлечение суммы из JSONB
    BEGIN
        amount_value := COALESCE((NEW.payload->>'amount')::DECIMAL(20,2), 0);
    EXCEPTION WHEN OTHERS THEN
        amount_value := 0;
    END;

    INSERT INTO daily_aggregates (date, event_type, total_count, total_amount)
    VALUES (
               DATE(NEW.created_at),
               NEW.event_type,
               1,
               amount_value
           )
    ON CONFLICT (date, event_type)
        DO UPDATE SET
                      total_count = daily_aggregates.total_count + 1,
                      total_amount = daily_aggregates.total_amount + EXCLUDED.total_amount,
                      created_at = CURRENT_TIMESTAMP;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION update_daily_aggregates() IS 'Автоматически обновляет агрегаты при вставке новых событий';

-- Триггер для автоматического обновления агрегатов
DROP TRIGGER IF EXISTS trigger_update_daily_aggregates ON raw_events;

CREATE TRIGGER trigger_update_daily_aggregates
    AFTER INSERT ON raw_events
    FOR EACH ROW
EXECUTE FUNCTION update_daily_aggregates();

COMMENT ON TRIGGER trigger_update_daily_aggregates ON raw_events IS 'Автоматически обновляет daily_aggregates при вставке событий';

-- Функция для получения статистики по событиям за период
CREATE OR REPLACE FUNCTION get_event_stats(
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    p_event_type VARCHAR(64) DEFAULT NULL,
    p_user_id VARCHAR(64) DEFAULT NULL
)
    RETURNS TABLE(
                     event_type VARCHAR(64),
                     total_count BIGINT,
                     total_amount DECIMAL(20,2),
                     unique_users BIGINT
                 ) AS $$
BEGIN
    RETURN QUERY
        SELECT
            re.event_type,
            COUNT(*)::BIGINT as total_count,
            COALESCE(SUM((re.payload->>'amount')::DECIMAL(20,2)), 0) as total_amount,
            COUNT(DISTINCT re.user_id)::BIGINT as unique_users
        FROM raw_events re
        WHERE re.created_at BETWEEN start_date AND end_date
          AND (p_event_type IS NULL OR re.event_type = p_event_type)
          AND (p_user_id IS NULL OR re.user_id = p_user_id)
        GROUP BY re.event_type
        ORDER BY re.event_type;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION get_event_stats IS 'Возвращает статистику по событиям за период';