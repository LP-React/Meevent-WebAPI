
-- =============================================
-- TABLE VERIFICATION_TOKENS (new table for email verification and password resets)
-- =============================================

CREATE TABLE verification_tokens (
    id SERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    user_id INTEGER NOT NULL,
    CONSTRAINT fk_verification_tokens_user FOREIGN KEY (user_id)
        REFERENCES users(user_id) ON DELETE CASCADE
);