DROP TABLE IF EXISTS compilation_events CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(512) NOT NULL
);

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE compilations (
    id BIGSERIAL PRIMARY KEY,
    pinned BOOLEAN NOT NULL DEFAULT FALSE,
    title VARCHAR(255) NOT NULL
);

CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    annotation VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_date TIMESTAMP NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT NOW(),
    published_on TIMESTAMP,
    state VARCHAR(50) NOT NULL,
    paid BOOLEAN NOT NULL DEFAULT FALSE,
    participant_limit INT DEFAULT 0,
    request_moderation BOOLEAN NOT NULL DEFAULT TRUE,
    views INT DEFAULT 0,
    lat DOUBLE PRECISION,   -- широта
    lon DOUBLE PRECISION,   -- долгота
    category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    compilation_id BIGINT REFERENCES compilations(id) ON DELETE SET NULL
);

CREATE TABLE requests (
    id BIGSERIAL PRIMARY KEY,
    created TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    requester_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE compilation_events (
    compilation_id BIGINT NOT NULL REFERENCES compilations(id) ON DELETE CASCADE,
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    PRIMARY KEY (compilation_id, event_id)
);