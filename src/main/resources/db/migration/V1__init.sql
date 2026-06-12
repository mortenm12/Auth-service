CREATE TABLE users
(
    id            UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    email         VARCHAR(255) NOT NULL,
    username      VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name    VARCHAR(100),
    last_name     VARCHAR(100),
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    version       BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_username UNIQUE (username)
);

CREATE TABLE organizations
(
    id         UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    slug       VARCHAR(100) NOT NULL,
    parent_id  UUID,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    version    BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT uk_organizations_slug UNIQUE (slug),
    CONSTRAINT fk_organizations_parent FOREIGN KEY (parent_id) REFERENCES organizations (id)
);

CREATE TABLE roles
(
    id          UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uk_roles_name UNIQUE (name)
);

CREATE TABLE user_organization_roles
(
    id              UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id         UUID        NOT NULL,
    organization_id UUID        NOT NULL,
    role_id         UUID        NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT fk_uor_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_uor_organization FOREIGN KEY (organization_id) REFERENCES organizations (id) ON DELETE CASCADE,
    CONSTRAINT fk_uor_role FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT uk_uor_user_organization UNIQUE (user_id, organization_id)
);

CREATE TABLE api_keys
(
    id              UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    key_prefix      VARCHAR(10)  NOT NULL,
    key_hash        VARCHAR(255) NOT NULL,
    scopes          VARCHAR(1000),
    organization_id UUID,
    user_id         UUID,
    expires_at      TIMESTAMPTZ,
    last_used_at    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    version         BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT uk_api_keys_key_hash UNIQUE (key_hash),
    CONSTRAINT fk_api_keys_organization FOREIGN KEY (organization_id) REFERENCES organizations (id) ON DELETE CASCADE,
    CONSTRAINT fk_api_keys_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_organizations_slug ON organizations (slug);
CREATE INDEX idx_organizations_parent_id ON organizations (parent_id);
CREATE INDEX idx_uor_user_id ON user_organization_roles (user_id);
CREATE INDEX idx_uor_organization_id ON user_organization_roles (organization_id);
CREATE INDEX idx_api_keys_key_hash ON api_keys (key_hash);

INSERT INTO roles (name, description)
VALUES ('ADMIN', 'Full administrative access'),
       ('MEMBER', 'Standard member access'),
       ('VIEWER', 'Read-only access');
