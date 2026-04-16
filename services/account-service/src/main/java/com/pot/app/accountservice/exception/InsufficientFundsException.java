package com.pot.app.accountservice.exception;

public class InsufficientFundsException extends AccountBusinessException {
    private final static String errorCode = "INSUFFICIENT_FUNDS";

    public InsufficientFundsException(String userMessage) {
        super(errorCode, userMessage);
    }
}
