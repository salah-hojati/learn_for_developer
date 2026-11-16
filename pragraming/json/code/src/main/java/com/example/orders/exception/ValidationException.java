package com.example.orders.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) { super(message); }
}