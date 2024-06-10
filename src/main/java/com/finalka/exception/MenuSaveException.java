package com.finalka.exception;

public class MenuSaveException extends RuntimeException {
    public MenuSaveException(String message) {
        super(message);
    }

    public MenuSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}