-- ORDERS
ALTER TABLE orders DROP CONSTRAINT IF EXISTS fk_orders_user;

-- PROFILES
ALTER TABLE organizer_profiles DROP CONSTRAINT IF EXISTS fk_organizer_profiles_user;
ALTER TABLE artist_profiles DROP CONSTRAINT IF EXISTS fk_artist_profiles_user;
ALTER TABLE attendee_profiles DROP CONSTRAINT IF EXISTS fk_attendee_user;

-- EVENTS
ALTER TABLE event_images DROP CONSTRAINT IF EXISTS fk_event_images_event;
ALTER TABLE ticket_types DROP CONSTRAINT IF EXISTS fk_ticket_types_event;
ALTER TABLE event_followers DROP CONSTRAINT IF EXISTS fk_event_followers_event;
ALTER TABLE event_reviews DROP CONSTRAINT IF EXISTS fk_event_reviews_event;

-- OTHERS (ajusta si tienes más)
ALTER TABLE order_items DROP CONSTRAINT IF EXISTS fk_order_items_order;
ALTER TABLE payments DROP CONSTRAINT IF EXISTS fk_payments_order;

ALTER TABLE users ALTER COLUMN user_id TYPE BIGINT;
ALTER TABLE events ALTER COLUMN event_id TYPE BIGINT;
ALTER TABLE orders ALTER COLUMN order_id TYPE BIGINT;
ALTER TABLE audit_logs ALTER COLUMN audit_id TYPE BIGINT;

ALTER TABLE artist_profiles ALTER COLUMN artist_profile_id TYPE BIGINT;
ALTER TABLE attendee_profiles ALTER COLUMN attendee_profile_id TYPE BIGINT;
ALTER TABLE organizer_profiles ALTER COLUMN organizer_profile_id TYPE BIGINT;

-- USERS
ALTER TABLE orders ALTER COLUMN user_id TYPE BIGINT;
ALTER TABLE organizer_profiles ALTER COLUMN user_id TYPE BIGINT;
ALTER TABLE artist_profiles ALTER COLUMN user_id TYPE BIGINT;
ALTER TABLE attendee_profiles ALTER COLUMN user_id TYPE BIGINT;

-- EVENTS
ALTER TABLE event_images ALTER COLUMN event_id TYPE BIGINT;
ALTER TABLE ticket_types ALTER COLUMN event_id TYPE BIGINT;
ALTER TABLE event_followers ALTER COLUMN event_id TYPE BIGINT;
ALTER TABLE event_reviews ALTER COLUMN event_id TYPE BIGINT;

-- ORDERS
ALTER TABLE order_items ALTER COLUMN order_id TYPE BIGINT;
ALTER TABLE payments ALTER COLUMN order_id TYPE BIGINT;

ALTER SEQUENCE users_user_id_seq AS BIGINT;
ALTER SEQUENCE events_event_id_seq AS BIGINT;
ALTER SEQUENCE orders_order_id_seq AS BIGINT;

ALTER SEQUENCE artist_profiles_artist_profile_id_seq AS BIGINT;
ALTER SEQUENCE attendee_profiles_attendee_profile_id_seq AS BIGINT;
ALTER SEQUENCE organizer_profiles_organizer_profile_id_seq AS BIGINT;

ALTER TABLE orders
ADD CONSTRAINT fk_orders_user
FOREIGN KEY (user_id) REFERENCES users(user_id);

ALTER TABLE organizer_profiles
ADD CONSTRAINT fk_organizer_profiles_user
FOREIGN KEY (user_id) REFERENCES users(user_id);

ALTER TABLE artist_profiles
ADD CONSTRAINT fk_artist_profiles_user
FOREIGN KEY (user_id) REFERENCES users(user_id);

ALTER TABLE attendee_profiles
ADD CONSTRAINT fk_attendee_user
FOREIGN KEY (user_id) REFERENCES users(user_id);

ALTER TABLE event_images
ADD CONSTRAINT fk_event_images_event
FOREIGN KEY (event_id) REFERENCES events(event_id);

ALTER TABLE ticket_types
ADD CONSTRAINT fk_ticket_types_event
FOREIGN KEY (event_id) REFERENCES events(event_id);

ALTER TABLE event_followers
ADD CONSTRAINT fk_event_followers_event
FOREIGN KEY (event_id) REFERENCES events(event_id);

ALTER TABLE event_reviews
ADD CONSTRAINT fk_event_reviews_event
FOREIGN KEY (event_id) REFERENCES events(event_id);

ALTER TABLE event_followers DROP CONSTRAINT fk_event_followers_user;
ALTER TABLE event_reviews DROP CONSTRAINT fk_event_reviews_user;
ALTER TABLE organizer_reviews DROP CONSTRAINT fk_organizer_reviews_user;
ALTER TABLE audit_logs DROP CONSTRAINT fk_audit_logs_user;
ALTER TABLE plan_followers DROP CONSTRAINT fk_plan_followers_user;
ALTER TABLE wishlists DROP CONSTRAINT fk_wishlists_user;

ALTER TABLE attendees DROP CONSTRAINT fk_attendees_order;

ALTER TABLE events DROP CONSTRAINT fk_events_organizer;
ALTER TABLE organizer_reviews DROP CONSTRAINT fk_organizer_reviews_profile;

-- users FK
ALTER TABLE event_followers ALTER COLUMN user_id TYPE BIGINT;
ALTER TABLE event_reviews ALTER COLUMN user_id TYPE BIGINT;
ALTER TABLE organizer_reviews ALTER COLUMN user_id TYPE BIGINT;
ALTER TABLE audit_logs ALTER COLUMN user_id TYPE BIGINT;
ALTER TABLE plan_followers ALTER COLUMN user_id TYPE BIGINT;
ALTER TABLE wishlists ALTER COLUMN user_id TYPE BIGINT;

-- orders FK
ALTER TABLE attendees ALTER COLUMN order_id TYPE BIGINT;

-- organizer_profiles FK
ALTER TABLE events ALTER COLUMN organizer_profile_id TYPE BIGINT;
ALTER TABLE organizer_reviews ALTER COLUMN organizer_profile_id TYPE BIGINT;

ALTER TABLE event_followers
ADD CONSTRAINT fk_event_followers_user
FOREIGN KEY (user_id) REFERENCES users(user_id);

ALTER TABLE event_reviews
ADD CONSTRAINT fk_event_reviews_user
FOREIGN KEY (user_id) REFERENCES users(user_id);

ALTER TABLE organizer_reviews
ADD CONSTRAINT fk_organizer_reviews_user
FOREIGN KEY (user_id) REFERENCES users(user_id);

ALTER TABLE audit_logs
ADD CONSTRAINT fk_audit_logs_user
FOREIGN KEY (user_id) REFERENCES users(user_id);

ALTER TABLE plan_followers
ADD CONSTRAINT fk_plan_followers_user
FOREIGN KEY (user_id) REFERENCES users(user_id);

ALTER TABLE wishlists
ADD CONSTRAINT fk_wishlists_user
FOREIGN KEY (user_id) REFERENCES users(user_id);

ALTER TABLE attendees
ADD CONSTRAINT fk_attendees_order
FOREIGN KEY (order_id) REFERENCES orders(order_id);

ALTER TABLE events
ADD CONSTRAINT fk_events_organizer
FOREIGN KEY (organizer_profile_id)
REFERENCES organizer_profiles(organizer_profile_id);

ALTER TABLE organizer_reviews
ADD CONSTRAINT fk_organizer_reviews_profile
FOREIGN KEY (organizer_profile_id)
REFERENCES organizer_profiles(organizer_profile_id);
