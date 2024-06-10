package com.finalka.exception;

public class RecipeServiceException extends RuntimeException {
    public RecipeServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
