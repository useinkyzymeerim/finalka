package com.finalka.exception;

public class ProductDeletedException extends RuntimeException {
    public ProductDeletedException(String message) {
        super(message);
    }
}
