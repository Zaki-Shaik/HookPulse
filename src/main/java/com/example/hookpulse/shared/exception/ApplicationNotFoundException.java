package com.example.hookpulse.shared.exception;

public class ApplicationNotFoundException extends RuntimeException {
    public ApplicationNotFoundException(Long applicationId) {
        super("Application not found: " + applicationId);
    }
}
