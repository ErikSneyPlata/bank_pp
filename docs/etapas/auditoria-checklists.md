# Auditoría de checklists — Bank PP

**Corte de revisión:** 2026-11-11  
**Etapa activa:** Etapa 6, interfaz Swing, en desarrollo.

## Cómo leer esta auditoría

Se revisaron las casillas y evidencias de las nueve páginas de etapa en Notion frente a los archivos del proyecto. Los checkboxes originales se conservan; esta auditoría asigna estado de evidencia sin marcar una intención como terminada.

- **OK:** el elemento está presente y existe evidencia técnica directa en los archivos o comprobación.
- **PARCIAL:** hay diseño o código, pero falta ejecución, aprobación, captura o evidencia completa.
- **PENDIENTE:** no se encontró el entregable o prueba.
- **CONFLICTO:** una decisión actual contradice un criterio original y necesita confirmación del docente.

Un estado OK sobre documentación/configuración no significa que toda la etapa esté aprobada. La ruta de cada ficha local se indica en su encabezado.

## Etapa 1. Entorno, Maven y repositorio

**Ficha local:** `docs/etapas/etapa-01-entorno-maven-repositorio.md`  
**Bitácora Notion:** Etapa 1 - Entorno, Maven y repositorio

| Elemento del checklist | Estado | Evidencia o brecha localizada |
| --- | --- | --- |
| Proyecto inicial creado en IntelliJ | OK | Proyecto y fuentes presentes; etapa 1 local. |
| Java JDK 25.0.2 verificado | OK | Compilación javac --release 25 tuvo código 0; falta adjuntar captura pedida. |
| PostgreSQL 18.3 verificado | PARCIAL | Instalación anotada previamente y puerto 127.0.0.1:5432 accesible; autenticación aún no verificada. |
| Confirmar configuración Maven real | PARCIAL | pom.xml raíz y database/pom.xml existen; mvn no está disponible para validar. |
| Crear/validar pom.xml | PARCIAL | pom.xml real está en la raíz; empaquetado Maven aún no ejecutado. |
| Crear repositorio GitHub compartido | PENDIENTE | URL/remote no confirmado. |
| Definir ramas y estrategia de integración | PENDIENTE | No existe evidencia documental. |
| Crear paquetes iniciales | OK | Paquetes com.bankpp visibles bajo src/main/java. |
| Crear README y .gitignore | PARCIAL | .gitignore existe; no hay README.md raíz. |
| Captura Project Structure IntelliJ | PENDIENTE | No se adjuntó captura. |
| Captura JDK configurado | PENDIENTE | No se adjuntó captura; compile log es evidencia técnica distinta. |
| Captura Maven/paquetes | PENDIENTE | No se adjuntó captura. |
| URL del repositorio GitHub | PENDIENTE | No confirmada. |
| Captura del primer commit | PENDIENTE | No se adjuntó evidencia. |

## Etapa 2. Análisis y requerimientos

**Ficha local:** `docs/etapas/etapa-02-analisis-requerimientos.md`  
**Bitácora Notion:** Etapa 2 - Análisis y requerimientos

| Elemento del checklist | Estado | Evidencia o brecha localizada |
| --- | --- | --- |
| Nombre, integrantes y docente definidos | OK | Registrado en página principal de Notion. |
| Entidad bancaria ficticia y roles definidos | OK | Notion describe banco ficticio y roles cliente/empleado. |
| Alcance intermedio definido | OK | Alcance inicial y fuera-de-alcance descritos en Notion. |
| Validar requerimientos con el docente | PENDIENTE | No hay aprobación/evidencia adjunta. |
| Definir reglas de negocio detalladas | PARCIAL | Reglas en páginas 3/5 y código; aceptación formal pendiente. |
| Especificar permisos por rol | PARCIAL | Propiedad de cuenta cliente aparece en repositorios; empleado y matriz de permisos no están completos. |
| Documentar casos de uso y flujos alternativos | PARCIAL | Hay fuente casos-de-uso.puml y borrador Notion; flujos alternos no detallados. |
| Aprobar criterios de aceptación | PENDIENTE | No hay acta de aprobación ni casos aceptados. |

## Etapa 3. UML, arquitectura y diseño de datos

**Ficha local:** `docs/etapas/etapa-03-uml-arquitectura-datos.md`  
**Bitácora Notion:** Etapa 3 - UML, arquitectura y diseño de datos

| Elemento del checklist | Estado | Evidencia o brecha localizada |
| --- | --- | --- |
| Diagrama de casos de uso validado | PARCIAL | casos-de-uso.puml y borrador Notion existen; validación pendiente. |
| Diagrama de clases validado | PARCIAL | MDJ y PUML activos existen; revisión/aprobación pendiente. |
| Relaciones POO justificadas | PARCIAL | arquitectura/modelo local explica relaciones; confirmación docente pendiente. |
| Diagrama entidad-relación | PENDIENTE | No se encontró diagrama ER explícito; hay modelo relacional. |
| Diagrama relacional | PARCIAL | modelo-relacional.puml existe; confirmar con esquema aplicado. |
| Diagrama de arquitectura | PARCIAL | arquitectura-componentes.puml y arquitectura.md existen; aprobación/PNG pendiente. |
| Diagrama de navegación/pantallas | PARCIAL | navegacion.puml y diagrama-formularios.puml existen; no se validaron contra todos los roles. |
| Exportar cada diagrama a PNG y conservar editable | PARCIAL | Fuentes PUML/MDJ están; no se encontraron PNG finales. |
| PNG de casos de uso | PENDIENTE | No encontrado. |
| PNG de clases | PENDIENTE | No encontrado. |
| PNG ER/relacional | PENDIENTE | No encontrado. |
| PNG de arquitectura | PENDIENTE | No encontrado. |
| PNG de navegación | PENDIENTE | No encontrado. |
| Fuentes .puml o .mmd | OK | Se encontraron seis .puml; los MDJ activos están en docs/etapa-3/staruml. |

## Etapa 4. Base de datos y persistencia

**Ficha local:** `docs/etapas/etapa-04-base-datos-persistencia.md`  
**Bitácora Notion:** Etapa 4 - Base de datos y persistencia

| Elemento del checklist | Estado | Evidencia o brecha localizada |
| --- | --- | --- |
| Crear base local bankpp | PENDIENTE | El usuario confirmó que la base existente se llama bankpp y el puerto responde; falta autenticar y aplicar tablas. |
| Crear tablas, claves y restricciones | PARCIAL | Script V1 define seis tablas y constraints; no se aplicó. |
| Crear índices necesarios | PARCIAL | Índices definidos en V1; no ejecutados. |
| Insertar datos de prueba | PENDIENTE | No hay seed de datos confirmado. |
| Conectar Java mediante JDBC | PARCIAL | Driver/config/repositorios existen; conexión real no probada. |
| Implementar repositorios | OK | Hay cuatro repositorios JDBC concretos. |
| Probar CRUD usuarios/cuentas/citas | PENDIENTE | No hay pruebas contra base activa. |
| Persistencia de transacciones/solicitudes | PARCIAL | SQL y métodos JDBC escritos; persistencia no verificada. |
| Probar integridad y atomicidad | PENDIENTE | Atomicidad codificada para transferencia; sin resultado de test. |
| Migrar esquema a Supabase | PENDIENTE | No migrado ni configurado. |
| Probar conexión remota desde app | PENDIENTE | No hay entorno Supabase comprobado. |
| Proteger credenciales y secretos | PARCIAL | DatabaseConfig lee variables de entorno; falta auditoría completa de secretos y validación de operación. |

## Etapa 5. Modelo POO y lógica de negocio

**Ficha local:** `docs/etapas/etapa-05-modelo-poo-logica.md`  
**Bitácora Notion:** Etapa 5 - Modelo POO y lógica de negocio

| Elemento del checklist | Estado | Evidencia o brecha localizada |
| --- | --- | --- |
| Implementar entidades y constructores | OK | Clases de modelo presentes y compiladas. |
| Definir encapsulamiento y validaciones | PARCIAL | Atributos/modelo y validaciones presentes; no hay pruebas unitarias. |
| Herencia Usuario → Cliente/Empleado | OK | Clases reflejan la jerarquía y compilan. |
| Polimorfismo en operaciones | PARCIAL | Transaccion abstracta y subclases; no se han ejecutado pruebas. |
| Implementar interfaces | CONFLICTO | No implementado por decisión del equipo; Notion/profesor aún lo pide. |
| Implementar enums | OK | Enums de rol, cuenta, transacción, cita y solicitud presentes. |
| Servicios de autenticación | PARCIAL | AutenticacionService y JDBC existen; no probados con DB. |
| Reglas de saldo/estado de cuenta | PARCIAL | Validaciones codificadas en servicios/repositorios; no verificadas contra PostgreSQL. |
| Transferencias y retiros | PARCIAL | Lógica implementada con transacción JDBC; sin prueba de integración. |
| Citas y solicitudes | PARCIAL | Servicios/repositorio/formularios presentes; sin DB activa. |
| Pruebas unitarias de negocio | PENDIENTE | No se encontró src/test. |

## Etapa 6. Interfaz gráfica Swing

**Ficha local:** `docs/etapas/etapa-06-interfaz-swing.md`  
**Bitácora Notion:** Etapa 6 - Interfaz gráfica Swing

| Elemento del checklist | Estado | Evidencia o brecha localizada |
| --- | --- | --- |
| Definir identidad visual | PENDIENTE | Hay layouts funcionales básicos; no se documentó sistema visual aprobado. |
| Crear navegación principal | PARCIAL | PanelClienteForm tiene pestañas; rol empleado no tiene panel. |
| Implementar Login y Registro | PARCIAL | Clases/forms y acciones están; no probadas con DB. |
| Dashboard cliente | PARCIAL | PanelClienteForm con cinco pestañas; flujo real pendiente. |
| Dashboard empleado | PENDIENTE | No está implementado. |
| Formularios de operaciones | PARCIAL | Retiro/transferencia aparecen en OperacionesForm; sin prueba funcional. |
| Tablas de historial y administración | PARCIAL | Historial del cliente presente; tablas administrativas no. |
| Vistas llaman servicios, no SQL | OK | Clases de formulario delegan a service; SQL vive en JDBC. |
| Validar entradas y mostrar errores | PARCIAL | Hay validaciones y mensajes; recorrido integral no probado. |
| Permisos y cierre de sesión | PARCIAL | Logout cliente existe; el login envía cualquier rol al panel cliente; autorización empleado pendiente. |
| Probar resolución habitual | PENDIENTE | No hay evidencia visual/resolución. |

## Etapa 7. Integración, pruebas y calidad

**Ficha local:** `docs/etapas/etapa-07-integracion-pruebas.md`  
**Bitácora Notion:** Etapa 7 - Integración, pruebas y calidad

| Elemento del checklist | Estado | Evidencia o brecha localizada |
| --- | --- | --- |
| Integrar ramas de ambos integrantes | PENDIENTE | No se verificaron ramas/remotos en este checkout. |
| Probar registro/login por rol | PENDIENTE | No hay base activa ni resultados; route de rol incompleta. |
| Probar CRUD usuarios | PENDIENTE | Sin datos de prueba ni conexión comprobada. |
| Probar CRUD cuentas | PENDIENTE | Sin datos de prueba ni conexión comprobada. |
| Probar CRUD citas | PENDIENTE | Sin datos de prueba ni conexión comprobada. |
| Probar saldo suficiente/insuficiente | PENDIENTE | No hay ejecución registrada. |
| Probar transferencia exitosa | PENDIENTE | No hay ejecución registrada. |
| Probar rollback de transferencia | PENDIENTE | Implementación atómica escrita; rollback sin test. |
| Probar cuenta bloqueada/cerrada | PENDIENTE | No hay ejecución registrada. |
| Probar acceso a cuenta ajena | PENDIENTE | No hay ejecución registrada. |
| Probar cancelación/duplicidad de citas | PENDIENTE | No hay ejecución registrada. |
| Probar conexión remota | PENDIENTE | Supabase no configurado. |
| Corregir defectos y registrar evidencia | PARCIAL | Se corrigió arranque Swing; falta registro de pruebas funcionales. |

## Etapa 8. Informe técnico y documentación PDF

**Ficha local:** `docs/etapas/etapa-08-informe-documentacion.md`  
**Bitácora Notion:** Etapa 8 - Informe técnico y documentación PDF

| Elemento del checklist | Estado | Evidencia o brecha localizada |
| --- | --- | --- |
| Portada con integrantes, docente y asignatura | PARCIAL | Datos están en Notion; no hay portada de informe/PDF. |
| Descripción y objetivos | PARCIAL | Están en página del proyecto Notion; no se integran en informe local. |
| Requerimientos | PARCIAL | Notion Stage2 tiene requerimientos; aprobación y conciliación pendiente. |
| Diagramas finales con pie de figura | PENDIENTE | Fuentes editables existen; no PNG final ni informe con pies. |
| Explicación POO herencia/agregación/composición/interfaces/enums | PARCIAL | Modelo local explica relaciones, pero se omite interface por decisión; discrepancia no aprobada. |
| Modelo relacional y explicación de tablas | OK | modelo-de-datos.md y migración explican las seis tablas. |
| Capturas reales de aplicación | PENDIENTE | No encontradas. |
| Evidencia de CRUD y conexión remota | PENDIENTE | No hay conexión ni resultados. |
| Matriz de pruebas y resultados | PARCIAL | Notion Stage7 define escenarios esperados; resultados no existen. |
| Conclusiones basadas en resultados reales | PENDIENTE | Sin batería ejecutada ni informe. |
| Referencias | PENDIENTE | No hay bibliografía final. |
| Revisión de ortografía/formato | PENDIENTE | No existe el documento final para revisar. |
| Exportar/revisar PDF final | PENDIENTE | No se encontró PDF. |

## Etapa 9. Entregables, ejecutables y sustentación

**Ficha local:** `docs/etapas/etapa-09-entregables-sustentacion.md`  
**Bitácora Notion:** Etapa 9 - Entregables, ejecutables y sustentación

| Elemento del checklist | Estado | Evidencia o brecha localizada |
| --- | --- | --- |
| Código fuente del proyecto Maven | PARCIAL | Fuentes y pom presentes; build Maven no ejecutado. |
| JAR ejecutable | PENDIENTE | No se encontró artefacto. |
| EXE Windows | PENDIENTE | No se encontró artefacto. |
| PDF informe | PENDIENTE | No se encontró. |
| Diagramas PNG | PENDIENTE | No se encontraron exportaciones PNG. |
| Fuentes editables de diagramas | OK | PlantUML y MDJ presentes. |
| ZIP/RAR/7z proyecto | PENDIENTE | No encontrado. |
| README de instalación/ejecución | PENDIENTE | No hay README raíz. |
| Configurar empaquetado Maven | PARCIAL | Maven Shade está configurado; build pendiente. |
| Generar JAR | PENDIENTE | No generado. |
| Probar JAR en entorno limpio | PENDIENTE | No generado. |
| Generar EXE Windows | PENDIENTE | No generado. |
| Probar EXE Windows | PENDIENTE | No generado. |
| Conexión remota desde ambos ejecutables | PENDIENTE | No hay remoto ni artefactos. |
| Preparar carpeta final | PENDIENTE | No encontrada. |
| Incluir fuentes y diagramas PNG | PARCIAL | Fuentes presentes, PNG y paquete final faltan. |
| Incluir informe PDF | PENDIENTE | No PDF. |
| Verificar ausencia de secretos | PARCIAL | Credenciales JDBC se leen desde entorno; auditoría global de secretos no documentada. |
| Preparar demo funcional | PENDIENTE | No demostración integrada comprobada. |
| Ensayar sustentación de ambos integrantes | PENDIENTE | No hay evidencia. |
| Interfaz gráfica Java (criterio final docente) | PARCIAL | Swing existe; rol empleado y flujos DB no están probados. |
| CRUD mínimo en tres entidades | PENDIENTE | No evidencia de CRUD completo de tres entidades. |
| Base remota 24/7 | PENDIENTE | No Supabase desplegado. |
| Mínimo tres tablas | PARCIAL | Script declara seis tablas; no hay esquema aplicado. |
| Diagramas relacional y de clases | PARCIAL | Fuentes editables; exportación/aprobación final pendiente. |
| Agregación, composición y herencia | PARCIAL | Aparecen en modelo/código; falta validación docente y pruebas. |
| Interface y Enumeration | CONFLICTO | Enums existen; interface se omite por decisión del equipo aunque el criterio final la exige. |
| JAR y EXE funcionales | PENDIENTE | No generados ni probados. |
| PDF completo | PENDIENTE | No existe PDF. |
| Proyecto comprimido con PNG y ejecutables | PENDIENTE | No existe paquete final. |

## Hallazgos transversales que impiden cerrar etapas

1. Las páginas originales de requisitos conservan criterios de interface Java y hash de contraseña; el equipo pidió no incluirlos. Falta decisión explícita del docente sobre el criterio evaluado.
2. El nombre confirmado de la base local es `bankpp`; `database/README.md` ya quedó alineado. El puerto responde; falta autenticar y aplicar tablas.
3. Existe código para enviar al usuario autenticado al panel de cliente independientemente del rol; el panel empleado no está hecho. No se debe afirmar que la autorización por rol esté terminada.
4. Las fuentes UML editables están presentes, pero las exportaciones PNG/aprobaciones no se encontraron.
5. `V1__initial_schema.sql`, repositorios y servicios existen, pero la migración y conexión no se probaron: psql no tiene credenciales disponibles en este proceso.
6. Compilación y smoke check de arranque son las únicas verificaciones técnicas directas resumidas aquí; no hay `src/test` ni matriz de flujos ejecutados.
7. Documentación técnica existe, pero falta el informe académico PDF, capturas funcionales y artefactos de entrega.

## Próximos criterios de actualización

Al completar una brecha, actualizar la fila con evidencia concreta (ruta, captura o resultado) y solo entonces revisar el checkbox de la etapa correspondiente. La etapa activa 6 no implica cierre de las etapas 1–5.

