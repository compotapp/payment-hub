package com.pot.app.shared.constans;

public final class OutboxConstants {

    private OutboxConstants() {

    }

    public static final class OutboxStatus {
        public static String PENDING = "PENDING"; //ожидает
        public static String PROCESSING = "PROCESSING"; //обрабатывается
        public static String COMPLETED = "COMPLETED"; //успешно отправлено
        public static String FAILED = "FAILED"; //ошибка

        private OutboxStatus() {

        }
    }
}
