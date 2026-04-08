-- users: make password_hash nullable, add auth_provider and google_sub
ALTER TABLE users ALTER COLUMN password_hash DROP NOT NULL;
ALTER TABLE users ADD COLUMN auth_provider VARCHAR(20) NOT NULL DEFAULT 'LOCAL';
ALTER TABLE users ADD CONSTRAINT chk_users_auth_provider CHECK (auth_provider IN ('LOCAL', 'GOOGLE'));
ALTER TABLE users ADD COLUMN google_sub VARCHAR(255);
ALTER TABLE users ADD CONSTRAINT uk_users_google_sub UNIQUE (google_sub);

-- attendee_profiles: make fields nullable for Google users with incomplete profiles
ALTER TABLE attendee_profiles ALTER COLUMN city_id DROP NOT NULL;
ALTER TABLE attendee_profiles ALTER COLUMN country_code DROP NOT NULL;
ALTER TABLE attendee_profiles ALTER COLUMN phone_number DROP NOT NULL;
ALTER TABLE attendee_profiles ALTER COLUMN phone_e164 DROP NOT NULL;
