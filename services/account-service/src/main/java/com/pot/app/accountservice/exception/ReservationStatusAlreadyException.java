package com.pot.app.accountservice.exception;

import com.pot.app.accountservice.entity.Reservation.ReservationStatus;

public class ReservationStatusAlreadyException extends AccountBusinessException {
    private final static String errorCode = "RESERVATION_ALREADY_";

    public ReservationStatusAlreadyException(ReservationStatus status, String userMessage) {
        super(errorCode + status, userMessage);
    }
}
