package com.pot.app.accountservice.dto;

import java.util.UUID;

public record ReservationResult(
        boolean success,
        String reservationId,
        String message
) {
    // Фабричные методы — хороший тон
    public static ReservationResult success(UUID reservationId, String message) {
        return new ReservationResult(true, reservationId.toString(), message);
    }

    public static ReservationResult failure(String message) {
        return new ReservationResult(false, null, message);
    }
}
