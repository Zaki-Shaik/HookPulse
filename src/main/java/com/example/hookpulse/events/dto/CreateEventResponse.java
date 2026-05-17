package com.example.hookpulse.events.dto;

import com.example.hookpulse.events.domain.EventType;

import java.time.OffsetDateTime;

public class CreateEventResponse {
    private EventType eventType;
    private OffsetDateTime createdAt;


    public CreateEventResponse(EventType eventType, OffsetDateTime createdAt) {
        this.eventType = eventType;
        this.createdAt = createdAt;
    }

    public EventType getEventType() {
        return eventType;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
