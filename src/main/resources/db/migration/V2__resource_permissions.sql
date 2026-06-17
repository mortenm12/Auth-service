CREATE TYPE permission_level AS ENUM ('READ', 'WRITE', 'ADMIN');

CREATE TABLE resource_permissions (
    id            UUID             NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id       UUID             NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    resource_type VARCHAR(100)     NOT NULL,
    resource_id   VARCHAR(255)     NOT NULL,
    level         permission_level NOT NULL,
    granted_by    UUID             NOT NULL REFERENCES users(id),
    created_at    TIMESTAMPTZ      NOT NULL DEFAULT now(),
    CONSTRAINT uk_rp_user_resource UNIQUE (user_id, resource_type, resource_id)
);

CREATE INDEX idx_rp_resource ON resource_permissions (resource_type, resource_id);
CREATE INDEX idx_rp_user ON resource_permissions (user_id);
