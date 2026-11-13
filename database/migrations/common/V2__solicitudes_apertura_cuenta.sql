-- La apertura requiere la aprobación de un empleado.

CREATE SEQUENCE cuenta_numero_seq START WITH 1000000001;

SELECT setval(
    'cuenta_numero_seq',
    GREATEST(
        COALESCE((SELECT MAX(numero::BIGINT) FROM cuentas WHERE numero ~ '^[0-9]{1,18}$'), 1000000000),
        1000000000
    ),
    TRUE
);

CREATE UNIQUE INDEX uq_cuentas_usuario ON cuentas (usuario_id);

CREATE TABLE solicitudes_cuenta (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cliente_id UUID NOT NULL REFERENCES usuarios(id) ON DELETE RESTRICT,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    creada_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resuelta_en TIMESTAMPTZ,
    empleado_id UUID REFERENCES usuarios(id) ON DELETE RESTRICT,
    cuenta_id UUID UNIQUE REFERENCES cuentas(id) ON DELETE RESTRICT,
    CONSTRAINT ck_solicitudes_cuenta_estado CHECK (
        estado IN ('PENDIENTE', 'APROBADA', 'RECHAZADA')
    ),
    CONSTRAINT ck_solicitudes_cuenta_resolucion CHECK (
        (estado = 'PENDIENTE' AND resuelta_en IS NULL AND empleado_id IS NULL AND cuenta_id IS NULL)
        OR (estado = 'APROBADA' AND resuelta_en IS NOT NULL AND empleado_id IS NOT NULL AND cuenta_id IS NOT NULL)
        OR (estado = 'RECHAZADA' AND resuelta_en IS NOT NULL AND empleado_id IS NOT NULL AND cuenta_id IS NULL)
    )
);

CREATE UNIQUE INDEX uq_solicitudes_cuenta_pendiente_cliente
    ON solicitudes_cuenta (cliente_id)
    WHERE estado = 'PENDIENTE';

CREATE INDEX ix_solicitudes_cuenta_estado_fecha
    ON solicitudes_cuenta (estado, creada_en);
