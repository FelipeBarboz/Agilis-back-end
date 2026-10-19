CREATE TABLE store_invites (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    store_id UUID NOT NULL REFERENCES provider_profiles(id) ON DELETE CASCADE,
    code VARCHAR(50) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL DEFAULT 'EMPLOYEE',
    created_by UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expires_at TIMESTAMP,
    max_uses INT,
    used_count INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_store_invites_store_id ON store_invites(store_id);
CREATE INDEX idx_store_invites_code ON store_invites(code);
