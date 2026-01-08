CREATE OR REPLACE FUNCTION fn_auditoria_trigger()
RETURNS TRIGGER AS $$
DECLARE
    v_datos_anteriores JSONB;
    v_datos_nuevos JSONB;
    v_campos_modificados TEXT[];
    v_usuario_id INTEGER;
    v_usuario_email VARCHAR(150);
    v_usuario_nombre VARCHAR(150);
    v_ip VARCHAR(45);
    v_user_agent TEXT;
    v_origen VARCHAR(50);
    key TEXT;
BEGIN
    BEGIN
        v_usuario_id := current_setting('app.usuario_id', true)::INTEGER;
        v_usuario_email := current_setting('app.usuario_email', true);
        v_usuario_nombre := current_setting('app.usuario_nombre', true);
        v_ip := current_setting('app.ip_address', true);
        v_user_agent := current_setting('app.user_agent', true);
        v_origen := current_setting('app.origen', true);
    EXCEPTION
        WHEN OTHERS THEN
            v_usuario_id := NULL;
            v_usuario_email := NULL;
            v_usuario_nombre := NULL;
            v_ip := NULL;
            v_user_agent := NULL;
            v_origen := 'SYSTEM';
    END;

    IF TG_OP = 'DELETE' THEN
        v_datos_anteriores := row_to_json(OLD)::JSONB;
        v_datos_nuevos := NULL;

        INSERT INTO auditoria (
            tabla_nombre, operacion, registro_id,
            datos_anteriores, datos_nuevos, campos_modificados,
            usuario_id, usuario_email, usuario_nombre,
            direccion_ip, user_agent, origen
        ) VALUES (
            TG_TABLE_NAME, TG_OP, (row_to_json(OLD)->>'id_' || TG_TABLE_NAME)::INTEGER,
            v_datos_anteriores, v_datos_nuevos, NULL,
            v_usuario_id, v_usuario_email, v_usuario_nombre,
            v_ip, v_user_agent, v_origen
        );

        RETURN OLD;

    ELSIF TG_OP = 'UPDATE' THEN
        v_datos_anteriores := row_to_json(OLD)::JSONB;
        v_datos_nuevos := row_to_json(NEW)::JSONB;

        v_campos_modificados := ARRAY[]::TEXT[];
        FOR key IN SELECT jsonb_object_keys(v_datos_nuevos)
        LOOP
            IF v_datos_anteriores->key IS DISTINCT FROM v_datos_nuevos->key THEN
                v_campos_modificados := array_append(v_campos_modificados, key);
            END IF;
        END LOOP;

        IF array_length(v_campos_modificados, 1) > 0 THEN
            INSERT INTO auditoria (
                tabla_nombre, operacion, registro_id,
                datos_anteriores, datos_nuevos, campos_modificados,
                usuario_id, usuario_email, usuario_nombre,
                direccion_ip, user_agent, origen
            ) VALUES (
                TG_TABLE_NAME, TG_OP, (row_to_json(NEW)->>'id_' || TG_TABLE_NAME)::INTEGER,
                v_datos_anteriores, v_datos_nuevos, v_campos_modificados,
                v_usuario_id, v_usuario_email, v_usuario_nombre,
                v_ip, v_user_agent, v_origen
            );
        END IF;

        RETURN NEW;

    ELSIF TG_OP = 'INSERT' THEN
        v_datos_anteriores := NULL;
        v_datos_nuevos := row_to_json(NEW)::JSONB;

        INSERT INTO auditoria (
            tabla_nombre, operacion, registro_id,
            datos_anteriores, datos_nuevos, campos_modificados,
            usuario_id, usuario_email, usuario_nombre,
            direccion_ip, user_agent, origen
        ) VALUES (
            TG_TABLE_NAME, TG_OP, (row_to_json(NEW)->>'id_' || TG_TABLE_NAME)::INTEGER,
            v_datos_anteriores, v_datos_nuevos, NULL,
            v_usuario_id, v_usuario_email, v_usuario_nombre,
            v_ip, v_user_agent, v_origen
        );

        RETURN NEW;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;