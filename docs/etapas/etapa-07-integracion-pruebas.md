# Etapa 7 — Integración, pruebas y calidad

**Estado: Parcial; compilación y migración V2 comprobadas, flujo Swing pendiente.**

## Evidencia disponible

- Compilación Maven con Java 25: código de salida `0` después del flujo de solicitudes.
- Flyway validó y aplicó V2 sobre V1 baseline en PostgreSQL local `bankpp`.
- La migración preservó los 2 usuarios existentes y no creó cuentas ni solicitudes artificiales.
- Tras el reporte de `btnSolicitar == null`, se alinearon los `.form` Swing y la compilación volvió a terminar con éxito.
- No se encontró `src/test`, resultados de JUnit ni matriz de escenarios ejecutados.

## Sin probar

Falta volver a ejecutar desde IntelliJ y probar el login de cliente que reportó el error; también recorrer registro/solicitud → login empleado → aprobación o rechazo → estado y cuenta del cliente. Retiros, saldo insuficiente, transferencias/rollback, historial, citas, solicitudes de atención y conexión remota siguen pendientes de validación funcional.

**Próximo trabajo:** iniciar la aplicación, solicitar apertura con el cliente existente, resolverla con el empleado existente y comprobar que se crea una única cuenta. Registrar el resultado sin incluir secretos. Compilar y migrar no sustituyen una prueba funcional.
