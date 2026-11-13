# Modelo de dominio y base local

## Clases

| Clase | Responsabilidad | Relación POO |
|---|---|---|
| Usuario | Datos comunes de identidad | Abstracta |
| Cliente | Datos del titular | Hereda de Usuario; compone DatosContacto |
| Empleado | Identidad de empleado | Hereda de Usuario |
| DatosContacto | Teléfono y dirección | Compuesto dentro de Cliente |
| Banco | Mantiene una colección de empleados | Agregación |
| CuentaBancaria | Datos de cuenta y saldo consultado | Asociada al cliente |
| SolicitudCuenta | Solicitud de apertura y decisión | Asociada al cliente; al aprobarse queda vinculada a la cuenta y al empleado que la resolvió |
| Transaccion | Datos y validación común | Abstracta |
| Retiro | Operación sin cuenta destino | Hereda de Transaccion |
| Transferencia | Operación con cuentas distintas | Hereda de Transaccion |
| Cita | Fecha, motivo y estado de atención | Asociada al cliente |
| SolicitudAtencion | Descripción y estado de atención | Asociada al cliente |

Las clases `Retiro` y `Transferencia` especializan la abstracción `Transaccion`; sus validaciones se resuelven mediante sobrescritura y uso de la clase base, sin declarar interfaces Java.

## PostgreSQL

Las migraciones definen las tablas base `usuarios`, `datos_contacto`, `cuentas`, `transacciones`, `citas`, `solicitudes_atencion`, más `solicitudes_cuenta`. Esta última registra `PENDIENTE`, `APROBADA` o `RECHAZADA`; al aprobar, crea la cuenta y la vincula con el empleado responsable.

V2 agrega el generador secuencial de números de cuenta y limita a una cuenta por cliente para el flujo actual. La cuenta se abre solo después de la aprobación.

`usuarios.contrasena` es texto simple por requisito académico actual. Utiliza solo credenciales de demostración.

## Reglas de operaciones

Retiro valida monto positivo, propiedad de cuenta, cuenta activa y saldo suficiente. Transferencia valida ambas cuentas, evita cuenta igual, comprueba saldo y actualiza saldo origen/destino e historial en una sola transacción JDBC con rollback ante error.
