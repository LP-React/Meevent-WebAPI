-- Usuarios
CREATE TRIGGER trg_auditoria_usuarios
    AFTER INSERT OR UPDATE OR DELETE ON usuarios
    FOR EACH ROW EXECUTE FUNCTION fn_auditoria_trigger('id_usuario');

-- Eventos
CREATE TRIGGER trg_auditoria_eventos
    AFTER INSERT OR UPDATE OR DELETE ON eventos
    FOR EACH ROW EXECUTE FUNCTION fn_auditoria_trigger('id_evento');

-- Ordenes
CREATE TRIGGER trg_auditoria_ordenes
    AFTER INSERT OR UPDATE OR DELETE ON ordenes
    FOR EACH ROW EXECUTE FUNCTION fn_auditoria_trigger('id_orden');

-- Pagos
CREATE TRIGGER trg_auditoria_pagos
    AFTER INSERT OR UPDATE OR DELETE ON pagos
    FOR EACH ROW EXECUTE FUNCTION fn_auditoria_trigger('id_pago');

-- Asistentes
CREATE TRIGGER trg_auditoria_asistentes
    AFTER INSERT OR UPDATE OR DELETE ON asistentes
    FOR EACH ROW EXECUTE FUNCTION fn_auditoria_trigger('id_asistente');

-- Tipos de Ticket
CREATE TRIGGER trg_auditoria_tipos_ticket
    AFTER INSERT OR UPDATE OR DELETE ON tipos_ticket
    FOR EACH ROW EXECUTE FUNCTION fn_auditoria_trigger('id_tipo_ticket');

-- Perfiles Organizador
CREATE TRIGGER trg_auditoria_perfiles_organizador
    AFTER INSERT OR UPDATE OR DELETE ON perfiles_organizador
    FOR EACH ROW EXECUTE FUNCTION fn_auditoria_trigger('id_perfil_organizador');

-- Perfiles Artista
CREATE TRIGGER trg_auditoria_perfiles_artista
    AFTER INSERT OR UPDATE OR DELETE ON perfiles_artista
    FOR EACH ROW EXECUTE FUNCTION fn_auditoria_trigger('id_perfil_artista');

-- Códigos Promocionales
CREATE TRIGGER trg_auditoria_codigos_promocionales
    AFTER INSERT OR UPDATE OR DELETE ON codigos_promocionales
    FOR EACH ROW EXECUTE FUNCTION fn_auditoria_trigger('id_codigo_promocional');