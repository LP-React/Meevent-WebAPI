-- =====================================================
-- Create attendee_profiles table
-- Each attendee profile belongs to exactly one user
-- =====================================================

CREATE TABLE attendee_profiles (
    attendee_profile_id SERIAL PRIMARY KEY,

    user_id INTEGER NOT NULL UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    birth_date DATE,
    city_id INTEGER NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_attendee_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id),

    CONSTRAINT fk_attendee_city
        FOREIGN KEY (city_id)
        REFERENCES cities(city_id)
);
