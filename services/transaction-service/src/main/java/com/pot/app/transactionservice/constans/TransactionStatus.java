package com.pot.app.transactionservice.constans;

public class TransactionStatus {

    public static final String PENDING = "PENDING"; //создана, ожидает обработки
    public static final String PROCESSING = "PROCESSING"; //в процессе обработки (резервирование средств)
    public static final String SUCCESS = "SUCCESS"; //успешно выполнена
    public static final String FAILED = "FAILED"; //ошибка при выполнении
    public static final String COMPENSATED = "COMPENSATED"; //откачена (компенсирована)
}
