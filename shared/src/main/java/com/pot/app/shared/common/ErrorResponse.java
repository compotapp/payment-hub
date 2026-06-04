package com.pot.app.shared.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String correlationId;

    // Для validation errors (опционально)
    private Map<String, String> validationErrors;

    // Конструктор без validationErrors для обычных ошибок
    public ErrorResponse(String timestamp, int status, String error,
                         String message, String path, String correlationId) {
        this(timestamp, status, error, message, path, correlationId, null);
    }
}