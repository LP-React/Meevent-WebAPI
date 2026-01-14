-- =====================================================
-- Support multiple countries and external integrations
--
-- DESIGN:
-- country_code : UX and validation (e.g. +51)
-- phone_number : Local number without country prefix
-- phone_e164   : International standard format, UNIQUE
-- =====================================================

-- Add new phone-related columns
ALTER TABLE users
ADD COLUMN country_code VARCHAR(5),
ADD COLUMN phone_e164 VARCHAR(20);

-- phone_number already exists and is reused as local number
-- No rename to avoid breaking existing code and data

-- Add UNIQUE constraint for integrations
ALTER TABLE users
ADD CONSTRAINT uk_users_phone_e164 UNIQUE (phone_e164);
