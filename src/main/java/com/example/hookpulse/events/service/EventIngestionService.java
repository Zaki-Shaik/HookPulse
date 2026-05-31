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
import com.example.hookpulse.shared.exception.ApplicationNotFoundException;
import com.example.hookpulse.shared.exception.EventTypeNotFoundException;
import com.example.hookpulse.shared.exception.IdempotencyConflictException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventIngestionService {

    private final EventRepository eventRepository;
    private final EventTypeRepository eventTypeRepository;
    private final EndpointSubscriptionRepository endpointSubscriptionRepository;
    private final DeliveryRepository deliveryRepository;
    private final ApplicationRepository applicationRepository;

    public EventIngestionService(EventRepository eventRepository,
                                 EventTypeRepository eventTypeRepository,
                                 EndpointSubscriptionRepository endpointSubscriptionRepository,
                                 DeliveryRepository deliveryRepository,
                                 ApplicationRepository applicationRepository) {
        this.eventRepository = eventRepository;
        this.eventTypeRepository = eventTypeRepository;
        this.endpointSubscriptionRepository = endpointSubscriptionRepository;
        this.deliveryRepository = deliveryRepository;
        this.applicationRepository = applicationRepository;
    }

    @Transactional
    public CreateEventResponse createEvent(CreateEventRequest request, Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException(applicationId));

        if (eventRepository.findByApplicationIdAndIdempotencyKey(application.getId(), request.idempotencyKey()).isPresent()) {
            throw new IdempotencyConflictException(request.idempotencyKey());
        }

        EventType eventType = eventTypeRepository.findByApplicationIdAndName(application.getId(), request.eventType());
        if (eventType == null) {
            throw new EventTypeNotFoundException(request.eventType());
        }

        Event event = new Event(application, eventType, request.sourceId(), request.idempotencyKey(),
                request.payload(), request.metadata());
        eventRepository.save(event);

        List<EndpointSubscription> subscriptions =
                endpointSubscriptionRepository.findByEventTypeId(eventType.getId());

        for (EndpointSubscription subscription : subscriptions) {
            deliveryRepository.save(new Delivery(event, subscription.getEndpoint()));
        }

        return new CreateEventResponse(event.getId(), eventType.getName(), "accepted", event.getCreatedAt());
    }
}
