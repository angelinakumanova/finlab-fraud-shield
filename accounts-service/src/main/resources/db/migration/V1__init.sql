CREATE TABLE IF NOT EXISTS iban_accounts (
                                             id SERIAL PRIMARY KEY,
                                             iban VARCHAR(34) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS iban_reports (
                                            id BIGSERIAL PRIMARY KEY,
                                            iban_id INT NOT NULL REFERENCES iban_accounts(id) ON DELETE CASCADE,
                                            reporter_hash CHAR(64) NOT NULL,
                                            reason TEXT,
                                            created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS iban_edges (
                                          id BIGSERIAL PRIMARY KEY,
                                          source_iban_id INT NOT NULL REFERENCES iban_accounts(id) ON DELETE CASCADE,
                                          target_iban_id INT NOT NULL REFERENCES iban_accounts(id) ON DELETE CASCADE,
                                          weight FLOAT DEFAULT 1.0,
                                          last_seen TIMESTAMP DEFAULT NOW(),
                                          CONSTRAINT unique_edge UNIQUE (source_iban_id, target_iban_id)
);

CREATE TABLE IF NOT EXISTS iban_risk (
                                         iban_id INT PRIMARY KEY REFERENCES iban_accounts(id) ON DELETE CASCADE,
                                         score NUMERIC(6,3) DEFAULT 0.0,
                                         decision VARCHAR(10) DEFAULT 'ALLOW',
                                         last_calc TIMESTAMP DEFAULT NOW()
);