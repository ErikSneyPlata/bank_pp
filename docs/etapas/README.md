# Bank PP — Bitácora de las nueve etapas

**Corte:** 2026-11-12  
**Etapa activa:** Etapa 6 — Interfaz gráfica Swing (en desarrollo).

La etapa activa indica dónde se trabaja ahora; no implica que las anteriores estén cerradas. Estas fichas separan lo presente en archivos, lo comprobado y lo pendiente.

| Etapa | Tema | Estado | Ficha |
| --- | --- | --- | --- |
| 1 | Entorno, Maven y repositorio | Parcial | [Etapa 1](etapa-01-entorno-maven-repositorio.md) |
| 2 | Análisis y requerimientos | Parcial | [Etapa 2](etapa-02-analisis-requerimientos.md) |
| 3 | UML, arquitectura y diseño de datos | Parcial | [Etapa 3](etapa-03-uml-arquitectura-datos.md) |
| 4 | Base de datos y persistencia | En desarrollo; migración V2 aplicada | [Etapa 4](etapa-04-base-datos-persistencia.md) |
| 5 | Modelo POO y lógica de negocio | Parcial; solicitud/aprobación implementada, recorrido manual pendiente | [Etapa 5](etapa-05-modelo-poo-logica.md) |
| 6 | Interfaz gráfica Swing | En desarrollo; panel empleado implementado | [Etapa 6](etapa-06-interfaz-swing.md) |
| 7 | Integración, pruebas y calidad | Parcial; compilación/migración comprobadas, flujo Swing pendiente | [Etapa 7](etapa-07-integracion-pruebas.md) |
| 8 | Informe y documentación PDF | Documentación técnica parcial; PDF pendiente | [Etapa 8](etapa-08-informe-documentacion.md) |
| 9 | Entregables y sustentación | Pendiente | [Etapa 9](etapa-09-entregables-sustentacion.md) |

## Evidencias comunes al corte

- Maven compiló los 39 archivos Java con Java 25.
- `com.bankpp.Main` permaneció activo durante una comprobación de arranque de cuatro segundos sin repetir `contentPane cannot be set to null`.
- Ocho `.form` parsean como XML y sus campos enlazados existen en las clases Java.
- PostgreSQL local `bankpp` respondió por JDBC y Flyway aplicó V2 sobre V1 baseline.
- Antes de V2 había 1 cliente, 1 empleado y 0 cuentas; la migración no creó datos de prueba.
- No se encontró `src/test` ni artefacto `.jar`, `.exe`, `.pdf` o `.zip` en las rutas inspeccionadas.

## Decisiones y diferencias a resolver

- Tecnologías de trabajo: Java 25, Swing/UI Designer y JDBC.
- El equipo pidió no usar declaraciones Java `interface` y retirar el hasheo de contraseñas para ajustarse al curso.
- Las páginas originales de requisitos de Notion aún piden una interface y prohíben texto plano. Confirmar esa discrepancia con el docente antes de declarar cumplimiento global.

La documentación técnica existente está en [Etapa 3](../etapa-3/README.md), la corrección Swing en [Etapa 6](../etapa-6/correccion-inicializacion-formularios.md) y la vista ejecutiva en [Estado actual](../ESTADO_ACTUAL.md).

La revisión elemento por elemento de los checklists de Notion está en [Auditoría de checklists](auditoria-checklists.md). La auditoría diferencia entre documentado, implementado, comprobado, parcial, pendiente y conflicto; no reemplaza la aprobación del docente.
