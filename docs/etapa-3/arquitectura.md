# Arquitectura sencilla del proyecto

## Flujo

`Formulario UI Designer → Servicio Java → clase JDBC concreta → PostgreSQL local`.

La interfaz no contiene consultas SQL. El servicio valida la operación académica y el repositorio concreto JDBC lee o modifica la base de datos. No hay adaptadores alternativos ni interfaces Java de repositorio.

## Paquetes

- `com.bankpp.formulario`: formularios Swing ligados a `.form`.
- `com.bankpp.service`: validaciones y coordinación de acciones.
- `com.bankpp.repository.impl`: clases concretas JDBC.
- `com.bankpp.model`: objetos de dominio.
- `com.bankpp.model.enums`: roles y estados.
- `com.bankpp.config`: conexión JDBC.

## Formularios

`LoginForm` permite iniciar sesión o abrir el registro. `RegistroClienteForm` registra al cliente. `PanelClienteForm` organiza pestañas de cuentas, operaciones, historial, citas y solicitudes, cuyos componentes también se diseñan con UI Designer.

## Construcción

IntelliJ debe generar la interfaz en código fuente y compilar los formularios. JetBrains advierte que Maven no instrumenta `.form` automáticamente; el proyecto incorpora el runtime de UI Designer y el JAR empaqueta dependencias. Instrucciones: `formularios.md`.
