-- =====================================================
-- Миграция V3: Дополнительные индексы и констрейнты
-- Описание: Улучшение производительности и целостности данных
-- =====================================================

-- Дополнительный индекс для поиска по user_id + status
-- Часто используется: "найти все активные резервирования пользователя"
CREATE INDEX IF NOT EXISTS idx_accounts_user_status ON accounts(user_id);
COMMENT ON INDEX idx_accounts_user_status IS 'Индекс для быстрого поиска счета по user_id';

-- Индекс для поиска по диапазону дат создания
CREATE INDEX IF NOT EXISTS idx_reservations_created_range ON reservations(created_at);
COMMENT ON INDEX idx_reservations_created_range IS 'Индекс для поиска резервирований по диапазону дат создания';

-- Частичный индекс для активных резервирований (только ACTIVE)
-- Экономит место и ускоряет запросы по активным резервированиям
CREATE INDEX IF NOT EXISTS idx_reservations_active ON reservations(account_id, expires_at)
    WHERE status = 'ACTIVE';
COMMENT ON INDEX idx_reservations_active IS 'Частичный индекс только для активных резервирований (оптимизация)';

-- Индекс для поиска просроченных активных резервирований
-- Используется фоновым джобом для очистки
CREATE INDEX IF NOT EXISTS idx_reservations_expired_active ON reservations(expires_at, status)
    WHERE status = 'ACTIVE' AND expires_at < CURRENT_TIMESTAMP;
COMMENT ON INDEX idx_reservations_expired_active IS 'Индекс для быстрого поиска просроченных активных резервирований';

-- Добавляем внешний ключ с каскадным обновлением
-- При обновлении account_id в таблице accounts, обновляется и в reservations
ALTER TABLE reservations
    DROP CONSTRAINT IF EXISTS reservations_account_id_fkey,
    ADD CONSTRAINT reservations_account_id_fkey
        FOREIGN KEY (account_id) REFERENCES accounts(id)
            ON DELETE RESTRICT
            ON UPDATE CASCADE;

-- Функция для автоматического освобождения просроченных резервирований
-- (может вызываться по расписанию)
CREATE OR REPLACE FUNCTION release_expired_reservations()
    RETURNS INTEGER AS $$
DECLARE
    released_count INTEGER;
BEGIN
    WITH expired AS (
        UPDATE reservations r
            SET status = 'CANCELLED',
                updated_at = CURRENT_TIMESTAMP
            FROM accounts a
            WHERE r.account_id = a.id
                AND r.status = 'ACTIVE'
                AND r.expires_at < CURRENT_TIMESTAMP
            RETURNING r.account_id, r.amount
    )
    UPDATE accounts a
    SET available_balance = a.available_balance + e.amount,
        reserved_balance = a.reserved_balance - e.amount,
        version = a.version + 1
    FROM expired e
    WHERE a.id = e.account_id;

    GET DIAGNOSTICS released_count = ROW_COUNT;
    RETURN released_count;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION release_expired_reservations() IS 'Освобождает просроченные резервирования и возвращает средства на счета';

-- Создаем view для удобного просмотра баланса пользователя с учетом резервирований
CREATE OR REPLACE VIEW user_balance_summary AS
SELECT
    a.user_id,
    a.available_balance,
    a.reserved_balance,
    a.available_balance + a.reserved_balance AS total_balance,
    COALESCE((
                 SELECT SUM(r.amount)
                 FROM reservations r
                 WHERE r.account_id = a.id AND r.status = 'ACTIVE'
             ), 0) AS active_reservations_sum,
    COUNT(r.id) AS active_reservations_count
FROM accounts a
         LEFT JOIN reservations r ON r.account_id = a.id AND r.status = 'ACTIVE'
GROUP BY a.id, a.user_id, a.available_balance, a.reserved_balance;

COMMENT ON VIEW user_balance_summary IS 'Представление для удобного просмотра баланса пользователя с учетом активных резервирований';