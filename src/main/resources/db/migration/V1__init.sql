CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TYPE store_role        AS ENUM ('owner', 'admin', 'employee');
CREATE TYPE booking_status    AS ENUM ('pending', 'confirmed', 'cancelled', 'completed');
CREATE TYPE negotiation_status AS ENUM ('pending', 'accepted', 'rejected', 'countered');

CREATE TABLE users (
  id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
  name        VARCHAR(255)  NOT NULL,
  email       VARCHAR(255)  NOT NULL UNIQUE,
  phone       CHAR(20),
  created_at  TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE user_images (
  id        UUID  PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id   UUID  NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  image_url TEXT  NOT NULL
);

CREATE TABLE addresses (
  id          UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id     UUID          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  street      VARCHAR(255)  NOT NULL,
  number      VARCHAR(20)   NOT NULL,
  complement  VARCHAR(100),
  city        VARCHAR(100)  NOT NULL,
  state       CHAR(2)       NOT NULL,
  cep         CHAR(9)       NOT NULL
);

CREATE TABLE clients (
  fk_user_id  UUID     PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
  cpf         CHAR(14) NOT NULL UNIQUE
);

CREATE TABLE provider_profiles (
  id               UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
  store_name       VARCHAR(255)  NOT NULL,
  slug             VARCHAR(255)  NOT NULL UNIQUE,
  description      TEXT,
  profile_img_url  TEXT,
  created_at       TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE providers (
  fk_user_id               UUID     PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
  cnpj                     CHAR(18) NOT NULL UNIQUE,
  fk_provider_profiles_id  UUID     NOT NULL REFERENCES provider_profiles(id) ON DELETE RESTRICT
);

CREATE TABLE store_memberships (
  id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
  store_id    UUID        NOT NULL REFERENCES provider_profiles(id) ON DELETE CASCADE,
  provider_id UUID        NOT NULL REFERENCES providers(fk_user_id) ON DELETE CASCADE,
  role        store_role  NOT NULL DEFAULT 'employee',
  invited_by  UUID        REFERENCES providers(fk_user_id) ON DELETE SET NULL,
  created_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
  UNIQUE (provider_id, store_id)
);

CREATE TABLE services (
  id           UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
  store_id     UUID           NOT NULL REFERENCES provider_profiles(id) ON DELETE CASCADE,
  title        VARCHAR(255)   NOT NULL,
  description  TEXT,
  price        DECIMAL(10,2)  NOT NULL,
  price_type   VARCHAR(50)    NOT NULL,
  duration_minutes INT        NOT NULL,
  created_at   TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE TABLE service_thumbnail (
  id          UUID  PRIMARY KEY DEFAULT gen_random_uuid(),
  service_id  UUID  NOT NULL UNIQUE REFERENCES services(id) ON DELETE CASCADE,
  url         TEXT  NOT NULL
);

CREATE TABLE service_images (
  id          UUID  PRIMARY KEY DEFAULT gen_random_uuid(),
  service_id  UUID  NOT NULL REFERENCES services(id) ON DELETE CASCADE,
  url         TEXT  NOT NULL
);

CREATE TABLE bookings (
  id            UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
  client_id     UUID            NOT NULL REFERENCES clients(fk_user_id) ON DELETE RESTRICT,
  service_id    UUID            NOT NULL REFERENCES services(id) ON DELETE RESTRICT,
  scheduled_at  TIMESTAMP       NOT NULL,
  date          DATE            GENERATED ALWAYS AS (scheduled_at::DATE) STORED,
  status        booking_status  NOT NULL DEFAULT 'pending',
  created_at    TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE TABLE negotiations (
  id          UUID                PRIMARY KEY DEFAULT gen_random_uuid(),
  booking_id  UUID                NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
  sender_id   UUID                NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
  receiver_id UUID                NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
  amount      DECIMAL(10,2)       NOT NULL,
  status      negotiation_status  NOT NULL DEFAULT 'pending',
  sent_at     TIMESTAMP           NOT NULL DEFAULT NOW(),
  created_at  TIMESTAMP           NOT NULL DEFAULT NOW()
);

CREATE TABLE messages (
  id          UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
  booking_id  UUID      NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
  sender_id   UUID      NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
  receiver_id UUID      NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
  content     TEXT      NOT NULL,
  sent_at     TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE reviews (
  id           UUID      PRIMARY KEY DEFAULT gen_random_uuid(),
  booking_id   UUID      NOT NULL UNIQUE REFERENCES bookings(id) ON DELETE CASCADE,
  reviewer_id  UUID      NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
  reviewed_id  UUID      NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
  rating       INT       NOT NULL CHECK (rating BETWEEN 1 AND 5),
  comment      TEXT,
  created_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_user_images_user_id         ON user_images(user_id);
CREATE INDEX idx_addresses_user_id           ON addresses(user_id);
CREATE INDEX idx_store_memberships_store_id  ON store_memberships(store_id);
CREATE INDEX idx_store_memberships_provider  ON store_memberships(provider_id);
CREATE INDEX idx_services_store_id           ON services(store_id);
CREATE INDEX idx_service_images_service_id   ON service_images(service_id);
CREATE INDEX idx_bookings_client_id          ON bookings(client_id);
CREATE INDEX idx_bookings_service_id         ON bookings(service_id);
CREATE INDEX idx_bookings_status             ON bookings(status);
CREATE INDEX idx_negotiations_booking_id     ON negotiations(booking_id);
CREATE INDEX idx_negotiations_sender_id      ON negotiations(sender_id);
CREATE INDEX idx_negotiations_receiver_id    ON negotiations(receiver_id);
CREATE INDEX idx_messages_booking_id         ON messages(booking_id);
CREATE INDEX idx_messages_sender_id          ON messages(sender_id);
CREATE INDEX idx_messages_receiver_id        ON messages(receiver_id);
CREATE INDEX idx_reviews_booking_id          ON reviews(booking_id);
CREATE INDEX idx_reviews_reviewer_id         ON reviews(reviewer_id);
CREATE INDEX idx_reviews_reviewed_id         ON reviews(reviewed_id);
CREATE INDEX idx_provider_profiles_slug      ON provider_profiles(slug);

ALTER TABLE users               ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_images         ENABLE ROW LEVEL SECURITY;
ALTER TABLE addresses           ENABLE ROW LEVEL SECURITY;
ALTER TABLE clients             ENABLE ROW LEVEL SECURITY;
ALTER TABLE providers           ENABLE ROW LEVEL SECURITY;
ALTER TABLE provider_profiles   ENABLE ROW LEVEL SECURITY;
ALTER TABLE store_memberships   ENABLE ROW LEVEL SECURITY;
ALTER TABLE services            ENABLE ROW LEVEL SECURITY;
ALTER TABLE service_thumbnail   ENABLE ROW LEVEL SECURITY;
ALTER TABLE service_images      ENABLE ROW LEVEL SECURITY;
ALTER TABLE bookings            ENABLE ROW LEVEL SECURITY;
ALTER TABLE negotiations        ENABLE ROW LEVEL SECURITY;
ALTER TABLE messages            ENABLE ROW LEVEL SECURITY;
ALTER TABLE reviews             ENABLE ROW LEVEL SECURITY;

CREATE POLICY "users: acesso proprio"
  ON users FOR ALL
  USING (auth.uid() = id);

CREATE POLICY "user_images: acesso proprio"
  ON user_images FOR ALL
  USING (auth.uid() = user_id);

CREATE POLICY "addresses: acesso proprio"
  ON addresses FOR ALL
  USING (auth.uid() = user_id);

CREATE POLICY "clients: acesso proprio"
  ON clients FOR ALL
  USING (auth.uid() = fk_user_id);

CREATE POLICY "providers: acesso proprio"
  ON providers FOR ALL
  USING (auth.uid() = fk_user_id);

CREATE POLICY "provider_profiles: leitura publica"
  ON provider_profiles FOR SELECT
  USING (true);

CREATE POLICY "provider_profiles: escrita pelo owner"
  ON provider_profiles FOR ALL
  USING (
    EXISTS (
      SELECT 1 FROM store_memberships sm
      WHERE sm.store_id    = provider_profiles.id
        AND sm.provider_id = auth.uid()
        AND sm.role        = 'owner'
    )
  );

CREATE POLICY "store_memberships: ver propria membership"
  ON store_memberships FOR SELECT
  USING (auth.uid() = provider_id);

CREATE POLICY "store_memberships: owner e admin gerenciam"
  ON store_memberships FOR ALL
  USING (
    EXISTS (
      SELECT 1 FROM store_memberships sm
      WHERE sm.store_id    = store_memberships.store_id
        AND sm.provider_id = auth.uid()
        AND sm.role        IN ('owner', 'admin')
    )
  );

CREATE POLICY "services: leitura publica"
  ON services FOR SELECT
  USING (true);

CREATE POLICY "services: escrita por membros"
  ON services FOR ALL
  USING (
    EXISTS (
      SELECT 1 FROM store_memberships sm
      WHERE sm.store_id    = services.store_id
        AND sm.provider_id = auth.uid()
        AND sm.role        IN ('owner', 'admin')
    )
  );

CREATE POLICY "service_thumbnail: leitura publica"
  ON service_thumbnail FOR SELECT USING (true);

CREATE POLICY "service_images: leitura publica"
  ON service_images FOR SELECT USING (true);

CREATE POLICY "bookings: acesso do client"
  ON bookings FOR ALL
  USING (auth.uid() = client_id);

CREATE POLICY "bookings: leitura pela loja"
  ON bookings FOR SELECT
  USING (
    EXISTS (
      SELECT 1
      FROM   services s
      JOIN   store_memberships sm ON sm.store_id = s.store_id
      WHERE  s.id            = bookings.service_id
        AND  sm.provider_id  = auth.uid()
    )
  );

CREATE POLICY "negotiations: participantes"
  ON negotiations FOR ALL
  USING (auth.uid() = sender_id OR auth.uid() = receiver_id);

CREATE POLICY "messages: participantes"
  ON messages FOR ALL
  USING (auth.uid() = sender_id OR auth.uid() = receiver_id);

CREATE POLICY "reviews: leitura publica"
  ON reviews FOR SELECT USING (true);

CREATE POLICY "reviews: escrita pelo reviewer"
  ON reviews FOR INSERT
  WITH CHECK (auth.uid() = reviewer_id);