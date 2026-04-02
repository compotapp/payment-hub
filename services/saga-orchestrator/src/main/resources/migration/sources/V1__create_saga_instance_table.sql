-- V1__create_saga_instance_table.sql
-- =====================================================
-- Миграция V1: Создание таблицы saga_instance
-- Сервис: Saga Orchestrator
-- Описание: Хранит состояния и контекст SAGA транзакций
-- =====================================================

-- Создание таблицы SAGA инстансов
CREATE TABLE IF NOT EXISTS saga_instance (
    -- Уникальный идентификатор SAGA
                                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Тип SAGA:
    -- PAYMENT_SAGA       - платежная SAGA
    -- REFUND_SAGA        - SAGA возврата
    -- COMPENSATION_SAGA  - SAGA компенсации
                                             saga_type VARCHAR(32) NOT NULL,

    -- Статус SAGA:
    -- STARTED       - начата
    -- IN_PROGRESS   - в процессе выполнения
    -- COMPLETED     - успешно завершена
    -- COMPENSATING  - выполняется компенсация
    -- COMPENSATED   - компенсирована (откат завершен)
    -- FAILED        - ошибка без возможности компенсации
                                             status VARCHAR(32) NOT NULL DEFAULT 'STARTED',

    -- Текущий шаг выполнения (индекс шага)
                                             current_step INTEGER NOT NULL DEFAULT 0,

    -- Исходные данные SAGA (входные параметры)
                                             payload JSONB NOT NULL,

    -- Контекст выполнения (результаты каждого шага)
    -- Хранит данные для компенсации и восстановления
                                             context JSONB DEFAULT '{}',

    -- Временные метки
                                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Констрейнты
                                             CONSTRAINT check_saga_type_valid CHECK (saga_type IN ('PAYMENT_SAGA', 'REFUND_SAGA', 'COMPENSATION_SAGA')),
                                             CONSTRAINT check_status_valid CHECK (status IN ('STARTED', 'IN_PROGRESS', 'COMPLETED', 'COMPENSATING', 'COMPENSATED', 'FAILED')),
                                             CONSTRAINT check_current_step_non_negative CHECK (current_step >= 0)
);

-- Комментарии
COMMENT ON TABLE saga_instance IS 'SAGA транзакции для распределенных операций (паттерн SAGA)';
COMMENT ON COLUMN saga_instance.id IS 'Уникальный идентификатор SAGA инстанса';
COMMENT ON COLUMN saga_instance.saga_type IS 'Тип SAGA: PAYMENT_SAGA, REFUND_SAGA, COMPENSATION_SAGA';
COMMENT ON COLUMN saga_instance.status IS 'Статус выполнения SAGA';
COMMENT ON COLUMN saga_instance.current_step IS 'Текущий шаг выполнения (индекс)';
COMMENT ON COLUMN saga_instance.payload IS 'Исходные данные SAGA в JSON формате';
COMMENT ON COLUMN saga_instance.context IS 'Контекст выполнения (результаты шагов для компенсации)';
COMMENT ON COLUMN saga_instance.created_at IS 'Дата и время создания SAGA';
COMMENT ON COLUMN saga_instance.updated_at IS 'Дата и время последнего обновления';

-- Индексы для производительности
CREATE INDEX IF NOT EXISTS idx_saga_instance_status ON saga_instance(status);
COMMENT ON INDEX idx_saga_instance_status IS 'Индекс для поиска SAGA по статусу';

CREATE INDEX IF NOT EXISTS idx_saga_instance_type ON saga_instance(saga_type);
COMMENT ON INDEX idx_saga_instance_type IS 'Индекс для фильтрации по типу SAGA';

CREATE INDEX IF NOT EXISTS idx_saga_instance_created_at ON saga_instance(created_at);
COMMENT ON INDEX idx_saga_instance_created_at IS 'Индекс для поиска по дате создания';

-- Составной индекс для поиска висячих SAGA
CREATE INDEX IF NOT EXISTS idx_saga_instance_status_created ON saga_instance(status, created_at);
COMMENT ON INDEX idx_saga_instance_status_created IS 'Индекс для поиска долго выполняющихся SAGA';

-- Индекс для JSONB полей (для поиска по данным)
CREATE INDEX IF NOT EXISTS idx_saga_instance_payload ON saga_instance USING GIN(payload);
COMMENT ON INDEX idx_saga_instance_payload IS 'GIN индекс для поиска по JSONB полю payload';

CREATE INDEX IF NOT EXISTS idx_saga_instance_context ON saga_instance USING GIN(context);
COMMENT ON INDEX idx_saga_instance_context IS 'GIN индекс для поиска по JSONB полю context';

-- Триггер для автоматического обновления updated_at
CREATE TRIGGER trigger_saga_instance_updated_at
    BEFORE UPDATE ON saga_instance
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TRIGGER trigger_saga_instance_updated_at ON saga_instance IS 'Автоматически обновляет updated_at при изменении записи';

-- Функция для очистки старых завершенных SAGA
CREATE OR REPLACE FUNCTION cleanup_old_saga_instances()
    RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    DELETE FROM saga_instance
    WHERE status IN ('COMPLETED', 'COMPENSATED', 'FAILED')
      AND created_at < CURRENT_TIMESTAMP - INTERVAL '30 days'
    RETURNING id INTO deleted_count;

    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION cleanup_old_saga_instances() IS 'Очищает завершенные SAGA старше 30 дней';

-- Таблица для логирования шагов SAGA (аудит)
CREATE TABLE IF NOT EXISTS saga_step_log (
                                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                             saga_id UUID NOT NULL REFERENCES saga_instance(id) ON DELETE CASCADE,
                                             step_index INTEGER NOT NULL,
                                             step_name VARCHAR(100) NOT NULL,
                                             action VARCHAR(20) NOT NULL, -- 'COMMAND' или 'COMPENSATION'
                                             status VARCHAR(32) NOT NULL, -- 'SUCCESS', 'FAILED', 'COMPENSATED'
                                             payload JSONB,
                                             error_message TEXT,
                                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE saga_step_log IS 'Лог выполнения шагов SAGA для аудита';
COMMENT ON COLUMN saga_step_log.saga_id IS 'ID SAGA инстанса';
COMMENT ON COLUMN saga_step_log.step_index IS 'Номер шага';
COMMENT ON COLUMN saga_step_log.step_name IS 'Название шага';
COMMENT ON COLUMN saga_step_log.action IS 'Тип действия: COMMAND или COMPENSATION';
COMMENT ON COLUMN saga_step_log.status IS 'Результат выполнения шага';
COMMENT ON COLUMN saga_step_log.payload IS 'Данные шага';
COMMENT ON COLUMN saga_step_log.error_message IS 'Сообщение об ошибке (если есть)';
COMMENT ON COLUMN saga_step_log.created_at IS 'Время выполнения шага';

-- Индексы для таблицы логов
CREATE INDEX IF NOT EXISTS idx_saga_step_log_saga_id ON saga_step_log(saga_id);
CREATE INDEX IF NOT EXISTS idx_saga_step_log_created_at ON saga_step_log(created_at);

COMMENT ON INDEX idx_saga_step_log_saga_id IS 'Индекс для поиска шагов по SAGA ID';
COMMENT ON INDEX idx_saga_step_log_created_at IS 'Индекс для поиска по времени выполнения';