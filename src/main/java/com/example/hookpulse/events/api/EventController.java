package com.example.hookpulse.events.api;

import com.example.hookpulse.events.dto.CreateEventRequest;
import com.example.hookpulse.events.dto.CreateEventResponse;
import com.example.hookpulse.events.service.EventIngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/events")
@Tag(name = "Events", description = "Ingest events for delivery to subscribed webhook endpoints")
public class EventController {

    private final EventIngestionService eventIngestionService;

    public EventController(EventIngestionService eventIngestionService) {
        this.eventIngestionService = eventIngestionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(
        summary = "Ingest an event",
        description = "Accepts an event, persists it, and fans out delivery jobs to all subscribed endpoints. " +
                      "Idempotent: re-submitting the same idempotencyKey returns 409."
    )
    public CreateEventResponse createEvent(
            @Valid @RequestBody CreateEventRequest request,
            @Parameter(description = "Tenant application ID (Phase 0 stub — replaced by API key auth in Phase 1)")
            @RequestHeader(value = "X-Application-Id", defaultValue = "1") Long applicationId) {
        return eventIngestionService.createEvent(request, applicationId);
    }
}
