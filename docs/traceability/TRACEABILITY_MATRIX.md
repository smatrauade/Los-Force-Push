# Matriz viva de trazabilidad

Revisado 2026-10-08. **Todas las implementaciones y pruebas productivas están pendientes.** A/B significan sólo evidencia histórica externa no reproducida; “plan” refiere a TEST_STRATEGY, no tests ejecutados. Las responsabilidades y UML son candidatos.

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
