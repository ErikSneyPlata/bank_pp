# Etapa 6 — Corrección de inicialización de formularios

**Fecha:** 2026-11-11  
**Estado:** Corrección implementada; etapa 6 continúa en desarrollo.

## Síntoma

Al ejecutar `com.bankpp.Main`, Swing lanzó:

```text
java.awt.IllegalComponentStateException: contentPane cannot be set to null
    at javax.swing.JFrame.setContentPane(...)
    at com.bankpp.formulario.LoginForm.<init>(...)
```

La línea `setContentPane(panelPrincipal)` recibía `null`. El campo `panelPrincipal` y los demás controles dependen de la inicialización del archivo `.form` enlazado. En la configuración de ejecución usada, el código generado por UI Designer no estaba presente, por lo que los componentes seguían nulos.

## Corrección

En los ocho formularios (`LoginForm`, `RegistroClienteForm`, `PanelClienteForm`, `CuentasForm`, `OperacionesForm`, `HistorialForm`, `CitasForm` y `SolicitudesForm`) se añadió una inicialización alternativa:

1. El constructor verifica si `panelPrincipal` está inicializado.
2. Si sigue nulo, llama a `crearInterfaz()` para crear el panel raíz y los componentes Swing que usa la clase.
3. La clase conecta sus listeners y acciones a esos componentes.
4. Si IntelliJ ya generó el formulario, se conserva el panel generado y no se crea una segunda interfaz.

También se sustituyeron los tipos `javax.swing.undefined` de varios `.form` por sus clases concretas (`JTable`, `JComboBox` y `JTabbedPane`), de modo que los enlaces corresponden a los campos declarados en Java.

## Archivos afectados

- Clases Java y archivos `.form` dentro de `src/main/java/com/bankpp/formulario/`.
- El detalle del arranque de la aplicación continúa en `src/main/java/com/bankpp/Main.java`; no fue necesario cambiarlo.

## Comprobaciones realizadas

- Compilación: `javac --release 25` terminó con código de salida `0`.
- Arranque: `com.bankpp.Main` se ejecutó durante la comprobación durante cuatro segundos y el proceso permaneció activo sin producir la excepción original.
- Formularios: los ocho archivos `.form` se analizaron como XML válido; cada enlace tiene un campo Java correspondiente.

Estas comprobaciones no equivalen a una prueba funcional completa: no se hizo clic en las pantallas ni se ejecutaron operaciones contra PostgreSQL.

## Pendientes de la etapa

- PostgreSQL local no respondió en `127.0.0.1:5432`; iniciar el servicio y comprobar JDBC.
- Abrir cada formulario en IntelliJ UI Designer y revisar su presentación visual.
- Probar registro, login y operaciones con datos de prueba.
- Completar y validar las pantallas del empleado y sus acciones según el alcance acordado.
- Conservar captura o evidencia de cada flujo probado.

## Decisiones de alcance relacionadas

- El código se ajusta a los temas indicados para la asignatura; no utiliza declaraciones Java `interface`.
- Se retiró el hasheo de contraseña a petición del equipo y se mantiene `contrasena` en texto simple para este ejercicio académico.
- En el plan original de Notion todavía aparece como criterio una interfaz Java y una regla de no almacenar contraseñas en texto plano. Esos puntos no se consideran resueltos por esta corrección; deben confirmarse con el docente/equipo antes de cerrar la aceptación global.
