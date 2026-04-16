package com.pot.app.shared.common;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ErrorResponse {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String correlationId;
}
