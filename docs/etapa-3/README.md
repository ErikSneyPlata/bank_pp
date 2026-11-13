# Bank PP — Etapa 3 adaptada a la asignatura

## Objetivo

Implementar el alcance académico usando POO básica (clases, encapsulamiento, herencia, abstracción, composición/agregación), formularios diseñados en IntelliJ UI Designer y JDBC para PostgreSQL local.

## Entregables

- `uml/diagrama-clases.puml`: modelo de objetos con atributos, constructores, métodos y relaciones.
- `uml/diagrama-formularios.puml`: formularios Swing, controles y acciones.
- `staruml/BankPP-diagrama-clases.mdj` y `staruml/BankPP-diagramas-formularios.mdj`: archivos editables de StarUML basados en las referencias adjuntas.
- `formularios.md`: clases de formulario, campos, etiquetas y acciones.
- `modelo-de-datos.md`: clases y seis tablas relacionadas.
- `configuracion-jdbc.md`: conexión local y variables de entorno.

## Decisiones de clase

- Se conserva Java 25, Maven, Swing y PostgreSQL/JDBC.
- Las acciones bancarias se representan mediante la clase abstracta `Transaccion` y sus subclases `Retiro` y `Transferencia`; no se usa la palabra reservada `interface` en el código del proyecto.
- Login, registro y panel del cliente se implementan como formularios enlazados a archivos `.form` de UI Designer. Las acciones de botones permanecen en sus clases Java y llaman servicios/repositorios.
- La contraseña se almacena en texto simple para el ejercicio académico; usar únicamente datos de prueba.
- No se implementan adaptadores Supabase ni otra persistencia en esta etapa. La migración local permanece en PostgreSQL.

## Estado de validación

Los formularios deben compilarse desde IntelliJ IDEA con **Settings → Editor → GUI Designer → Generate GUI into: Java source code**. Maven no procesa por sí solo los archivos `.form`; tras generar el código desde IntelliJ, el `mvn package` puede compilar y empaquetar las dependencias. La disponibilidad del servidor PostgreSQL determina las pruebas de integración.
