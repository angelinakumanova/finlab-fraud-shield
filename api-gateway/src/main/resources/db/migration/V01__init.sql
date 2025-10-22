CREATE TABLE IF NOT EXISTS users (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            username VARCHAR(15) UNIQUE NOT NULL,
                            password CHAR(60) NOT NULL,
                            created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS auth_tokens (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             user_id UUID NOT NULL,
                             jwt_id VARCHAR(64) UNIQUE NOT NULL,
                             token TEXT NOT NULL,
                             issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             expires_at TIMESTAMP NOT NULL,
                             revoked BOOLEAN DEFAULT FALSE,
                             CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
