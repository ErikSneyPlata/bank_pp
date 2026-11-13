# Guía técnica integral — Bank PP

**Actualizada:** 12 de noviembre de 2026  
**Etapa activa:** 6 — interfaz Swing (integrada en código; recorrido manual y pruebas funcionales pendientes).

Esta guía explica cómo está organizado Bank PP, cómo se conectan las pantallas con PostgreSQL, cómo se crean y actualizan las tablas y qué está comprobado. La fuente de verdad para el esquema está en `database/migrations/common`; la fuente de verdad para el comportamiento está en `src/main/java`.

## 1. Tecnologías y alcance

- Java 25 y Maven.
- Java Swing para la interfaz de escritorio; formularios `.form` de IntelliJ UI Designer cuando existen, y composición de componentes Swing desde Java para los paneles internos.
- JDBC con el controlador PostgreSQL `org.postgresql:postgresql:42.7.14`.
- PostgreSQL local, base `bankpp`.
- Flyway Maven para aplicar cambios versionados del esquema. No se requiere npm.

El alcance es académico: login y registro de clientes, consulta de cuentas, retiros, transferencias, depósitos por empleado, historial, solicitudes de apertura, citas y solicitudes de atención. No se implementaron productos bancarios comerciales, Spring Boot, ORM, API remota ni conexión activa a Supabase. La contraseña se mantiene simple por la decisión académica indicada; no hay hashing.

## 2. Cómo arranca la aplicación

1. Maven compila las clases desde `src/main/java` con nivel de lenguaje 25, definido en `pom.xml`.
2. `com.bankpp.Main` programa la creación de `LoginForm` en el hilo de eventos de Swing con `SwingUtilities.invokeLater`.
3. `LoginForm` crea los servicios y repositorios JDBC que usa la aplicación.
4. `AutenticacionService` valida campos básicos y delega la autenticación a `AuthRepositoryJdbc`.
5. Si las credenciales corresponden a un usuario activo, la vista decide por `RolUsuario`: cliente abre `PanelClienteForm`; empleado abre `EmpleadoFrame`.
6. La ventana principal incorpora paneles Swing que llaman a los servicios. Las vistas no ejecutan sentencias SQL.

El cierre de sesión cierra la ventana de rol actual y vuelve al login. Las conexiones JDBC se abren al ejecutar una operación y se liberan al terminar; no se mantienen conexiones globales dentro de la interfaz.

## 3. Clases y POO

Los paquetes activos principales son `model`, `model.enums`, `service`, `repository.impl`, `config` y `formulario`.

### Modelo de dominio (`model`)

- `Usuario` es abstracta y agrupa id, documento, nombres, apellidos, correo, rol y estado activo.
- `Cliente` y `Empleado` heredan de `Usuario`. `Cliente` contiene un `DatosContacto`; en Java esto se modela como composición porque el contacto forma parte del objeto cliente.
- `CuentaBancaria` representa número, propietario, saldo, moneda y estado.
- `Transaccion` es abstracta y concentra los campos comunes de una operación. `Retiro`, `Transferencia` y `Deposito` heredan de ella y aportan su tipo y validación concreta. El repositorio usa polimorfismo para reconstruir el subtipo según el tipo guardado.
- `Cita`, `SolicitudAtencion` y `SolicitudCuenta` representan los tres procesos de atención y apertura.
- `Banco` mantiene una colección de empleados como agregación. La persistencia actual no tiene una tabla `bancos`; esa relación se expresa en el modelo académico, no en el esquema SQL vigente.
- Las enumeraciones de `model.enums` restringen valores válidos como rol, tipo de transacción y estados.

Los datos del dominio validan campos obligatorios y montos positivos. La lógica que depende de los datos actuales (propiedad, saldo, estado de cuenta y transición permitida) se vuelve a comprobar en servicios/repositorios antes de persistir.

Por el acuerdo de la asignatura, no se declaran tipos Java `interface`. Los contratos de persistencia se implementan aquí como clases JDBC concretas, por ejemplo `AuthRepositoryJdbc`, `CuentaRepositoryJdbc`, `TransaccionRepositoryJdbc`, `AtencionRepositoryJdbc` y `SolicitudCuentaRepositoryJdbc`.

### Servicios (`service`)

Los servicios conectan la pantalla con las operaciones de negocio:

- `AutenticacionService`: login y alta de cliente.
- `ClienteService`: listar, editar y desactivar clientes; exige empleado activo en acciones de empleado.
- `CuentaService`: consultar cuentas/saldos del cliente y listar/cambiar estado de cuentas para el empleado.
- `SolicitudCuentaService`: crear, listar, aprobar y rechazar solicitudes de apertura.
- `TransaccionService`: validar montos, consultar historial y coordinar retiro, transferencia y depósito; las operaciones de empleado comprueban rol y estado activo.
- `AtencionService`: solicitudes y cambios de estado de citas y solicitudes de atención.

La secuencia general es:

`Formulario Swing → Service → Repository JDBC → DatabaseConfig → PostgreSQL`

### Persistencia (`repository.impl`)

Cada repositorio contiene SQL parametrizado con `PreparedStatement`. Los resultados se convierten a objetos de dominio y regresan al servicio. Los formularios reciben esos objetos y llenan tablas o muestran mensajes.

No se usa concatenación de valores de usuario dentro de SQL. Para listar movimientos globales se admite un filtro opcional de número de cuenta.

## 4. Formularios Swing y UI Designer

La carpeta `formulario` concentra los JFrame/paneles. Los JFrame son ventanas principales; los paneles con `JPanel` se insertan como pestañas o secciones.

### Cliente

`LoginForm` → `PanelClienteForm` → `CuentasForm`, `OperacionesForm`, `HistorialForm`, `CitasForm` y `SolicitudesForm`. `RegistroClienteForm` permite crear cliente. El registro no abre automáticamente una cuenta: crea una solicitud pendiente que debe aprobar un empleado.

### Empleado

`LoginForm` → `EmpleadoFrame` → `ClientesEmpleadoForm`, `CuentasEmpleadoForm`, `TransaccionesEmpleadoForm`, `CitasEmpleadoForm` y `AtencionEmpleadoForm`.

- Clientes: buscar/listar, registrar, editar y desactivar.
- Cuentas: listar, bloquear/cerrar y decidir solicitudes de apertura. Cerrar exige saldo cero.
- Transacciones: consultar todo el historial, filtrar por cuenta y registrar depósitos.
- Citas: listar, confirmar, cancelar y completar conforme a estados válidos.
- Atención: listar, tomar/asignar, poner en proceso, resolver y cerrar solicitudes.

`EmpleadoFrame.form` define la estructura raíz que IntelliJ UI Designer puede enlazar. Los cinco paneles internos del empleado crean sus controles Swing en Java; por eso no hay un `.form` individual para cada panel. Los formularios existentes del cliente conservan sus `.form` y una inicialización alternativa donde se necesita evitar campos nulos si el diseñador no genera sus bindings. No se usa el runtime de UI Designer para construir estas pantallas en producción.

## 5. Base de datos PostgreSQL

El esquema se instala por migraciones SQL secuenciales:

### V1 — esquema inicial

Crea `usuarios`, `datos_contacto`, `cuentas`, `transacciones`, `citas` y `solicitudes_atencion`. Usa UUID como claves, claves foráneas para relaciones, índices para consultas frecuentes y restricciones `CHECK` para roles, saldos, moneda, estados, tipo y montos.

### V2 — solicitudes de apertura

Crea secuencia para numeración de cuenta, asegura una sola cuenta por usuario y crea `solicitudes_cuenta`. La apertura se hace por aprobación del empleado. Los estados `PENDIENTE`, `APROBADA` y `RECHAZADA` se restringen; la relación con la cuenta se completa únicamente al aprobar.

### V3 — depósitos

Amplía las restricciones de `transacciones` para aceptar `DEPOSITO`. Depósitos y retiros guardan la cuenta afectada en `cuenta_origen_id` y dejan `cuenta_destino_id` vacío; transferencia requiere origen y destino distintos.

Flyway registra lo aplicado en `flyway_schema_history`, comprueba versiones y permite que otra instalación reproduzca el mismo esquema. La base local `bankpp` está en V3. Las migraciones comunes están preparadas para PostgreSQL; Supabase remoto todavía no está conectado ni probado desde la aplicación.

## 6. Conexión Java ↔ PostgreSQL (JDBC)

`DatabaseConfig.getConnection()` es el único punto que entrega conexiones. Lee:

- `BANKPP_JDBC_URL` (opcional; por defecto `jdbc:postgresql://localhost:5432/bankpp`).
- `BANKPP_DB_USER` (si no se configura, valor por defecto `postgres`).
- `BANKPP_DB_PASSWORD` (obligatoria; no existe contraseña predeterminada).

La aplicación busca las variables del entorno primero y después el archivo local `.env` ubicado en el directorio de ejecución. `.env` está excluido de Git; `.env.example` sirve de guía y no lleva secretos. No copies credenciales a las clases, al POM ni a esta documentación. Flyway Maven no carga `.env` automáticamente: sus propiedades de conexión deben configurarse en el entorno local o pasarse al plugin sin guardar secretos en el repositorio.

Patrón de consulta:

1. Obtener `Connection` desde `DatabaseConfig`.
2. Crear `PreparedStatement` con placeholders `?`.
3. Asignar valores usando `setString`, `setObject`, `setBigDecimal` u otro setter.
4. Leer `ResultSet` y mapear filas.
5. Cerrar `ResultSet`, sentencia y conexión mediante `try-with-resources`.

`try-with-resources` ejecuta el cierre aun si ocurre una excepción. Para una operación con varias escrituras se usa `setAutoCommit(false)`, se hacen todos los cambios, `commit()` al final y `rollback()` si algo falla; al salir, la conexión también se cierra.

### Atomicidad de las operaciones

- Registro de cliente: inserta usuario, contacto y solicitud de cuenta en una única transacción.
- Aprobación de solicitud: resuelve solicitud y crea la cuenta dentro de una transacción.
- Retiro: bloquea la cuenta con `SELECT ... FOR UPDATE`, revisa propietario, estado y saldo; descuenta e inserta historial en la misma transacción.
- Transferencia: bloquea las cuentas, verifica ambas activas y saldo suficiente, debita/acredita e inserta movimiento antes de `commit`; ante error ejecuta `rollback`.
- Depósito: el empleado debe estar activo, el monto debe ser positivo, la cuenta debe existir y estar activa; bloquea la fila, acredita saldo e inserta el movimiento dentro de la misma transacción.
- Edición de cliente: cambios de usuario y contacto se agrupan para no dejar datos parcialmente actualizados.

El saldo no puede ser negativo por validación de negocio y por la restricción SQL de `cuentas`.

## 7. Aplicar migraciones y ejecutar

1. Instalar/iniciar PostgreSQL y crear la base `bankpp`.
2. Configurar usuario, clave y URL local sin publicar `.env`.
3. Desde la raíz del proyecto inspeccionar migraciones con `mvn -f database/pom.xml flyway:info`.
4. Aplicarlas con `mvn -f database/pom.xml flyway:migrate`. Si `mvn` no está en PATH, usar el Maven configurado dentro de IntelliJ IDEA.
5. Abrir el proyecto Maven en IntelliJ y ejecutar `com.bankpp.Main`; también se puede compilar/empaquetar con `mvn package` usando Java 25.
6. Para login se necesita un usuario activo. El primer empleado se provisiona de forma controlada; no hay registro público de empleados.

No uses `flyway:clean` en una base con información que quieras conservar. La migración no crea usuarios de demostración.

## 8. Qué está comprobado y qué falta

**Comprobado en esta intervención:** compilación Java 25 y empaquetado Maven con pruebas omitidas; JAR contiene la clase principal, panel de empleado, `Deposito` y el controlador JDBC; PostgreSQL local `bankpp` quedó en Flyway V3. No se incluyeron credenciales ni se insertaron movimientos de prueba.

**Pendiente:** abrir y recorrer visualmente todos los formularios, probar login con un empleado activo, alta/edición/desactivación, aprobación/rechazo de cuenta, depósitos, retiros, transferencias, saldo insuficiente, estados inválidos, citas y atención con registros de prueba. También están pendientes configuración y pruebas reales de Supabase, empaquetado/ejecución final por sistema operativo y entregables de sustentación.

La compilación demuestra que el código compila; el empaquetado demuestra que se generó el archivo; ninguna de las dos cosas demuestra por sí sola que los flujos GUI y de negocio funcionen de extremo a extremo.

## 9. Archivos para profundizar

- Código Java: `src/main/java/com/bankpp/`.
- Configuración JDBC: `src/main/java/com/bankpp/config/DatabaseConfig.java`.
- Migraciones: `database/migrations/common/`.
- Configuración Flyway: `database/pom.xml`.
- Diagrama de clases y formularios StarUML: `docs/etapa-3/staruml/`.
- Estado general: `docs/ESTADO_ACTUAL.md`.
- Implementación del empleado: `docs/etapa-6/implementacion-panel-empleado.md`.

