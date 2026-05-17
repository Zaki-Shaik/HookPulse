package com.example.hookpulse.events.repository;

import com.example.hookpulse.events.domain.EventType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventTypeRepository extends CrudRepository<EventType, Long> {

    EventType findByApplicationIdAndName(Long applicationId, String name);
}
