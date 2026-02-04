CREATE TABLE users
(
    id          UUID PRIMARY KEY,
    created_at  TIMESTAMP NOT NULL,
    created_by  VARCHAR(255),
    updated_at  TIMESTAMP,
    updated_by  VARCHAR(255),
    deleted_at  TIMESTAMP,
    deleted_by  VARCHAR(255),
    external_id VARCHAR(255) NOT NULL UNIQUE,
    email       VARCHAR(255) NOT NULL UNIQUE,
    first_name  VARCHAR(255) NOT NULL,
    last_name   VARCHAR(255) NOT NULL,
    tenant_id   VARCHAR(255) NOT NULL,
    is_active   BOOLEAN DEFAULT true
);

CREATE INDEX idx_users_email
    ON users (email);

CREATE INDEX idx_users_external_id
    ON users (external_id);