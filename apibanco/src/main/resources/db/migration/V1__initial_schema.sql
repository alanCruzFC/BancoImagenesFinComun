-- ================================================
-- V1: Esquema inicial del sistema de banco de archivos
-- ================================================

-- Tabla de usuarios
CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    email VARCHAR(255),
    password_hash VARCHAR(255),
    rol VARCHAR(50),
    activo BOOLEAN DEFAULT TRUE,
    team VARCHAR(100),
    department VARCHAR(100),
    supervisor_id BIGINT,
    created_at DATETIME,
    updated_at DATETIME,
    deleted_at DATETIME,
    CONSTRAINT fk_usuario_supervisor FOREIGN KEY (supervisor_id) REFERENCES usuario(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de registros
CREATE TABLE registro (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_solicitud VARCHAR(100),
    carpeta_ruta VARCHAR(500),
    fecha_creacion DATETIME,
    fecha_eliminacion DATETIME,
    creador_id BIGINT,
    CONSTRAINT fk_registro_creador FOREIGN KEY (creador_id) REFERENCES usuario(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de correos autorizados
CREATE TABLE correos_autorizados (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    correo VARCHAR(255) NOT NULL,
    registro_id BIGINT NOT NULL,
    CONSTRAINT fk_correo_registro FOREIGN KEY (registro_id) REFERENCES registro(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de archivos
CREATE TABLE archivo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_original VARCHAR(500),
    nombre_final VARCHAR(500),
    tipo VARCHAR(100),
    ruta_local VARCHAR(1000),
    fecha_subida DATETIME,
    fecha_eliminacion DATETIME,
    registro_id BIGINT,
    usuario_id BIGINT,
    CONSTRAINT fk_archivo_registro FOREIGN KEY (registro_id) REFERENCES registro(id),
    CONSTRAINT fk_archivo_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de metadata
CREATE TABLE metadata (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_archivo VARCHAR(500),
    tipo_documento VARCHAR(100),
    fecha_subida DATETIME,
    fecha_desactivacion DATETIME,
    activo BOOLEAN DEFAULT TRUE,
    registro_id BIGINT,
    subido_por_id BIGINT,
    CONSTRAINT fk_metadata_registro FOREIGN KEY (registro_id) REFERENCES registro(id),
    CONSTRAINT fk_metadata_usuario FOREIGN KEY (subido_por_id) REFERENCES usuario(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de API Keys
CREATE TABLE api_key (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    clave VARCHAR(255) UNIQUE,
    consumidor VARCHAR(255),
    lectura BOOLEAN DEFAULT FALSE,
    escritura BOOLEAN DEFAULT FALSE,
    actualizacion BOOLEAN DEFAULT FALSE,
    eliminacion BOOLEAN DEFAULT FALSE,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion DATETIME,
    fecha_actualizacion DATETIME,
    fecha_eliminacion DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Índices para mejorar el rendimiento
CREATE INDEX idx_usuario_email ON usuario(email);
CREATE INDEX idx_usuario_username ON usuario(username);
CREATE INDEX idx_usuario_supervisor ON usuario(supervisor_id);
CREATE INDEX idx_archivo_registro ON archivo(registro_id);
CREATE INDEX idx_archivo_usuario ON archivo(usuario_id);
CREATE INDEX idx_metadata_registro ON metadata(registro_id);
CREATE INDEX idx_metadata_usuario ON metadata(subido_por_id);
CREATE INDEX idx_registro_creador ON registro(creador_id);
CREATE INDEX idx_correo_registro ON correos_autorizados(registro_id);
CREATE INDEX idx_apikey_clave ON api_key(clave);
