CREATE TABLE terms_acceptances (
    id               UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          UUID         NOT NULL,
    terms_accepted   BOOLEAN      NOT NULL,
    privacy_accepted BOOLEAN      NOT NULL,
    terms_version    VARCHAR(50)  NOT NULL DEFAULT '1.0',
    privacy_version  VARCHAR(50)  NOT NULL DEFAULT '1.0',
    ip_address       VARCHAR(45),
    user_agent       TEXT,
    accepted_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_terms_acceptances_user_id ON terms_acceptances(user_id);
