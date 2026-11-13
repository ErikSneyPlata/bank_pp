# Etapa 6 — Interfaz gráfica Swing

**Estado:** vistas y acciones del empleado implementadas; falta recorrido funcional manual en Swing.

## Acceso de empleado

`LoginForm` conserva el enrutamiento por `RolUsuario`. Para `EMPLEADO` abre `EmpleadoFrame`; para `CLIENTE` conserva `PanelClienteForm`. El marco del empleado se definió en `EmpleadoFrame.form` para IntelliJ UI Designer y crea cinco pestañas Swing con paneles especializados.

| Sección | Acciones implementadas | Persistencia / regla |
| --- | --- | --- |
| Clientes | Buscar/listar, registrar, editar, desactivar | `ClienteService`/`ClienteRepositoryJdbc`; el registro crea solicitud de apertura pendiente. |
| Cuentas | Listar, bloquear, cerrar; consultar aperturas pendientes, aprobar/rechazar | Cierre solo si saldo cero. La aprobación existente crea cuenta de forma transaccional. |
| Transacciones | Consultar todas, filtrar por número de cuenta, registrar depósito | Depósito solo a cuenta activa; saldo e historial se actualizan en una transacción SQL. |
| Citas | Listar, confirmar, cancelar, completar | Solo transiciones permitidas; el empleado queda asignado al gestionar. |
| Atención | Listar, tomar, pasar a en proceso, resolver o cerrar | Al tomar/cambiar estado, se registra el empleado asignado. |

Las clases de panel Swing construyen sus controles con Java Swing, igual que el fallback usado por los formularios existentes. La ventana raíz conserva archivo IntelliJ UI Designer `.form`; los diagramas StarUML describen los controles y acciones de cada sección. Los paneles no están todavía guardados cada uno como archivo `.form` individual.

## Arquitectura de las acciones

Flujo seguido:

`EmpleadoFrame / panel Swing → service → repository JDBC → PostgreSQL`

No se ejecuta SQL desde las vistas. Los servicios rechazan roles distintos de `EMPLEADO` activo. Las conexiones JDBC en repositorios se abren con `try-with-resources`; las operaciones que requieren atomicidad desactivan autocommit y hacen `commit` o `rollback` antes de cerrar conexión y sentencias.

## Base de datos

Se añadió `V3__depositos_empleado.sql`: amplía los `CHECK` existentes para aceptar `DEPOSITO`, y mantiene el destino nulo para depósitos y retiros. Se aplicó con Flyway Maven integrado en IntelliJ; el historial local quedó en versión 3. No se alteraron ni eliminaron filas de clientes, cuentas o movimientos.

## Comprobaciones realizadas

- `javac --release 25` sobre todos los `.java`: terminó con código 0.
- PostgreSQL local respondió para `bankpp` en PostgreSQL 18.3.
- Flyway validó cuatro migraciones y aplicó V3; historial actual en versión 3.
- No se pudo ejecutar `mvn` como comando estándar porque no está en `PATH`; se usó el ejecutable Maven incluido con IntelliJ.
- No se ejecutó un recorrido manual en la GUI ni se crearon datos financieros de prueba. Por eso las operaciones quedan “implementadas, prueba manual pendiente”.

## Pendientes

- Ejecutar desde IntelliJ y revisar visualmente el marco y los formularios.
- Recorrer con una cuenta de empleado los flujos de cada pestaña.
- Probar depósitos con datos de prueba acordados y verificar el historial.
- Validar rechazo de operaciones cuando no corresponda rol o estado.
- Continuar la etapa 7 con pruebas, control de errores y evidencia.

Consulta el detalle de reglas por operación en [implementacion-panel-empleado.md](../etapa-6/implementacion-panel-empleado.md).

## Guía integral — 2026-11-12

La referencia actualizada para entender arranque, navegación, formularios, clases, servicios, JDBC, esquema y migraciones es [GUIA_TECNICA.md](../GUIA_TECNICA.md). El JAR Maven fue empaquetado con Java 25; queda pendiente validar los flujos manualmente en la interfaz.
