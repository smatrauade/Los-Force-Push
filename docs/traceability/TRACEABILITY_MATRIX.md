# Matriz viva de trazabilidad

## Cobertura integrada tras PR 8 y PR 9 — 2026-10-28

[CURRENT_STATE](../continuity/CURRENT_STATE.md) identifica los merges; [TEST_STRATEGY](../testing/TEST_STRATEGY.md#qa-combinado-pr-8-y-pr-9--2026-10-28) registra el árbol probado y la QA anterior a este mantenimiento. La tabla de plan/candidato y las evidencias fechadas que siguen no niegan las capacidades actuales.

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


Revisado 2026-10-23 (mapa de decisiones TEAM; evidencia ejecutada anterior preservada). **La aceptación productiva completa está pendiente; este snapshot contiene implementaciones y pruebas experimentales con cobertura parcial, detalladas abajo.** A/B significan sólo evidencia histórica externa no reproducida; “plan” refiere a TEST_STRATEGY, no tests ejecutados. El modelo integral es candidato; los UML acotados describen los tipos experimentales reales.

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

RF-02/RF-05 PARTIAL: ExperimentalFragileParser reconoce referencias individuales en piso o destino y ExperimentalFragileLevel conserva ID, posición y resistencia como definición inmutable. La plantilla no contiene una caja normal por cada frágil. El loader jugable sigue rechazando Fragile. Los 31 casos actuales del spike están separados de los 32 C2 y no implementan resistencia en gameplay ni cierran OQ-06/OQ-08. Ver [contrato](../../README.md#prototipo-experimental-de-atributos-individuales) y [pruebas](../testing/TEST_STRATEGY.md#prototipo-experimental-de-atributos-individuales).
