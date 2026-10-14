# Matriz viva de trazabilidad

Revisado 2026-10-14 (carga R1–R4 y movimiento R1–R3 experimentales). **La aceptación productiva completa está pendiente; este snapshot contiene implementaciones y pruebas experimentales con cobertura parcial, detalladas abajo.** A/B significan sólo evidencia histórica externa no reproducida; “plan” refiere a TEST_STRATEGY, no tests ejecutados. El modelo integral es candidato; los UML acotados describen los tipos experimentales reales.

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
| RT-02 | README, docs, StarUML/video/informe futuros | Proceso | Lista entrega; plan | Visibilidad pública futura |
| RT-03 | Responsabilidades/decisiones/trazabilidad | SOLID/GRASP/MVC/GoF | Revisión y defensa individual; plan | Ratificación arquitectura |
| RT-04 | Revisión humana/declaración IA | Trazabilidad | Informe y defensa; plan | Revisión del equipo |

[UML](../architecture/uml/sokoban-candidate.puml), [responsabilidades](../architecture/RESPONSIBILITIES.md), [plan de pruebas](../testing/TEST_STRATEGY.md), [historial](../testing/HISTORICAL_EVIDENCE.md) y [preguntas](../requirements/OPEN_QUESTIONS.md) permiten continuar sin otro chat.

Para marcar DONE: requisito → decisión/autoridad → responsabilidad → implementación real (ruta/commit) → test ejecutado (comando/resultado) → documentación. Completar rutas sólo cuando existan; no inventarlas en este bootstrap.

## Evidencia local R1, sin DONE productivo

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

[MovementTest real: 31 casos](../../src/test/java/ar/edu/uade/sokoban/movement/MovementTest.java), [contrato histórico R1](../experiments/MOVEMENT_R1.md), [UML del subconjunto](../architecture/uml/movement-core-spike-v1.puml) y [evidencia separada R1/R2/R3 y comandos](../testing/TEST_STRATEGY.md). Este snapshot contiene el núcleo experimental de movimiento sobre la base histórica de carga `44b3c63b437465b5f54c1f0f5ca5cbadb5def0e5`. La revisión independiente R2 está realizada; reprodujo Java 17 y no ejecutó Java 26. R3 reprodujo ambas suites con los dos JDK. El oráculo temporal de R2 no forma parte de los tests versionados. No hay DONE productivo, aceptación grupal, integración de movimiento en main ni reproducción histórica de A/B.
