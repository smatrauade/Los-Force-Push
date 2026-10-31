# Matriz viva de trazabilidad

## Cobertura integrada tras PR 8 y PR 9 — 2026-10-28

[CURRENT_STATE](../continuity/CURRENT_STATE.md) identifica los merges; [TEST_STRATEGY](../testing/TEST_STRATEGY.md#qa-combinado-pr-8-y-pr-9--2026-10-28) registra el árbol probado y la QA anterior a este mantenimiento. La tabla de plan/candidato y las evidencias fechadas que siguen no niegan las capacidades actuales. Sus OQ abiertas describen aquellos cortes; el cierre posterior del subconjunto soportado se registra al final, sin reatribuir QA.

| Req | Implementación integrada | Evidencia / límite |
|---|---|---|
| RF-01 / RF-02 | LevelLoader/LevelParser/C2LevelParser → LevelBuilder → Level/Board | Legacy 40 y C2 32 PASS en R1; siete combinaciones, rechazo de atributos/especiales; OQ-06/07/08 pendientes |
| RF-03 / RF-04 | MovementEngine y ArrowBindings/GameController | Movement 31, Desktop 16 y UI Smoke 17 PASS; sin hielo ni cajas especiales |
| RF-10 | GameWindow/GamePresentation, hooks GameView y UiCommands | HUD y navegación implementados; Undo/Restart/persistencia/Next sólo callbacks opcionales deshabilitados sin servicio |
| RF-11 | BoardPanel, PixelAssets, PixelTheme y SoundManager/Effect | VisualAssets 44 y manifiesto 43 PASS; 29 sprites base, dos opcionales, 12 WAV; derechos/QA audible pendientes |
| RF-08 / RF-09 | LevelResult y showVictory como presentación | Pantalla probada con DTO demostrativo; sin detección de victoria, score calculado ni progresión |
| RT-01 / RT-03 | Runners core/desktop separados, UML implementado actualizado | Release 17 provisional, java.base/java.desktop; Java/build y ratificación pendientes |
| RT-02 / RT-04 | Índices, README, contratos de módulos, manifiesto y declaración de IA | Clon autocontenido para el subconjunto; StarUML .mdj, entrega y revisión del equipo pendientes |

Las suites anteriores son evidencia R1; R2 ejecuta la regresión del verificador y controles documentales. No se suman repeticiones CLI/C2 como nuevos casos Java. Los controles de integridad no convierten RF-11 ni otros requisitos completos en DONE.


Checkpoint histórico 2026-10-23 (mapa de decisiones TEAM; evidencia ejecutada anterior preservada). **La aceptación productiva completa está pendiente; este snapshot contiene implementaciones y pruebas experimentales con cobertura parcial, detalladas abajo.** A/B significan sólo evidencia histórica externa no reproducida; “plan” refiere a TEST_STRATEGY, no tests ejecutados. El modelo integral es candidato; los UML acotados describen los tipos experimentales reales.

| Req | Responsabilidad / UML candidato | Principio/patrón candidato | Prueba prevista / historial | Decisiones abiertas |
|---|---|---|---|---|
| RF-01 | Board, Cell, Terrain, Occupant | Expert/composición | Representación; A reportado | D-004/006 |
| RF-02 | LevelLoader, Repository, Parser, Builder, Level | Builder PREFERRED | TXT externo modificado, errores; A reportado | OQ-06/07/08, D-011 |
| RF-03 | GameSession, MovementEngine, Board, MoveResult | Expert/bajo acoplamiento | Libre/bloqueos/una caja; B reportado | Regla base; límites de interacción |
| RF-04 | Box/NormalBox, Ice, motor | Polimorfismo | Empuje/hielo; B reportado | OQ-02 |
| RF-05 | FragileBox, estado restaurable | LSP/polimorfismo | Decremento/rotura/Undo; A/B reportados | OQ-03/06 |
| RF-06 | KeyBox, LockTile, LockObserver, OpenableWall | Observer PREFERRED | Activación/normal no activa; B reportado | OQ-07 |
| RF-07 | Session, GameMemento, UndoManager, SessionMetrics propuesto | Memento PREFERRED | 15/5/3, snapshots; A reportado | OQ-04/05, D-008 |
| RF-08 | Session.isWon/finishLevel, LevelCatalog, Controller | Expert/Controller | Victoria/progreso/final catálogo; plan | OQ-01 |
| RF-09 | ScoreStrategy, LevelStats, LevelResult | Strategy PREFERRED | Score tabulado; plan | OQ-09 |
| RF-10 | GameView, GameFrame/paneles, HudData, GameActions | MVC/DTO | HUD/input/Undo/Restart; plan | OQ-04/05 para métricas |
| RF-11 | BoardPanel, SpriteProvider, AudioService | MVC/fronteras justificadas | Cobertura imágenes/SFX y licencias; plan | D-016, selección assets |
| RF-12 | No hay clases definitivas | Patrón TBD | Dos extras; plan | OQ-10 |
| RT-01 | Estructura src, configuración futura | Restricción stack | Build/ejecución clon limpio; plan | OQ-11 |
| RT-02 | README, docs, StarUML/video/informe futuros | Proceso | Lista entrega; plan | Repo público verificado 21/10; resto de entrega pendiente |
| RT-03 | Responsabilidades/decisiones/trazabilidad | SOLID/GRASP/MVC/GoF | Revisión y defensa individual; plan | Ratificación arquitectura |
| RT-04 | Revisión humana/declaración IA | Trazabilidad | Informe y defensa; plan | Revisión del equipo |

[UML](../architecture/uml/sokoban-candidate.puml), [responsabilidades](../architecture/RESPONSIBILITIES.md), [plan de pruebas](../testing/TEST_STRATEGY.md), [historial](../testing/HISTORICAL_EVIDENCE.md) y [preguntas](../requirements/OPEN_QUESTIONS.md) permiten continuar sin otro chat.

Para marcar DONE: requisito → decisión/autoridad → responsabilidad → implementación real (ruta/commit) → test ejecutado (comando/resultado) → documentación. Completar rutas sólo cuando existan; no inventarlas en este bootstrap.

## Evidencia local R1, sin DONE productivo

Actualización local 24/10, RF-02 **PARTIAL**: [LevelParser](../../src/main/java/ar/edu/uade/sokoban/level/LevelParser.java) delega sólo el prefijo reservado a [C2LevelParser](../../src/main/java/ar/edu/uade/sokoban/level/C2LevelParser.java). [C2LevelLoadingTest](../../src/test/java/ar/edu/uade/sokoban/level/C2LevelLoadingTest.java), 32 casos ejecutados, cubre equivalencia de las siete combinaciones, estructura/cuotas, errores, recarga y rechazo de especiales/atributos sin publicación parcial. [Evidencia y comandos](../testing/TEST_STRATEGY.md#spike-experimental-c2-l7--2026-10-24). Es un spike `EXPERIMENTAL_NOT_RATIFIED`, integrado posteriormente por PR #8; no implementa E02 ni cierra OQ-06/07/08/11. En el corte del 24/10 los UML no incorporaban el helper; la revision integrada del 28/10 lo incluye en el diagrama de Loading, sin ratificar el candidato.

| Requisito | Implementación experimental | Evidencia actual / límite |
|---|---|---|
| RF-01 | Position/Terrain/Occupant/Cell/Board en `src/main/java/ar/edu/uade/sokoban/level` | Pared/piso/destino/jugador/caja normal; entidades especiales pendientes |
| RF-02 | LevelLoader/LevelParser/LevelBuilder/Level | TXT reales, distintos/modificados; errores, determinismo y atomicidad; OQ-08 OPEN |
| RT-01 | `scripts/test-level-loading.sh` / `test-level-loading.ps1` | Compilación/ejecución real con JDK 17 y 26; release 17 y sólo java.base; Linux nativo PASS, Windows/macOS NOT_RUN; OQ-11 OPEN |

[40 casos R4 y comandos](../experiments/LEVEL_LOADING_R1.md), [tests reales](../../src/test/java/ar/edu/uade/sokoban/level/LevelLoadingTest.java). Evidencia experimental R4 sobre las fuentes referenciadas, con base histórica del merge PR #2; no es la reproducción A/B histórica ni aceptación final de requisitos. Los demás RF permanecen pendientes.

## Movimiento básico — trazabilidad PARTIAL, 2026-10-14

| Requisito | Implementación experimental | Evidencia / límite | Estado |
|---|---|---|---|
| RF-03 | [Direction](../../src/main/java/ar/edu/uade/sokoban/movement/Direction.java), [MovementEngine](../../src/main/java/ar/edu/uade/sokoban/movement/MovementEngine.java), [MoveResult](../../src/main/java/ar/edu/uade/sokoban/movement/MoveResult.java) | MovementTest 01–23 y 26–31: cardinalidad, paredes/bordes, una caja, bloqueo de dos, sin arrastre, identidad e inmutabilidad. Flechas/UI y muros dinámicos pendientes | PARTIAL |
| RF-04 | MovementEngine desplaza Occupant.BOX sobre FLOOR/GOAL | MovementTest 08–12 y 18–20: empuje normal y destinos. Hielo/deslizamiento pendiente, OQ-02 sin resolver | PARTIAL |
| RF-01 | Board/Cell/Position heredados sin cambios | MovementTest verifica invariantes, terrenos/destinos y cantidad de cajas; sin entidades especiales | PARTIAL |
| RF-02 | LevelLoader → Level/Board → MovementEngine | MovementTest 24–25: TXT externo modificado cambia movimiento; 40 casos de LevelLoadingTest intactos. Contrato final OQ-08 OPEN | PARTIAL |
| RT-01 | Runners opcionales Bash/PowerShell, compilación única | Movimiento R3: 40 + 31 PASS por JDK 17/26 en Linux, salida 0, release 17 y sólo java.base; Windows/macOS nativos NOT_RUN, OQ-11 OPEN | PARTIAL |

[MovementTest real: 31 casos](../../src/test/java/ar/edu/uade/sokoban/movement/MovementTest.java), [contrato histórico R1](../experiments/MOVEMENT_R1.md), [UML del subconjunto](../architecture/uml/movement-core-spike-v1.puml) y [evidencia separada R1/R2/R3 y comandos](../testing/TEST_STRATEGY.md). Este snapshot contiene el núcleo experimental de movimiento sobre la base histórica de carga `44b3c63b437465b5f54c1f0f5ca5cbadb5def0e5`. La revisión independiente R2 está realizada; reprodujo Java 17 y no ejecutó Java 26. R3 reprodujo ambas suites con los dos JDK. El oráculo temporal de R2 no forma parte de los tests versionados. RF-03/RF-04 conservan PARTIAL independientemente de la ubicación de la rama. La aceptación del equipo requiere ratificación expresa; la integración se verifica en el historial Git y el PR correspondiente. No se reprodujeron los spikes históricos A/B ni se declara DONE productivo.

## Integración Swing V1 — cobertura adicional PARTIAL, 2026-10-15

| Requisito | Implementación real | Evidencia / límite |
|---|---|---|
| RF-01 / RF-11 | BoardPanel consume Terrain/Occupant del Board existente | Render Windows observado: pared, piso, destino, jugador, caja y superposiciones. Placeholders Graphics2D; imágenes definitivas y audio pendientes |
| RF-02 | GameController.load → LevelLoader; selector y LevelPath | TXT externo, recarga y errores controlados; gramática experimental sin ampliar, OQ-08 OPEN |
| RF-03 / RF-04 | ArrowBindings → GameController.move → MovementEngine | Cuatro direcciones, MOVED/PUSHED/BLOCKED; integración headless y secuencia visual Windows. Hielo, especiales y motor integral pendientes |
| RF-10 | GameWindow + GamePresentation | Nivel y contadores demostrativos, sin Undo/Restart ni resumen |
| RT-01 / RT-03 | Fuentes desktop separadas, run-swing.ps1, GameView | release 17, MVC con mismo Board inmutable, 16 pruebas nuevas PASS; core 40 + 31 PASS sin cambios. Java/build OPEN, revisión humana pendiente |

[Rutas, comandos y resultados](../experiments/SWING_VERTICAL_SLICE_V1.md), [UML real acotado](../architecture/uml/swing-playable-spike-v1.puml). La evidencia anterior de cada iteración se preserva como historia; la ejecución Windows corresponde al incremento original del 15/10. La validación integrada Linux R2 reprodujo 40 Loading + 31 Movement + 16 desktop headless PASS, salida 0, JDK 17/release 17 y C.utf8; detalle en la guía y TEST_STRATEGY; GUI interactiva Linux y macOS: NOT_RUN. Ningún requisito integral pasa a DONE ni se ratifica una decisión por estos tests.

## Evidencia Windows adicional del PR #5 (2026-10-21, posterior a R5)

[QA nativa Windows](../continuity/checkpoints/WINDOWS_PR5_NATIVE_QA_2026-10-21.md) validó el HEAD experimental `0670a4d5137f0e9f1db221866a02c29987024847`, base integrada `ee83f9ebd30ee1b553c555c89d9ea453131d6678`: **40 casos Loading y 31 Movement únicos** ejecutados dentro de la regresión CLI 9/9 por PS5.1/7; 16/16 Swing headless; GUI real reportada con capturas (flechas, empujes, bloqueo, contadores, TXT externo válido/inválido). Es **evidencia de plataforma adicional** para las filas del subconjunto implementado (carga TXT, movimiento, presentación), no una nueva funcionalidad ni aceptación integral.

Los estados RF parciales, cobertura futura, dependencias de OQ-01…OQ-11 y StarUML permanecen como estaban. Las notas previas Windows `NOT_RUN` o históricas se refieren a sus iteraciones fechadas, no al checkpoint actual. Logs/capturas completos permanecen externos; no inferir reejecución desde Git. Revisión humana posterior al merge del PR #5 pendiente; integración y SHA en [CURRENT_STATE](../continuity/CURRENT_STATE.md).

## Decisiones TEAM y casos futuros — 2026-10-23

Fuente normativa TEAM única: [Ratificación por mayoría](../decisions/DECISION_LOG.md#ratificación-por-mayoría--2026-10-23). [OPEN_QUESTIONS](../requirements/OPEN_QUESTIONS.md) conserva las dimensiones pendientes. Las tablas de evidencia anteriores corresponden a sus fechas y no se reescriben como cobertura de estas reglas. Todos los casos siguientes son **PLAN / NOT_RUN**, no implementación ni tests ejecutados.

| RF | Encuesta / D | OQ | Caso futuro o gate de aceptación |
|---|---|---|---|
| RF-01 / RF-02 | [E01 / D-020](../decisions/DECISION_LOG.md#e01) | OQ-08 | Contrato de una capa con superposiciones y ejemplos válidos/inválidos, sin inventar sintaxis |
| RF-02 / RF-05 | [E02 / D-005/D-020](../decisions/DECISION_LOG.md#e02) | OQ-06/OQ-08 | Dos Fragile con valores individuales; errores/rangos después de especificarlos |
| RF-03 / RF-04 | [E03 / D-005](../decisions/DECISION_LOG.md#e03) | OQ-02 | Comparar fin contra pared, sin casilla libre y bordes; no generalizar a otros obstáculos sin especificación |
| RF-05 | [E04 / D-005](../decisions/DECISION_LOG.md#e04) | OQ-03 | Resolver compatibilidad de resistencia cero antes de fijar outcomes de hielo/rotura |
| RF-06 / RF-08 | [E05 / D-019](../decisions/DECISION_LOG.md#e05) | OQ-01 | Criterios KeyBox/Goal y puzzle al combinar retorno de muros, sin asumir resolubilidad |
| RF-06 / RF-07 | [E06 / D-010](../decisions/DECISION_LOG.md#e06) | OQ-07 | Definir cierre con jugador/caja ocupando muro, múltiples cerrojos y restauración Undo antes de probar |
| RF-07 / RF-10 | [E07 / D-007](../decisions/DECISION_LOG.md#e07) | OQ-05 | Disponibilidad con 4/5 movimientos, historial 15, intento bloqueado y cambio de nivel |
| RF-07 | [E08 / D-008](../decisions/DECISION_LOG.md#e08) | OQ-04 | Ejemplos de Undo, movimiento, Restart y nivel; resolver «consecutivos» sin reinterpretar el voto |
| RF-07 / RF-09 | [E09 / D-008](../decisions/DECISION_LOG.md#e09) | OQ-09 | Restauración de contadores frente a total de usos y tratamiento de Restart una vez especificado |
| RF-09 | [E10 / D-009](../decisions/DECISION_LOG.md#e10) | OQ-09 | Tablas numéricas deterministas cuando existan base, pesos, límites y normalización aprobados |
| RF-12 | [E11 / D-022](../decisions/DECISION_LOG.md#e11) | OQ-10 | Guardar/recuperar estado validado con Undo/errores y GoF justificado después del contrato; segunda adicional aún pendiente |

Pistas no cuenta como adicional aprobada; consultar E11 para el resultado y el umbral. Ningún caso ratifica D-011/D-012/D-016 ni el UML. La aceptación del código sigue NOT_RECORDED y OQ-11 continúa como decisión técnica sin encuesta.

## Spike experimental de atributos individuales

RF-02/RF-05 PARTIAL: ExperimentalFragileParser reconoce referencias individuales en piso o destino y ExperimentalFragileLevel conserva ID, posición y resistencia como definición inmutable. La plantilla no contiene una caja normal por cada frágil. El loader jugable sigue rechazando Fragile. Los 31 casos del spike previo al incremento filesystem están separados de los 32 C2 y no implementan resistencia en gameplay ni cierran OQ-06/OQ-08. Ver [contrato](../../README.md#prototipo-experimental-de-atributos-individuales) y [pruebas](../testing/TEST_STRATEGY.md#prototipo-experimental-de-atributos-individuales).

## Reconciliación académica — 29/10/2026

Checkpoint histórico de la reconciliación del PR #13, anterior al incremento filesystem y a D-020 del 29/10. Matriz afirmación→fuente→autoridad/decisión→código→evidencia→destino. Fuentes externas y fidelidad en [SOURCE_REGISTRY](../continuity/SOURCE_REGISTRY.md#fuentes-académicas-aportadas--29102026); no duplica votos. Verificación del código en solo lectura; ejecución funcional nueva NOT_RUN.

| RF/RT y afirmación | Fuente / autoridad / decisión | Implementación y evidencia | Documento afectado / siguiente aceptación |
|---|---|---|---|
| RF-02: TXT propio/editado debe llegar a UI | 23/09 y 30/09 ASR oral; OQ-08 parcial | LevelLoader→LevelParser→Board→GameController; Loading 40 y Desktop 16 históricos | REQUIREMENTS/TEST_STRATEGY: ensayo externo válido, recarga y error atómico PLAN |
| RF-02/05: resistencia individual, puzzle resoluble | E01/E02 TEAM; 23/09 ASR; OQ-06/08 pendientes | ExperimentalFragileParser/Level; Fragile 31 histórico, no jugable | TODO: contrato candidato #12 y solución de puzzle futura |
| RF-06 / RT-03: Observer | 21/10 ASR oral indica incorporarlo; D-010 PREFERRED | Lock/Observer/muros dinámicos ausentes | DECISION_LOG/ARCHITECTURE: OQ-07 y aislamiento de asociaciones antes de prueba |
| RF-07/09: Undo y cálculo | ASR Command/Memento/Strategy; E08 en conflicto, fórmula OQ-09 abierta | UiCommands/LevelResult son presentación; servicios ausentes | RESPONSIBILITIES/TODO: contratos y luego pruebas de sesión/score |
| RT-02/03/04: entrega y defensa | Teams aportado / TEACHER-EXPLICIT | Software parcial y suites históricas; entrega no realizada | REQUIREMENTS/TODO: 18/11, demo 15–20 min, correo, video/StarUML/informe/README e IA |

PR #12 integra documentación candidata, no RF-05 completo ni aprobación de gramática. En ese checkpoint, R6 completa y R7 preparada, no ejecutada. El incremento filesystem ejecutado después está documentado más abajo; aquel siguiente gate no describe el estado operativo actual.

## Elegibilidad RF-12 y evidencia grupal — complemento 29/10/2026

La fila RF-12 de casos futuros del 23/10 conserva su contexto histórico; no certifica hoy guardar/recuperar como adicional válida. [E11 preservada y nota de compatibilidad](../decisions/DECISION_LOG.md#compatibilidad-académica-de-e11--complemento-29102026); [OQ-10 actualizado](../requirements/OPEN_QUESTIONS.md#elegibilidad-de-adicionales--complemento-29102026).

| Afirmación | Fuente / autoridad | Implementación / evidencia | Gate pendiente |
|---|---|---|---|
| Guardar podría no computar como adicional | Mensaje TEAM que atribuye criterio docente, SECONDHAND; original no verificado | UiCommands expone callbacks, no servicio de persistencia; ninguna prueba nueva | Confirmar elegibilidad antes de cómputo RF-12; no alterar voto E11 |
| Selector visual candidato | TEAM_PROPOSAL / TEACHER_REPORTED_ELIGIBILITY / IMPLEMENTATION_NOT_VERIFIED | No se certifica implementación ni patrón desde la encuesta o sprites | Confirmar alcance, selección y patrón real; luego prueba funcional autorizada |
| Undo no implica disco | RF-07 OFFICIAL normalizado; distinción técnica de responsabilidades | Memento candidato, sin obligación textual de persistencia | Especificar separadamente snapshots y eventual guardado |
| Organización en cinco áreas e Issues | Propuesta grupal 24/10 resumida | Sin asignación nominal completa; consulta GitHub sin Issues | Acordar responsables y tareas sin inventar asignaciones |

[Procedencia](../continuity/SOURCE_REGISTRY.md#contexto-grupal-privado--complemento-29102026). Java, GUI y gameplay: NOT_RUN_DOCS_ONLY en este complemento; evidencia histórica intacta. La entrega sigue el 18/11/2026; no hay fecha confirmada de parcial ni deadline 1/11.

## Incremento filesystem de definición Fragile — 29/10/2026

Capacidad implementada en el árbol presente; publicación e integración se verifican en Git/GitHub. [Matriz de brechas y alternativas G2](../requirements/TXT_LEVELS_EXECUTION_PLAN.md#g2--decisión-técnica-del-incremento-29102026); [comandos y contenido probado](../testing/TEST_STRATEGY.md#incremento-filesystem-fragile--29102026).

| Requisito / alcance | Implementación | Casos reales / evidencia del incremento | Brecha restante |
|---|---|---|---|
| RF-02, carga externa real | `LevelLoader.loadExperimentalFragile` y `readText` privado compartido | Fragile 32 fixture externo; 33 edición/recarga Unicode, LF/CRLF/mixtos y declaraciones reordenadas | Flujo especial hacia UI no implementado |
| RF-01/02/05, E01/E02 definición inicial | `ExperimentalFragileParser`/`ExperimentalFragileLevel` reutilizados sin cambios | Fragile 32/33: ID, posición, valores 3/12, piso/Goal, normal Box en Goal e inmutabilidad | Resistencia runtime, rotura/hielo y snapshots pendientes; no RF-05 DONE |
| RF-02, errores sin publicación parcial | Lectura→decodificación→parseo completo→retorno | Fragile 34 tardío/UTF-8 conserva referencia y recupera; 35 UTF-8; 36–38 bytes/frontera; 39 nulos/faltante; 40 BOM/vacío/legacy | Mensajes son técnicos provisionales, sin API de códigos estable |
| RF-02/03/04, compatibilidad | `load`/parsers normales y Movement conservados | Loading 40, C2 32, Movement 31 y Desktop headless 16 PASS; Fragile 41 verifica ambas entradas e intercalado | GUI manual y mecánicas especiales NOT_RUN |

F01–F13 conservan sus referencias de las suites previas; F14 ahora tiene pruebas **directas de la entrada experimental de archivo** (35–38). No se suman como gameplay. Ese incremento no ratificó OQ-06/OQ-08; la adopción explícita posterior por el responsable se registra debajo. TEAM_ACCEPTANCE del código sigue NOT_RECORDED.

## Cierre semántico del subconjunto TXT — 29/10/2026

La [fuente directa](../continuity/SOURCE_REGISTRY.md#cierre-semántico-txt-y-corrección-e04--29102026) distingue la corrección del informante y la autoridad delegada del responsable. No hay código ni casos nuevos: se consolidan decisiones sobre el comportamiento probado.

| Afirmación / requisito | Fuente y autoridad | Elección | Implementación / prueba existente | Documento afectado |
|---|---|---|---|---|
| RF-01/02, una grilla y subconjunto C2 | E01 TEAM; D-020 MODULE_OWNER_DECISION para detalles | TEAM_DECIDED + MODULE_OWNER_DECIDED_PARTIAL | LevelParser/C2LevelParser, C2 1–32; especiales no soportados rechazados | Contrato, OQ-08, README |
| RF-02/05, identidad/resistencia inicial | E02 TEAM; D-020 MODULE_OWNER_DECISION | OQ-06 RESOLVED_TXT_SCHEMA_BY_MODULE_OWNER | ExperimentalFragileParser/Level, Fragile 1–17/23–32; rango int y errores/invariantes | Contrato, OQ-06, TODO |
| RF-02, entrada externa y compatibilidad | Consigna/ASR normalizados; D-020 contrato TXT | MODULE_OWNER_DECIDED; definición IMPLEMENTED | LevelLoader, Fragile 32–41; Loading 40/C2 32/Movement 31/Desktop 16 | Arquitectura, contrato y TEST_STRATEGY |
| RF-05, orden de rotura en hielo | E04 TEAM; corrección directa de recuento con originales no reverificados | TEAM_DECIDED | NOT_IMPLEMENTED en este árbol; casos runtime pendientes en otro módulo | DECISION_LOG, OQ-03 y REQUIREMENTS, sin alterar texto oficial |
| RF-07, reinicio de límite Undo | E08 TEAM preservada | TEAM_DECIDED | NOT_IMPLEMENTED; bordes de sesión fuera de TXT | OQ-04, referencias de continuidad; sin especificación Undo nueva |
| OQ-07/08 extensiones y OQ-11 global | Dependencia real de consumidores/integración | OPEN_DEPENDENCY / OPEN | No tokens hielo/KeyBox/Lock/muros ni build global ratificado | OQ, contrato y siguiente tarea TODO |

QA anterior sobre `38e5a2e0ce4dd4688c4eb903dea93a2a3bf54bd6`: 160 PASS/exit 0, src/scripts idénticos a 5ac2bba. [Entorno, comandos y límites](../testing/TEST_STRATEGY.md#cierre-semántico-documental--29102026). Nueva ejecución Java NOT_RUN_DOCS_ONLY. Elegir una regla o consolidar un esquema no cierra RF-05/RF-07 ni prueba puzzles de demo.

## Runtime individual R2 — experimental

RF-01/02/03/04/05: `RuntimeState.fromDefinition` conserva terreno, jugador, normales e identidad/resistencia Fragile; `RuntimeMovementEngine` ofrece movimiento y hielo programático, con E04 al romper. [Diseño e hipótesis individuales](../continuity/IMPACIENCIA_MASTERPLAN.md); [evidencia y comandos](../testing/TEST_STRATEGY.md#runtime-individual-r2--2026-10-31). No hay hielo TXT, integración Swing, aceptación TEAM ni cierre de OQ-02/08. RF-06–12 y entregables continúan parciales o pendientes según sus fuentes.


## Mundo dinámico individual R3 — experimental

RF-01/03/04/06: KeyBox, Lock y muros dinámicos existen en runtime individual; E06 se aplica bajo hipótesis de integridad, E05 queda preservada sin implementar victoria. [Decisiones](../continuity/IMPACIENCIA_MASTERPLAN.md) y [evidencia](../testing/TEST_STRATEGY.md#mundo-dinámico-individual-r3--2026-10-31). No hay cierre oficial de OQ-07/08, Observer, TXT especial ni integración Swing.


## Carga externa individual R4 — experimental

RF-01/02/03/04/05/06: [contrato individual](../requirements/IMPACIENCIA_RUNTIME_TXT_CONTRACT.md) y fixture externo conectan definición a runtime y movimiento con hielo y entidades dinámicas. [Evidencia](../testing/TEST_STRATEGY.md#carga-externa-individual-r4--2026-10-31). No cierra OQ-07/08, no acredita puzzle resoluble ni integración Swing o aceptación TEAM.


## Sesión individual R5 — experimental

RF-07/09/10: `GameSession` implementa historial 15, Undo de cinco y presupuesto de tres, restaurando mundo/movimientos/empujes y acumulando usos; Restart restituye el estado inicial. [Hipótesis y autoridad](../continuity/IMPACIENCIA_MASTERPLAN.md) · [pruebas](../testing/TEST_STRATEGY.md#sesión-individual-r5--2026-10-31). No hay fórmula de score ni integración Swing; OQ-04/05/09 siguen abiertas oficialmente.


## Finalización individual R6 — experimental

RF-08/09: evaluador puro, Strategy individual y progresión explícita entre dos fixtures externos resueltos; E05/E09/E10 preservadas. [Hipótesis y recorridos](../continuity/IMPACIENCIA_MASTERPLAN.md) · [pruebas](../testing/TEST_STRATEGY.md#finalización-individual-r6--2026-10-31). OQ-01/09 oficiales siguen abiertas; Swing, aceptación TEAM y entrega integral pendientes.


## Integración Swing individual R7 — experimental

RF-01–10: la interfaz consume runtime, sesión y progresión individuales, con carga STANDARD/EXTENDED y dos niveles de campaña. [Pruebas headless y smoke](../testing/TEST_STRATEGY.md#integración-swing-individual-r7--2026-10-31); QA manual, elegibilidad RF-12, derechos y aceptación TEAM permanecen pendientes. Las hipótesis individuales no cierran OQ oficiales.


## Extras y Observer individuales R8 — experimental

RF-12: selector de estilos y replay son dos candidatos técnicos con Abstract Factory y Command, respectivamente; Observer de Lock es colaboración separada, no un tercer extra. [Roles, pruebas y autoridad](../continuity/IMPACIENCIA_MASTERPLAN.md#extras-y-observer-individuales-r8) · [QA](../testing/TEST_STRATEGY.md#extras-y-observer-individuales-r8--2026-10-31). Elegibilidad académica no confirmada, selección TEAM de replay no registrada y OQ-10 abierta.

## Evidencia experimental R9

La [matriz de aceptación y testigos](../testing/IMPACIENCIA_R9_ACCEPTANCE.md) vincula las mecánicas de movimiento, hielo, Fragile, KeyBox/Lock/muros, sesión, victoria y extras con pruebas actuales sobre una campaña individual de tres TXT. No altera requisitos, decisiones TEAM ni OQ; QA humana y aceptación académica siguen pendientes.
