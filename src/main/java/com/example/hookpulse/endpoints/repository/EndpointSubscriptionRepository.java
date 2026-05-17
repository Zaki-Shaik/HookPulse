package com.example.hookpulse.endpoints.repository;

import com.example.hookpulse.endpoints.domain.EndpointSubscription;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EndpointSubscriptionRepository extends CrudRepository<EndpointSubscription, Long> {

    List<EndpointSubscription> findByEventTypeId(Long eventTypeId);
}
