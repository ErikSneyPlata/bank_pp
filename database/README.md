# Base de datos Bank PP

Migraciones versionadas con Flyway y Maven; no se requiere npm. La migración común define el esquema inicial para PostgreSQL local y Supabase. Data API no se habilita hasta implementar y revisar RLS, grants y RPC.

## Estructura
~~~text
database/
  pom.xml
  migrations/common/V1__initial_schema.sql
  migrations/supabase/README.md
  seeds/local/README.md
~~~

## PostgreSQL local
1. Inicia PostgreSQL y crea la base bankpp (nombre usado por DatabaseConfig).
2. La aplicación Swing lee `BANKPP_JDBC_URL`, `BANKPP_DB_USER` y `BANKPP_DB_PASSWORD` del entorno; si faltan, usa `.env` en el directorio de trabajo. No subas `.env`.
3. Flyway Maven requiere sus propias propiedades/variables (`flyway.url`, `flyway.user`, `flyway.password`); el plugin no lee `.env` automáticamente.
4. Desde la raíz revisa: `mvn -f database/pom.xml flyway:info`.
5. Tras revisar la migración, aplica: `mvn -f database/pom.xml flyway:migrate`.

V1 define el esquema base. V2 agrega solicitudes de apertura y numeración de cuenta. V3 permite registrar depósitos y amplía las restricciones del historial. El depósito actualiza el saldo y registra el movimiento en una sola transacción JDBC. La base local `bankpp` llegó a V3 mediante Flyway; no se insertaron datos de prueba.

## Supabase
La conexión JDBC se usa solo desde una máquina confiable para aplicar migraciones. No va en el escritorio.

Cuando existan migraciones de seguridad Supabase, consulta:
mvn -f database/pom.xml flyway:info "-Dflyway.locations=filesystem:database/migrations/common,filesystem:database/migrations/supabase"

Y aplica solo tras revisar scripts y credenciales:
mvn -f database/pom.xml flyway:migrate "-Dflyway.locations=filesystem:database/migrations/common,filesystem:database/migrations/supabase"

No usar flyway:clean en una base con datos que deban conservarse. No se ejecuta migración en esta entrega.

## Guía de funcionamiento

Para comprender las tablas, el uso de Flyway, la conexión desde Java con JDBC, el manejo y cierre de conexiones y las transacciones, consulta [docs/GUIA_TECNICA.md](../docs/GUIA_TECNICA.md).
