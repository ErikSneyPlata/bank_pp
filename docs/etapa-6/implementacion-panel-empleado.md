# Implementación del panel de empleado

**Proyecto:** Bank PP  
**Etapa:** 6 — Interfaz Swing (integración con persistencia existente)  
**Fecha:** 2026-11-12

## Alcance realizado

Se implementó el apartado de empleado definido por las vistas UML: gestión de clientes y cuentas, consulta de transacciones, gestión de citas y atención de solicitudes. Se agregó el registro de depósitos solicitado, porque el esquema anterior solo admitía retiros y transferencias. El panel de cliente conserva sus formularios y acciones existentes.

No se añadieron préstamos, pagos, tarjetas, transferencias externas ni módulos administrativos ajenos al alcance.

## Pantallas y responsabilidades

### `EmpleadoFrame`

Ventana principal de empleado. Muestra nombre del usuario, pestañas para Clientes, Cuentas, Transacciones, Citas y Atención, y cierre de sesión hacia el Login. Su estructura raíz está en `EmpleadoFrame.form`; los contenidos de cada pestaña son paneles Swing.

### `ClientesEmpleadoForm`

- Buscar por documento, nombre, apellido o correo.
- Registrar cliente con los campos académicos básicos. El alta usa `AutenticacionService` y conserva la regla actual de crear solicitud pendiente de cuenta.
- Editar nombre, apellido, correo y contacto.
- Desactivar cliente (baja lógica en `usuarios.activo`; no elimina cuentas ni transacciones).

Persistencia: `ClienteService` valida empleado activo y campos; `ClienteRepositoryJdbc` consulta y actualiza `usuarios`/`datos_contacto` con JDBC. La actualización de ambos registros usa una transacción SQL.

### `CuentasEmpleadoForm`

- Listar cuentas existentes y estado.
- Bloquear una cuenta.
- Cerrar una cuenta solo cuando su saldo sea cero.
- Revisar solicitudes de apertura pendientes y aprobarlas o rechazarlas.

La cuenta solo se abre desde la aprobación de una solicitud; no existe alta directa que salte la autorización. La solicitud y la cuenta se resuelven de manera atómica por el repositorio ya existente.

### `TransaccionesEmpleadoForm`

- Consultar el historial global de transacciones.
- Filtrar por número de cuenta.
- Registrar depósito indicando cuenta y monto.

El depósito busca una cuenta existente y activa. `TransaccionService` valida monto positivo con máximo dos decimales y rol del empleado. `TransaccionRepositoryJdbc` bloquea la fila con `SELECT ... FOR UPDATE`, acredita el saldo, registra `DEPOSITO` y confirma; cualquier excepción revierte la operación completa. El historial incluye depósitos al mapearlos como `Deposito`.

### `CitasEmpleadoForm`

Lista citas y permite confirmarlas, cancelarlas o completarlas. Las transiciones se comprueban frente al estado actual: `SOLICITADA → CONFIRMADA/CANCELADA`; `CONFIRMADA → COMPLETADA/CANCELADA`. La gestión registra el empleado asignado.

### `AtencionEmpleadoForm`

Lista solicitudes de atención. “Tomar solicitud” las asigna al empleado y las pasa a `EN_PROCESO`; también se pueden resolver o cerrar. Solo se actualizan solicitudes `ABIERTA` o `EN_PROCESO`.

## Cambios en capas y archivos

| Capa | Cambios |
| --- | --- |
| View | `EmpleadoFrame`, `ClientesEmpleadoForm`, `CuentasEmpleadoForm`, `TransaccionesEmpleadoForm`, `CitasEmpleadoForm`, `AtencionEmpleadoForm`; la ruta de Login abre el nuevo marco. |
| Service | `ClienteService`; ampliación de `CuentaService`, `TransaccionService` y `AtencionService` con validaciones del rol/estado. |
| Repository JDBC | `ClienteRepositoryJdbc`; listados y cambios de estado para cuentas, movimientos globales, citas y solicitudes. |
| Model | `Deposito` hereda de `Transaccion`; `TipoTransaccion` acepta `DEPOSITO`. |
| Database | `V3__depositos_empleado.sql`; Flyway aplicada en `bankpp`. |
| Diagramas | UML de interfaces refleja depósito en transacciones del empleado; UML de clases incorpora `Deposito` como subtipo de `Transaccion`. |

## Conexiones y desconexiones

Los repositorios gestionan `Connection`, `PreparedStatement` y `ResultSet` mediante `try-with-resources`, por lo que se cierran tanto en ejecución correcta como ante excepción. Retiros, transferencias, altas con múltiples filas, actualización de datos de cliente y depósitos agrupan los cambios en transacciones con `commit`; al fallar se ejecuta `rollback`. No se conserva una conexión global abierta en los formularios.

El `.env` sigue local y excluido del repositorio. Esta documentación no registra usuario/contraseña ni publica secretos.

## Migración local

`database/migrations/common/V3__depositos_empleado.sql` añade el tipo de movimiento y ajusta restricciones existentes. Se aplicó vía Flyway Maven 13.12.0; la base local quedó en v3. Para otra máquina, iniciar PostgreSQL, crear `bankpp`, configurar las variables Flyway localmente y ejecutar desde raíz:

```powershell
mvn -f database/pom.xml flyway:info
mvn -f database/pom.xml flyway:migrate
```

Si Maven no está instalado globalmente, usar el Maven configurado por IntelliJ IDEA. La migración no crea datos demo ni modifica saldos existentes.

## Verificación y límites

- Compilación y empaquetado Maven con Java 25: correctos; las pruebas automatizadas se omitieron.
- Conexión read-only a `bankpp`: confirmada; PostgreSQL 18.3.
- Flyway: V3 aplicada correctamente, registro actualizado.
- Maven `package -DskipTests` generó `target/bank_pp-1.0-SNAPSHOT.jar`; el JAR incluye las clases de la aplicación y el controlador PostgreSQL. No se generó EXE.
- No se verificaron clics, diálogos ni operaciones desde la GUI.
- No se ejecutaron depósitos reales ni operaciones financieras de prueba; el recorrido manual queda pendiente.
- La aplicación sigue siendo JDBC local; Supabase no se implementó.

## Instrucciones de prueba manual

1. Abrir Bank PP desde IntelliJ.
2. Iniciar sesión con un empleado activo provisionado por el método ya documentado.
3. Confirmar que aparecen las cinco pestañas.
4. En Clientes, buscar, editar, registrar y desactivar un cliente de prueba; revisar que el registro cree solicitud pendiente.
5. En Cuentas, aprobar y rechazar solicitudes; intentar cerrar una cuenta con saldo distinto de cero y comprobar el mensaje.
6. En Transacciones, depositar en una cuenta activa, revisar saldo e historial; repetir con cuenta inexistente/inactiva y monto inválido.
7. En Citas, probar solo transiciones válidas y luego una no válida.
8. En Atención, tomar una solicitud, resolverla y revisar el estado/empleado asignado.
9. Cerrar sesión; el login debe mostrarse de nuevo.

Las pruebas manuales deben usar registros de prueba controlados y comprobar resultados en pgAdmin; esta implementación no los crea automáticamente.


## Guía de arquitectura y ejecución

La explicación integral de clases, herencia/composición/agregación, flujo de formularios, tablas, migraciones Flyway, configuración JDBC, cierre de conexiones y atomicidad está en [GUIA_TECNICA.md](../GUIA_TECNICA.md). Esa guía también separa las comprobaciones realizadas de las pruebas manuales pendientes.

