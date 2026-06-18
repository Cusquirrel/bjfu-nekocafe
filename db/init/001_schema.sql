CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(64) NOT NULL UNIQUE,
  phone VARCHAR(32),
  password_hash VARCHAR(128) NOT NULL,
  role_code VARCHAR(32) NOT NULL DEFAULT 'CUSTOMER',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS members (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id),
  level_code VARCHAR(32) NOT NULL DEFAULT 'SILVER',
  points INT NOT NULL DEFAULT 0,
  privacy_consent BOOLEAN NOT NULL DEFAULT TRUE,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS stores (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(128) NOT NULL,
  city VARCHAR(64) NOT NULL,
  address VARCHAR(255) NOT NULL,
  opening_time VARCHAR(16) NOT NULL,
  closing_time VARCHAR(16) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS dining_tables (
  id BIGSERIAL PRIMARY KEY,
  store_id BIGINT NOT NULL REFERENCES stores(id),
  code VARCHAR(32) NOT NULL,
  capacity INT NOT NULL,
  area_type VARCHAR(32) NOT NULL,
  UNIQUE(store_id, code)
);
CREATE TABLE IF NOT EXISTS cats (
  id BIGSERIAL PRIMARY KEY,
  store_id BIGINT NOT NULL REFERENCES stores(id),
  name VARCHAR(64) NOT NULL,
  breed VARCHAR(64),
  age_months INT NOT NULL DEFAULT 12,
  personality VARCHAR(255),
  interaction_status VARCHAR(32) NOT NULL DEFAULT 'AVAILABLE',
  health_status VARCHAR(32) NOT NULL DEFAULT 'NORMAL',
  weight_kg DECIMAL(4,2),
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS cat_health_records (
  id BIGSERIAL PRIMARY KEY,
  cat_id BIGINT NOT NULL REFERENCES cats(id),
  record_type VARCHAR(32) NOT NULL,
  value_text VARCHAR(255),
  recorded_by VARCHAR(64) NOT NULL,
  recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS reservations (
  id BIGSERIAL PRIMARY KEY,
  reservation_no VARCHAR(64) NOT NULL UNIQUE,
  user_id BIGINT NOT NULL REFERENCES users(id),
  store_id BIGINT NOT NULL REFERENCES stores(id),
  table_id BIGINT REFERENCES dining_tables(id),
  visit_date DATE NOT NULL,
  slot VARCHAR(32) NOT NULL,
  party_size INT NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'CONFIRMED',
  request_id VARCHAR(64) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(store_id, visit_date, slot, table_id),
  UNIQUE(request_id)
);
CREATE TABLE IF NOT EXISTS orders (
  id BIGSERIAL PRIMARY KEY,
  order_no VARCHAR(64) NOT NULL UNIQUE,
  reservation_id BIGINT REFERENCES reservations(id),
  user_id BIGINT NOT NULL REFERENCES users(id),
  amount_cents INT NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'CREATED',
  payment_channel VARCHAR(32),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS reviews (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id),
  store_id BIGINT NOT NULL REFERENCES stores(id),
  rating INT NOT NULL,
  content VARCHAR(1000),
  status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS audit_logs (
  id BIGSERIAL PRIMARY KEY,
  actor VARCHAR(64) NOT NULL,
  action VARCHAR(128) NOT NULL,
  target_type VARCHAR(64),
  target_id VARCHAR(64),
  detail VARCHAR(1000),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_reservation_lookup ON reservations(store_id, visit_date, slot, status);
CREATE INDEX IF NOT EXISTS idx_cats_store_status ON cats(store_id, interaction_status, health_status);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status, created_at);
