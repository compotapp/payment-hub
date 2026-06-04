--V2__create_outbox_table.sql

CREATE TABLE IF NOT EXISTS outbox
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_type      VARCHAR(64) NOT NULL,
    transaction_id  VARCHAR(64) NOT NULL,
    payload         TEXT        NOT NULL,
    status          VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    retry_count     INT         NOT NULL DEFAULT 0,
    created_at      TIMESTAMP   NOT NULL,
    next_attempt_at TIMESTAMP   NOT NULL
);

-- Комментарии к таблице и колонкам
COMMENT ON TABLE outbox IS 'Таблица для паттерна Transactional Outbox. Обеспечивает атомарную публикацию событий при изменении бизнес-данных';
COMMENT ON COLUMN outbox.id IS 'Уникальный идентификатор события (UUID)';
COMMENT ON COLUMN outbox.event_type IS 'Тип события, например: ORDER_CREATED, PAYMENT_COMPLETED, USER_REGISTERED';
COMMENT ON COLUMN outbox.transaction_id IS 'Идентификатор бизнес-транзакции для трассировки, дедупликации и группировки событий';
COMMENT ON COLUMN outbox.payload IS 'Тело события в формате JSON, содержащее сериализованные бизнес-данные';
COMMENT ON COLUMN outbox.status IS 'Статус обработки события: PENDING (ожидает), PROCESSING (обрабатывается), COMPLETED (успешно отправлено), FAILED (ошибка)';
COMMENT ON COLUMN outbox.retry_count IS 'Количество попыток отправки события. Используется для экспоненциальной задержки';
COMMENT ON COLUMN outbox.created_at IS 'Время создания события (UTC). Заполняется при вставке записи';
COMMENT ON COLUMN outbox.next_attempt_at IS 'Время следующей попытки отправки (UTC). Для немедленной отправки = created_at';

-- Индексы для poller
CREATE INDEX idx_outbox_pending ON outbox (status, next_attempt_at) WHERE status = 'PENDING';
COMMENT ON INDEX idx_outbox_pending IS 'Частичный индекс для быстрого поиска событий, готовых к отправке. Учитывает только статус PENDING и сортировку по времени следующей попытки';


