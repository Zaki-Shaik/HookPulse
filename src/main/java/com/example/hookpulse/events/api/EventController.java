package com.example.hookpulse.events.api;


import com.example.hookpulse.events.dto.CreateEventRequest;
import com.example.hookpulse.events.dto.CreateEventResponse;
import com.example.hookpulse.events.service.EventIngestionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/events")
public class EventController {
    private final EventIngestionService eventIngestionService;

    public EventController(EventIngestionService eventIngestionService) {
        this.eventIngestionService = eventIngestionService;
    }


    @PostMapping
    public CreateEventResponse createEvent(@RequestBody CreateEventRequest eventRequest,
                                           @RequestHeader(value = "X-Application-Id", defaultValue = "1L")
                                           Long applicationId) {
        return eventIngestionService.createEvent(eventRequest, applicationId);
    }




}
