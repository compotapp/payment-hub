package com.pot.app.shared.dto.event;

import lombok.Getter;
import lombok.Setter;

/**
 * Событие об изменении баланса.
 * Публикуется Account Service в топик balance-events.
 */
@Getter @Setter
public class BalanceEvent extends BaseEvent {

    // Типы изменений
    public static final String TYPE_RESERVED = "BALANCE_RESERVED";
    public static final String TYPE_COMMITTED = "BALANCE_COMMITTED";
    public static final String TYPE_CANCELLED = "BALANCE_CANCELLED";

    private String userId;
    private long oldBalance;      // Баланс до изменения
    private long newBalance;      // Баланс после изменения
    private long changeAmount;    // На сколько изменился (со знаком)
    private String changeType;    // RESERVATION, COMMIT, CANCEL
    private String reservationId; // ID резервирования (если есть)

    public BalanceEvent() {
        super();
    }
}
