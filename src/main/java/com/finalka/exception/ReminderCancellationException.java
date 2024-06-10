package com.finalka.exception;

public class ReminderCancellationException extends RuntimeException {
    public ReminderCancellationException(String message, Throwable cause) {
        super(message, cause);
    }
}
