CREATE TABLE paises (
    id_pais SERIAL PRIMARY KEY,
    nombre_pais VARCHAR(100) NOT NULL,
    codigo_iso VARCHAR(3) NOT NULL
);

CREATE TABLE ciudades (
    id_ciudad SERIAL PRIMARY KEY,
    nombre_ciudad VARCHAR(100) NOT NULL,
    pais_id INTEGER NOT NULL,
    CONSTRAINT fk_ciudades_pais FOREIGN KEY (pais_id)
        REFERENCES paises(id_pais)
);

CREATE TABLE categorias_evento (
    id_categoria_evento SERIAL PRIMARY KEY,
    nombre_categoria VARCHAR(100) NOT NULL,
    slug_categoria VARCHAR(100) NOT NULL UNIQUE,
    icono_url VARCHAR(500),
    estado BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE subcategorias_evento (
    id_subcategoria_evento SERIAL PRIMARY KEY,
    nombre_subcategoria VARCHAR(100) NOT NULL,
    slug_subcategoria VARCHAR(100) NOT NULL,
    categoria_evento_id INTEGER NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_subcategorias_evento_categoria FOREIGN KEY (categoria_evento_id)
        REFERENCES categorias_evento(id_categoria_evento)
);

-- Tabla: categorias_plan
CREATE TABLE categorias_plan (
    id_categoria_plan SERIAL PRIMARY KEY,
    nombre_categoria_plan VARCHAR(100) NOT NULL
);

CREATE TABLE subcategorias_plan (
    id_subcategoria_plan SERIAL PRIMARY KEY,
    nombre_subcategoria_plan VARCHAR(100) NOT NULL,
    categoria_plan_id INTEGER NOT NULL,
    CONSTRAINT fk_subcategorias_plan_categoria FOREIGN KEY (categoria_plan_id)
        REFERENCES categorias_plan(id_categoria_plan)
);

CREATE TABLE locales (
    id_local SERIAL PRIMARY KEY,
    nombre_local VARCHAR(200) NOT NULL,
    capacidad_local INTEGER NOT NULL,
    direccion_local VARCHAR(300) NOT NULL,
    ciudad_id INTEGER NOT NULL,
    slug_local VARCHAR(220) NOT NULL DEFAULT '',
    latitud NUMERIC(9, 6) NOT NULL DEFAULT 0.0,
    longitud NUMERIC(9, 6) NOT NULL DEFAULT 0.0,
    CONSTRAINT fk_locales_ciudad FOREIGN KEY (ciudad_id)
        REFERENCES ciudades(id_ciudad)
);

CREATE TABLE codigos_promocionales (
    id_codigo_promocional SERIAL PRIMARY KEY,
    codigo_promocional VARCHAR(50) NOT NULL UNIQUE,
    descripcion_promocion VARCHAR(300),
    tipo_descuento VARCHAR(20) NOT NULL DEFAULT 'porcentaje',
    valor_descuento NUMERIC(10, 2) NOT NULL,
    compra_minima NUMERIC(10, 2),
    descuento_maximo NUMERIC(10, 2),
    limite_uso INTEGER,
    contador_uso INTEGER NOT NULL DEFAULT 0,
    fecha_inicio_promocion TIMESTAMP NOT NULL,
    fecha_fin_promocion TIMESTAMP NOT NULL,
    promocion_activa BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_tipo_descuento CHECK (tipo_descuento IN ('porcentaje', 'monto_fijo'))
);

-- =============================================
-- TABLAS DE USUARIOS Y PERFILES
-- =============================================

CREATE TABLE usuarios (
    id_usuario SERIAL PRIMARY KEY,
    nombre_completo VARCHAR(150) NOT NULL,
    correo_electronico VARCHAR(150) NOT NULL UNIQUE,
    contrasena_hash VARCHAR(255) NOT NULL,
    numero_telefono VARCHAR(20),
    imagen_perfil_url VARCHAR(500),
    fecha_nacimiento DATE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    email_verificado BOOLEAN NOT NULL DEFAULT FALSE,
    cuenta_activa BOOLEAN NOT NULL DEFAULT TRUE,
    tipo_usuario VARCHAR(20) NOT NULL DEFAULT 'normal',
    id_ciudad INTEGER,
    CONSTRAINT fk_usuarios_ciudades FOREIGN KEY (id_ciudad)
        REFERENCES ciudades(id_ciudad),
    CONSTRAINT chk_tipo_usuario CHECK (tipo_usuario IN ('normal', 'artista', 'organizador'))
);

CREATE TABLE perfiles_organizador (
    id_perfil_organizador SERIAL PRIMARY KEY,
    nombre_organizador VARCHAR(200) NOT NULL,
    descripcion_organizador TEXT NOT NULL,
    sitio_web VARCHAR(300),
    logo_url VARCHAR(500),
    facebook_url VARCHAR(300),
    instagram_url VARCHAR(300),
    tiktok_url VARCHAR(300),
    twitter_url VARCHAR(300),
    direccion_organizador VARCHAR(300),
    telefono_contacto VARCHAR(20),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_id INTEGER NOT NULL UNIQUE,
    CONSTRAINT fk_perfiles_organizador_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id_usuario)
);

CREATE TABLE perfiles_artista (
    id_perfil_artista SERIAL PRIMARY KEY,
    nombre_artistico VARCHAR(150) NOT NULL,
    biografia_artista TEXT NOT NULL,
    genero_musical VARCHAR(100) NOT NULL,
    sitio_web VARCHAR(300),
    facebook_url VARCHAR(300),
    instagram_url VARCHAR(300),
    tiktok_url VARCHAR(300),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_id INTEGER NOT NULL UNIQUE,
    CONSTRAINT fk_perfiles_artista_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id_usuario)
);

-- =============================================
-- TABLAS DE EVENTOS Y PLANES
-- =============================================

CREATE TABLE eventos (
    id_evento SERIAL PRIMARY KEY,
    titulo_evento VARCHAR(250) NOT NULL,
    slug_evento VARCHAR(250) NOT NULL UNIQUE,
    descripcion_evento TEXT NOT NULL,
    descripcion_corta VARCHAR(500),
    fecha_inicio TIMESTAMPTZ NOT NULL,
    fecha_fin TIMESTAMPTZ NOT NULL,
    zona_horaria VARCHAR(50) NOT NULL DEFAULT 'UTC',
    estado_evento VARCHAR(20) NOT NULL DEFAULT 'borrador',
    capacidad_evento INTEGER NOT NULL,
    evento_gratuito BOOLEAN NOT NULL DEFAULT FALSE,
    evento_online BOOLEAN NOT NULL DEFAULT FALSE,
    imagen_portada_url VARCHAR(500),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    perfil_organizador_id INTEGER NOT NULL,
    subcategoria_evento_id INTEGER NOT NULL,
    local_id INTEGER,
    CONSTRAINT fk_eventos_organizador FOREIGN KEY (perfil_organizador_id)
        REFERENCES perfiles_organizador(id_perfil_organizador),
    CONSTRAINT fk_eventos_subcategoria FOREIGN KEY (subcategoria_evento_id)
        REFERENCES subcategorias_evento(id_subcategoria_evento),
    CONSTRAINT fk_eventos_local FOREIGN KEY (local_id)
        REFERENCES locales(id_local),
    CONSTRAINT chk_estado_evento CHECK (estado_evento IN ('borrador', 'publicado', 'cancelado', 'finalizado'))
);

CREATE TABLE imagenes_evento (
    id_imagen_evento SERIAL PRIMARY KEY,
    url_imagen VARCHAR(500) NOT NULL,
    texto_alternativo VARCHAR(200),
    orden_imagen INTEGER NOT NULL DEFAULT 0,
    evento_id INTEGER NOT NULL,
    CONSTRAINT fk_imagenes_evento_evento FOREIGN KEY (evento_id)
        REFERENCES eventos(id_evento)
);

CREATE TABLE tipos_ticket (
    id_tipo_ticket SERIAL PRIMARY KEY,
    nombre_ticket VARCHAR(150) NOT NULL,
    descripcion_ticket VARCHAR(500),
    precio_ticket NUMERIC(10, 2) NOT NULL,
    cantidad_total INTEGER NOT NULL,
    cantidad_vendida INTEGER NOT NULL DEFAULT 0,
    cantidad_disponible INTEGER NOT NULL,
    fecha_inicio_venta TIMESTAMP NOT NULL,
    fecha_fin_venta TIMESTAMP NOT NULL,
    compra_minima INTEGER NOT NULL DEFAULT 1,
    compra_maxima INTEGER NOT NULL DEFAULT 10,
    ticket_activo BOOLEAN NOT NULL DEFAULT TRUE,
    evento_id INTEGER NOT NULL,
    CONSTRAINT fk_tipos_ticket_evento FOREIGN KEY (evento_id)
        REFERENCES eventos(id_evento)
);

CREATE TABLE planes (
    id_plan SERIAL PRIMARY KEY,
    titulo_plan VARCHAR(250) NOT NULL,
    descripcion_plan TEXT NOT NULL,
    subcategoria_plan_id INTEGER NOT NULL,
    CONSTRAINT fk_planes_subcategoria FOREIGN KEY (subcategoria_plan_id)
        REFERENCES subcategorias_plan(id_subcategoria_plan)
);

CREATE TABLE imagenes_plan (
    id_imagen_plan SERIAL PRIMARY KEY,
    url_imagen_plan VARCHAR(500) NOT NULL,
    plan_id INTEGER NOT NULL,
    CONSTRAINT fk_imagenes_plan_plan FOREIGN KEY (plan_id)
        REFERENCES planes(id_plan)
);

-- =============================================
-- TABLAS DE ORDENES Y PAGOS
-- =============================================

CREATE TABLE ordenes (
    id_orden SERIAL PRIMARY KEY,
    numero_orden VARCHAR(50) NOT NULL UNIQUE,
    subtotal_orden NUMERIC(10, 2) NOT NULL,
    impuesto_orden NUMERIC(10, 2) NOT NULL DEFAULT 0,
    tarifa_servicio NUMERIC(10, 2) NOT NULL DEFAULT 0,
    descuento_orden NUMERIC(10, 2) NOT NULL DEFAULT 0,
    total_orden NUMERIC(10, 2) NOT NULL,
    estado_orden VARCHAR(20) NOT NULL DEFAULT 'pendiente',
    email_cliente VARCHAR(150) NOT NULL,
    nombre_cliente VARCHAR(150) NOT NULL,
    telefono_cliente VARCHAR(20),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_id INTEGER NOT NULL,
    codigo_promocional_id INTEGER,
    CONSTRAINT fk_ordenes_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_ordenes_codigo_promocional FOREIGN KEY (codigo_promocional_id)
        REFERENCES codigos_promocionales(id_codigo_promocional),
    CONSTRAINT chk_estado_orden CHECK (estado_orden IN ('pendiente', 'completado', 'cancelado', 'reembolsado'))
);

CREATE TABLE items_orden (
    id_item_orden SERIAL PRIMARY KEY,
    cantidad_item INTEGER NOT NULL,
    precio_unitario NUMERIC(10, 2) NOT NULL,
    subtotal_item NUMERIC(10, 2) NOT NULL,
    orden_id INTEGER NOT NULL,
    tipo_ticket_id INTEGER NOT NULL,
    CONSTRAINT fk_items_orden_orden FOREIGN KEY (orden_id)
        REFERENCES ordenes(id_orden),
    CONSTRAINT fk_items_orden_tipo_ticket FOREIGN KEY (tipo_ticket_id)
        REFERENCES tipos_ticket(id_tipo_ticket)
);

CREATE TABLE asistentes (
    id_asistente SERIAL PRIMARY KEY,
    numero_ticket VARCHAR(50) NOT NULL UNIQUE,
    nombre_asistente VARCHAR(100) NOT NULL,
    apellido_asistente VARCHAR(100) NOT NULL,
    email_asistente VARCHAR(150) NOT NULL,
    telefono_asistente VARCHAR(20),
    registro_entrada BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_entrada TIMESTAMP,
    codigo_qr VARCHAR(500) NOT NULL,
    orden_id INTEGER NOT NULL,
    tipo_ticket_id INTEGER NOT NULL,
    CONSTRAINT fk_asistentes_orden FOREIGN KEY (orden_id)
        REFERENCES ordenes(id_orden),
    CONSTRAINT fk_asistentes_tipo_ticket FOREIGN KEY (tipo_ticket_id)
        REFERENCES tipos_ticket(id_tipo_ticket)
);

CREATE TABLE pagos (
    id_pago SERIAL PRIMARY KEY,
    metodo_pago VARCHAR(50) NOT NULL,
    monto_pago NUMERIC(10, 2) NOT NULL,
    moneda_pago VARCHAR(10) NOT NULL DEFAULT 'PEN',
    estado_pago VARCHAR(20) NOT NULL DEFAULT 'pendiente',
    transaccion_id VARCHAR(100),
    gateway_pago VARCHAR(50),
    respuesta_gateway TEXT,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_completado TIMESTAMP,
    fecha_reembolso TIMESTAMP,
    orden_id INTEGER NOT NULL,
    CONSTRAINT fk_pagos_orden FOREIGN KEY (orden_id)
        REFERENCES ordenes(id_orden),
    CONSTRAINT chk_estado_pago CHECK (estado_pago IN ('pendiente', 'completado', 'fallido', 'reembolsado'))
);

-- =============================================
-- TABLAS DE INTERACCIONES Y RESEÑAS
-- =============================================

CREATE TABLE lista_deseos (
    id_lista_deseos SERIAL PRIMARY KEY,
    tipo_item VARCHAR(20) NOT NULL,
    fecha_agregado TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_id INTEGER NOT NULL,
    item_id INTEGER NOT NULL,
    CONSTRAINT fk_lista_deseos_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id_usuario),
    CONSTRAINT chk_tipo_item CHECK (tipo_item IN ('evento', 'plan'))
);

CREATE TABLE seguidores_evento (
    id_seguidor_evento SERIAL PRIMARY KEY,
    fecha_seguimiento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_id INTEGER NOT NULL,
    evento_id INTEGER NOT NULL,
    CONSTRAINT fk_seguidores_evento_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_seguidores_evento_evento FOREIGN KEY (evento_id)
        REFERENCES eventos(id_evento),
    CONSTRAINT uk_seguidores_evento UNIQUE (usuario_id, evento_id)
);

CREATE TABLE seguidores_plan (
    id_seguidor_plan SERIAL PRIMARY KEY,
    fecha_seguimiento_plan TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    plan_id INTEGER NOT NULL,
    usuario_id INTEGER NOT NULL,
    CONSTRAINT fk_seguidores_plan_plan FOREIGN KEY (plan_id)
        REFERENCES planes(id_plan),
    CONSTRAINT fk_seguidores_plan_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id_usuario),
    CONSTRAINT uk_seguidores_plan UNIQUE (usuario_id, plan_id)
);

CREATE TABLE resenas_evento (
    id_resena_evento SERIAL PRIMARY KEY,
    calificacion_evento INTEGER NOT NULL,
    comentario_evento TEXT NOT NULL,
    fue_util BOOLEAN,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_id INTEGER NOT NULL,
    evento_id INTEGER NOT NULL,
    CONSTRAINT fk_resenas_evento_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_resenas_evento_evento FOREIGN KEY (evento_id)
        REFERENCES eventos(id_evento),
    CONSTRAINT chk_calificacion_evento CHECK (calificacion_evento >= 1 AND calificacion_evento <= 5)
);

CREATE TABLE resenas_organizador (
    id_resena_organizador SERIAL PRIMARY KEY,
    calificacion_resena INTEGER NOT NULL,
    comentario_resena TEXT NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    contador_utilidad INTEGER NOT NULL DEFAULT 0,
    comprador_verificado BOOLEAN NOT NULL DEFAULT FALSE,
    perfil_organizador_id INTEGER NOT NULL,
    usuario_id INTEGER NOT NULL,
    CONSTRAINT fk_resenas_organizador_perfil FOREIGN KEY (perfil_organizador_id)
        REFERENCES perfiles_organizador(id_perfil_organizador),
    CONSTRAINT fk_resenas_organizador_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id_usuario),
    CONSTRAINT chk_calificacion_organizador CHECK (calificacion_resena >= 1 AND calificacion_resena <= 5)
);