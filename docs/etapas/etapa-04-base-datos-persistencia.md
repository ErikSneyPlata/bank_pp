# Etapa 4 — Base de datos y persistencia

**Estado: En desarrollo; conexión local y migración V2 comprobadas.**

## Implementado en archivos

- `database/migrations/common/V1__initial_schema.sql` define las seis tablas base.
- `database/migrations/common/V2__solicitudes_apertura_cuenta.sql` añade solicitudes de apertura, estados y el generador de números de cuenta.
- `database/pom.xml` ejecutó Flyway hasta la versión 2; V1 ya existía y quedó registrada como baseline.
- `DatabaseConfig` usa JDBC, prioriza `BANKPP_*` del entorno y permite `.env` local como alternativa; `.env` está excluido por `.gitignore`.
- Hay repositorios JDBC de autenticación/registro, cuentas, transacciones y atención. Las transferencias están codificadas como operación JDBC transaccional con rollback.
- El registro de cliente, sus datos de contacto y la solicitud pendiente se guardan en una transacción. La aprobación crea la cuenta y resuelve la solicitud en otra transacción.
- Supabase solo tiene documentación futura; no hay adaptador remoto activo.

## Pendiente/discrepancias

- PostgreSQL local es `bankpp`; Flyway confirmó V2 aplicada y la compilación Java 25 terminó correctamente.
- Antes de aplicar V2 había 1 cliente, 1 empleado y 0 cuentas. Los usuarios existentes se conservaron; no se insertaron solicitudes ni cuentas artificiales.
- La conexión JDBC se comprobó previamente usando `.env`. Supabase no está desplegado ni probado.
- El cliente existente puede solicitar la apertura desde Cuentas; nuevos registros generan una solicitud pendiente.
- La contraseña se guarda en `contrasena` en texto simple por decisión del equipo, distinto del requisito antiguo de Notion.

## Actualización de estado — 2026-11-12

La base PostgreSQL local bankpp avanzó de V2 a V3 mediante Flyway. V3 habilita depósitos y se registró en el historial de migraciones. La guía [GUIA_TECNICA.md](../GUIA_TECNICA.md) explica V1–V3, restricciones, JDBC, transacciones y configuración. Supabase continúa sin conexión activa desde la aplicación.


