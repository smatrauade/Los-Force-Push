# Responsabilidades candidatas

Roles candidatos al 2026-10-08; la implementación integrada al 28/10 se distingue en la tabla posterior. Aplicar GRASP Expert/Creator/Controller, bajo acoplamiento y alta cohesión antes de adoptar un patrón.

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

## Roles implementados — revisión 28/10/2026

Los roles de la tabla superior son CANDIDATE. La lista siguiente corresponde al código integrado por PR #8/#9 y sus incrementos previos; [arquitectura actual](ARCHITECTURE.md) y [contratos Punto 5](../punto5/INTEGRACION_EQUIPO.md).

| Tipo real | Responsabilidad y límite |
|---|---|
| Position / Terrain / Occupant / Cell / Board | Valores, siete combinaciones e invariantes espaciales; Board profundamente inmutable, sin especiales |
| Level | Producto inicial válido que contiene Board |
| LevelBuilder | Acumulación interna por carga y publicación única; sin interfaz Builder/Director extra |
| LevelParser / C2LevelParser | Selección legacy/C2 e interpretación; C2 interno valida envelope/tokens y rechaza atributos/especiales |
| ExperimentalFragileParser / ExperimentalFragileLevel | Prototipo aislado: definición inmutable con identidad, posición y resistencia; no es Level jugable |
| LevelLoader / LevelFormatException | E/S externa limitada y UTF-8 estricto compartidos por `load` jugable y `loadExperimentalFragile` de definición experimental; errores explícitos, sin caché ni reglas runtime |
| Direction / MovementEngine / MoveResult | Transición normal MOVED/PUSHED/BLOCKED; motor sin estado ni contadores |
| GameController | Carga atómica, delegación de movimiento, GamePresentation y eventos onMove; sin reglas/parser/pintura |
| GamePresentation | Proyección con referencia al Board inmutable, nombre y dos contadores demostrativos |
| GameView | Frontera render/error y hooks default de eventos, comandos y resultado final |
| SwingApplication / GameWindow | Composición EDT, tarjetas, HUD, selector TXT y captura de acciones; sin score/victoria automática |
| ArrowBindings | Traduce flechas a Direction; la ventana controla la pantalla activa |
| BoardPanel | Render por capas, orientación y escala; no modifica el Board ni infiere resistencia |
| PixelAssets | Carga y caché de PNG 32×32 por classpath |
| PixelTheme | Paleta, tipografía y widgets Swing |
| SoundManager / Effect | Clips y reproducción opcional por ventana, mute y fallo de dispositivo controlado; sin Singleton/core global |
| UiCommands | Callbacks opcionales de servicios externos; null indica no disponible |
| LevelResult | DTO de nivel/moves/pushes/undoUses/score ya calculados, sólo para presentación |

`RuntimeState` / `RuntimeMovementEngine` administran KeyBox, Lock y muros. `VictoryEvaluator` y `LevelCatalog` / `LevelProgression` coordinan victoria y avance. GameSession en este incremento sólo cuenta movimientos/empujes y reinicia el nivel; no hay Undo/Memento ni ScoreStrategy. Persistencia, hielo y Fragile jugable siguen ausentes. Las interfaces y la jerarquía integral no se adoptan por existir hooks visuales. Requisitos/decisiones y evidencia de ejecución se consultan por separado.

## Responsables de cierre por módulo — 29/10/2026

Son roles de planificación, no asignaciones personales ni arquitectura ratificada. [Prioridades y aceptación](../../TODO.md#ruta-crítica-hacia-el-1811).

- TXT/niveles: contrato externo, errores atómicos, definición inicial e invariantes; preservar identidad/resistencia antes de integración runtime.
- Model/Movement: reglas, estado de cajas y asociaciones; candidato Observer para Lock→Walls pendiente de OQ-07. No delegar esos efectos a Swing.
- Sesión/score/persistencia: Undo/Restart, métricas, victoria/progresión y guardado; justificar Command/Memento/Strategy cuando exista contrato.
- Swing: representar DTO/Board y conectar servicios realmente disponibles. Los callbacks UiCommands no son el patrón Command demostrado.
- Integración/entrega: coordinar compatibilidad, pruebas, licencias, StarUML, informe, video y defensa. Evaluar LevelBuilder por intención y consecuencias, no por nombre.
