CREATE TABLE IF NOT EXISTS event (
    id BIGSERIAL PRIMARY KEY,
    picture_url VARCHAR(500),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    place VARCHAR(500),
    time TEXT NOT NULL,
    additional_info TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_event_created_at ON event(created_at);
