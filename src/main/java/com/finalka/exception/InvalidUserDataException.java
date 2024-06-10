package com.finalka.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class InvalidUserDataException extends Exception {
    public InvalidUserDataException(String message) {
        super(message);
    }

    public InvalidUserDataException(String message, DataIntegrityViolationException e) {
    }
}
