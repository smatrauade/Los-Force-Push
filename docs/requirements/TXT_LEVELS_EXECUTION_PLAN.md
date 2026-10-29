# Los Force Push — Plan de ejecución TXT/niveles y promoción única

**Estado:** `PLAN_ONLY / CODE_NOT_STARTED / NO_PR_OPENED`. Este plan se destina a una rama de trabajo, no a una incorporación funcional a `main` ni a una ratificación de gramática. **Dominio único:** `SOKOBAN_TXT_LEVEL_DEFINITION_AND_VALIDATION`.

**Referencia inicial:** repositorio público `smatrauade/Los-Force-Push`, `main=24bb3f0d711dd15c79a078b09ec92134a7a600b2`, consultado el 29/10/2026. Revalidar HEAD, PR y worktrees antes de cada operación. **Rama propuesta:** `feat/txt-levels-release-2026-10-29`. **Política:** varios hitos comprobables en la misma rama; **un solo PR cuando código, pruebas y documentación estén completos**; promoción preferida mediante **squash merge**, de modo que `main` reciba un único commit temático. No se ejecuta merge por el mero hecho de que un test sea PASS.

## 1. Objetivo, límites y autoridad

Construir el máximo avance justificable **dentro del módulo TXT/niveles** antes del PR: formato, lectura externa, validación de definición inicial, atributos individuales, error atómico, pruebas reproducibles y contrato para futuros consumidores. Evitar cambios de tipo cosmético o patrones por obligación. **No se implementan aquí** Movimiento, reglas Fragile/hielo, KeyBox/Lock dinámicos, Observer productivo, Undo, Score, persistencia, interfaz Swing, sprites ni generador/solver externo. Se puede acordar el contrato con esos módulos sin introducir su implementación.

**Fuente de autoridad, en orden:** consigna PDF oficial → comunicaciones directas posteriores del profesor → explicaciones orales del profesor con procedencia ASR → material de cátedra → decisiones explícitas del equipo → bibliografía → referencias externas → inferencias. El mensaje de un compañero que consultó al profesor es una **fuente docente indirecta relevante y operativamente atendible**: aplicarla como hipótesis de planificación conservadora sin presentarla como audio/respuesta primaria corroborada ni usarla para anular una votación. En particular, **guardar/recuperar NO debe descontarse automáticamente de las dos adicionales**; un selector de estilos es candidato elegible, y OQ-10 sigue abierta. No forma parte de este dominio TXT.

**Requisitos oficiales:** Java/Swing, tablero 2D y niveles en `.txt`, cajas normales/frágiles/llave, hielo, cerrojos y muros, Undo 15/5/3, puntaje, HUD y dos adicionales con patrones. **Entrega miércoles 18/11/2026:** exposición en vivo 15–20 min y envío institucional con video, README, StarUML `.mdj`, informe, link público y copia al equipo. Uso de IA permitido si se declara y defiende. Fechas de `TODO.md` anteriores al 18/11 son metas internas propuestas, no plazos docentes.

**Indicaciones docentes pertinentes:** clase del 23/09 a las 03:00:50–03:02:14 y del 30/09 a las 01:07:57–01:08:51: el equipo define el alfabeto y el profesor podrá editar o construir un TXT compatible, que debe modificar el tablero real. Clase 30/09 00:34:18–00:35:47 y 07/10 00:31:47–00:32:06: Builder es una alternativa válida, **no obligatoria**. Clase 21/10 00:04:17 y 00:15:01–00:16:24: Observer es esperado para alguna parte del TP, con cerrojos y muros asociados como ejemplo; pertenece al dominio Movement/Model posterior. Estos timestamps provienen de una transcripción ASR normalizada **sin verificación acústica independiente**. No redistribuir PDFs, audio ni WhatsApp privados.

**Decisiones canónicas:** E01 acepta una única capa espacial con tokens combinados; E02 acepta resistencia inicial **individual por Fragile desde TXT**, pero NO fija ID, alfabeto, sintaxis, rango ni formato final. `OQ-06/OQ-08` siguen abiertos; `OQ-07` Lock↔Walls abierto; `OQ-03/E04` Fragile+hielo y `OQ-04/E08` Undo exigen revisión de compatibilidad; `OQ-11` Java/build no está ratificado. Preservar todos los votos en `DECISION_LOG.md`, sin duplicar tablas ni cambiar `TEAM_ACCEPTANCE=NOT_RECORDED`.

## 2. Hechos de código contrastados el 29/10

- `LevelLoader.load(Path)`: archivo externo, máximo **65.536 bytes** UTF-8 con decodificación estricta; publica un `Level` tras parseo completo.
- `LevelParser`: formato legacy de siete símbolos; `C2LevelParser` opt-in, siete aliases/combinaciones, `[attributes]` **vacía**, dimensiones 100×100 y longitud de token 128. Rechaza especiales todavía no modelados.
- `Board`/`Cell`/`Terrain`/`Occupant`: inmutables, con caja **normal** solamente.
- `LevelBuilder`: constructor concreto por carga. Evaluar su utilidad frente a creación directa; no crear Director ni Abstract Factory si no resuelve variación real.
- `ExperimentalFragileParser.parse(String)`: límite **65.536 caracteres Java**, distinto del límite de bytes del loader; `{floor|fragile:id}`, `{goal|fragile:id}` y atributos individuales; no es ruta pública de filesystem.
- `ExperimentalFragileLevel`: `spatialTemplate` más lista inmutable de `(id, position, initialResistance)` y validaciones de unicidad, terreno/posición e invariantes. **No convertir Fragile a `Occupant.BOX`** porque perdería ID/resistencia; no crear un agregado paralelo sin justificarlo.
- Pruebas HISTÓRICAS, no reejecutadas al elaborar este plan: Loading 40, C2 32, Fragile 31, Movement 31, Desktop 16, Assets 44, UI 17 y CLI 18. `ExperimentalFragileParserTest` ya cubre muchas de las F01–F14 de `FRAGILE_TXT_CONTRACT_PROPOSAL.md`: no repetir casos para inflar cobertura.

## 3. Ciclo de trabajo por gates pequeños y reversibles

| Gate | Trabajo de un solo alcance | Resultado exigible | Condición de bloqueo |
|---|---|---|---|
| G0 — Rehidratación y preflight | Leer íntegros `AGENTS.md`, índice, requisitos, OQ, E01/E02, propuesta Fragile, fuentes, arquitectura, tests y código TXT. Inspeccionar Git, SHA, worktrees, rama remota y estado local antes de escribir. | Matriz fuente→estado→archivo→cambio propuesto; identidad y limpieza verificadas. | Repo incorrecto, branch ya ocupada, divergencia sin estudiar, worktree sucio o escritura concurrente. |
| G1 — Baseline/diagnóstico | Compilar con UTF-8, `--release 17`, `-Xlint:all -Werror`; ejecutar pruebas de Loading/C2/Fragile y regresiones pertinentes. Mapear F01–F14 a **tests reales**; localizar brecha demostrable. | Tabla F01–F14 con cobertura/resultado por SHA. | Suite FAIL sin analizar o ninguna mejora útil. Si no hay brecha, `NO_CHANGE_NEEDED`; avanzar sin commit ceremonial. |
| G2 — Decisiones de gramática | Especificar y contrastar IDs, sintaxis, límites, atributos, versionado, errores y ejemplos válidos/inválidos con E01/E02 y OQ-06/OQ-08. Pedir decisión concreta cuando cambie semántica normativa. | Contrato firmado/registrado o propuesta explícitamente provisional, con casos de aceptación. | No afirmar `TEAM_ACCEPTED` ni aprobar un rango por usarlo el spike. |
| G3 — Implementación TXT | Una mejora por subiteración: validación o definición inicial sin duplicaciones; preservar compatibilidad legacy/C2, aislamiento, UTF-8 estricto, límites y publicación atómica. Integrar especiales en API jugable **solo con un modelo consumidor que preserve ID/resistencia**. | Cambio productivo y pruebas de frontera; commit razonado en **la misma rama**. | No tocar Movement/Swing ni habilitar `Level` especial con pérdida de estado. |
| G4 — Aceptación profesor | Fixture externo editado (pared/caja, Goal) y recarga real en el subconjunto soportado. Dos Fragile con resistencias 3/12 y attributes invertidos: preservar posición, ID y suelo. Errores no cambian nivel válido previo. Coordinar prueba UI final con responsable, fuera de este dominio. | Archivo y secuencia reproducible, comandos y exit, SHA probado, `NOT_RUN` honestos. | No proclamar especiales jugables ni QA visual por tests de parser. |
| G5 — Documentación prepromoción | En **el mismo incremento**, actualizar README, TODO, CURRENT_STATE, contrato y OQ, arquitectura, responsabilidades, UML si cambió, trazabilidad, estrategia de pruebas, referencias e índices solo por impacto. Revisar `AGENTS`/`FRESHNESS_POLICY` sin reescribirlos por rutina. | Documentos en HEAD de la rama que describen **exactamente el producto que existiría en `main` al aprobar el PR**; `git diff --check`, verificador Markdown y enlaces PASS. | Planes rotulados como código real; tests históricos como nuevos; estado de `main` futuro ficticio o autocitas de SHA. |
| G6 — ÚNICO PR y promoción | Comprobar HEAD remoto y cambios de compañeros; ejecutar QA final sobre HEAD publicable; abrir un PR técnico único con descripción breve; revisión de código/contratos; **squash merge** solo con autorización explícita y SHA esperado. | Un commit temático en `main`; estado y árbol verificados, docs coherentes sin PR de seguimiento. | Drift, OQ normativo irresuelto para código afectado, QA fallida o revisión requerida no satisfecha. |

**Control de carga:** no lanzar procesos masivos. Cada agente local ejecuta un gate o subgate medible y se detiene para inspeccionar resultados. Mantener la **misma rama** entre subiteraciones. Los commits internos son para puntos semánticos verificables, no para cada observación o actualización de resumen. Los logs del agente pueden ser efímeros, pero las decisiones y resultados importantes deben terminar en documentos vivos del PR.

## 4. Estrategia de compatibilidad y pruebas

Mantener legacy y C2-L7 para todos los niveles previamente aceptados; no reinterpretar símbolos. Verificar ancho/alto, exactamente un jugador, sobreposiciones, límites de bytes/Unicode, separadores, rechazo explícito de entidades no soportadas y fallos tardíos sin publicación. Para Fragile: IDs únicos, atributos exactamente una vez, resistencia positiva provisional, posicionamiento sobre FLOOR/GOAL, listas profundamente inmutables y reversión del orden de declaraciones. No decidir el máximo real de resistencia por los 3 sprites de estado.

**Contrato docente externo:** el juego usa la ruta del archivo realmente elegido, no el nombre del fixture hardcodeado; editar un archivo válido debe cambiar el tablero al recargarlo. **No prometer** que toda edición es válida: una edición inválida debe fallar sin reemplazar el estado publicado. La prueba completa TXT→Swing se realizará con quien integre la UI; esta rama puede suministrar fixture y oráculo sin tocar Swing.

No sumar contadores de suites distintas como evidencia de casos únicos sin explicación. Registrar versión exacta de Java/SO, commit ensayado, comandos, salida, exit codes y límites de cualquier prueba no ejecutada. El checker de enlaces/UTF-8/PNG/WAV no equivale a ejecutar Java. No inventar aprobación de licencias por comparar hashes.

## 5. Política de Git, PR y texto

**Baseline de `main` estable:** los desarrollos en curso **no se reflejan como hechos implementados en `main`** hasta su promoción. La verdad del trabajo no integrado reside en esta rama; el documento vivo de `main` solo declara el último producto fusionado. Esta diferencia es esperada y no equivale a documentación vencida. Un chat nuevo debe rehidratar ambas refs y evaluar su relación.

- No `force-push`, `reset --hard`, `git clean -fd`, `worktree prune`, `branch -D`, cambios de permisos ni eliminación de ramas históricas. Si falla una postcondición tras un push, **inspeccionar remotos antes de reintentar**: el push puede haberse completado.
- Mantener un solo PR de este dominio. No abrir uno para cada gate ni otro después solo para narrar el merge. Preferir squash con resumen de autoría y cambios en el commit final, conservando los commits de desarrollo y la discusión del PR en GitHub.
- Los títulos de commits deben referir a cambios sustantivos (`test(level):`, `fix(level):`, `feat(level):`, `docs(level):`), no simular progreso. La cantidad de commits no demuestra uso de IA, pero consolidar unidades semánticas mejora legibilidad de `main`.
- Descripción de PR: **problema → comportamiento implementado → diseño/alternativas y patrón real → pruebas/entorno/SHA → compatibilidad y límites → documentación actualizada**. Escribir conciso, sin comentarios repetitivos de estado, firmas ficticias o listas de PASS de otra ejecución. Declarar asistencia de IA en informe técnico, como exige el profesor.
- Antes de promoción, verificar que el README/índices/TODO/casos/descripciones ya representan el **estado posterior a aprobar**; citar SHA de base como referencia histórica, no escribir el nuevo SHA dentro del mismo commit. Actualizar `CURRENT_STATE` por **cambio semántico**, no por cada nuevo PR documental.

## 6. Matriz de frescura obligatoria

| Archivo | Disparador real | Qué debe comprobarse antes del PR |
|---|---|---|
| `README.md`, `docs/README.md`, `REHYDRATION_INDEX.md` | Cambia ejecución/contrato o navegación | Rutas correctas y capacidades efectivamente presentes; acceso desde clon limpio. |
| `TODO.md`, `CURRENT_STATE.md` | Cierra gap, cambia dependencia o capacidad | `IMPLEMENTED`/`PARTIAL`/`EXPERIMENTAL`/`OPEN`, pruebas y próximos pasos sin confundir rama con `main`. |
| `AGENTS.md`, `CONTRIBUTING.md`, `FRESHNESS_POLICY.md` | Cambia política de trabajo, **no cada commit** | Gate de rehidratación y promoción única; revisión semanal durante actividad ya indicada. |
| `SOURCE_REGISTRY.md`, `BIBLIOGRAPHY.md` | Nueva fuente o aclaración realmente utilizada | Procedencia y límites de disponibilidad; no subir originales privados. |
| `REQUIREMENTS.md`, `OPEN_QUESTIONS.md`, `DECISION_LOG.md` | Aclaración/ratificación concreta | Fuente, fecha, autoridad y efecto; votos E01–E11 intactos salvo decisión realmente nueva. |
| `ARCHITECTURE.md`, `RESPONSIBILITIES.md`, UML | Código o relación arquitectónica cambió | `AS_IMPLEMENTED` ≠ candidato; patrón por intención, no por nombre de clase. |
| `TRACEABILITY_MATRIX.md`, `TEST_STRATEGY.md` | Código, tests o QA nuevos | RF→implementación→test→resultado real, por SHA, con `NOT_RUN` honesto. |

La política vigente [`FRESHNESS_POLICY`](../continuity/FRESHNESS_POLICY.md) ya requiere revisión **en cada hito** y revisión **semanal mientras haya actividad**. No hace falta crear una automatización ni cambiar fechas sin modificar significado.

## 7. Cierre de la planificación

`PLAN_CREATED_ON_FEATURE_BRANCH` solo cuando la rama haya recibido este archivo y sus enlaces. `JAVA_TESTS_THIS_STEP=NOT_RUN`; `GAMEPLAY_MODIFIED=false`; `PR_OPENED=false`; `MAIN_MODIFIED=false`. Ningún agente debe interpretar este texto como autorización de code merge o como evidencia de contrato aprobado.
