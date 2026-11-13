-- Esquema inicial para el proyecto académico local con PostgreSQL.
-- La conexión de esta etapa se realiza localmente mediante JDBC.
-- Los identificadores UUID relacionan las tablas del proyecto.

CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    documento VARCHAR(30) NOT NULL UNIQUE,
    nombres VARCHAR(80) NOT NULL,
    apellidos VARCHAR(80) NOT NULL,
    correo VARCHAR(254) NOT NULL,
    contrasena VARCHAR(120) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_usuarios_rol CHECK (rol IN ('CLIENTE', 'EMPLEADO'))
);

CREATE UNIQUE INDEX uq_usuarios_correo_lower ON usuarios (lower(correo));

CREATE TABLE datos_contacto (
    usuario_id UUID PRIMARY KEY REFERENCES usuarios(id) ON DELETE CASCADE,
    telefono VARCHAR(25),
    direccion VARCHAR(180)
);

CREATE TABLE cuentas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL REFERENCES usuarios(id) ON DELETE RESTRICT,
    numero VARCHAR(30) NOT NULL UNIQUE,
    saldo NUMERIC(18, 2) NOT NULL DEFAULT 0,
    moneda CHAR(3) NOT NULL DEFAULT 'COP',
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVA',
    creada_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_cuentas_saldo CHECK (saldo >= 0),
    CONSTRAINT ck_cuentas_moneda CHECK (moneda = 'COP'),
    CONSTRAINT ck_cuentas_estado
        CHECK (estado IN ('ACTIVA', 'BLOQUEADA', 'CERRADA'))
);

CREATE INDEX ix_cuentas_usuario ON cuentas (usuario_id);

CREATE TABLE transacciones (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cuenta_origen_id UUID NOT NULL REFERENCES cuentas(id) ON DELETE RESTRICT,
    cuenta_destino_id UUID REFERENCES cuentas(id) ON DELETE RESTRICT,
    usuario_actor_id UUID NOT NULL REFERENCES usuarios(id) ON DELETE RESTRICT,
    tipo VARCHAR(20) NOT NULL,
    monto NUMERIC(18, 2) NOT NULL,
    fecha TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descripcion TEXT,
    CONSTRAINT ck_transacciones_tipo
        CHECK (tipo IN ('RETIRO', 'TRANSFERENCIA')),
    CONSTRAINT ck_transacciones_monto CHECK (monto > 0),
    CONSTRAINT ck_transacciones_cuentas CHECK (
        (tipo = 'TRANSFERENCIA'
            AND cuenta_destino_id IS NOT NULL
            AND cuenta_destino_id <> cuenta_origen_id)
        OR
        (tipo = 'RETIRO' AND cuenta_destino_id IS NULL)
    )
);

CREATE INDEX ix_transacciones_origen_fecha
    ON transacciones (cuenta_origen_id, fecha DESC);
CREATE INDEX ix_transacciones_destino_fecha
    ON transacciones (cuenta_destino_id, fecha DESC)
    WHERE cuenta_destino_id IS NOT NULL;

CREATE TABLE citas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cliente_id UUID NOT NULL REFERENCES usuarios(id) ON DELETE RESTRICT,
    empleado_asignado_id UUID REFERENCES usuarios(id) ON DELETE SET NULL,
    fecha_hora TIMESTAMPTZ NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'SOLICITADA',
    motivo TEXT,
    creada_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_citas_estado CHECK (
        estado IN ('SOLICITADA', 'CONFIRMADA', 'CANCELADA', 'COMPLETADA')
    )
);

CREATE INDEX ix_citas_cliente_fecha ON citas (cliente_id, fecha_hora);
CREATE INDEX ix_citas_empleado_fecha
    ON citas (empleado_asignado_id, fecha_hora)
    WHERE empleado_asignado_id IS NOT NULL;

CREATE TABLE solicitudes_atencion (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cliente_id UUID NOT NULL REFERENCES usuarios(id) ON DELETE RESTRICT,
    empleado_asignado_id UUID REFERENCES usuarios(id) ON DELETE SET NULL,
    descripcion TEXT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ABIERTA',
    creada_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_solicitudes_estado CHECK (
        estado IN ('ABIERTA', 'EN_PROCESO', 'RESUELTA', 'CERRADA')
    )
);

CREATE INDEX ix_solicitudes_cliente_estado
    ON solicitudes_atencion (cliente_id, estado);
CREATE INDEX ix_solicitudes_empleado_estado
    ON solicitudes_atencion (empleado_asignado_id, estado)
    WHERE empleado_asignado_id IS NOT NULL;