# Estado vivo — Los Force Push

```text
AS_OF = 2026-10-23
REPOSITORY = smatrauade/Los-Force-Push
PRIMARY_DOMAIN = SOKOBAN_TEAM_MAJORITY_DECISIONS_DOCUMENTATION
IMPLEMENTATION_STATUS = IMPLEMENTED_EXPERIMENTAL
TEST_STATUS = TESTED
TEAM_ACCEPTANCE = NOT_RECORDED
GAME_RULE_DECISIONS = MAJORITY_ACCEPTED_PARTIAL
MOVEMENT_INTEGRATION_EVIDENCE = GIT_HISTORY_AND_PULL_REQUEST
EXPERIMENT_ORIGIN_BRANCH = feat/swing-playable-spike-v1
EXPERIMENT_BASE = be490aecb2bbcb92b863778a63499c587313146c
ORIGINAL_SWING_COMMIT = 1303a33748e129f3d8a55ab2b1799518aaa8df5d
RECOVERY_BASE = ee83f9ebd30ee1b553c555c89d9ea453131d6678
VALIDATION_ITERATION = WINDOWS_PR5_NATIVE_QA_2026_10_21
RECOVERED_SWING_BASELINE = 99b81c5aa04dd6c6a19cc72f42c766ce53551bbd
DESKTOP_INTEGRATION_TESTS = 16_PASS_0_FAIL_LINUX_JDK17_RELEASE17
INTERACTIVE_GUI_WINDOWS = PR5_HEAD_PASS_REPORTED_WITH_SCREENSHOTS
INTERACTIVE_GUI_LINUX = NOT_RUN
SWING_BASE = be490aecb2bbcb92b863778a63499c587313146c
LEVEL_LOADING_TESTS = 40_PASS_0_FAIL_LINUX_JDK17_RELEASE17
MOVEMENT_TESTS = 31_PASS_0_FAIL_LINUX_JDK17_RELEASE17
LINUX_CORE_HISTORICAL_TEST = PASS
LINUX_SWING_TEST = PASS_HEADLESS_R3_CLEAN_SNAPSHOT
WINDOWS_NATIVE_TEST = PR5_HEAD_NATIVE_PASS_2026_10_21
RUNNER_VALIDATION_ITERATION = LINUX_CLEAN_SNAPSHOT_R3
WINDOWS_RUNNER_HISTORICAL_VALIDATION = RUNNER_R3
MACOS_NATIVE_TEST = NOT_RUN
DEFAULT_BRANCH = main
BOOTSTRAP_STATUS = INTEGRATED
PRODUCTION_IMPLEMENTATION = NONE
JAVA_TARGET = OPEN
BUILD_TOOL = OPEN
TXT_FORMAT = MAJORITY_ACCEPTED_LAYOUT_GRAMMAR_PENDING
PR5_STATUS = MERGED
PR5_MERGE_COMMIT = 54c03d4520dfc542c125686e5446687dc07d85db
NEXT_GATE = SOKOBAN_TXT_GRAMMAR_SPECIFICATION
WINDOWS_PR5_TESTED_HEAD = 0670a4d5137f0e9f1db221866a02c29987024847
```

## Checkpoint mayoritario del equipo — 2026-10-23

[DECISION_LOG](../decisions/DECISION_LOG.md#ratificación-por-mayoría--2026-10-23) registra E01–E11 completos, método de mayoría y anomalías; [OPEN_QUESTIONS](../requirements/OPEN_QUESTIONS.md) conserva OQ-01…OQ-11 con dimensiones aceptadas y pendientes. Fuente: transcripción del solicitante, capturas privadas externas no verificadas independientemente aquí. Se documentan acuerdos de reglas, no aceptación del código ni del diseño. E04/E08 mantienen CONFLICT_REVIEW_REQUIRED, fórmula/gramática/segunda adicional y Java/build siguen pendientes. Pistas no está aprobada.

Revisar este Draft documental por otra persona; el siguiente dominio propuesto es especificar TXT y revisar compatibilidad de reglas especiales, sin implementación ni otra ronda general de encuestas. La revisión humana del código sigue pendiente. Base Git observada de esta tarea: `2c2c2e4c6180ca0639e511c480ca5b93cd4e4a01`, merge de PR #6; no es un HEAD permanente. No hubo nuevas pruebas Java ni cambios de fuentes/UML.

## Estado posterior al merge de PR #5

El [PR #5](https://github.com/smatrauade/Los-Force-Push/pull/5) está MERGED/CLOSED, integrado en main el `2026-10-21T23:38:50Z` mediante `54c03d4520dfc542c125686e5446687dc07d85db`. Ésa es la base observada de este mantenimiento documental, no un HEAD permanente: obtener `git rev-parse HEAD` y contrastar los commits posteriores y GitHub antes de actuar. No volver a fusionar ni aprobar el propio PR. La integración técnica por decisión del autor no demuestra revisión independiente ni ratificación del equipo.

Los cuatro commits del PR permanecen en el historial: `99b81c5aa04dd6c6a19cc72f42c766ce53551bbd` (recuperación), `422886d653c6862fcb061f5ceddc1e1830b0cafe` (consistencia documental), `0670a4d5137f0e9f1db221866a02c29987024847` (continuidad de publicación, código probado en Windows) y `a722d4e5229e4151e9ca73971e1987e05bc1dac7` (registro documental de QA Windows). Main contiene TXT externo, Board inmutable, movimiento/empuje normal y presentación Swing experimental; faltan las mecánicas, assets y entregables listados en TODO.

### Integraciones verificadas

| PR | Alcance | Commit de merge |
|---|---|---|
| [#1](https://github.com/smatrauade/Los-Force-Push/pull/1) | Bootstrap | `ff6fbf170bd9782fb703e1ab4fa1e811ec0782ed` |
| [#2](https://github.com/smatrauade/Los-Force-Push/pull/2) | Reconciliación inicial | `1943053ec520ff3d8be0aecb21c2f417f3126380` |
| [#3](https://github.com/smatrauade/Los-Force-Push/pull/3) | Loading y Movement | `e8f76cec23de019cd244b6d089bd6de3a24251a3` |
| [#4](https://github.com/smatrauade/Los-Force-Push/pull/4) | Aislamiento de runners core | `ee83f9ebd30ee1b553c555c89d9ea453131d6678` |
| [#5](https://github.com/smatrauade/Los-Force-Push/pull/5) | Swing y QA Windows | `54c03d4520dfc542c125686e5446687dc07d85db` |
| [#6](https://github.com/smatrauade/Los-Force-Push/pull/6) | Rehidratación postmerge | `2c2c2e4c6180ca0639e511c480ca5b93cd4e4a01` |

Los seis PR históricos están fusionados; esos merges no ratifican reglas ni cambian `TEAM_ACCEPTANCE = NOT_RECORDED` del código. Los acuerdos posteriores del 23/10 tienen fuente TEAM independiente, en el checkpoint inicial; las elecciones de patrones siguen sin ratificar. Las ramas históricas pueden retirarse una vez probada su ascendencia; el historial de integración se localiza por PR y commit.

## Checkpoint posterior a R5 — QA Windows nativa (2026-10-21)

Sobre `0670a4d5137f0e9f1db221866a02c29987024847` se reportó PASS nativo Windows: 18/18 escenarios CLI PS5.1/7, 40 Loading + 31 Movement como casos Java únicos transitivos, 16/16 Swing headless y GUI interactiva real con capturas, TXT válido/inválido y estado preservado. [Procedencia y límites](checkpoints/WINDOWS_PR5_NATIVE_QA_2026-10-21.md) · [JSON](checkpoints/WINDOWS_PR5_NATIVE_QA_2026-10-21.json). GitHub confirmó al corte previo al merge `main=ee83f9ebd30ee1b553c555c89d9ea453131d6678` y PR #5 OPEN/Draft; comprobar el estado posterior en Git. Los Windows históricos/NOT_RUN mencionados en párrafos anteriores/posteriores son observaciones fechadas de iteraciones diferentes. La documentación posterior **no cambia el SHA del código probado** ni implica aprobación grupal.

## Estado comprobado y cómo continuar

En la publicación histórica R4 del 21/10 el PR #5 quedó OPEN/Draft sobre main `ee83f9ebd30ee1b553c555c89d9ea453131d6678`, con los dos primeros commits enumerados arriba. R5 añadió continuidad documental y posteriormente se registró la QA Windows; el merge posterior está identificado al inicio de este documento. La revisión humana del código integrado sigue pendiente; para especificación de reglas rige el checkpoint del 23/10, no una preparación ni integración de un nuevo PR Swing.

Revisión técnica R3 del 21/10 sobre `99b81c5aa04dd6c6a19cc72f42c766ce53551bbd`: MVC y colaboraciones de los UML implementados contrastados con el código; cuatro PlantUML comprobados/renderizados, candidato integral separado y StarUML pendiente. Una exportación limpia de ese commit reprodujo 40 Loading + 31 Movement + 16 desktop headless PASS, JDK 17.0.20.1/release 17 y C.utf8, sin bundle ni QA externos. Las correcciones R3 son sólo documentales: se conserva la evidencia por identidad de los blobs ejecutables; [detalle](../testing/TEST_STRATEGY.md#revisión-de-consistencia-swing-r3--2026-10-21). Al cierre de aquella revisión R3 la rama Swing todavía era local; su publicación posterior mediante R4 se registra arriba. En aquel corte main contenía #3/#4 y aún no Swing; el merge posterior de #5 se registra al inicio. Revisión humana del equipo, GUI interactiva Linux y macOS siguen pendientes.

Recuperación R2 del 2026-10-21: repositorio público verificado mediante API; [PR #3](https://github.com/smatrauade/Los-Force-Push/pull/3) MERGED el 20/10, merge `e8f76cec23de019cd244b6d089bd6de3a24251a3`; [PR #4](https://github.com/smatrauade/Los-Force-Push/pull/4) MERGED el 21/10, merge `ee83f9ebd30ee1b553c555c89d9ea453131d6678`. La nueva base incluye ambos, y se incorpora únicamente el delta Swing original `1303a33748e129f3d8a55ab2b1799518aaa8df5d`, cuyo padre es `be490aecb2bbcb92b863778a63499c587313146c`. Se conservan los runners con selección por paquetes del #4, las fuentes/tests/fixtures del core y los bytes Java/Swing originales. Linux R2: 40 Loading, 31 Movement y 16 desktop headless PASS, cero FAIL y salida 0 con OpenJDK 17.0.20.1/release 17 y C.utf8. GUI interactiva Linux y macOS NOT_RUN. No hay publicación ni aceptación académica implícita. La validación Linux R2 se registra separadamente de la evidencia histórica Windows en la [guía](../experiments/SWING_VERTICAL_SLICE_V1.md#recuperación-linux-r2--2026-10-21).

El preflight R3A del 2026-10-14, vía Git y GitHub CLI como LucasCap12, confirmó el repositorio privado correcto, permisos de lectura/escritura, rama predeterminada `main`, checkout limpio y coincidencia con `origin/main` (divergencia 0/0). En aquel preflight anterior al PR #2 no se encontraron commits posteriores al merge del bootstrap ni otro PR de reconciliación; esa observación se conserva como historia fechada.

**Evento histórico confirmado:** el [PR #1](https://github.com/smatrauade/Los-Force-Push/pull/1) se fusionó el `2026-10-08T20:00:25Z` mediante el commit `ff6fbf170bd9782fb703e1ab4fa1e811ec0782ed`. Ese SHA identifica el merge histórico, no un HEAD permanente. El [preflight inicial](BOOTSTRAP_PREFLIGHT.md) describe el estado anterior al PR y se conserva como evidencia fechada.

El bootstrap documental está integrado en `main`: documentación, UML candidato, script de verificación y estructura `src/main/java`, `src/main/resources` y `src/test/java` con placeholders. La base histórica del merge PR #2 (`1943053ec520ff3d8be0aecb21c2f417f3126380`) no contenía clases Java ni configuración de build. Esta rama incorpora fuentes y tests experimentales, sin ratificación productiva. El merge del bootstrap integró documentación y estructura; no constituye ratificación grupal de las propuestas arquitectónicas.

La fuente del estado actual es Git contrastado con la documentación viva: inspeccionar `git rev-parse HEAD`, `git log`, el árbol y los PRs antes de continuar. Las cápsulas y los informes de preflight anteriores son historia, no sustituyen esa verificación. Las revisiones exactas evaluadas se identifican en el informe de revisión/entrega; evitar autorreferenciar el SHA del commit que contiene este documento.

Requisitos normalizados y aclaraciones están autocontenidos. PDF/libros/materiales originales no están disponibles aquí; SOURCE_REGISTRY distingue bytes presentes de relato histórico. UML v0.5 y cápsula anterior sí estaban en el ZIP y se preservaron byte a byte.

## Baseline experimental de Level Loading posterior al PR #2

El PR #2 se confirmó MERGED el `2026-10-14T17:45:53Z`, commit histórico `1943053ec520ff3d8be0aecb21c2f417f3126380`. Esta es la base histórica del incremento de carga, no la base inmediata del movimiento ni un HEAD permanente. La sincronización local autorizada fue FF-only; no se fusionó ningún PR desde esta ejecución.

El incremento histórico de carga de `feat/level-loading-spike-v1`, versionado localmente en `44b3c63b437465b5f54c1f0f5ca5cbadb5def0e5` y heredado por este snapshot: [TXT candidato y comparación](../experiments/LEVEL_LOADING_R1.md), fuentes bajo `src/main/java/ar/edu/uade/sokoban/level`, fixtures y tests bajo `src/test`, y [UML del spike](../architecture/uml/level-loading-spike-v1.puml). Implementó el subconjunto paredes/piso/destinos/jugador/cajas normales, incluidas superposiciones sobre destinos. Aquel incremento de carga excluía movimiento, cajas especiales, Undo, score, victoria, GUI y audio; el movimiento básico presente se describe a continuación.

Evidencia histórica de Level Loading R3 del 2026-10-14: `scripts/test-level-loading.sh`, compilación/ejecución real con JDK 17.0.20.1 y JDK 26.0.2, opciones estrictas y sólo java.base, **38 PASS / 0 FAIL en cada JDK**. Los tests 25/38 detectaron la desactivación temporal del límite de bytes en una copia descartable (dos fallos esperados, fuente original preservada). PlantUML 1.2020.02 y Graphviz 14.1.2 comprobaron y renderizaron el UML experimental. Las fuentes, fixtures y comandos de esta rama permiten reproducir las pruebas; la base histórica precede a este incremento. Revisión del equipo pendiente; D-011 PREFERRED, OQ-08/OQ-11 OPEN.

Evidencia histórica de Level Loading R4 del 2026-10-14: **40 PASS / 0 FAIL en cada JDK** sobre Linux, incluidas rutas con espacios/Unicode, recarga externa y diagnósticos con Locale.ROOT. Runner Unix compatible por diseño con Bash 3.2/GNU/BSD y runner Windows PowerShell 5.1/7 revisado estáticamente. Copia temporal con checkout/TMPDIR de nombres Unicode y espacios: PASS con ambos JDK; Java 17 también con charset predeterminado US-ASCII. Espacios de niveles protegidos en EditorConfig y atributos LF acotados. Windows/macOS nativos NOT_RUN: verificarlos antes de afirmar portabilidad nativa completa. Código/contrato/modelo siguen experimentales; esta evidencia no ratifica D-011 ni OQ-08/OQ-11.

## Contenido del núcleo experimental de movimiento

Este incremento contiene el núcleo experimental de movimiento cardinal y empuje de una caja normal, con pruebas reproducibles, sobre la base histórica de carga citada. [Informe histórico R1 y contrato de transición](../experiments/MOVEMENT_R1.md); [UML acotado](../architecture/uml/movement-core-spike-v1.puml). Direction, MovementEngine sin estado y MoveResult implementan `Board + Direction → MoveResult(kind, Board)`. Las clases, fixtures y los 40 tests de level se conservan intactos. MOVED/PUSHED crean un Board válido nuevo; BLOCKED reutiliza la misma instancia de entrada. No se agrega un GameState redundante, sesión, contadores ni historial.

### Evidencia separada por iteración — 2026-10-14

| Iteración de movimiento | Evidencia y alcance |
|---|---|
| R1, informe histórico | Reporta 40/40 carga y 31/31 movimiento PASS en Linux/JDK 17.0.20.1 y 26.0.2; copia Unicode/espacios y charset US-ASCII con 17; mutaciones y opciones inválidas detectadas |
| R2, revisión independiente realizada | Reprodujo 40/40 y 31/31 en Java 17; Java 26 NOT_RUN. No encontró defectos funcionales bloqueantes. Reprodujo 11 fallos al desactivar empujes sólo en copia temporal |
| R2, auditoría adicional temporal | Oráculo independiente: 150.000 transiciones satisfactorias sobre 37.500 tableros 2×3; no incorporado a las suites versionadas ni sumado a sus 31 casos |
| R3, validación del incremento | Reprodujo 40/40 carga y 31/31 movimiento en Linux/JDK 17.0.20.1 y 26.0.2, salida 0, release 17, UTF-8, -Xlint:all, -Werror y sólo java.base; temporales del runner eliminados |
| R3, UML | Sintaxis y renderizado comprobados con PlantUML; correspondencia con tipos, firma y contrato revisada |

Los 31 casos incluyen 2.664 transiciones dentro del caso 30, integración TXT, destinos y preservación de estados anteriores. El caso 30 comprueba invariantes, pero no determina independientemente el outcome correcto de cada intento; los casos golden sí comparan Kind y tableros esperados. Ambos runners compilan una vez y admiten la suite adicional opcional; aquella revisión estática para Bash 3.2 y PowerShell 5.1/7 no constituía ejecución nativa Windows/macOS. La validación posterior del runner Windows R3 está en [TEST_STRATEGY](../testing/TEST_STRATEGY.md#contrato-cli-powershell--runner-r3): PS5.1/PS7, JDK 21, 40 Loading y 31 Movement, contrato CLI, rutas Unicode y cleanup. macOS nativo sigue NOT_RUN.

RF-03/RF-04 tienen cobertura PARTIAL: jugador y caja normal sobre piso/destino, paredes y bordes. Aquel núcleo excluía flechas/UI; la integración Swing actual se describe abajo. Hielo, cajas especiales, cerrojos, Undo, Restart, victoria/progresión, score, audio y extras siguen pendientes. IMPLEMENTED_EXPERIMENTAL y TESTED describen capacidades y evidencia; TEAM_ACCEPTED requiere ratificación explícita. La integración efectiva se comprueba mediante el historial Git y el Pull Request correspondiente, independientemente de la cobertura funcional. Publicar o fusionar el mismo snapshot no ratifica por sí solo la arquitectura ni cierra OQ-08/OQ-11/D-011.

## Integración Swing experimental — 2026-10-15

Base be490aecb2bbcb92b863778a63499c587313146c; fuentes separadas en src/desktop/java, tests en src/desktop-test/java y TXT real en src/desktop/resources/levels. GameController reutiliza LevelLoader/MovementEngine; GameView/BoardPanel leen el mismo Board inmutable. Flechas por key bindings, carga externa, empuje normal y contadores demostrativos; sin nuevo motor ni cambios en el core.

[Guía autocontenida, comandos y límites](../experiments/SWING_VERTICAL_SLICE_V1.md), [UML real del subconjunto](../architecture/uml/swing-playable-spike-v1.puml). Windows nativo, Temurin 21/release 17: 40 Loading y 31 Movement PASS, 16 pruebas de integración PASS; interacción gráfica observada con movimiento, empujes, destino y pared bloqueada. En aquel incremento del 15/10 no se ejecutó Swing Linux/macOS; la validación headless Linux R2 se registra separadamente al inicio de este estado y en la guía. La carga local acotada es síncrona en el EDT; archivos remotos/lentos pueden demorar la ventana. Graphics2D es placeholder y no cumple aún imágenes definitivas. No hay victoria, Undo/Restart, score, especiales ni audio.

Las evidencias históricas del core y su runner no sustituyen los tests desktop. La recuperación Linux R2 descrita al inicio prueba el incremento sin pantalla; no certifica GUI interactiva ni macOS. La aceptación del código no está registrada; el estado actual de cada dimensión OQ está en OPEN_QUESTIONS y no se infiere de los tests históricos. La revisión independiente R3 está realizada y R4 publicó el Draft PR #5 contra main integrado. En ese corte se proponía revisión posterior y triage; el checkpoint del 23/10 registra los acuerdos y el siguiente gate de especificación. GUI interactiva Linux y macOS siguen pendientes; cualquier cambio funcional requiere otra iteración autorizada.

## Diseño, evidencia y decisiones

Terrain/Occupant, polimorfismo de cajas, Builder, Memento, Observer y Strategy son **PREFERRED**, no ACCEPTED. Herencia/composición sigue abierta. SoundManager Singleton es propuesto; Adapter no se justifica en baseline, Decorator depende de futuras funcionalidades. El diseño SessionMetrics sigue TEAM-PROPOSAL; los aspectos de comportamiento E09/E10 se ratifican sólo según [DECISION_LOG](../decisions/DECISION_LOG.md#ratificación-por-mayoría--2026-10-23), sin fórmula ni efectos de Restart elegidos.

Los spikes A/B reportaron 8/8 y 12/12 con Java 21 y sin warnings históricamente. Son `HISTORICAL_EXTERNAL_EVIDENCE`; `REPRODUCIBILITY_IN_CURRENT_REPOSITORY = NOT_VERIFIED`. **No se ejecutaron tests Java en R3A.** En el preflight se ejecutaron `bash scripts/verify-repo-readonly.sh --require-clean` (PASS: 32 archivos y 49 enlaces relativos), `git fsck --full --no-progress` y `git diff --check` (PASS). Son verificaciones documentales y de Git; no validan gameplay ni la semántica del UML mediante un compilador.

OQ-01…OQ-11 conservan sus IDs, con acuerdos por alcance y deuda residual en [OPEN_QUESTIONS](../requirements/OPEN_QUESTIONS.md). No reabrir los acuerdos por falta de gramática, fórmula o bordes; E04/E08 sí requieren compatibilidad. Arquitectura y audio mantienen sus estados previos.

## Riesgos y mitigación

| Riesgo | Acción siguiente |
|---|---|
| TXT hardcodeado o formato tácito | Contrato ratificado y prueba con TXT escrito/modificado por otra persona |
| Patrones ornamentales / interfaces ceremoniales | Comparar alternativa simple y justificar fuerzas/colaboraciones |
| Snapshot alias o reversión incompleta | Pruebas de resistencia/muros/contadores y aislamiento |
| Métricas/score incompletos | Aplicar alcance E09/E10; especificar fórmula y reinicios, sin ratificar diseño por el voto |
| Controller monolítico, reglas Swing o datos duplicados | Roles explícitos y core testeable sin GUI |
| UML/cápsula se confunden con estado ejecutado | Contrastar HEAD/vivos; pruebas históricas etiquetadas |
| Licencia no cumple dominio público | Ledger por recurso; no incorporar hasta verificar |
| IA no comprendida / entrega incompleta | Revisión humana, declaración y defensa; checklist TODO |
| Visibilidad no sustituye entregables | Repo público verificado 21/10; completar guía del producto, StarUML, informe y video |

## Próxima iteración segura

Los resultados técnicos R2/R3 y Windows conservan su SHA y alcance, sin pruebas nuevas aquí. Tras revisar el Draft de decisiones, especificar el contrato TXT en `SOKOBAN_TXT_GRAMMAR_SPECIFICATION` y contrastar E04/E08 y combinaciones especiales antes de implementar. El inventario canónico distingue dimensiones aceptadas de las pendientes; no se propone otra encuesta general. Builder sigue PREFERRED, la gramática OQ-08 y Java/build OQ-11 siguen pendientes. Revisión del código, GUI Linux/macOS y entregables restantes no se cierran por esta votación.

Resolver las preguntas que afecten cada mecánica antes de implementarla. Obtener los bytes de los spikes históricos si se desean reutilizar, sin tratarlos como código productivo auditado. El spike no convierte la carga en una funcionalidad productiva completa ni autoriza por sí solo su integración o una ampliación de alcance.

No se necesita ningún chat anterior para planificar esa continuación: leer AGENTS → requisitos/preguntas → decisiones → arquitectura/responsabilidades/UML → trazabilidad/pruebas → TODO/frescura. Las cápsulas se usan sólo para profundizar historia, y sus contratos antiguos no bloquean ni autorizan la tarea actual.
