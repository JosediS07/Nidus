CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(255) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS recursos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    tipo VARCHAR(255) NOT NULL,
    descripcion TEXT,
    capacidad INTEGER,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS reservas (
    id BIGSERIAL PRIMARY KEY,
    recurso_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP NOT NULL,
    estado VARCHAR(255) NOT NULL,
    version INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_reserva_recurso_fechas ON reservas(recurso_id, fecha_inicio, fecha_fin);
CREATE INDEX IF NOT EXISTS idx_reserva_usuario ON reservas(usuario_id);

CREATE TABLE IF NOT EXISTS solicitudes_cola (
    id BIGSERIAL PRIMARY KEY,
    recurso_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    estado VARCHAR(255) NOT NULL,
    creado TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS historial_reservas (
    id BIGSERIAL PRIMARY KEY,
    reserva_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    tipo_evento VARCHAR(20) NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    creado TIMESTAMP NOT NULL
);
