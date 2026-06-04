package com.pot.app.shared.constans;

/**
 * Константы для Saga Orchestrator.
 * Используются во всех сервисах-участниках.
 */
public final class SagaConstants {
    
    private SagaConstants() {
        // Запрещаем создание экземпляров
    }
    
    // ===== Типы команд =====
    public static final class Commands {
        private Commands() {}
        
        // Account Service команды
        public static final String RESERVE_FUNDS = "RESERVE_FUNDS";
        public static final String COMMIT_FUNDS = "COMMIT_FUNDS";
        public static final String CANCEL_RESERVATION = "CANCEL_RESERVATION";
        
        // Transaction Service команды
        public static final String PROCESS_PAYMENT = "PROCESS_PAYMENT";
        
        // Notification Service команды (опционально)
        public static final String SEND_NOTIFICATION = "SEND_NOTIFICATION";
    }
    
    // ===== Типы событий =====
    public static final class Events {
        private Events() {}
        
        // Account Service события
        public static final String FUNDS_RESERVED = "FUNDS_RESERVED";
        public static final String FUNDS_COMMITTED = "FUNDS_COMMITTED";
        public static final String RESERVATION_CANCELLED = "RESERVATION_CANCELLED";
        
        // Transaction Service события
        public static final String PAYMENT_PROCESSED = "PAYMENT_PROCESSED";
        public static final String PAYMENT_FAILED = "PAYMENT_FAILED";
        
        // Общие события
        public static final String ERROR = "ERROR";
    }
    
    // ===== Статусы саги =====
    public static final class SagaStatus {
        private SagaStatus() {}
        
        public static final String STARTED = "STARTED";
        public static final String IN_PROGRESS = "IN_PROGRESS";
        public static final String COMPLETED = "COMPLETED";
        public static final String COMPENSATING = "COMPENSATING";
        public static final String COMPENSATED = "COMPENSATED";
        public static final String FAILED = "FAILED";
    }
    
    // ===== Типы саг =====
    public static final class SagaType {
        private SagaType() {}
        
        public static final String PAYMENT_SAGA = "PAYMENT_SAGA";
        public static final String REFUND_SAGA = "REFUND_SAGA";
    }
}