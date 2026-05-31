package com.example.hookpulse.shared.exception;

public class EventTypeNotFoundException extends RuntimeException {
    public EventTypeNotFoundException(String eventTypeName) {
        super("Event type not registered for this application: " + eventTypeName);
    }
}
