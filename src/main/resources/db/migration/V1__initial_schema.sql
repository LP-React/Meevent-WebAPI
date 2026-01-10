CREATE TABLE countries (
    country_id SERIAL PRIMARY KEY,
    country_name VARCHAR(100) NOT NULL,
    iso_code VARCHAR(3) NOT NULL
);

CREATE TABLE cities (
    city_id SERIAL PRIMARY KEY,
    city_name VARCHAR(100) NOT NULL,
    country_id INTEGER NOT NULL,
    CONSTRAINT fk_cities_country FOREIGN KEY (country_id)
        REFERENCES countries(country_id)
);

CREATE TABLE event_categories (
    event_category_id SERIAL PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    category_slug VARCHAR(100) NOT NULL UNIQUE,
    icon_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE event_subcategories (
    event_subcategory_id SERIAL PRIMARY KEY,
    subcategory_name VARCHAR(100) NOT NULL,
    subcategory_slug VARCHAR(100) NOT NULL,
    event_category_id INTEGER NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_event_subcategories_category FOREIGN KEY (event_category_id)
        REFERENCES event_categories(event_category_id)
);

CREATE TABLE plan_categories (
    plan_category_id SERIAL PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL
);

CREATE TABLE plan_subcategories (
    plan_subcategory_id SERIAL PRIMARY KEY,
    subcategory_name VARCHAR(100) NOT NULL,
    plan_category_id INTEGER NOT NULL,
    CONSTRAINT fk_plan_subcategories_category FOREIGN KEY (plan_category_id)
        REFERENCES plan_categories(plan_category_id)
);

CREATE TABLE venues (
    venue_id SERIAL PRIMARY KEY,
    venue_name VARCHAR(200) NOT NULL,
    venue_capacity INTEGER NOT NULL,
    venue_address VARCHAR(300) NOT NULL,
    city_id INTEGER NOT NULL,
    venue_slug VARCHAR(220) NOT NULL DEFAULT '',
    latitude NUMERIC(9, 6) NOT NULL DEFAULT 0.0,
    longitude NUMERIC(9, 6) NOT NULL DEFAULT 0.0,
    CONSTRAINT fk_venues_city FOREIGN KEY (city_id)
        REFERENCES cities(city_id)
);

CREATE TABLE promo_codes (
    promo_code_id SERIAL PRIMARY KEY,
    promo_code VARCHAR(50) NOT NULL UNIQUE,
    promo_description VARCHAR(300),
    discount_type VARCHAR(20) NOT NULL DEFAULT 'percentage',
    discount_value NUMERIC(10, 2) NOT NULL,
    minimum_purchase NUMERIC(10, 2),
    maximum_discount NUMERIC(10, 2),
    usage_limit INTEGER,
    usage_count INTEGER NOT NULL DEFAULT 0,
    promotion_start TIMESTAMP NOT NULL,
    promotion_end TIMESTAMP NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_discount_type CHECK (discount_type IN ('percentage', 'fixed_amount'))
);

-- =============================================
-- USERS AND PROFILES
-- =============================================

CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    profile_image_url VARCHAR(500),
    birth_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    user_type VARCHAR(20) NOT NULL DEFAULT 'normal',
    city_id INTEGER,
    CONSTRAINT fk_users_city FOREIGN KEY (city_id)
        REFERENCES cities(city_id),
    CONSTRAINT chk_user_type CHECK (user_type IN ('normal', 'artist', 'organizer'))
);

CREATE TABLE organizer_profiles (
    organizer_profile_id SERIAL PRIMARY KEY,
    organizer_name VARCHAR(200) NOT NULL,
    organizer_description TEXT NOT NULL,
    website_url VARCHAR(300),
    logo_url VARCHAR(500),
    facebook_url VARCHAR(300),
    instagram_url VARCHAR(300),
    tiktok_url VARCHAR(300),
    twitter_url VARCHAR(300),
    address VARCHAR(300),
    contact_phone VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id INTEGER NOT NULL UNIQUE,
    CONSTRAINT fk_organizer_profiles_user FOREIGN KEY (user_id)
        REFERENCES users(user_id)
);

CREATE TABLE artist_profiles (
    artist_profile_id SERIAL PRIMARY KEY,
    stage_name VARCHAR(150) NOT NULL,
    biography TEXT NOT NULL,
    music_genre VARCHAR(100) NOT NULL,
    website_url VARCHAR(300),
    facebook_url VARCHAR(300),
    instagram_url VARCHAR(300),
    tiktok_url VARCHAR(300),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id INTEGER NOT NULL UNIQUE,
    CONSTRAINT fk_artist_profiles_user FOREIGN KEY (user_id)
        REFERENCES users(user_id)
);

-- =============================================
-- EVENTS AND PLANS
-- =============================================

CREATE TABLE events (
    event_id SERIAL PRIMARY KEY,
    event_title VARCHAR(250) NOT NULL,
    event_slug VARCHAR(250) NOT NULL UNIQUE,
    event_description TEXT NOT NULL,
    short_description VARCHAR(500),
    start_date TIMESTAMPTZ NOT NULL,
    end_date TIMESTAMPTZ NOT NULL,
    time_zone VARCHAR(50) NOT NULL DEFAULT 'UTC',
    event_status VARCHAR(20) NOT NULL DEFAULT 'draft',
    event_capacity INTEGER NOT NULL,
    is_free BOOLEAN NOT NULL DEFAULT FALSE,
    is_online BOOLEAN NOT NULL DEFAULT FALSE,
    cover_image_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    organizer_profile_id INTEGER NOT NULL,
    event_subcategory_id INTEGER NOT NULL,
    venue_id INTEGER,
    CONSTRAINT fk_events_organizer FOREIGN KEY (organizer_profile_id)
        REFERENCES organizer_profiles(organizer_profile_id),
    CONSTRAINT fk_events_subcategory FOREIGN KEY (event_subcategory_id)
        REFERENCES event_subcategories(event_subcategory_id),
    CONSTRAINT fk_events_venue FOREIGN KEY (venue_id)
        REFERENCES venues(venue_id),
    CONSTRAINT chk_event_status CHECK (event_status IN ('draft', 'published', 'cancelled', 'finished'))
);

CREATE TABLE event_images (
    event_image_id SERIAL PRIMARY KEY,
    image_url VARCHAR(500) NOT NULL,
    alt_text VARCHAR(200),
    image_order INTEGER NOT NULL DEFAULT 0,
    event_id INTEGER NOT NULL,
    CONSTRAINT fk_event_images_event FOREIGN KEY (event_id)
        REFERENCES events(event_id)
);

CREATE TABLE ticket_types (
    ticket_type_id SERIAL PRIMARY KEY,
    ticket_name VARCHAR(150) NOT NULL,
    ticket_description VARCHAR(500),
    ticket_price NUMERIC(10, 2) NOT NULL,
    total_quantity INTEGER NOT NULL,
    sold_quantity INTEGER NOT NULL DEFAULT 0,
    available_quantity INTEGER NOT NULL,
    sales_start TIMESTAMP NOT NULL,
    sales_end TIMESTAMP NOT NULL,
    min_purchase INTEGER NOT NULL DEFAULT 1,
    max_purchase INTEGER NOT NULL DEFAULT 10,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    event_id INTEGER NOT NULL,
    CONSTRAINT fk_ticket_types_event FOREIGN KEY (event_id)
        REFERENCES events(event_id)
);

CREATE TABLE plans (
    plan_id SERIAL PRIMARY KEY,
    plan_title VARCHAR(250) NOT NULL,
    plan_description TEXT NOT NULL,
    plan_subcategory_id INTEGER NOT NULL,
    CONSTRAINT fk_plans_subcategory FOREIGN KEY (plan_subcategory_id)
        REFERENCES plan_subcategories(plan_subcategory_id)
);

CREATE TABLE plan_images (
    plan_image_id SERIAL PRIMARY KEY,
    image_url VARCHAR(500) NOT NULL,
    plan_id INTEGER NOT NULL,
    CONSTRAINT fk_plan_images_plan FOREIGN KEY (plan_id)
        REFERENCES plans(plan_id)
);

-- =============================================
-- ORDERS AND PAYMENTS
-- =============================================

CREATE TABLE orders (
    order_id SERIAL PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    subtotal NUMERIC(10, 2) NOT NULL,
    tax NUMERIC(10, 2) NOT NULL DEFAULT 0,
    service_fee NUMERIC(10, 2) NOT NULL DEFAULT 0,
    discount NUMERIC(10, 2) NOT NULL DEFAULT 0,
    total NUMERIC(10, 2) NOT NULL,
    order_status VARCHAR(20) NOT NULL DEFAULT 'pending',
    customer_email VARCHAR(150) NOT NULL,
    customer_name VARCHAR(150) NOT NULL,
    customer_phone VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id INTEGER NOT NULL,
    promo_code_id INTEGER,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id)
        REFERENCES users(user_id),
    CONSTRAINT fk_orders_promo_code FOREIGN KEY (promo_code_id)
        REFERENCES promo_codes(promo_code_id),
    CONSTRAINT chk_order_status CHECK (order_status IN ('pending', 'completed', 'cancelled', 'refunded'))
);

CREATE TABLE order_items (
    order_item_id SERIAL PRIMARY KEY,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(10, 2) NOT NULL,
    subtotal NUMERIC(10, 2) NOT NULL,
    order_id INTEGER NOT NULL,
    ticket_type_id INTEGER NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id)
        REFERENCES orders(order_id),
    CONSTRAINT fk_order_items_ticket_type FOREIGN KEY (ticket_type_id)
        REFERENCES ticket_types(ticket_type_id)
);

CREATE TABLE attendees (
    attendee_id SERIAL PRIMARY KEY,
    ticket_number VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone VARCHAR(20),
    checked_in BOOLEAN NOT NULL DEFAULT FALSE,
    check_in_time TIMESTAMP,
    qr_code VARCHAR(500) NOT NULL,
    order_id INTEGER NOT NULL,
    ticket_type_id INTEGER NOT NULL,
    CONSTRAINT fk_attendees_order FOREIGN KEY (order_id)
        REFERENCES orders(order_id),
    CONSTRAINT fk_attendees_ticket_type FOREIGN KEY (ticket_type_id)
        REFERENCES ticket_types(ticket_type_id)
);

CREATE TABLE payments (
    payment_id SERIAL PRIMARY KEY,
    payment_method VARCHAR(50) NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'PEN',
    payment_status VARCHAR(20) NOT NULL DEFAULT 'pending',
    transaction_id VARCHAR(100),
    payment_gateway VARCHAR(50),
    gateway_response TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    refunded_at TIMESTAMP,
    order_id INTEGER NOT NULL,
    CONSTRAINT fk_payments_order FOREIGN KEY (order_id)
        REFERENCES orders(order_id),
    CONSTRAINT chk_payment_status CHECK (payment_status IN ('pending', 'completed', 'failed', 'refunded'))
);

-- =============================================
-- INTERACTIONS AND REVIEWS
-- =============================================

CREATE TABLE wishlists (
    wishlist_id SERIAL PRIMARY KEY,
    item_type VARCHAR(20) NOT NULL,
    added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id INTEGER NOT NULL,
    item_id INTEGER NOT NULL,
    CONSTRAINT fk_wishlists_user FOREIGN KEY (user_id)
        REFERENCES users(user_id),
    CONSTRAINT chk_item_type CHECK (item_type IN ('event', 'plan'))
);

CREATE TABLE event_followers (
    event_follower_id SERIAL PRIMARY KEY,
    followed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id INTEGER NOT NULL,
    event_id INTEGER NOT NULL,
    CONSTRAINT fk_event_followers_user FOREIGN KEY (user_id)
        REFERENCES users(user_id),
    CONSTRAINT fk_event_followers_event FOREIGN KEY (event_id)
        REFERENCES events(event_id),
    CONSTRAINT uk_event_followers UNIQUE (user_id, event_id)
);

CREATE TABLE plan_followers (
    plan_follower_id SERIAL PRIMARY KEY,
    followed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    plan_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    CONSTRAINT fk_plan_followers_plan FOREIGN KEY (plan_id)
        REFERENCES plans(plan_id),
    CONSTRAINT fk_plan_followers_user FOREIGN KEY (user_id)
        REFERENCES users(user_id),
    CONSTRAINT uk_plan_followers UNIQUE (user_id, plan_id)
);

CREATE TABLE event_reviews (
    event_review_id SERIAL PRIMARY KEY,
    rating INTEGER NOT NULL,
    comment TEXT NOT NULL,
    is_helpful BOOLEAN,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id INTEGER NOT NULL,
    event_id INTEGER NOT NULL,
    CONSTRAINT fk_event_reviews_user FOREIGN KEY (user_id)
        REFERENCES users(user_id),
    CONSTRAINT fk_event_reviews_event FOREIGN KEY (event_id)
        REFERENCES events(event_id),
    CONSTRAINT chk_event_rating CHECK (rating BETWEEN 1 AND 5)
);

CREATE TABLE organizer_reviews (
    organizer_review_id SERIAL PRIMARY KEY,
    rating INTEGER NOT NULL,
    comment TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    helpful_count INTEGER NOT NULL DEFAULT 0,
    verified_buyer BOOLEAN NOT NULL DEFAULT FALSE,
    organizer_profile_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    CONSTRAINT fk_organizer_reviews_profile FOREIGN KEY (organizer_profile_id)
        REFERENCES organizer_profiles(organizer_profile_id),
    CONSTRAINT fk_organizer_reviews_user FOREIGN KEY (user_id)
        REFERENCES users(user_id),
    CONSTRAINT chk_organizer_rating CHECK (rating BETWEEN 1 AND 5)
);
