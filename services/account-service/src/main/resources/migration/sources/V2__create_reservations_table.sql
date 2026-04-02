-- =====================================================
-- Миграция V2: Создание таблицы резервирований (reservations)
-- Описание: Хранит информацию о заблокированных средствах
-- =====================================================

-- Создание таблицы резервирований
CREATE TABLE IF NOT EXISTS reservations (
    -- Уникальный идентификатор резервирования (UUID)
                                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Сквозной ID транзакции (из внешней системы)
    -- Уникальный для идемпотентности - один и тот же платеж не может быть зарезервирован дважды
                                            transaction_id VARCHAR(64) NOT NULL UNIQUE,

    -- Ссылка на счет пользователя (внешний ключ)
                                            account_id UUID NOT NULL REFERENCES accounts(id) ON DELETE RESTRICT,

    -- Сумма резервирования (точное хранение денег)
                                            amount DECIMAL(20,2) NOT NULL,

    -- Статус резервирования:
    -- ACTIVE    - средства зарезервированы, ожидают подтверждения или отмены
    -- COMMITTED - средства списаны (подтверждены)
    -- CANCELLED - резервирование отменено, средства возвращены
                                            status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',

    -- Время истечения резервирования
    -- По истечении этого времени резерв автоматически освобождается
                                            expires_at TIMESTAMP NOT NULL,

    -- Временные метки для аудита
                                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Констрейнт: сумма не может быть отрицательной
                                            CONSTRAINT check_amount_positive CHECK (amount > 0),

    -- Констрейнт: только допустимые статусы
                                            CONSTRAINT check_status_valid CHECK (status IN ('ACTIVE', 'COMMITTED', 'CANCELLED'))
);

-- Комментарии к таблице и колонкам
COMMENT ON TABLE reservations IS 'Резервирования средств. Хранит временно заблокированные средства перед списанием.';
COMMENT ON COLUMN reservations.id IS 'Уникальный идентификатор резервирования (UUID)';
COMMENT ON COLUMN reservations.transaction_id IS 'Сквозной ID транзакции из внешней системы для идемпотентности';
COMMENT ON COLUMN reservations.account_id IS 'Ссылка на счет пользователя (внешний ключ)';
COMMENT ON COLUMN reservations.amount IS 'Сумма зарезервированных средств';
COMMENT ON COLUMN reservations.status IS 'Статус резервирования: ACTIVE, COMMITTED, CANCELLED';
COMMENT ON COLUMN reservations.expires_at IS 'Время истечения резервирования (автоматическое освобождение)';
COMMENT ON COLUMN reservations.created_at IS 'Дата и время создания резервирования';
COMMENT ON COLUMN reservations.updated_at IS 'Дата и время последнего обновления резервирования';

-- Индексы для производительности
-- Индекс для поиска по account_id (часто используется при запросе всех резервирований пользователя)
CREATE INDEX IF NOT EXISTS idx_reservations_account_id ON reservations(account_id);
COMMENT ON INDEX idx_reservations_account_id IS 'Индекс для поиска резервирований по счету';

-- Индекс для поиска по статусу (часто фильтруют активные резервирования)
CREATE INDEX IF NOT EXISTS idx_reservations_status ON reservations(status);
COMMENT ON INDEX idx_reservations_status IS 'Индекс для фильтрации резервирований по статусу';

-- Индекс для expires_at (нужен для поиска просроченных резервирований)
CREATE INDEX IF NOT EXISTS idx_reservations_expires_at ON reservations(expires_at);
COMMENT ON INDEX idx_reservations_expires_at IS 'Индекс для поиска просроченных резервирований (для автоматического освобождения)';

-- Составной индекс для частого запроса: активные резервирования конкретного счета
CREATE INDEX IF NOT EXISTS idx_reservations_account_status ON reservations(account_id, status);
COMMENT ON INDEX idx_reservations_account_status IS 'Составной индекс для быстрого поиска активных резервирований по счету';

-- Индекс для поиска по transaction_id (уже уникальный, но для документации)
COMMENT ON INDEX reservations_transaction_id_key IS 'Уникальный индекс для идемпотентности по transaction_id';

-- Триггер для автоматического обновления updated_at
CREATE TRIGGER trigger_reservations_updated_at
    BEFORE UPDATE ON reservations
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TRIGGER trigger_reservations_updated_at ON reservations IS 'Автоматически обновляет updated_at при изменении записи';