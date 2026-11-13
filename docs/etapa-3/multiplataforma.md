# Distribución multiplataforma — nota de diseño

La aplicación Swing/JDBC puede compilarse como JAR con Java 25 y dependencias incluidas. Para abrirla necesita conectividad al PostgreSQL local indicado por las variables del entorno. Un JAR Java no es un `.exe` o instalador nativo; cada paquete nativo se construye en su sistema operativo con `jpackage` cuando corresponda.

La construcción del JAR solo se valida cuando el código fuente de UI Designer esté generado desde IntelliJ y Maven esté disponible. No se afirma que el artefacto esté probado hasta completar esa comprobación.
