package com.example.hookpulse.events.domain;

import com.example.hookpulse.auth.domain.Application;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne
    @JoinColumn(name = "event_type_id", nullable = false)
    private EventType eventType;

    @Column(name = "source_id")
    private String sourceId;

    @Column(name = "idempotency_key", nullable = false)
    private String idempotencyKey;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private JsonNode payload;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private JsonNode metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;


    public Event() {
    }


    public Event(
        Application application,
        EventType eventType,
        String sourceId,
        String idempotencyKey,
        JsonNode payload,
        JsonNode metadata
    ) {
        this.application = application;
        this.eventType = eventType;
        this.sourceId = sourceId;
        this.idempotencyKey = idempotencyKey;
        this.payload = payload;
        this.metadata = metadata;
    }

    public Long getId() {
        return id;
    }

    public Application getApplication() {
        return application;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public JsonNode getPayload() {
        return payload;
    }

    public JsonNode getMetadata() {
        return metadata;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
