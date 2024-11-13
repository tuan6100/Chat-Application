package com.chat.app.exception;

import java.time.LocalDateTime;

public class ErrorDetail {
    private String error;
    private String message;
    private LocalDateTime timestamp;

    public ErrorDetail(String error, String message, LocalDateTime timestamp) {
        this.error = error;
        this.message = message;
        this.timestamp = timestamp;
    }
}
