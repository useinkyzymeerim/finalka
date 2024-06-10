package com.finalka.exception;

public class InvalidProductDataException extends Exception {
    public InvalidProductDataException(String message, Throwable cause) {
        super(message, cause);
    }
}