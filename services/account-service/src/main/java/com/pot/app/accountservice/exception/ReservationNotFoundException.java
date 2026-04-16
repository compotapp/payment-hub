package com.pot.app.accountservice.exception;

public class ReservationNotFoundException extends AccountBusinessException {
    private final static String errorCode = "RESERVATION_NOT_FOUND";

    ReservationNotFoundException(String userMessage) {
        super(errorCode, userMessage);
    }
}
