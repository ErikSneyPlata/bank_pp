# Formularios de IntelliJ UI Designer

Cada formulario tiene una clase Java enlazada a un archivo `.form`. En el diseñador se organizan etiquetas, campos, tablas, combos y botones; los listeners y acciones se escriben en el archivo Java asociado.

| Formulario | Componentes (campos y etiquetas) | Acciones |
|---|---|---|
| `LoginForm` | Correo, contraseña, mensaje, botones Iniciar y Crear cliente | `iniciarSesion`, abrir registro |
| `RegistroClienteForm` | Documento, nombres, apellidos, correo, contraseña, teléfono, dirección | `registrarCliente`, volver |
| `PanelClienteForm` | Bienvenida, pestañas, botón cerrar sesión | Navegar pestañas, cerrar sesión |
| `CuentasForm` | Tabla de cuentas, actualizar | `cargarCuentas` |
| `OperacionesForm` | Cuenta origen, número destino, monto, Retirar, Transferir | `retirar`, `transferir` |
| `HistorialForm` | Cuenta, tabla de movimientos, actualizar | `cargarMovimientos` |
| `CitasForm` | Fecha/hora, motivo, tabla, solicitar/cancelar/actualizar | crear, listar y cancelar cita |
| `SolicitudesForm` | Descripción, tabla, enviar/actualizar | crear y listar solicitud |

## Configuración de IntelliJ y Maven

1. Habilitar Swing UI Designer.
2. Ir a **Settings → Editor → GUI Designer**.
3. Seleccionar **Generate GUI into: Java source code**.
4. Abrir y compilar el proyecto desde IntelliJ para generar el inicializador Java de los `.form`.
5. No delegar la primera generación de formularios a Maven; el compilador externo no procesa los `.form` automáticamente.
6. Después de la generación, ejecutar `mvn package`; el JAR incorpora pgJDBC y el runtime de UI Designer.

Las etiquetas, controles y nombres de acción también están dibujados en `uml/diagrama-formularios.puml` y en el archivo StarUML de formularios.

## Formularios de empleado incorporados

| Formulario / panel | Objetos principales | Acciones |
|---|---|---|
| `EmpleadoFrame` | Bienvenida, `JTabbedPane`, cerrar sesión | Abrir Clientes, Cuentas, Transacciones, Citas y Atención |
| `ClientesEmpleadoForm` | búsqueda, tabla, alta/edición/desactivación | buscar, registrar, actualizar, desactivar |
| `CuentasEmpleadoForm` | tablas de cuentas y solicitudes de apertura | listar, bloquear, cerrar, aprobar, rechazar |
| `TransaccionesEmpleadoForm` | filtro, campos de depósito, tabla de movimientos | filtrar, consultar, depositar |
| `CitasEmpleadoForm` | tabla de citas y botones de estado | consultar, confirmar, cancelar, completar |
| `AtencionEmpleadoForm` | tabla, selector de estado | listar, tomar, pasar a proceso, resolver, cerrar |

Los paneles se construyen con Java Swing dentro de sus clases; `EmpleadoFrame` tiene su `.form` de IntelliJ y el resto se agrega como pestañas Swing. Todos delegan persistencia a servicios/repositorios JDBC.