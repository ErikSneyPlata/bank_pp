# Etapa 5 — Modelo POO y lógica de negocio

**Estado: Parcial; compilado con flujo de solicitud de apertura de cuenta.**

## Código encontrado

- Dominio: `Usuario` abstracta, `Cliente`, `Empleado`, `DatosContacto`, `Banco`, `CuentaBancaria`, `SolicitudCuenta`, `Transaccion` abstracta, `Retiro`, `Transferencia`, `Cita`, `SolicitudAtencion`.
- Enumeraciones de rol, tipo de transacción y estados de cuenta, citas, atención y apertura de cuenta.
- Servicios de autenticación, cuentas, transacciones, atención y solicitudes de apertura; repositorios JDBC concretos.
- La apertura requiere una solicitud pendiente y una decisión de un empleado.
- `javac --release 25` compiló el código fuente sin errores.

## Acuerdos y límites

- El equipo pidió no declarar Java `interface`; `OperacionBancaria` y `Autenticable` de propuestas anteriores no están en el código activo.
- Se retiró el hasheo; el esquema y la autenticación usan `contrasena` en texto simple por indicación del equipo.
- La solicitud y la aprobación se ejecutan de forma atómica con JDBC; falta recorrer manualmente el flujo desde Swing.
- El primer empleado sigue siendo un alta controlada; no existe registro público de empleados.

**Pendiente:** confirmar criterios discrepantes con el docente y probar manualmente reglas de negocio, permisos y transacciones en PostgreSQL.

## Actualización de modelo — 2026-11-12

Se incorporó Deposito como subtipo concreto de Transaccion, junto con TipoTransaccion.DEPOSITO, para soportar el depósito registrado por empleado. No se añadieron declaraciones Java interface; se conserva la decisión de nivel académico. La guía [GUIA_TECNICA.md](../GUIA_TECNICA.md) explica las clases, herencia, composición y agregación presentes.
