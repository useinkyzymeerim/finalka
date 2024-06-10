package com.finalka.exception;

public class InvalidReminderTimeException extends RuntimeException {
    public InvalidReminderTimeException(String message) {
        super(message);
    }
}
