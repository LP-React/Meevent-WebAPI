CREATE TABLE auditoria (
    id_auditoria BIGSERIAL PRIMARY KEY,

    tabla_nombre VARCHAR(100) NOT NULL,
    operacion VARCHAR(10) NOT NULL CHECK (operacion IN ('INSERT', 'UPDATE', 'DELETE')),
    registro_id INTEGER NOT NULL,

    datos_anteriores JSONB,
    datos_nuevos JSONB,
    campos_modificados TEXT[],

    usuario_id INTEGER,
    usuario_email VARCHAR(150),
    usuario_nombre VARCHAR(150),
    direccion_ip VARCHAR(45),
    user_agent TEXT,

    fecha_operacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    origen VARCHAR(50),

    CONSTRAINT fk_auditoria_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id_usuario) ON DELETE SET NULL
);

-- Índices para optimizar consultas
CREATE INDEX idx_auditoria_tabla ON auditoria(tabla_nombre);
CREATE INDEX idx_auditoria_operacion ON auditoria(operacion);
CREATE INDEX idx_auditoria_registro_id ON auditoria(registro_id);
CREATE INDEX idx_auditoria_usuario ON auditoria(usuario_id);
CREATE INDEX idx_auditoria_fecha ON auditoria(fecha_operacion DESC);
CREATE INDEX idx_auditoria_tabla_registro ON auditoria(tabla_nombre, registro_id);

-- Índice GIN para búsquedas en JSONB
CREATE INDEX idx_auditoria_datos_anteriores ON auditoria USING GIN (datos_anteriores);
CREATE INDEX idx_auditoria_datos_nuevos ON auditoria USING GIN (datos_nuevos);
