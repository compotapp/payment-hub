package com.pot.app.accountservice.exception;

public class ReservationExpiredException extends AccountBusinessException {
    private final static String errorCode = "RESERVATION_EXPIRED";

    public ReservationExpiredException(String userMessage) {
        super(errorCode, userMessage);
    }
}
