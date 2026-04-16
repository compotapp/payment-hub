package com.pot.app.shared.dto.event;

import lombok.Getter;
import lombok.Setter;

/**
 * Событие о платеже.
 * Публикуется Transaction Service в топик payment-events.
 */
@Getter @Setter
public class PaymentEvent extends BaseEvent {

    // Типы событий для этого класса
    public static final String TYPE_CREATED = "PAYMENT_CREATED";
    public static final String TYPE_PROCESSING = "PAYMENT_PROCESSING";
    public static final String TYPE_SUCCESS = "PAYMENT_SUCCESS";
    public static final String TYPE_FAILED = "PAYMENT_FAILED";
    public static final String TYPE_COMPENSATED = "PAYMENT_COMPENSATED";

    private String userId;
    private long amount;        // в копейках/центах
    private String status;      // PENDING, SUCCESS, FAILED
    private String paymentId;

    public PaymentEvent() {
        super();
    }

    // Конструктор для быстрого создания
    public PaymentEvent(String transactionId, String userId, long amount, String status, String source) {
        super(TYPE_CREATED, transactionId, source);
        this.userId = userId;
        this.amount = amount;
        this.status = status;
    }
}
