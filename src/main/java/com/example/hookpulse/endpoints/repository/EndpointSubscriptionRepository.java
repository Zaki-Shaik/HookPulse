package com.example.hookpulse.endpoints.repository;

import com.example.hookpulse.endpoints.domain.EndpointSubscription;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EndpointSubscriptionRepository extends CrudRepository<EndpointSubscription, Long> {
}
