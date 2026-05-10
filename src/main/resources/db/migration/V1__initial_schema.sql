CREATE TABLE IF NOT EXISTS applications (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT applications_name_key UNIQUE (name),
    CONSTRAINT applications_status_check CHECK (status IN ('active', 'disabled'))
);

CREATE TABLE IF NOT EXISTS api_keys (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    application_id BIGINT NOT NULL,
    key_prefix VARCHAR(20) NOT NULL,
    hashed_key VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    last_used_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    revoked_at TIMESTAMPTZ,
    CONSTRAINT api_keys_application_fk
        FOREIGN KEY (application_id) REFERENCES applications (id) ON DELETE CASCADE,
    CONSTRAINT api_keys_key_prefix_key UNIQUE (key_prefix),
    CONSTRAINT api_keys_hashed_key_key UNIQUE (hashed_key),
    CONSTRAINT api_keys_status_check CHECK (status IN ('active', 'revoked'))
);

CREATE TABLE IF NOT EXISTS event_types (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    application_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    description TEXT,
    payload_schema JSONB,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT event_types_application_fk
        FOREIGN KEY (application_id) REFERENCES applications (id) ON DELETE CASCADE,
    CONSTRAINT event_types_application_name_key UNIQUE (application_id, name)
);

CREATE TABLE IF NOT EXISTS events (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    application_id BIGINT NOT NULL,
    event_type_id BIGINT NOT NULL,
    source_id VARCHAR(255),
    idempotency_key VARCHAR(255) NOT NULL,
    payload JSONB NOT NULL,
    metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT events_application_fk
        FOREIGN KEY (application_id) REFERENCES applications (id) ON DELETE RESTRICT,
    CONSTRAINT events_event_type_fk
        FOREIGN KEY (event_type_id) REFERENCES event_types (id) ON DELETE RESTRICT,
    CONSTRAINT events_application_idempotency_key UNIQUE (application_id, idempotency_key)
);

CREATE TABLE IF NOT EXISTS webhook_endpoints (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    application_id BIGINT NOT NULL,
    url TEXT NOT NULL,
    description TEXT,
    signing_secret TEXT NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT webhook_endpoints_application_fk
        FOREIGN KEY (application_id) REFERENCES applications (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS endpoint_subscriptions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    endpoint_id BIGINT NOT NULL,
    event_type_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT endpoint_subscriptions_endpoint_fk
        FOREIGN KEY (endpoint_id) REFERENCES webhook_endpoints (id) ON DELETE CASCADE,
    CONSTRAINT endpoint_subscriptions_event_type_fk
        FOREIGN KEY (event_type_id) REFERENCES event_types (id) ON DELETE CASCADE,
    CONSTRAINT endpoint_subscriptions_endpoint_event_type_key UNIQUE (endpoint_id, event_type_id)
);

CREATE TABLE IF NOT EXISTS deliveries (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id BIGINT NOT NULL,
    endpoint_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    attempt_count INT NOT NULL DEFAULT 0,
    next_attempt_at TIMESTAMPTZ,
    last_attempt_at TIMESTAMPTZ,
    final_failure_reason TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT deliveries_event_fk
        FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    CONSTRAINT deliveries_endpoint_fk
        FOREIGN KEY (endpoint_id) REFERENCES webhook_endpoints (id) ON DELETE CASCADE,
    CONSTRAINT deliveries_event_endpoint_key UNIQUE (event_id, endpoint_id),
    CONSTRAINT deliveries_status_check CHECK (
        status IN ('pending', 'in_progress', 'retry_scheduled', 'succeeded', 'failed', 'dead_letter')
    ),
    CONSTRAINT deliveries_attempt_count_check CHECK (attempt_count >= 0)
);

CREATE TABLE IF NOT EXISTS delivery_attempts (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    delivery_id BIGINT NOT NULL,
    attempt_number INT NOT NULL,
    request_headers JSONB NOT NULL DEFAULT '{}'::jsonb,
    request_body JSONB NOT NULL,
    response_status_code INT,
    response_headers JSONB,
    response_body TEXT,
    error_message TEXT,
    latency_ms INT,
    outcome VARCHAR(20) NOT NULL,
    attempted_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT delivery_attempts_delivery_fk
        FOREIGN KEY (delivery_id) REFERENCES deliveries (id) ON DELETE CASCADE,
    CONSTRAINT delivery_attempts_delivery_attempt_number_key UNIQUE (delivery_id, attempt_number),
    CONSTRAINT delivery_attempts_outcome_check CHECK (
        outcome IN ('succeeded', 'http_error', 'timeout', 'network_error')
    ),
    CONSTRAINT delivery_attempts_latency_ms_check CHECK (latency_ms IS NULL OR latency_ms >= 0)
);

CREATE INDEX IF NOT EXISTS idx_api_keys_application_id
    ON api_keys (application_id);

CREATE INDEX IF NOT EXISTS idx_event_types_application_id
    ON event_types (application_id);

CREATE INDEX IF NOT EXISTS idx_events_application_created_at
    ON events (application_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_events_event_type_id
    ON events (event_type_id);

CREATE INDEX IF NOT EXISTS idx_webhook_endpoints_application_id
    ON webhook_endpoints (application_id);

CREATE INDEX IF NOT EXISTS idx_deliveries_status_next_attempt_at
    ON deliveries (status, next_attempt_at);

CREATE INDEX IF NOT EXISTS idx_deliveries_endpoint_id
    ON deliveries (endpoint_id);

CREATE INDEX IF NOT EXISTS idx_delivery_attempts_delivery_id_attempted_at
    ON delivery_attempts (delivery_id, attempted_at DESC);
