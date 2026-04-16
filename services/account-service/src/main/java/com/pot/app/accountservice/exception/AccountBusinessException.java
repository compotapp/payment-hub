package com.pot.app.accountservice.exception;

import lombok.Getter;

// Базовое исключение для всех бизнес-ошибок
@Getter
class AccountBusinessException extends RuntimeException {
    private final String errorCode;
    private final String userMessage;

    AccountBusinessException(String errorCode, String userMessage) {
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
}
