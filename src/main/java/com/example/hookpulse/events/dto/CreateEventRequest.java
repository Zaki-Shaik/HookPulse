package com.example.hookpulse.events.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class CreateEventRequest {
    private final String eventType;
    private final String sourceId;
    private final String idempotencyKey;
    private final JsonNode payload;
    private final JsonNode metadata;


    public CreateEventRequest(String eventTypeId, JsonNode metadata, JsonNode payload,
                              String idempotencyKey, String sourceId) {
        this.eventType = eventTypeId;
        this.metadata = metadata;
        this.payload = payload;
        this.idempotencyKey = idempotencyKey;
        this.sourceId = sourceId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getidempotencyKey() {
        return idempotencyKey;
    }

    public JsonNode getPayload() {
        return payload;
    }

    public JsonNode getMetadata() {
        return metadata;
    }
}
