CREATE OR REPLACE FUNCTION fn_audit_trigger()
RETURNS TRIGGER AS $$
DECLARE
    v_old_data JSONB;
    v_new_data JSONB;
    v_modified_fields TEXT[];
    v_user_id INTEGER;
    v_user_email VARCHAR(150);
    v_user_name VARCHAR(150);
    v_ip_address VARCHAR(45);
    v_user_agent TEXT;
    v_source VARCHAR(50);
    v_record_id INTEGER;
    key TEXT;
BEGIN
    -- User context
    BEGIN
        v_user_id := current_setting('app.user_id', true)::INTEGER;
        v_user_email := current_setting('app.user_email', true);
        v_user_name := current_setting('app.user_name', true);
        v_ip_address := current_setting('app.ip_address', true);
        v_user_agent := current_setting('app.user_agent', true);
        v_source := COALESCE(current_setting('app.source', true), 'SYSTEM');
    EXCEPTION
        WHEN OTHERS THEN
            v_user_id := NULL;
            v_user_email := NULL;
            v_user_name := NULL;
            v_ip_address := NULL;
            v_user_agent := NULL;
            v_source := 'SYSTEM';
    END;

    -- Dynamically resolve PK (passed as TG_ARGV[0])
    IF TG_OP = 'DELETE' THEN
        EXECUTE format('SELECT ($1).%I', TG_ARGV[0])
        INTO v_record_id
        USING OLD;
    ELSE
        EXECUTE format('SELECT ($1).%I', TG_ARGV[0])
        INTO v_record_id
        USING NEW;
    END IF;

    IF TG_OP = 'DELETE' THEN
        v_old_data := row_to_json(OLD)::JSONB;
        v_new_data := NULL;

    ELSIF TG_OP = 'UPDATE' THEN
        v_old_data := row_to_json(OLD)::JSONB;
        v_new_data := row_to_json(NEW)::JSONB;

        v_modified_fields := ARRAY[]::TEXT[];
        FOR key IN SELECT jsonb_object_keys(v_new_data)
        LOOP
            IF v_old_data->key IS DISTINCT FROM v_new_data->key THEN
                v_modified_fields := array_append(v_modified_fields, key);
            END IF;
        END LOOP;

        -- No real changes, skip audit
        IF array_length(v_modified_fields, 1) IS NULL THEN
            RETURN NEW;
        END IF;

    ELSIF TG_OP = 'INSERT' THEN
        v_old_data := NULL;
        v_new_data := row_to_json(NEW)::JSONB;
    END IF;

    INSERT INTO audit_logs (
        table_name,
        operation,
        record_id,
        old_data,
        new_data,
        modified_fields,
        user_id,
        user_email,
        user_name,
        ip_address,
        user_agent,
        source
    ) VALUES (
        TG_TABLE_NAME,
        TG_OP,
        v_record_id,
        v_old_data,
        v_new_data,
        v_modified_fields,
        v_user_id,
        v_user_email,
        v_user_name,
        v_ip_address,
        v_user_agent,
        v_source
    );

    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;
