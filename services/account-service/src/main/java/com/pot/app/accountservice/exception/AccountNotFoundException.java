package com.pot.app.accountservice.exception;

public class AccountNotFoundException extends AccountBusinessException{
    private final static String errorCode = "ACCOUNT_NOT_FOUND";

    public AccountNotFoundException(String userMessage) {
        super(errorCode, userMessage);
    }
}
