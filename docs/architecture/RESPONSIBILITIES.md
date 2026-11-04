# Responsabilidades candidatas

## Observer de presentación — 04/11/2026

`LockChangeSubject` publica diferencias confirmadas después del render para MOVE/UNDO/RESTART y carga de otro nivel; no retiene ni modifica el mundo. Dos suscriptores reales actualizan el aviso Swing y solicitan audio MOVE; Undo/Restart conservan sus SFX propios. Suscripciones deduplicadas, unsubscribe/close y errores aislados; muros derivados exclusivamente por `RuntimeState`. `LockObserverTest`: 6 escenarios PASS. Adaptación selectiva del subject individual de `impaciencia`, sin importar su sesión ni reglas. [Auditoría y defensa de patrones](../integration/PR17_FUNCTIONAL_RC_2026-11-04.md).

## Roles efectivos Replay — 04/11/2026

- `GameController`: registra éxitos, reconcilia intentos y verifica la traza al ganar; no reproduce sobre la sesión.
- `ReplayRecorder`/`ReplayTrace`/`MoveCommand`/`ReplayPlayer`: historial acotado, valor inmutable, acción y cursor aislado respectivamente; core I3.D.1 conservado.
- `ReplayPanel`: controles, tablero independiente y timer EDT cancelable; sin score/audio/progresión.
- `VisualThemeController`: un cambio de familia para tablero real, Replay, menú y árbol Swing.

[Auditoría](../integration/PR17_FUNCTIONAL_RC_2026-11-04.md). `UiCommands` son callbacks; el Command defendible es `MoveCommand`.

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
| GameController | Carga atómica y coordinación de movimiento, sesión, `undo()`, `restart()`, `getLevelResult()` y solicitud de animación posterior a publicar; sin historial propio ni reglas/parser/pintura |
| ScoreCalculator | Calcula el score del intento desde 1000 con penalidades incrementalmente aplicadas: −1 por movimiento, −2 por push y −10 por uso de Undo; evita overflow con `applyPenalty` y devuelve un mínimo de 0 |
| GamePresentation | Proyección con referencia al Board inmutable, nombre y dos contadores fundamentales del intento (`moves`, `pushes`) |
| GameView | Frontera render/error y hooks default de eventos, animación de caja, comandos y resultado final |
| SwingApplication / GameWindow | Composición EDT, tarjetas, HUD, selector TXT, selector de estilo visual y captura de acciones; `configureUiCommands` activa/desactiva Undo y Restart según callbacks disponibles |
| ArrowBindings | Traduce flechas a Direction; la ventana controla la pantalla activa |
| BoardPanel | Render por capas, orientación y escala; proyecta frames de `BoxAnimation` desde el path autoritativo sin recalcular física; toma la paleta de la familia visual activa; no modifica el Board ni infiere resistencia |
| PixelAssets | Carga y caché de PNG 32×32 por classpath; aplica la presentación de sprites de la familia activa e invalida la caché al cambiarla, sin escribir archivos |
| PixelTheme | Paleta, tipografía y widgets Swing; `Surface` lee la paleta de la familia activa |
| VisualThemeFactory | Abstract Factory de vista: contrato de `UiPalette` y `SpritePresentation`; no expone estado de juego |
| VisualThemes | Catálogo inmutable de las familias `ORIGINAL COBRE`, `NOCTURNO` y `JADE`, con tinte determinista que preserva alfa |
| VisualThemeController | Único punto de cambio de familia: actualiza tablero, arte de menú y árbol Swing; rechaza nulos |
| VisualThemeApplier | Aplica la paleta y restaura colores/bordes Swing originales al volver al tema base; no toca `BoardPanel` |
| SoundManager / Effect | Clips y reproducción opcional por ventana, mute y fallo de dispositivo controlado; sin Singleton/core global |
| UiCommands | Callbacks opcionales de servicios externos; `undo`, `restart` y `nextLevel` se habilitan según sesión; `save` y `loadSaved` siguen pendientes |
| LevelResult | DTO de nivel/moves/pushes/undoUses/score ya calculados; transporta el resumen final del nivel y se usa para la vista |

`RuntimeState` / `RuntimeMovementEngine` administran el mundo y las transiciones. `GameSession` concentra historial Undo y métricas; `LevelProgression` coordina victoria y avance; `GameController` conecta presentación y `ScoreCalculator`. Persistencia, arquitectura final y aceptación del equipo permanecen pendientes.

## Responsables de cierre por módulo — 29/10/2026

Son roles de planificación, no asignaciones personales ni arquitectura ratificada. [Prioridades y aceptación](../../TODO.md#ruta-crítica-hacia-el-1811).

- TXT/niveles: contrato externo, errores atómicos, definición inicial e invariantes; preservar identidad/resistencia antes de integración runtime.
- Model/Movement: reglas, estado de cajas y asociaciones; candidato Observer para Lock→Walls pendiente de OQ-07. No delegar esos efectos a Swing.
- Sesión/score: `GameSession` administra Undo/Restart e historial; `LevelProgression` coordina victoria/progresión y `GameController` usa `ScoreCalculator`. Persistencia sigue pendiente.
- Swing: representar DTO/Board y conectar servicios realmente disponibles. `UiCommands` habilita callbacks concretos cuando el módulo los entrega; no debe asumirse un patrón Command sin evidencia.
- Integración/entrega: coordinar compatibilidad, pruebas, licencias, StarUML, informe, video y defensa. Evaluar LevelBuilder por intención y consecuencias, no por nombre.
