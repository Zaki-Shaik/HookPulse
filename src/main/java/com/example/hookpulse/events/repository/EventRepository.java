package com.example.hookpulse.events.repository;

import com.example.hookpulse.events.domain.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {

    Optional<Event> findByApplicationIdAndIdempotencyKey(Long applicationId, String idempotencyKey);
}
