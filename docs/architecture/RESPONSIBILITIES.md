# Responsabilidades candidatas

Roles candidatos al 2026-10-08; sin clases implementadas en la base histórica del merge PR #2. El subconjunto local R1 se distingue abajo. Aplicar GRASP Expert/Creator/Controller, bajo acoplamiento y alta cohesión antes de adoptar un patrón.

| Rol | Responsabilidad y colaboradores | Límite |
|---|---|---|
| GameView / GameFrame y paneles | Capturar entrada, representar Board y HudData, mostrar LevelResult | Sin reglas ni estado mutable propio divergente |
| GameController | Coordinar GameSession, carga, progreso, presentación y audio de resultados | Sin parser/motor monolítico |
| GameSession | Sesión/nivel actual, operaciones, snapshots, victoria y resumen | Originator; no Swing |
| RestorableLevelState | Board, movimientos y empujes de la línea temporal | Fuente del estado restaurable |
| Board / Cell / Position | Estructura espacial, terreno y ocupación | Information Expert; sin segunda copia en motor |
| Terrain y variantes | Transitabilidad, hielo, destinos, cerrojo y muros | Reglas abiertas no inventadas |
| Box y variantes | Comportamiento de empuje, llave y resistencia específica | LSP; herencia/composición OPEN |
| MovementEngine / MoveResult | Transición atómica y resultado observable | Sin propiedad duplicada de Board |
| GameMemento | Snapshot aislado y opaco | No expuesto al Caretaker como estado editable |
| UndoManager | Historial acotado y entrega del Memento solicitado | Conserva; no manipula snapshot crudo |
| SessionMetrics | Métricas propuestas fuera del rewind | TEAM-PROPOSAL, reinicios pendientes |
| ScoreStrategy / LevelStats / LevelResult | Política y datos de cálculo/resumen | Fórmula OPEN; no strategies ficticias |
| Level / LevelLoader | Producto e inicio; coordinar Repository/Parser | Estado inicial aislado del actual |
| LevelRepository / FileLevelRepository | Leer texto externo | No niveles hardcodeados |
| LevelParser | Interpretar contrato TXT; posible Director | No fijar gramática implícita |
| LevelBuilder / SokobanLevelBuilder | Construcción e invariantes de Level | PREFERRED; comparar con directa |
| LevelCatalog | Determinar siguiente nivel | Revisar necesidad de interfaz |
| LockTile / LockObserver / OpenableWall | Notificar apertura a asociados | IDs/cardinalidad/cierre OPEN |
| AudioService / SoundManager | Audio solicitado por aplicación | Singleton propuesto; no global del dominio |
| SpriteProvider | Imagen adecuada para presentación | Assets auditados; no reglas de juego |

ScoreStrategy y LockObserver representan variaciones/roles identificables. GameView, AudioService, Repository y SpriteProvider pueden justificar una frontera concreta/testabilidad. GameActions y LevelCatalog requieren revisión especial para evitar indirección ornamental. LevelBuilder depende de que el spike demuestre valor.

## Roles implementados únicamente en el experimento R1

[Informe y clases reales](../experiments/LEVEL_LOADING_R1.md); [UML específico](uml/level-loading-spike-v1.puml).

| Tipo real | Responsabilidad |
|---|---|
| Position | Coordenadas no negativas; Board verifica límites |
| Terrain / Occupant | Valores del subconjunto; sin comportamiento de gameplay |
| Cell | Composición terreno/ocupante; pared sin ocupante |
| Board | Agregado inmutable, dimensiones/rectángulo/jugador único, consultas y conteos |
| Level | Producto válido que contiene Board, sin metadata artificial |
| LevelBuilder | Acumulación interna por carga y publicación única tras validar |
| LevelParser | Forma de filas validada antes de ensamblar; siete símbolos/EOL, sin estado entre cargas |
| LevelLoader | Lectura externa limitada y UTF-8 estricto; delegación al parser |
| LevelFormatException | Error explícito del contrato experimental |

Las demás responsabilidades candidatas permanecen sin implementación. No se adoptan contratos de cajas especiales ni interfaz Builder por anticipación.

## Roles del movimiento experimental R1 — 2026-10-14

[Informe](../experiments/MOVEMENT_R1.md) y [UML](uml/movement-core-spike-v1.puml).

| Tipo | Responsabilidad y límite |
|---|---|
| Direction | Desplazamiento cardinal de una celda, sin teclado ni UI |
| MovementEngine | Consultar Board y Direction, bloquear colisiones, crear un Board válido nuevo tras checks; sin estado retenido |
| MoveResult / Kind | Resultado MOVED/PUSHED/BLOCKED y Board resultante; sin estadísticas o semántica de conteo |
| Board/Cell/Position heredados | Expert espacial, terreno conservado e invariantes; contratos y fuentes sin cambios |

El llamador conserva la referencia al Board vigente; el motor no es otro propietario de estado ni una sesión. Level mantiene el valor inicial. No se agrega GameState, jerarquía de cajas o interfaces sin variación real. Clases productivas del resto del modelo siguen pendientes.

## Roles implementados en el incremento Swing V1 — 2026-10-15

LAST_REVIEWED = 2026-10-15. [Guía y límites](../experiments/SWING_VERTICAL_SLICE_V1.md), [UML](uml/swing-playable-spike-v1.puml). Estos roles concretan sólo el subconjunto gráfico experimental.

| Tipo real | Responsabilidad / límite |
|---|---|
| SwingApplication / GameWindow | Entrada y construcción EDT, selección de TXT, widgets y errores de carga; sin reglas |
| ArrowBindings | Flechas de ventana a Direction; no decide resultados |
| GameController | Carga atómica, delegación a MovementEngine, publicación y dos contadores demostrativos; sin parser/render/colisiones |
| GameView | Frontera render/error sustituible por una sonda de test sin pantalla |
| GamePresentation | Board inmutable compartido, nombre de nivel y contadores; sin grilla mutable |
| BoardPanel | Pinta Terrain/Occupant con placeholders; retiene sólo referencia al Board para repintar |
| LevelLoader / Board / MovementEngine | Contratos del core existentes, sin cambios |

No se implementan GameSession, Undo, victoria, cajas especiales, audio ni el resto del candidato integral.
