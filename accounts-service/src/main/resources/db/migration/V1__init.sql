CREATE TABLE IF NOT EXISTS risky_ibans (
                             id SERIAL PRIMARY KEY,
                             iban VARCHAR(34) UNIQUE NOT NULL,
                             reports INT DEFAULT 1,
                             last_reported TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS payments (
                          id SERIAL PRIMARY KEY,
                          iban VARCHAR(34) NOT NULL,
                          amount DECIMAL(12,2),
                          created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS iban_accounts (
                          id SERIAL PRIMARY KEY,
                          iban VARCHAR(34) UNIQUE NOT NULL
);
