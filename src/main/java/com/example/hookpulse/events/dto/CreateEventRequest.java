package com.example.hookpulse.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateEventRequest(
        @JsonProperty("eventType") @NotBlank(message = "eventType must not be blank") String eventType,
        @JsonProperty("sourceId") String sourceId,
        @JsonProperty("idempotencyKey") @NotBlank(message = "idempotencyKey must not be blank") String idempotencyKey,
        @JsonProperty("payload") @NotNull(message = "payload must not be null") JsonNode payload,
        @JsonProperty("metadata") JsonNode metadata
) {}
