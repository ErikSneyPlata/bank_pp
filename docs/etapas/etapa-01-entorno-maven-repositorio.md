# Etapa 1 — Entorno, Maven y repositorio

**Estado: Parcial.**

## Realizado y comprobado

- `pom.xml` raíz configura Java 25, PostgreSQL JDBC y Maven Shade con `com.bankpp.Main` como entrada del JAR.
- La estructura `src/main/java/com/bankpp` contiene configuración, formularios, modelo, repositorios y servicios.
- La compilación con JDK 25.0.2 y `javac --release 25` terminó correctamente.
- Existen `.gitignore`, directorio `.mvn` y `database/pom.xml` para migraciones Flyway.

## Pendiente/no comprobado

- Maven no estaba disponible como comando en la revisión; no se ejecutaron `mvn package` ni Flyway. No se encontró `mvnw` o `mvnw.cmd`.
- No se confirmó remoto GitHub, estrategia de ramas ni primer commit; `git status` no sirvió para verificar este checkout.
- No hay `README.md` en la raíz.
- PostgreSQL está instalado según la bitácora previa y el puerto local responde; falta comprobar autenticación y conexión a `bankpp`.

**Para cerrar:** validar Maven, remoto/colaboración y conexión al servicio; agregar instrucciones raíz de instalación y ejecución.
