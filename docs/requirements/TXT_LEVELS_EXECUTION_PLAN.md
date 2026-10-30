# Los Force Push — Plan de ejecución TXT/niveles y promoción única

**Estado técnico:** `AUTONOMOUS_SCOPE_COMPLETE / MODULE_OWNER_DECIDED_SUPPORTED_SUBSET / NON_PLAYABLE_DEFINITION / RUNTIME_PENDING_TEAM`. El contrato TXT soportado se adopta expresamente en D-020; OQ-06 resuelta, OQ-08 parcialmente resuelta. El plan inicial y sus checkpoints históricos se conservan. Git/GitHub determina publicación, revisión e integración; no se anticipa un merge. **Dominio único:** `SOKOBAN_TXT_LEVEL_DEFINITION_AND_VALIDATION`.

**Referencia inicial histórica:** repositorio público `smatrauade/Los-Force-Push`, `main=24bb3f0d711dd15c79a078b09ec92134a7a600b2`, consultado el 29/10/2026. La rama usada es `feat/txt-levels-release-2026-10-29`; el [PR #15](https://github.com/smatrauade/Los-Force-Push/pull/15) ya existe. Revalidar HEAD, PR y worktrees antes de cada operación. **Política:** varios hitos comprobables en la misma rama; **una sola promoción funcional**, preferentemente squash, con autorización propia y revisión. El cierre semántico sólo permite actualizar el Draft existente; no Ready ni merge. Consultar su estado real en GitHub, sin inferirlo de este checkpoint.

## 1. Objetivo, límites y autoridad

Construir el máximo avance justificable **dentro del módulo TXT/niveles** antes del PR: formato, lectura externa, validación de definición inicial, atributos individuales, error atómico, pruebas reproducibles y contrato para futuros consumidores. Evitar cambios de tipo cosmético o patrones por obligación. **No se implementan aquí** Movimiento, reglas Fragile/hielo, KeyBox/Lock dinámicos, Observer productivo, Undo, Score, persistencia, interfaz Swing, sprites ni generador/solver externo. Se puede acordar el contrato con esos módulos sin introducir su implementación.

**Fuente de autoridad, en orden:** consigna PDF oficial → comunicaciones directas posteriores del profesor → explicaciones orales del profesor con procedencia ASR → material de cátedra → decisiones explícitas del equipo → bibliografía → referencias externas → inferencias. El mensaje de un compañero que consultó al profesor es una **fuente docente indirecta relevante y operativamente atendible**: aplicarla como hipótesis de planificación conservadora sin presentarla como audio/respuesta primaria corroborada ni usarla para anular una votación. En particular, **guardar/recuperar NO debe descontarse automáticamente de las dos adicionales**; un selector de estilos es candidato elegible, y OQ-10 sigue abierta. No forma parte de este dominio TXT.

**Requisitos oficiales:** Java/Swing, tablero 2D y niveles en `.txt`, cajas normales/frágiles/llave, hielo, cerrojos y muros, Undo 15/5/3, puntaje, HUD y dos adicionales con patrones. **Entrega miércoles 18/11/2026:** exposición en vivo 15–20 min y envío institucional con video, README, StarUML `.mdj`, informe, link público y copia al equipo. Uso de IA permitido si se declara y defiende. Fechas de `TODO.md` anteriores al 18/11 son metas internas propuestas, no plazos docentes.

**Indicaciones docentes pertinentes:** clase del 23/09 a las 03:00:50–03:02:14 y del 30/09 a las 01:07:57–01:08:51: el equipo define el alfabeto y el profesor podrá editar o construir un TXT compatible, que debe modificar el tablero real. Clase 30/09 00:34:18–00:35:47 y 07/10 00:31:47–00:32:06: Builder es una alternativa válida, **no obligatoria**. Clase 21/10 00:04:17 y 00:15:01–00:16:24: Observer es esperado para alguna parte del TP, con cerrojos y muros asociados como ejemplo; pertenece al dominio Movement/Model posterior. Estos timestamps provienen de una transcripción ASR normalizada **sin verificación acústica independiente**. No redistribuir PDFs, audio ni WhatsApp privados.

**Decisiones canónicas:** E01 acepta una única grilla con tokens combinados; E02 acepta resistencia inicial **individual por Fragile desde TXT**. No votaron ID/sintaxis/rango: esos detalles del subconjunto implementado se adoptan por el responsable TXT en D-020. OQ-06 resuelta; OQ-08 sólo conserva extensiones no soportadas. E04/E08 tienen regla TEAM decidida e implementación/bordes en otros módulos; OQ-07 transversal y OQ-11 build global permanecen abiertas. Preservar votos y corrección de procedencia E04 sólo en DECISION_LOG, sin tabla rival ni cambiar `TEAM_ACCEPTANCE=NOT_RECORDED`.

## 2. Hechos de código contrastados el 29/10

**Checkpoint de planificación inicial 8b71712**, previo a la carga filesystem. Las secciones 2–7 conservan el plan y sus gates de aquel corte; capacidades ejecutadas y decisiones posteriores están en G2/G3 y en el cierre semántico final. No ejecutar órdenes de apertura/promoción ni reabrir acuerdos desde este historial.

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

**Contrato docente externo:** el juego usa la ruta del archivo realmente elegido, no el nombre del fixture hardcodeado; editar un archivo válido debe cambiar el tablero al recargarlo. **No prometer** que toda edición es válida: una edición inválida debe fallar sin reemplazar el estado publicado. La prueba completa TXT→Swing se realizará con quien integre la UI; el módulo TXT suministra fixture y oráculo sin tocar Swing.

No sumar contadores de suites distintas como evidencia de casos únicos sin explicación. Registrar versión exacta de Java/SO, commit ensayado, comandos, salida, exit codes y límites de cualquier prueba no ejecutada. El checker de enlaces/UTF-8/PNG/WAV no equivale a ejecutar Java. No inventar aprobación de licencias por comparar hashes.

## 5. Política de Git, PR y texto

**Baseline de `main` estable:** los desarrollos en curso **no se reflejan como hechos implementados en `main`** hasta su promoción. Comparar las refs de trabajo y `main` para distinguir contenido preparado de producto fusionado; cada documento describe las capacidades del árbol al que pertenece. Esta diferencia es esperada y no equivale a documentación vencida. Un chat nuevo debe rehidratar ambas refs y evaluar su relación.

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

## 7. Checkpoint histórico de planificación — 29/10/2026, 8b71712

`PLAN_CREATED_ON_FEATURE_BRANCH` solo cuando la rama haya recibido este archivo y sus enlaces. `JAVA_TESTS_THIS_STEP=NOT_RUN`; `GAMEPLAY_MODIFIED=false`; `PR_OPENED=false`; `MAIN_MODIFIED=false`. Ningún agente debe interpretar este texto como autorización de code merge o como evidencia de contrato aprobado.

## G2 — decisión técnica del incremento 29/10/2026

**Checkpoint histórico del incremento 5ac2bba**, anterior a la adopción contractual D-020. Los pendientes y límites provisionales de esta sección describen ese hito, no el inventario vigente de decisiones.

**Problema verificable:** `ExperimentalFragileParser.parse(String)` ya preserva datos e invariantes, pero el HEAD inicial `8b71712cec673651b8dffe792bdc4ee7c779c1cf` no ofrece carga de su definición desde filesystem. `LevelLoader.load(Path)` centraliza el límite de bytes y UTF-8 estricto, pero devuelve sólo `Level` normal. `Files.readString` seguido de parseo permite evaluar sintaxis, sin reutilizar el techo de lectura. La diferencia bytes/caracteres está documentada en la propuesta; no es un defecto del contrato de String.

Autoridad: RF-02 exige TXT externos y la aclaración oral normalizada contempla archivos editados por el docente; E01/E02 requieren disposición y resistencia individual. No exige esta firma ni ratifica C2. G2 selecciona una mejora técnica independiente de sintaxis/rango definitivos, sin resolver mecánicas abiertas. Se revisaron íntegros código/tests/contratos, inventario `git ls-files` y fuentes vivas antes de editar. G0–G1 aportado fue verificado sin reejecución rutinaria; [procedencia](../continuity/SOURCE_REGISTRY.md#evidencia-técnica-local-txt--29102026).

La etiqueta inicial de planificación procede de `8b71712`; las afirmaciones de R7 preparada de la reconciliación docente proceden de `2dad014`. Se consultó su historial antes de superseder ese estado operativo; no se modifica una decisión académica ni una cápsula histórica.

### Matriz de brechas al comenzar G2

| Requisito | Implementación inicial | Prueba existente | Brecha real | Propuesta | Prioridad |
|---|---|---|---|---|---|
| RF-02, archivo Fragile externo | Loader sólo devuelve Level; Fragile recibe String | Fragile «normal loader rejects same file»; Loading 16/25/38 | No existe entrada experimental con lectura acotada compartida | Añadir método explícito en Loader | P1, seleccionada |
| RF-01/02/05, E02 datos individuales | ExperimentalFragileLevel conserva ID/posición/resistencia | Fragile 1–3 y construcción directa 23–31 | Ninguna pérdida demostrada en el agregado | Reutilizarlo; probar extremo filesystem | P1, incluida |
| RF-02, política de E/S | load lee N+1, UTF-8 REPORT | Loading 16/25/31/38; C2 15/18 | Cobertura no atravesaba ruta experimental | Compartir readText privado; pruebas directas bytes/UTF-8 | P1, incluida |
| RF-02, edición externa | Recarga normal y C2 sin caché | Loading 3/39, C2 24, Movement 25 | No hay oráculo experimental de archivo editado | Fixture dos valores 3/12, Goal, editar posiciones/atributos/normal Box | P1, incluida |
| RF-02, errores atómicos | Parsers y Board validan antes del retorno | Fragile «late failure cannot publish»; C2 22 | Falta prueba de error tardío y E/S por nueva entrada | Rechazar sin devolver agregado; recuperación posterior | P1, incluida |
| RF-02/03, compatibilidad | Legacy/C2 siete estados y Movement normal | Loading 40, C2 32, Movement 31, Desktop 16 | Riesgo al extraer lectura compartida | Mantener firmas/diagnósticos y regresión pertinente | P1, incluida |
| RF-05, futura integración | Board/Occupant sólo conocen BOX normal | Fragile verifica plantilla vacía y rechazo jugable | Runtime no preserva entidad/resistencia propia | Acordar contrato con Model/Movement antes de adaptación | P0 posterior, bloqueada por contrato/reglas |
| RF-06 y RF-07 | Especiales, Observer/Undo no implementados | No hay prueba productiva | Fuera del dominio | Conservar OQ y límites | Fuera de este incremento |

### Alternativas y elección

| Alternativa | Beneficio | Coste/riesgo | Resultado |
|---|---|---|---|
| Caller usa Files.readString + parse | Una línea, sin API nueva | Omite la lectura acotada; obliga a duplicarla en cada consumidor | Descartada para la entrada de archivo |
| Método `LevelLoader.loadExperimentalFragile(Path)` + `readText` privado | Política única, salida tipada explícita, reutiliza parser/modelo; sin clase nueva | Loader conoce dos salidas, un método público adicional | Seleccionada e implementada |
| Nuevo ExperimentalFragileLoader y lector compartido | Frontera independiente por tipo de producto | Dos clases adicionales o acoplamiento entre loaders para una sola política idéntica | Diferida hasta una variación o consumidor que la justifique |
| Hacer que `load` entregue especiales como Level | Una entrada jugable | Board no representa metadata, riesgo de degradación a BOX o plantilla incompleta | Excluida por contrato y alcance |

**SRP/GRASP:** Loader sigue siendo frontera de filesystem, parser experto en sintaxis y agregado experto en invariantes. Un método privado comparte el conocimiento de bytes/UTF-8; no se duplica validación espacial ni se crea una segunda fuente mutable de estado. El nombre y tipo separan explícitamente la definición experimental de Level. No se introduce jerarquía, interfaz, genéricos ni patrón GoF: no hay variación que lo exija. LevelBuilder permanece intacto; su alternativa directa sigue siendo válida.

**Contrato implementado:** `Path → ExperimentalFragileLevel`, techo de 65.536 bytes antes de decodificar UTF-8 REPORT y después parseo experimental existente. C2 ordinario produce lista vacía; legacy/BOM no activan fallback. Error de E/S/UTF-8: IOException; tamaño o sintaxis: LevelFormatException; Path nulo: NullPointerException. Ningún resultado parcial ni caché. Se mantienen 65.536 caracteres Java del parser e int positivo como límites técnicos previos; no se decide el rango del producto.

**Coste y riesgos:** 12 líneas netas en Loader, diez casos nuevos y un fixture, más documentación/UML afectados. El método crea un parser experimental por llamada, sin estado compartido. Los mensajes existentes no son códigos públicos estables. La lectura no garantiza un snapshot frente a escritura concurrente del mismo archivo; guardar completamente antes de recargar. La plantilla por sí sola no representa el nivel especial completo. No se certifica solución de puzzles ni aceptación del equipo.

**G3/G4:** implementación mínima terminada, sin cambios Movement/Swing/modelos/parser experimental. Compilación estricta y pruebas pertinentes PASS: Loading 40, C2 32, Fragile 41 (31 previos + 10 filesystem), Movement 31 y Desktop headless 16. [Contenido ensayado, comandos y límites](../testing/TEST_STRATEGY.md#incremento-filesystem-fragile--29102026). Al cierre del incremento 5ac2bba del 29/10/2026 todavía no se había abierto PR ni modificado main; es un dato histórico, no un estado Git permanente.

**OQ preservadas:** OQ-06/OQ-08 sintaxis/rango/errores, OQ-07 asociaciones, OQ-03/E04 hielo/rotura, OQ-04/E08 Undo y OQ-11 build. No se edita DECISION_LOG: ésta es una elección técnica experimental autorizada, no un acuerdo TEAM ratificado. Próxima tarea segura: revisar ejemplos y frontera runtime con equipo/Model; un nuevo cambio requiere su propia justificación y regresión.

### Frescura y autocontención de este incremento

| Documento | Clasificación | Motivo |
|---|---|---|
| README; TODO; CURRENT_STATE; REHYDRATION_INDEX | UPDATED | API, progreso técnico, pruebas y próximo gate |
| TXT_LEVELS_EXECUTION_PLAN; FRAGILE_TXT_CONTRACT_PROPOSAL | UPDATED | Brecha/diseño y contrato filesystem implementado, sin ratificación |
| ARCHITECTURE; RESPONSIBILITIES; level-loading-spike-v1.puml; STARUML_STATUS | UPDATED | Dos entradas y colaboración con tipos experimentales reales |
| TRACEABILITY_MATRIX; TEST_STRATEGY | UPDATED | RF→ruta→casos, comandos, entorno y contenido ensayado |
| SOURCE_REGISTRY | UPDATED | Procedencia local aportada frente a nueva ejecución Codex |
| docs/README; FRESHNESS_POLICY; AGENTS; CONTRIBUTING | UNCHANGED_VERIFIED | Navegación y política ya cubren el incremento; sin política nueva |
| REQUIREMENTS; OPEN_QUESTIONS; DECISION_LOG | UNCHANGED_VERIFIED | Ninguna autoridad, regla ni voto cambió; OQ y elegibilidad RF-12 conservadas |
| BIBLIOGRAPHY; GITHUB_REFERENCES | UNCHANGED_VERIFIED | Diseño basado en código y principios ya normalizados; sin obra externa consultada o reutilizada |
| UML Movement/Swing/candidato; contratos Punto 5 | UNCHANGED_VERIFIED | API jugable y responsabilidades de esos módulos intactas |
| Assets/manifiesto/derechos, checkpoints/cápsulas históricas | NOT_APPLICABLE a edición | Sin recursos ni historia alterados; integridad de assets controlada por verificador |

Autocontención: consigna/docente/procedencia en REQUIREMENTS/SOURCE_REGISTRY; TEAM en DECISION_LOG; implementación y límites en README/CURRENT_STATE/arquitectura; deuda en OQ/TODO; principios/patrones en arquitectura y bibliografía; ejecución/QA en README/TEST_STRATEGY; siguiente tarea arriba. No se requiere recuperar chats ni ejecutar el script privado del baseline para continuar. La limpieza Git es operación administrativa independiente, sin commits de producto; su estado operativo se informa en la entrega y se vuelve a consultar en remoto.

## Contrato de integración con trabajo paralelo — 29/10/2026

Checkpoint histórico 529f38d del 29/10/2026: `MISSION = TXT_LEVELS_TEAM_INTEGRATION_CONTRACT`; dominio TXT/niveles sin cambios de gameplay. Gate A: rehidratación, inventario, diff de 5ac2bba, limpieza y remoto contrastados; base `main=24bb3f0d711dd15c79a078b09ec92134a7a600b2`, rama TXT `5ac2bba9a1d495a5da9e1ad998857fbe00613840`, dos commits por delante, sin PR abierto al preflight. Estos SHA son referencias de entrada, no HEAD permanentes. Los worktrees históricos se preservan sin intervención.

Gates B/C: [matriz de compatibilidad, entradas/salidas y preguntas concretas](FRAGILE_TXT_CONTRACT_PROPOSAL.md#traspaso-txt-a-modelmovement--29102026). Fuente nueva: [coordinación entre dominios](../continuity/SOURCE_REGISTRY.md#coordinación-txt-y-fragilehielo--29102026), sin votos ni confirmación de implementación terminada. Contrato TXT del Punto 5 enlazado al traspaso, sin modificar sus otras secciones.

Gate D: `CODE_CHANGE=NOT_JUSTIFIED`. La lectura acotada, preservación de datos y validación existentes cubren el subconjunto implementado por el spike; no apareció un fallo nuevo independiente. Faltan datos del consumidor, representación del hielo y acuerdos OQ, no otra entidad/parser/fábrica. Se descartan por ahora un adaptador con firma inventada, tokens nuevos, IDs runtime generados por TXT y niveles finales de demo: invadirían acuerdos o trabajo paralelo. La opción técnica independiente es describir los accessors existentes y sus límites, con ejemplos enlazados a tests reales.

Gate E y frescura por impacto:

| Documentos | Estado | Motivo |
|---|---|---|
| FRAGILE_TXT_CONTRACT_PROPOSAL; TXT_LEVELS_EXECUTION_PLAN | UPDATED | Traspaso completo, alternativas/preguntas pendientes y límites del incremento |
| SOURCE_REGISTRY; TODO; CURRENT_STATE | UPDATED | Coordinación anónima, responsables por dominio, estado reportado y siguiente integración |
| INTEGRACION_EQUIPO, sólo Contrato TXT; docs/README | UPDATED | Entrada experimental ya existente y navegación al traspaso; sin alterar otros módulos |
| TEST_STRATEGY | UPDATED | Identificación precisa del HEAD probado anteriormente; reutilización de evidencia, sin pruebas nuevas |
| README; REHYDRATION_INDEX; AGENTS; CONTRIBUTING; FRESHNESS_POLICY | UNCHANGED_VERIFIED | APIs, build, política y rutas principales vigentes; el contrato continúa en el documento ya enlazado |
| REQUIREMENTS; OPEN_QUESTIONS; DECISION_LOG | UNCHANGED_VERIFIED | Sin norma/voto nuevo; OQ-03/06/07/08 abiertas y E01/E02 preservadas |
| ARCHITECTURE; RESPONSIBILITIES; UML; TRACEABILITY_MATRIX | UNCHANGED_VERIFIED | Sin estructura, firmas, aceptación funcional ni cobertura nueva; la matriz de traspaso se mantiene en el contrato, sin duplicarla |
| BIBLIOGRAPHY; GITHUB_REFERENCES; fuentes de assets | UNCHANGED_VERIFIED | Sin bibliografía/código ajeno/recursos nuevos ni cambio de derechos |
| Cápsulas/checkpoints históricos | NOT_APPLICABLE a edición | Evidencia fechada preservada |

Se consultó la procedencia de dos afirmaciones operativas antes de actualizarlas: la representación individual futura del contrato Punto 5 se introdujo en `cbc1be7`; la frase R7 preparada del índice documental, en `2dad014`. El estado disponible de 5ac2bba las precisa; no cambió la autoridad académica ni se editan snapshots históricos.

Validación de este hito: revisión campo a campo de ejemplos contra parser/agregado y oráculos existentes; `git diff --check`, verificador documental y prueba de identidad de src/scripts frente a 5ac2bba. JAVA_QA nueva NOT_RUN_DOCS_ONLY; [evidencia previa verificada](../testing/TEST_STRATEGY.md#revisión-del-contrato-para-trabajo-paralelo--29102026). Asistencia de IA declarada, TEAM_ACCEPTANCE=NOT_RECORDED. En ese checkpoint se creó un único commit documental en la rama TXT, sin PR, merge, mensajes a integrantes ni cambios de otros dominios.

Siguiente acción segura: obtener, mediante coordinación humana autorizada, firma/commit del consumidor y archivo del nivel ofrecido; contrastarlos con la matriz antes de definir conversión o ampliar sintaxis. Hielo y runtime Fragile permanecen OPEN_PENDING_TEAM. La cantidad de niveles de demo no se fija por esta misión.

Controles documentales del hito: `git diff --check`, `bash scripts/verify-repo-readonly.sh` y `git diff --exit-code 5ac2bba -- src scripts`, exit 0. Verificador: 137 archivos, 415 enlaces relativos, 43 assets íntegros; ocho Markdown afectados. El ejemplo especial coincide byte a byte con el fixture y las secciones no TXT del contrato Punto 5 permanecen idénticas. No hubo compilación ni ejecución Java nuevas.

## Cierre del alcance autónomo y promoción única — 29/10/2026

**Checkpoint de promoción 38e5a2e**, anterior al cierre semántico siguiente. La autorización condicional de squash relatada en este apartado es histórica, no autorización operativa actual. El PR existente sólo se actualiza en Draft en esta misión.

`AUTONOMOUS_SCOPE=FROZEN_TXT_LEVEL_DEFINITION`: quedan completos la carga experimental desde Path, la validación, diez casos nuevos, fixture y contrato de traspaso de doce dimensiones/ocho preguntas. La integración de mecánicas especiales corresponde a otro gate con los responsables de Model/Movement; no bloquea la publicación del incremento aislado ni habilita resolver OQ por inferencia.

La solicitud de cierre autoriza abrir un único PR Draft de la rama TXT y, sólo después de revisión humana apropiada y SHA/QA verificados, un squash. Consultar Git/GitHub para número/estado/reviews y commit integrado; no hay un SHA de squash predicho en estos documentos. Si falta revisión, mantener Draft y no integrar. TEAM_ACCEPTANCE permanece NOT_RECORDED mientras no exista evidencia explícita; un PASS ni una revisión de IA la sustituyen. No eliminar ramas/worktrees como efecto del merge.

Frescura de promoción: README, TODO, CURRENT_STATE, REHYDRATION_INDEX, este plan, propuesta Fragile, contrato TXT Punto 5, índice documental, arquitectura, trazabilidad y procedencia se ajustaron únicamente para separar capacidades de estados Git transitorios. Los checkpoints de 8b71712/5ac2bba/529f38d conservan sus hechos fechados. AGENTS/CONTRIBUTING/FRESHNESS_POLICY, requisitos/OQ/votos, responsabilidades/UML, pruebas, bibliografía y assets se verificaron sin cambios: no hay nueva regla, API, estructura ni recurso. Fuentes/tests/fixtures/scripts siguen idénticos a 5ac2bba.

Gate de publicación: compilar y ejecutar sobre el HEAD final el bloque filesystem de TEST_STRATEGY (Loading 40, C2 32, Fragile 41, Movement 31, Desktop headless 16), verificador con --require-clean y diff --check. El PR registra SHA, comandos, exit y resultados reales de esa ejecución; las pruebas anteriores conservan su procedencia histórica. La reproducción requiere sólo el clon y un JDK completo, sin logs privados. El alcance público se revisa completo, incluidas licencias pendientes y declaración de IA; no se afirma CI, GUI manual ni gameplay Fragile.

## Cierre semántico del contrato soportado — 29/10/2026

Base inspeccionada: PR #15 OPEN/Draft, HEAD `38e5a2e0ce4dd4688c4eb903dea93a2a3bf54bd6`, cuatro commits/18 rutas respecto de main `24bb3f0d711dd15c79a078b09ec92134a7a600b2`. Son valores del preflight, no HEAD permanentes. Sin drift ni cambios locales ajenos. Identidad de src/scripts respecto de `5ac2bba9a1d495a5da9e1ad998857fbe00613840` comprobada antes de editar. Historial consultado: `e9b2b2a` introdujo el recuento E04 y los bloqueos E04/E08; `8b3ad10` la propuesta sin ratificar; `2dad014` la preparación R7; `5ac2bba` el loader; `529f38d` el traspaso y `38e5a2e` la frescura de promoción.

La [nueva fuente directa](../continuity/SOURCE_REGISTRY.md#cierre-semántico-txt-y-corrección-e04--29102026) permite corregir E04 y adoptar detalles TXT sin votar de nuevo. [D-020](../decisions/DECISION_LOG.md#d-020--decisión-del-responsable-txt--29102026) registra fecha, rol, autoridad, alternativas, coste, compatibilidad, consecuencias y casos verificables. No se fabrica código: `CODE_CHANGE=NOT_JUSTIFIED / DOCS_ONLY_AUTHORIZED`. Elección, implementación y dependencias quedan separadas en la trazabilidad y en la matriz de traspaso.

| Documentos revisados | Clasificación | Impacto / motivo |
|---|---|---|
| DECISION_LOG; OPEN_QUESTIONS | UPDATED | E04 corregida con antecedente, E08 decidida; OQ-06 y subconjunto OQ-08 adoptados por MODULE_OWNER |
| FRAGILE_TXT_CONTRACT_PROPOSAL | UPDATED | Gramática soportada, errores y límites adoptados; preguntas al consumidor sin reabrir rango ni regla E04 |
| REQUIREMENTS | UPDATED | Referencias y aceptación coherentes con decisiones; contenido oficial normalizado preservado |
| SOURCE_REGISTRY | UPDATED | Fuente directa anónima, disponibilidad, corrección del informante y autoridad delegada |
| README; TODO; docs/README; CURRENT_STATE; REHYDRATION_INDEX | UPDATED | Capacidad actual, dependencias reales y navegación autocontenida |
| TXT_LEVELS_EXECUTION_PLAN | UPDATED | Checkpoints explícitamente históricos, cierre actual y esta revisión por impacto |
| ARCHITECTURE; STARUML_STATUS | UPDATED | Alcance contractual y anotaciones históricas distinguidos; sin estructura o UML funcional nuevos |
| INTEGRACION_EQUIPO, sólo frontera TXT | UPDATED | Esquema inicial adoptado frente a consumidor runtime pendiente; demás secciones intactas |
| TRACEABILITY_MATRIX; TEST_STRATEGY | UPDATED | Autoridad→estado→implementación→casos; QA previa por SHA y blobs idénticos, sin nueva ejecución Java |
| AGENTS; CONTRIBUTING; FRESHNESS_POLICY | UNCHANGED_VERIFIED, política preservada | Lectura completa; prohibición expresa de edición. La frase de AGENTS sobre detalles TXT no ratificados es anterior y queda supersedida de forma acotada por D-020, aclarada en REHYDRATION_INDEX. No cambia la política de revisión ni se atribuye aceptación TEAM |
| RESPONSIBILITIES | UNCHANGED_VERIFIED | Roles, tipos y separación TXT/Model/Swing no cambian |
| BIBLIOGRAPHY; GITHUB_REFERENCES | UNCHANGED_VERIFIED | Sin nuevas obras consultadas, investigación/código ajeno ni licencias incorporadas |
| Fuentes/tests/fixtures/runners; diagramas .puml | UNCHANGED_VERIFIED | Bytes preservados por alcance; comentarios de gramática no ratificada conservan el corte previo y se contextualizan en índice/STARUML_STATUS. No se atribuye implementación especial |
| Manifiesto/derechos y assets | UNCHANGED_VERIFIED | Sin recursos ni derechos nuevos; la validación de integridad no prueba CC0 |
| Cápsulas/checkpoints históricos | NOT_APPLICABLE a edición | Evidencia fechada inmutable, sin instrucciones operativas heredadas |

Validación propia de este cierre: diff --check, verificador de repositorio, enlaces/anclas, allowlist de Markdown y consistencia semántica; resultados/exit en el informe y cuerpo del PR. Java no se repite: la ejecución previa sobre 38e5a2e es 160 PASS y los blobs de src/scripts son idénticos a 5ac2bba; [reproducción y límites](../testing/TEST_STRATEGY.md#cierre-semántico-documental--29102026). Revisión de ejemplos contra código/tests existentes, sin nuevos casos ni afirmación de CI.

Autocontención comprobable desde AGENTS→índice→estado→requisitos/fuentes→decisiones/OQ→contrato→pruebas: consigna y aclaraciones con procedencia, TEAM frente a MODULE_OWNER, APIs presentes, definición no jugable, extensiones/runtime abiertos, principios y alternativas, comandos/QA por contenido y siguiente tarea. Los antecedentes 4/2 y R7 preparada no son estado vigente; no hace falta un chat privado, un script temporal ni un PR documental después del squash para reconstruir estas conclusiones. Los estados Git se vuelven a consultar; no se predice SHA de integración.

Siguiente acción segura: revisión humana del diff funcional y contrato del PR #15, seguida de contraste con la firma/commit real del consumidor Fragile/hielo. Publicación de este cierre: como máximo un commit documental y push normal a la misma rama; actualizar el PR existente, conservar Draft. **Ready, aprobación propia y merge/squash no autorizados en este hito.** OQ-07 runtime y OQ-11 global abiertas; no se presume demo ni trabajo del compañero terminado.
