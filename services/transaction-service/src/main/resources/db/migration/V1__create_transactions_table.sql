--V1__create_transactions_table.sql
-- =====================================================
-- Миграция V1: Создание таблицы транзакций
-- Сервис: Transaction Service
-- Описание: Хранит все платежные транзакции
-- =====================================================

-- Создание таблицы транзакций
CREATE TABLE IF NOT EXISTS transactions (
    -- Уникальный идентификатор транзакции (UUID)
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Сквозной ID транзакции из внешней системы (клиента)
    -- Уникальный для идемпотентности - предотвращает дублирование платежей
    transaction_id VARCHAR(64) NOT NULL UNIQUE,

    -- ID пользователя (для будущего шардирования и поиска)
    user_id VARCHAR(64) NOT NULL,

    -- Сумма транзакции (точное хранение денежных сумм)
    amount DECIMAL(20,2) NOT NULL,

    -- Статус транзакции:
    -- PENDING     - создана, ожидает обработки
    -- PROCESSING  - в процессе обработки (резервирование средств)
    -- SUCCESS     - успешно выполнена
    -- FAILED      - ошибка при выполнении
    -- COMPENSATED - откачена (компенсирована)
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',

    -- Тип транзакции:
    -- PAYMENT     - платеж
    -- REFUND      - возврат средств
    -- HOLD        - блокировка средств
    -- RELEASE     - освобождение средств
    type VARCHAR(32) NOT NULL,

    -- Дополнительные данные в формате JSON
    -- Например: { "reservation_id": "uuid", "description": "Оплата заказа #123" }
    metadata JSONB DEFAULT '{}',

    -- Временные метки для аудита
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Констрейнты
    CONSTRAINT check_amount_positive CHECK (amount > 0),
    CONSTRAINT check_status_valid CHECK (status IN ('PENDING', 'PROCESSING', 'SUCCESS', 'FAILED', 'COMPENSATED')),
    CONSTRAINT check_type_valid CHECK (type IN ('PAYMENT', 'REFUND', 'HOLD', 'RELEASE'))
);

-- Комментарии к таблице и колонкам
COMMENT ON TABLE transactions IS 'Платежные транзакции. Хранит все операции по движению средств.';
COMMENT ON COLUMN transactions.id IS 'Уникальный идентификатор транзакции (UUID)';
COMMENT ON COLUMN transactions.transaction_id IS 'Сквозной ID транзакции из внешней системы (для идемпотентности)';
COMMENT ON COLUMN transactions.user_id IS 'ID пользователя (используется для шардирования)';
COMMENT ON COLUMN transactions.amount IS 'Сумма транзакции в денежных единицах';
COMMENT ON COLUMN transactions.status IS 'Статус: PENDING, PROCESSING, SUCCESS, FAILED, COMPENSATED';
COMMENT ON COLUMN transactions.type IS 'Тип: PAYMENT, REFUND, HOLD, RELEASE';
COMMENT ON COLUMN transactions.metadata IS 'Дополнительные данные в JSON формате';
COMMENT ON COLUMN transactions.created_at IS 'Дата и время создания транзакции';
COMMENT ON COLUMN transactions.updated_at IS 'Дата и время последнего обновления';

-- Индексы для производительности
-- Индекс для поиска по user_id (нужен для запросов истории пользователя)
CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON transactions(user_id);
COMMENT ON INDEX idx_transactions_user_id IS 'Индекс для поиска транзакций по пользователю';

-- Индекс для фильтрации по статусу
CREATE INDEX IF NOT EXISTS idx_transactions_status ON transactions(status);
COMMENT ON INDEX idx_transactions_status IS 'Индекс для фильтрации транзакций по статусу';

-- Индекс для поиска по диапазону дат (для партиционирования)
CREATE INDEX IF NOT EXISTS idx_transactions_created_at ON transactions(created_at);
COMMENT ON INDEX idx_transactions_created_at IS 'Индекс для поиска по диапазону дат (поддержка партиционирования)';

-- Составной индекс для частых запросов
CREATE INDEX IF NOT EXISTS idx_transactions_user_status ON transactions(user_id, status);
COMMENT ON INDEX idx_transactions_user_status IS 'Составной индекс для запросов истории пользователя с фильтром по статусу';

-- Индекс для поиска по типу транзакции
CREATE INDEX IF NOT EXISTS idx_transactions_type ON transactions(type);
COMMENT ON INDEX idx_transactions_type IS 'Индекс для фильтрации по типу транзакции';

-- Индекс для поиска по created_at + status (для мониторинга)
CREATE INDEX IF NOT EXISTS idx_transactions_created_status ON transactions(created_at, status);
COMMENT ON INDEX idx_transactions_created_status IS 'Индекс для мониторинга транзакций по дате и статусу';