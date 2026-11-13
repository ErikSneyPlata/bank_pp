# Etapa 2 — Análisis y requerimientos

**Estado: Parcial.**

## Definido

- Notion registra Bank PP, integrantes, docente, banco ficticio y roles cliente/empleado.
- Alcance de escritorio Java Swing, PostgreSQL/JDBC: autenticación, cuentas, saldos, retiros, transferencias, historial, citas y solicitudes; también administración de empleados en los requisitos iniciales.
- Se excluyen bancos reales, transferencias externas, préstamos, tarjetas, pagos de servicios y aplicación móvil.

## Ajustes y discrepancias

- El equipo pidió mantener POO al nivel trabajado en clase y no declarar Java `interface`.
- El equipo pidió retirar el hasheo de contraseña.
- Los requisitos originales aún mencionan interface Java obligatoria y hash seguro; no hay evidencia de que el docente haya aprobado cambiar esos criterios.
- La interfaz actual no implementa todavía las funciones administrativas del empleado.

**Pendiente:** acordar con docente/equipo permisos, criterios de aceptación, reglas de negocio detalladas y prioridad de los RF; confirmar las discrepancias de criterios.
