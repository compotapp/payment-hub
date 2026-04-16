package com.pot.app.accountservice.exception;

// для случая, когда optimistic lock сработал 3 раза и мы сдались
public class ConcurrentModificationException extends AccountBusinessException {
    private final static String errorCode = "CONCURRENT_MODIFICATION";

    public ConcurrentModificationException(String userMessage) {
        super(errorCode, userMessage);
    }
}
