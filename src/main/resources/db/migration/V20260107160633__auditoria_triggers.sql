-- Users
CREATE TRIGGER trg_audit_users
    AFTER INSERT OR UPDATE OR DELETE ON users
    FOR EACH ROW
    EXECUTE FUNCTION fn_audit_trigger('user_id');

-- Events
CREATE TRIGGER trg_audit_events
    AFTER INSERT OR UPDATE OR DELETE ON events
    FOR EACH ROW
    EXECUTE FUNCTION fn_audit_trigger('event_id');

-- Orders
CREATE TRIGGER trg_audit_orders
    AFTER INSERT OR UPDATE OR DELETE ON orders
    FOR EACH ROW
    EXECUTE FUNCTION fn_audit_trigger('order_id');

-- Payments
CREATE TRIGGER trg_audit_payments
    AFTER INSERT OR UPDATE OR DELETE ON payments
    FOR EACH ROW
    EXECUTE FUNCTION fn_audit_trigger('payment_id');

-- Attendees
CREATE TRIGGER trg_audit_attendees
    AFTER INSERT OR UPDATE OR DELETE ON attendees
    FOR EACH ROW
    EXECUTE FUNCTION fn_audit_trigger('attendee_id');

-- Ticket Types
CREATE TRIGGER trg_audit_ticket_types
    AFTER INSERT OR UPDATE OR DELETE ON ticket_types
    FOR EACH ROW
    EXECUTE FUNCTION fn_audit_trigger('ticket_type_id');

-- Organizer Profiles
CREATE TRIGGER trg_audit_organizer_profiles
    AFTER INSERT OR UPDATE OR DELETE ON organizer_profiles
    FOR EACH ROW
    EXECUTE FUNCTION fn_audit_trigger('organizer_profile_id');

-- Artist Profiles
CREATE TRIGGER trg_audit_artist_profiles
    AFTER INSERT OR UPDATE OR DELETE ON artist_profiles
    FOR EACH ROW
    EXECUTE FUNCTION fn_audit_trigger('artist_profile_id');

-- Promo Codes
CREATE TRIGGER trg_audit_promo_codes
    AFTER INSERT OR UPDATE OR DELETE ON promo_codes
    FOR EACH ROW
    EXECUTE FUNCTION fn_audit_trigger('promo_code_id');
