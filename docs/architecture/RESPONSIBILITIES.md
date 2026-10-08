# Responsabilidades candidatas

Roles al 2026-10-08; no clases implementadas. Aplicar GRASP Expert/Creator/Controller, bajo acoplamiento y alta cohesión antes de adoptar un patrón.

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
