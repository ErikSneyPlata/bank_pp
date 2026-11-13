# Conexión JDBC local

La aplicación usa PostgreSQL local y `DriverManager` con pgJDBC. La migración común define las seis tablas académicas.

Variables del entorno de ejecución:

- `BANKPP_JDBC_URL` (opcional; predeterminado `jdbc:postgresql://localhost:5432/bankpp`)
- `BANKPP_DB_USER` (opcional; predeterminado `postgres`)
- `BANKPP_DB_PASSWORD` (requerida)

No se almacenan las credenciales de conexión en Java. Aplica la migración con la configuración local de Flyway en `database/pom.xml`. El registro de clientes crea usuario y datos de contacto dentro de una transacción SQL. Los retiros y transferencias actualizan saldos e historial de forma atómica.
