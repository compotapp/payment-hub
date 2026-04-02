-- =====================================================
-- Общие функции для всех сервисов
-- =====================================================

-- Функция для безопасного извлечения суммы из JSONB
CREATE OR REPLACE FUNCTION extract_amount_from_jsonb(data JSONB)
    RETURNS DECIMAL(20,2) AS $$
BEGIN
    RETURN COALESCE((data->>'amount')::DECIMAL(20,2), 0);
END;
$$ LANGUAGE plpgsql IMMUTABLE;

COMMENT ON FUNCTION extract_amount_from_jsonb IS 'Безопасно извлекает сумму из JSONB поля';

-- Функция для проверки идемпотентности транзакций
CREATE OR REPLACE FUNCTION is_transaction_processed(transaction_id VARCHAR(64))
    RETURNS BOOLEAN AS $$
DECLARE
    exists_flag BOOLEAN;
BEGIN
    SELECT EXISTS(
        SELECT 1 FROM transactions
        WHERE transaction_id = $1
          AND status IN ('SUCCESS', 'FAILED')
    ) INTO exists_flag;

    RETURN exists_flag;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION is_transaction_processed IS 'Проверяет, была ли транзакция уже обработана';

-- Функция для получения статистики по транзакциям за период
CREATE OR REPLACE FUNCTION get_transaction_stats(
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    p_user_id VARCHAR(64) DEFAULT NULL
)
    RETURNS TABLE(
                     status VARCHAR(32),
                     total_count BIGINT,
                     total_amount DECIMAL(20,2)
                 ) AS $$
BEGIN
    RETURN QUERY
        SELECT
            t.status,
            COUNT(*)::BIGINT as total_count,
            COALESCE(SUM(t.amount), 0) as total_amount
        FROM transactions t
        WHERE t.created_at BETWEEN start_date AND end_date
          AND (p_user_id IS NULL OR t.user_id = p_user_id)
        GROUP BY t.status;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION get_transaction_stats IS 'Возвращает статистику по транзакциям за период';