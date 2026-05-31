package com.example.hookpulse.shared.exception;

public class IdempotencyConflictException extends RuntimeException {
    public IdempotencyConflictException(String idempotencyKey) {
        super("Event with idempotency key already exists: " + idempotencyKey);
    }
}
