-- V1__create_accounts_table.sql
-- =====================================================
-- Миграция V1: Создание таблицы счетов (accounts)
-- Описание: Хранит балансы пользователей с оптимистичной блокировкой
-- =====================================================

-- Создание таблицы счетов
CREATE TABLE IF NOT EXISTS accounts (
    -- Уникальный идентификатор счета (UUID)
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Внешний идентификатор пользователя (из системы аутентификации)
    -- Уникальный, т.к. одному пользователю соответствует один счет
    user_id VARCHAR(64) NOT NULL UNIQUE,

    -- Доступный баланс (средства, которые можно использовать)
    -- Используем DECIMAL для точного хранения денежных сумм
    -- DEFAULT 0 - новый счет открывается с нулевым балансом
    available_balance DECIMAL(20,2) NOT NULL DEFAULT 0.00,

    -- Зарезервированный баланс (средства, заблокированные для будущих списаний)
    reserved_balance DECIMAL(20,2) NOT NULL DEFAULT 0.00,

    -- Версия для оптимистичной блокировки
    -- При каждом обновлении увеличивается, предотвращает конкурентные изменения
    version BIGINT NOT NULL DEFAULT 0,

    -- Временные метки для аудита
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Констрейнт: балансы не могут быть отрицательными
    CONSTRAINT check_available_balance_non_negative CHECK (available_balance >= 0),
    CONSTRAINT check_reserved_balance_non_negative CHECK (reserved_balance >= 0)
    );

-- Комментарии к таблице и колонкам (документация в БД)
COMMENT ON TABLE accounts IS 'Счета пользователей. Хранит доступный и зарезервированный баланс.';
COMMENT ON COLUMN accounts.id IS 'Уникальный идентификатор счета (UUID)';
COMMENT ON COLUMN accounts.user_id IS 'Внешний идентификатор пользователя из системы аутентификации';
COMMENT ON COLUMN accounts.available_balance IS 'Доступный баланс - средства, которые можно использовать для платежей';
COMMENT ON COLUMN accounts.reserved_balance IS 'Зарезервированный баланс - средства, заблокированные для будущих списаний';
COMMENT ON COLUMN accounts.version IS 'Версия для оптимистичной блокировки (предотвращает конкурентные изменения)';
COMMENT ON COLUMN accounts.created_at IS 'Дата и время создания счета';
COMMENT ON COLUMN accounts.updated_at IS 'Дата и время последнего обновления счета';

-- Индекс для быстрого поиска по user_id (уже уникальный, но индекс нужен)
-- UNIQUE создает индекс автоматически, но добавим комментарий
COMMENT ON INDEX accounts_user_id_key IS 'Уникальный индекс для быстрого поиска счета по пользователю';

-- Создаем индекс для updated_at (полезно для поиска недавно измененных счетов)
CREATE INDEX IF NOT EXISTS idx_accounts_updated_at ON accounts(updated_at);
COMMENT ON INDEX idx_accounts_updated_at IS 'Индекс для поиска счетов по дате последнего обновления';