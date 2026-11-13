# Bank PP — Estado actual del proyecto

**Actualizado:** 2026-11-12  
**Etapa activa:** Etapa 6 — Interfaz gráfica Swing (**en desarrollo; vistas del empleado implementadas, recorrido manual pendiente**)

## Resumen

El acceso `EMPLEADO` ahora abre `EmpleadoFrame`, con pestañas para clientes, cuentas y solicitudes de apertura, transacciones y depósitos, citas y atención de solicitudes. El acceso `CLIENTE` conserva su `PanelClienteForm` y sus secciones actuales.

Las acciones del empleado usan servicios y repositorios JDBC con verificaciones de rol. El cierre de cuenta requiere saldo cero. La aprobación de apertura conserva el flujo existente: se crea la cuenta a partir de una solicitud pendiente. El depósito actualiza saldo y registra el movimiento dentro de una transacción SQL.

La base local `bankpp` está en PostgreSQL 18.3 y Flyway V3 quedó aplicada. La compilación y el empaquetado Maven con Java 25 finalizaron correctamente; las pruebas se omitieron porque no hay suite automatizada. Se usó el Maven integrado de IntelliJ.

## Estado por evidencia

| Área | Estado | Evidencia / límite |
| --- | --- | --- |
| Inicio de sesión por rol | Implementado | `LoginForm` dirige al cliente a `PanelClienteForm` y al empleado a `EmpleadoFrame`. |
| Gestión de clientes por empleado | Implementada | Búsqueda/listado, alta, edición y desactivación mediante `ClienteService` y `ClienteRepositoryJdbc`. El alta genera solicitud de apertura pendiente. |
| Cuentas/aperturas | Implementadas | Lista de cuentas, bloquear/cerrar (cierre exige saldo cero), aprobar/rechazar solicitudes pendientes. |
| Transacciones y depósitos | Implementados | Consulta global/filtro por número y depósito a cuenta activa con JDBC atómico. |
| Citas | Implementadas | Consulta y transiciones permitidas entre solicitada, confirmada, cancelada y completada. |
| Solicitudes de atención | Implementadas | Listado, asignación al empleado al tomarla, cambio a en proceso/resuelta/cerrada. |
| PostgreSQL local | Comprobado parcialmente | Conexión a `bankpp`; historial Flyway llegó a V3. No se registraron datos bancarios de prueba. |
| Compilación y JAR | Comprobados | Maven de IntelliJ empaquetó el JAR con Java 25 y PostgreSQL JDBC incluido; pruebas omitidas. |
| Uso manual de Swing | Pendiente | No se recorrieron clic a clic todos los formularios ni cada estado. |
| EXE, Supabase y entrega | Pendiente | El JAR sí se empaquetó; falta probar los flujos, generar EXE y configurar Supabase. |

## Decisiones vigentes

- Se mantienen Java 25, Swing, IntelliJ UI Designer, JDBC y PostgreSQL.
- Se conserva el nivel académico acordado, sin declaraciones Java `interface` nuevas.
- Se mantiene el almacenamiento simple de contraseña solicitado para este ejercicio.
- Los formularios del cliente no se modificaron funcionalmente.
- Las contraseñas y valores del `.env` no se incluyeron en documentación ni en Git.

## Siguiente recorrido manual

1. Iniciar sesión con un empleado activo y verificar las cinco pestañas.
2. Buscar, editar, desactivar y registrar un cliente; confirmar que el alta crea una solicitud pendiente.
3. Aprobar/rechazar una solicitud; verificar cuenta única, saldo inicial y estados.
4. Probar depósito con cuenta activa, rechazar cuenta inexistente/inactiva y revisar historial.
5. Confirmar/cancelar/completar citas según sus estados.
6. Tomar y resolver/cerrar solicitudes de atención.
7. Repetir una operación con un cliente y confirmar que no obtiene acciones de empleado.

Guía completa de clases, formularios, base de datos, JDBC, ejecución y pendientes: [GUIA_TECNICA.md](GUIA_TECNICA.md).

Detalle de pantallas del empleado: [implementacion-panel-empleado.md](etapa-6/implementacion-panel-empleado.md).
La ficha local de seguimiento está en [etapa-06-interfaz-swing.md](etapas/etapa-06-interfaz-swing.md).


