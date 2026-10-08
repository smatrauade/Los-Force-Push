# Arquitectura candidata

`STATUS = CANDIDATE`, `LAST_REVIEWED = 2026-10-08`. Ninguna clase de este modelo se implementa en el bootstrap. Los roles y firmas están sujetos a ratificación; [DECISION_LOG](../decisions/DECISION_LOG.md) describe alternativas.

## Flujo y fronteras

```text
Swing View → GameController → GameSession
                               ├─ MovementEngine
                               ├─ RestorableLevelState → Board → Cell
                               ├─ UndoManager / GameMemento
                               ├─ SessionMetrics (TEAM-PROPOSAL)
                               └─ ScoreStrategy
GameController → LevelLoader → LevelRepository + LevelParser
                                            LevelParser → LevelBuilder → Level
GameController → LevelCatalog (progresión) / AudioService
```

Model concentra reglas y estado; View representa y captura entradas; Controller coordina operaciones y resultados. El motor no tiene otra copia del tablero. View consume datos de presentación y no decide reglas inspeccionando subclases. `HudData` contiene nivel, movimientos y empujes; `LevelStats`/`LevelResult` sirven al resumen y cálculo.

La firma histórica `GameView.render(RestorableLevelState)` es candidata: **no autoriza entregar estado mutable a la UI**. Al implementarla deberá garantizarse lectura segura o sustituirse por proyección/DTO. No se crea otro modelo divergente en View.

## Espacio y cajas

`Cell = Terrain + Occupant[0..1]` separa suelo de ocupación, evitando `BoxOnGoal` o `PlayerOnIce`. Terrain contempla Floor, Wall, Goal, Ice, LockTile y OpenableWall. Occupant contempla Player y Box, con NormalBox, FragileBox y KeyBox como jerarquía ilustrativa.

Polimorfismo es preferido; **herencia frente a composición sigue abierta**. La resistencia es exclusiva de Fragile; no forzar operaciones que rompan LSP en las otras cajas. El resultado de un empuje puede comunicar rotura sin `instanceof` distribuido. Las posiciones, resistencia y estado de muros forman parte del estado restaurable.

## Carga

LevelLoader coordina obtener texto y parsearlo; Repository lee archivos; Parser interpreta sintaxis y puede dirigir pasos; LevelBuilder/SokobanLevelBuilder ensamblan Level. Builder es `PREFERRED`, no `ACCEPTED`: comparar con construcción directa en un spike. Director separado es opcional.

No confundir helper que encapsula `new` con Factory Method GoF. Abstract Factory sólo se justifica con familias coherentes de productos; Prototype no tiene problema demostrado de clonación costosa para el loader. El formato TXT sigue abierto; las operaciones Builder del UML son provisionales.

## Movimiento, Undo, score y progreso

MovementEngine coordina transiciones atómicas con Board y cajas. Los escenarios son movimiento libre, push, hielo, fragile y llave/Observer. No mutar parcialmente ante fallos. Las hipótesis de spikes en escenarios ambiguos no definen reglas oficiales.

Memento preferido: GameSession es Originator, GameMemento conserva snapshot opaco, UndoManager es Caretaker que almacena Mementos sin manipular estado crudo. Evitar alias mutable entre estado inicial, actual y snapshots. Historial 15, Undo 5 y tres usos consecutivos provienen de requisitos; bordes pendientes OQ-04/OQ-05.

`RestorableLevelState` contiene Board y contadores moves/pushes. `SessionMetrics` con undoUsesTotal/consecutiveUndoUses fuera del snapshot es **TEAM-PROPOSAL**; no obligación ratificada. ScoreStrategy calcula desde LevelStats; fórmula pendiente. GameSession evalúa victoria y genera resumen; Controller coordina el próximo Level mediante LevelCatalog. La interfaz de catálogo debe justificar frontera/variación o simplificarse.

## Observer y audio

LockTile es Subject; LockObserver contrato; OpenableWall observador. La llave sobre cerrojo notifica a muros asociados. Cardinalidad, IDs y permanencia/cierre están `OPEN`; reconstruir suscripciones/estado coherentemente al restaurar, sin notificaciones que alteren el snapshot restaurado.

Controller depende de AudioService; core independiente de audio global y Swing. SoundManager Singleton aparece en UML como `PROPOSED` y requiere justificar coste de estado global. Adapter no está justificado sin interfaz incompatible. Decorator puede servir a extras combinables sólo tras elegir esas funcionalidades. No hay clases definitivas para extras.

## UML y evolución

Se reportó v0.2 → auditoría v0.3 → interacciones v0.4 → candidato v0.5 con Builder. La revisión conceptual corrigió Caretaker acoplado a estado crudo, HUD sin DTO, Controller acoplado a SoundManager, operaciones de Fragile en Box, progresión ausente y agrupación por patrones en lugar de responsabilidades.

El [PlantUML candidato](uml/sokoban-candidate.puml) **estaba disponible en el ZIP**: se preservó byte a byte, no se reconstruyó ni se declara nuevo. Las secuencias revisadas históricamente fueron carga, movimiento, push, hielo, fragile, cerrojo/Observer, Undo, victoria/progresión y Restart. Su validación histórica no equivale a ejecución actual. Ver [STARUML_STATUS](uml/STARUML_STATUS.md).

Java target y build siguen abiertos. El siguiente desarrollo requiere decisiones pertinentes, pruebas del core sin Swing y sincronización de UML/trazabilidad.
