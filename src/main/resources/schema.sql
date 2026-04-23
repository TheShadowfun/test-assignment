CREATE TABLE accounts (
                        id BIGSERIAL PRIMARY KEY,
                        customer_id BIGINT NOT NULL,
                        country VARCHAR(2) NOT NULL
);

CREATE TABLE balances (
                        id BIGSERIAL PRIMARY KEY,
                        account_id BIGINT REFERENCES accounts(id),
                        currency VARCHAR(3) NOT NULL,
                        available_amount NUMERIC(20, 2) DEFAULT 0.00,
                        UNIQUE(account_id, currency)
);

CREATE TABLE transactions (
                        id BIGSERIAL PRIMARY KEY,
                        account_id BIGINT REFERENCES accounts(id) NOT NULL,
                        amount NUMERIC(20, 2) NOT NULL,
                        currency VARCHAR(3) NOT NULL,
                        direction VARCHAR(3) NOT NULL,
                        description TEXT NOT NULL,
                        balance_after NUMERIC(20, 2) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE outbox (
                        id BIGSERIAL PRIMARY KEY,
                        routing_key VARCHAR(255) NOT NULL,
                        payload TEXT NOT NULL,
                        status VARCHAR(50) DEFAULT 'PENDING',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);