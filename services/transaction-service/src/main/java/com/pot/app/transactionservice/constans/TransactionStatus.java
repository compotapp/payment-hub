package com.pot.app.transactionservice.constans;

public class TransactionStatus {

    public static final String PENDING_STATUS = "PENDING"; //создана, ожидает обработки
    public static final String PROCESSING_STATUS = "PROCESSING"; //в процессе обработки (резервирование средств)
    public static final String SUCCESS_STATUS = "SUCCESS"; //успешно выполнена
    public static final String FAILED_STATUS = "FAILED"; //ошибка при выполнении
    public static final String COMPENSATED_STATUS = "COMPENSATED"; //откачена (компенсирована)
}
