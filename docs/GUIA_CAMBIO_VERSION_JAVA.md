# Guía independiente: cambiar la versión de Java en Bank PP

**Propósito:** explicar cómo adaptar manualmente el proyecto para compilar y ejecutarse con Java 17 o Java 21.  
**Alcance:** solo instrucciones. Esta guía no cambia el código ni la configuración actual del proyecto.  
**Configuración actual observada:** el archivo pom.xml fija Java 25 en dos lugares. El proyecto debe verificarse antes de cambiar el nivel y generar un nuevo JAR.

## Recomendación

Si los equipos de la universidad pueden tener Java 17 o Java 21, conviene acordar con el docente la versión mínima y fijar el proyecto a esa versión. Si hay computadores con Java 17, la opción de mayor compatibilidad es **compilar con destino Java 17**. Ese JAR puede ejecutarse en Java 17 y versiones posteriores, sujeto a que el código y las bibliotecas también sean compatibles.

Un JAR compilado para Java 21 necesita Java 21 o posterior; no se ejecuta en Java 17. Uno compilado para Java 25 requiere Java 25 o posterior. Java no significa “funciona en cualquier versión”: el JAR debe tener una versión de bytecode que la JVM de destino pueda leer.

La dependencia PostgreSQL JDBC actual, versión 42.7.14, requiere Java 8 o posterior según pgJDBC; por ese requisito, Java 17 y 21 son suficientemente nuevos. Aun así, la compatibilidad de toda la aplicación se debe comprobar con una compilación y ejecución reales.

## Paso 1 — Revisar qué Java está instalado

En Windows, abrir PowerShell o CMD:

~~~powershell
java -version
javac -version
mvn -version
~~~

- java -version muestra la JVM usada para ejecutar.
- javac -version muestra el compilador disponible en el PATH.
- mvn -version informa con qué Java está ejecutándose Maven. Este puede ser distinto del Project SDK de IntelliJ.

En macOS o Linux, los mismos tres comandos funcionan en Terminal. Si java o mvn no se reconocen, el JDK/Maven no está instalado o no está configurado en el PATH. En IntelliJ se puede elegir un JDK ya instalado sin convertirlo en el Java global del equipo.

No es necesario cambiar el Java con el que corre IntelliJ IDEA. La JVM interna de IDEA y el JDK del proyecto son ajustes distintos.

## Paso 2 — Elegir el nivel objetivo

| Objetivo de compilación | Java mínimo para ejecutar el JAR |
| --- | --- |
| 17 | 17 |
| 21 | 21 |
| 25 (configuración actual) | 25 |

Si el docente confirma que todos usan Java 21 o posterior, puede elegirse 21. Si algunos equipos usan 17, elegir 17 evita exigir que instalen Java 21. Cuando la versión institucional no se conoce, se debe preguntar al docente antes de seleccionar el objetivo de entrega.

## Paso 3 — Ajustar el POM manualmente

El pom.xml actual contiene el número 25 en la propiedad Maven y en la configuración del compilador. Para Java 17, el resultado debe quedar así:

~~~xml
<properties>
    <maven.compiler.release>17</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
~~~

Y la configuración del plugin debe usar también 17:

~~~xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.14.1</version>
    <configuration>
        <release>17</release>
    </configuration>
</plugin>
~~~

Para Java 21, reemplaza ambos 17 por 21. No dejes un valor en 17 y el otro en 21: el POM de este repositorio define la versión en ambos lugares y conviene mantenerlos iguales.

La opción Maven release compila con las reglas del lenguaje y las APIs públicas disponibles para esa versión, no solo escribe bytecode viejo. Por eso es más útil para esta adaptación que cambiar únicamente source o target.

**Esto es una instrucción manual; no se ha aplicado al POM.** No cambies solo el nivel en IntelliJ: al recargar Maven, el POM vuelve a ser la configuración de compilación principal.

## Paso 4 — Seleccionar JDK en IntelliJ IDEA

Con el JDK 17 o 21 instalado:

1. Abre **File → Project Structure → Project** (Ctrl+Alt+Shift+S).
2. En **Project SDK**, selecciona el JDK objetivo.
3. En **Language level**, selecciona el mismo nivel, por ejemplo **17**.
4. En **Modules**, confirma que el SDK del módulo hereda el Project SDK o que usa el mismo JDK.
5. Abre **Settings → Build, Execution, Deployment → Build Tools → Maven → Importing** y elige ese JDK para el Maven importer.
6. Abre **Settings → Build, Execution, Deployment → Build Tools → Maven → Runner** y elige el mismo JDK en **JRE**.
7. En **Settings → Build, Execution, Deployment → Compiler → Java Compiler**, revisa que el bytecode objetivo del módulo no esté configurado en una versión mayor.
8. Recarga los proyectos Maven con **Reload All Maven Projects**.

La configuración de Maven importer sincroniza el proyecto; la de Maven Runner ejecuta las metas. También revisa **Run → Edit Configurations → JRE** para que la aplicación se ejecute con el JDK/JRE requerido. Las etiquetas pueden variar ligeramente entre versiones de IntelliJ.

## Paso 5 — Recompilar y validar

Desde la raíz del repositorio, con el JDK que se eligió:

~~~powershell
mvn clean package
~~~

Si el comando mvn no está instalado, ejecuta las metas clean y luego package desde la ventana **Maven** de IntelliJ. La primera compilación en otro computador puede requerir internet para descargar plugins y dependencias.

Si falla, revisa el primer error de compilación. Por ejemplo, si aparece que una API, clase o sintaxis solo existe en una versión posterior, el código necesita una adaptación concreta antes de poder usar ese nivel objetivo. No basta con repetir el cambio de número.

Después de compilar, valida qué JVM ejecutará el programa:

~~~powershell
java -version
java -jar target\bank_pp-1.0-SNAPSHOT.jar
~~~

Si tienes acceso a cada versión objetivo, compila y ejecuta con esa JVM también. Una compilación con --release 17 usando JDK 25 comprueba compatibilidad de lenguaje/APIs estándar de Java 17 durante la compilación, pero la aceptación final debe probarse ejecutando el JAR en Java 17 y verificando las dependencias y operaciones reales.

## Paso 6 — Preparar el equipo que descarga el repositorio

Cada equipo necesita:

1. Un JDK/JRE igual o superior al nivel objetivo del JAR.
2. PostgreSQL disponible y la base bankpp creada y migrada.
3. Credenciales propias configuradas en variables de entorno o en un archivo .env local no versionado. El repositorio ofrece .env.example como referencia, no como credencial funcional.
4. El repositorio descargado y las dependencias Maven resueltas.

Para abrir desde IntelliJ, importa el pom.xml como proyecto Maven y selecciona el JDK correcto. Para ejecutar el JAR, el controlador JDBC está empaquetado, pero el servidor PostgreSQL y las credenciales no se incluyen dentro del JAR. La base debe ser accesible desde esa máquina. Ejecutar desde la raíz del proyecto ayuda a que la aplicación encuentre el archivo .env local.

## Errores frecuentes

- **UnsupportedClassVersionError:** el JAR se compiló para una versión posterior a la JVM que lo ejecuta. Usa una JVM igual o posterior, o recompila el proyecto con un objetivo inferior.
- **release version 25 not supported:** Maven está ejecutándose con un JDK anterior, aunque IntelliJ pueda tener otro JDK configurado. Revisa mvn -version y Maven Runner.
- **IntelliJ marca una característica como inválida:** Project SDK, module SDK o language level no coinciden con el POM.
- **El JAR inicia pero no conecta con la BD:** Java puede estar correcto; revisa PostgreSQL, URL, usuario, clave y archivo .env.
- **Compila en Java 25 pero no en 17/21:** el código o alguna dependencia usa una función/API que no está disponible en el objetivo menor. Hay que adaptar y volver a compilar.

## Límites y estado

No se ha cambiado pom.xml, no se ha recompilado el proyecto con destino 17 o 21 y no se ha verificado todavía la ejecución en esos JDK. La compatibilidad con Java 17 o 21 **no se afirma hasta que esas pruebas se hagan**. La modificación del nivel de Java también requiere regenerar el JAR.

Un JAR de Java es multiplataforma en cuanto al sistema operativo si usa Java estándar, pero no trae una JVM ni el servidor de base de datos. En Windows, macOS y Linux deben instalar un runtime compatible y tener acceso a PostgreSQL. Un EXE tampoco elimina estos requisitos salvo que se empaquete un runtime en la distribución, lo cual aún no está hecho en Bank PP.

## Referencias oficiales

- [Apache Maven Compiler Plugin — parámetro release](https://maven.apache.org/plugins/maven-compiler-plugin/compile-mojo.html)
- [Oracle — comando javac para Java 17](https://docs.oracle.com/en/java/javase/17/docs/specs/man/javac.html)
- [JetBrains — cambiar JDK de un proyecto Maven](https://www.jetbrains.com/help/idea/maven-support.html)
- [JetBrains — Project SDK y JDK](https://www.jetbrains.com/help/idea/sdk.html)
- [pgJDBC — descarga y requisito de Java](https://jdbc.postgresql.org/download/)

