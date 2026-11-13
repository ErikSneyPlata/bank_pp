-- Añade depósitos realizados por un empleado a las operaciones soportadas.
ALTER TABLE transacciones DROP CONSTRAINT ck_transacciones_tipo;
ALTER TABLE transacciones ADD CONSTRAINT ck_transacciones_tipo
    CHECK (tipo IN ('DEPOSITO', 'RETIRO', 'TRANSFERENCIA'));

ALTER TABLE transacciones DROP CONSTRAINT ck_transacciones_cuentas;
ALTER TABLE transacciones ADD CONSTRAINT ck_transacciones_cuentas CHECK (
    (tipo = 'TRANSFERENCIA' AND cuenta_destino_id IS NOT NULL AND cuenta_destino_id <> cuenta_origen_id)
    OR (tipo IN ('DEPOSITO', 'RETIRO') AND cuenta_destino_id IS NULL)
);
