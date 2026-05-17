package com.example.hookpulse.events.service;

import com.example.hookpulse.auth.domain.Application;
import com.example.hookpulse.auth.repository.ApplicationRepository;
import com.example.hookpulse.delivery.domain.Delivery;
import com.example.hookpulse.delivery.repository.DeliveryRepository;
import com.example.hookpulse.endpoints.domain.EndpointSubscription;
import com.example.hookpulse.endpoints.repository.EndpointSubscriptionRepository;
import com.example.hookpulse.events.domain.Event;
import com.example.hookpulse.events.domain.EventType;
import com.example.hookpulse.events.dto.CreateEventRequest;
import com.example.hookpulse.events.dto.CreateEventResponse;
import com.example.hookpulse.events.repository.EventRepository;
import com.example.hookpulse.events.repository.EventTypeRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventIngestionService {
    private final EventRepository eventRepository;
    private final EventTypeRepository eventTypeRepository;
    private final EndpointSubscriptionRepository endpointSubscriptionRepository;
    private final DeliveryRepository deliveryRepository;
    private final ApplicationRepository applicationRepository;

    public EventIngestionService(EventRepository eventRepository, EventTypeRepository eventTypeRepository,
                                 EndpointSubscriptionRepository endpointSubscriptionRepository1,
                                 DeliveryRepository deliveryRepository, ApplicationRepository applicationRepository) {
        this.eventRepository = eventRepository;
        this.eventTypeRepository = eventTypeRepository;
        this.endpointSubscriptionRepository = endpointSubscriptionRepository1;
        this.deliveryRepository = deliveryRepository;
        this.applicationRepository = applicationRepository;
    }


    public CreateEventResponse createEvent(CreateEventRequest eventRequest, Long applicationId) {
        Application application = applicationRepository.findByAppId(applicationId);

        // check idempotency key
        if(eventRepository.findByApplicationIdAndIdempotencyKey( application.getId(),eventRequest.getidempotencyKey()) != null) {
            throw new DuplicateKeyException("Idempotency key with this application already exists");
        }

        EventType eventType = eventTypeRepository.findByApplicationIdAndName(application.getId(), eventRequest.getEventType());
        if (eventType == null) {
            throw new IllegalArgumentException("This event type has not been registered for this application");
        }

        Event event = new Event(application, eventType, "sourceId", eventRequest.getidempotencyKey(),
                eventRequest.getPayload(), eventRequest.getMetadata());

        eventRepository.save(event);

        List<EndpointSubscription> endpointSubscriptionList =
                endpointSubscriptionRepository.findByEventTypeId(eventType.getId());

        for(EndpointSubscription endpointSubscription : endpointSubscriptionList) {
            Delivery delivery = new Delivery(event, endpointSubscription.getEndpoint());
            deliveryRepository.save(delivery);
        }

        return new CreateEventResponse(eventType,event.getCreatedAt());
    }
}
