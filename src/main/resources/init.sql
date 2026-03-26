-- =============================================
-- StockFlow Inventory - Database Schema
-- Run this ONCE to seed initial data
-- =============================================

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    name        VARCHAR(100) NOT NULL,
    role        VARCHAR(20)  NOT NULL CHECK (role IN ('ADMIN', 'STAFF')),
    active      BOOLEAN DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products table
CREATE TABLE IF NOT EXISTS products (
    id                   BIGSERIAL PRIMARY KEY,
    name                 VARCHAR(200) NOT NULL,
    category             VARCHAR(50)  NOT NULL,
    stock_quantity       INT NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    low_stock_threshold  INT NOT NULL DEFAULT 5,
    description          TEXT,
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Requests table
CREATE TABLE IF NOT EXISTS requests (
    id                  BIGSERIAL PRIMARY KEY,
    staff_id            BIGINT NOT NULL REFERENCES users(id),
    product_id          BIGINT REFERENCES products(id),
    quantity_requested  INT NOT NULL CHECK (quantity_requested > 0),
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','APPROVED','REJECTED')),
    note                TEXT,
    request_date        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- New product requests table (staff requests for non-existent products)
CREATE TABLE IF NOT EXISTS new_product_requests (
    id           BIGSERIAL PRIMARY KEY,
    staff_id     BIGINT NOT NULL REFERENCES users(id),
    product_name VARCHAR(200) NOT NULL,
    category     VARCHAR(50)  NOT NULL,
    description  TEXT,
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','APPROVED','REJECTED')),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Purchases table
CREATE TABLE IF NOT EXISTS purchases (
    id             BIGSERIAL PRIMARY KEY,
    product_id     BIGINT NOT NULL REFERENCES products(id),
    quantity       INT NOT NULL CHECK (quantity > 0),
    price_per_unit NUMERIC(12,2) NOT NULL,
    total_cost     NUMERIC(14,2) NOT NULL,
    purchase_date  DATE NOT NULL,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id          BIGSERIAL PRIMARY KEY,
    message     TEXT NOT NULL,
    type        VARCHAR(30) NOT NULL,
    reference_id BIGINT,
    is_read     BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- Seed Data
-- =============================================

-- Admin user (password: admin123)
INSERT INTO users (username, password, name, role) VALUES
('admin', '$2a$12$XJfVWJjIjFk8K.H5gL3rJOiGI1PBv7r6dqPSK5TSyVdW.A/Eq.D6W', 'Admin User', 'ADMIN')
ON CONFLICT (username) DO NOTHING;

-- Staff users (password: staff123)
INSERT INTO users (username, password, name, role) VALUES
('staff1', '$2a$12$fXOPg2OGM2bCzE8U2Zm.mOY01rg3j6wU7.VCezJT8CUkIxJ5i7EZy', 'John Staff', 'STAFF'),
('staff2', '$2a$12$fXOPg2OGM2bCzE8U2Zm.mOY01rg3j6wU7.VCezJT8CUkIxJ5i7EZy', 'Sarah Smith', 'STAFF'),
('staff3', '$2a$12$fXOPg2OGM2bCzE8U2Zm.mOY01rg3j6wU7.VCezJT8CUkIxJ5i7EZy', 'Mike Johnson', 'STAFF')
ON CONFLICT (username) DO NOTHING;

-- Sample products
INSERT INTO products (name, category, stock_quantity, low_stock_threshold, description) VALUES
('Ceiling Fan 48"',     'FAN',        25, 10, 'Standard 48 inch ceiling fan for office use'),
('Split AC 1.5 Ton',    'AC',          4,  5, 'Energy-efficient 1.5 ton split air conditioner'),
('Tower Fan',           'FAN',         8,  5, 'Portable tower fan with remote control'),
('Window AC 1 Ton',     'AC',          2,  3, '1 ton window air conditioner'),
('Exhaust Fan 12"',     'FAN',        50, 15, '12 inch exhaust fan for ventilation'),
('Portable AC 1 Ton',   'AC',          6,  3, 'Portable 1 ton air conditioner'),
('Industrial Fan',      'FAN',        12,  4, 'Heavy duty industrial fan'),
('Air Cooler 30L',      'COOLER',      3,  5, '30 litre desert air cooler'),
('UPS 650VA',           'ELECTRICAL', 15,  5, '650VA UPS for computer backup'),
('Extension Board 6-Port','ELECTRICAL', 1, 10, '6-port surge-protected extension board')
ON CONFLICT DO NOTHING;
