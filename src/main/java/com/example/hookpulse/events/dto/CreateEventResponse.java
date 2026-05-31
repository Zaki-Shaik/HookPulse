package com.example.hookpulse.events.dto;

import java.time.OffsetDateTime;

public record CreateEventResponse(
        Long eventId,
        String eventType,
        String status,
        OffsetDateTime createdAt
) {}
