package com.example.hookpulse.endpoints.repository;

import com.example.hookpulse.endpoints.domain.WebhookEndpoint;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebhookEndpointRepository extends CrudRepository<WebhookEndpoint, Long> {
}
