# Arquitectura: implementación y candidato

Revisado 28/10/2026 tras PR #8 y #9. La implementación sigue siendo experimental, sin ratificación del diseño por los PASS ni por los merges. El [UML integral v0.5](uml/sokoban-candidate.puml) se conserva como CANDIDATE; no es el diagrama completo del software actual.

## Flujo implementado

~~~text
SwingApplication / GameWindow (EDT, GameView)
  -> GameController -> LevelLoader -> LevelParser
                                      -> legacy / C2LevelParser -> LevelBuilder -> Level / Board
                    -> MovementEngine(Board, Direction) -> MoveResult
  <- GamePresentation(Board inmutable, nombre, moves, pushes)
  -> BoardPanel -> PixelAssets -> /assets/sprites/*.png
  -> SoundManager -> /assets/sfx/*.wav
~~~

El core no importa Swing ni audio. GameController coordina y conserva una proyección del Board vigente; la vista comparte ese valor inmutable sin duplicar una grilla mutable. No hay GameSession ni loop/polling.

## Loading y C2-L7

[LevelLoader](../../src/main/java/ar/edu/uade/sokoban/level/LevelLoader.java) limita lectura a 65.536 bytes y decodifica UTF-8 estricto. [LevelParser](../../src/main/java/ar/edu/uade/sokoban/level/LevelParser.java) selecciona legacy o el helper interno sin estado [C2LevelParser](../../src/main/java/ar/edu/uade/sokoban/level/C2LevelParser.java) mediante el prefijo reservado. Las firmas públicas LevelLoader.load(Path), LevelParser.parse(String) y Level(Board) no cambian.

C2 valida header/secciones, tokens ASCII (máximo experimental 128 caracteres), dimensiones lógicas hasta 100×100 y [attributes] vacía. Sólo ofrece las siete combinaciones existentes; rechaza atributos/especiales. parse(String) no impone el techo de bytes de Loader. No hay caché compartida, fallback ante header inválido ni Level parcial.

LevelBuilder es concreto, interno y por carga; Board protege también la construcción directa. Builder/directa y Terrain/Occupant son elecciones experimentales/PREFERRED, sin interfaces o Director adicional. [UML implementado](uml/level-loading-spike-v1.puml).

## Movimiento

MovementEngine es un servicio sin estado: Board + Direction → MoveResult. MOVED/PUSHED crean un Board validado nuevo; BLOCKED conserva la misma instancia. Mantiene terreno/cajas/jugador e inmutabilidad de estados anteriores. Sólo piso, destino, pared, límites y caja normal. Copiar/validar cuesta O(alto×ancho) por éxito; no es Memento ni una decisión definitiva sobre el modelo futuro. [UML](uml/movement-core-spike-v1.puml).

## Swing y recursos — Punto 5

[GameView](../../src/desktop/java/ar/edu/uade/sokoban/desktop/GameView.java) permite sondas sin JFrame. Conserva render/showLoadError y ofrece métodos default onMove, showVictory, configureUiCommands y playEffect. GameController publica onMove para MOVED/PUSHED/BLOCKED; los bloqueos no cambian proyección ni contadores.

GameWindow representa MENU/GAME/PAUSE/OPTIONS/HELP/FINISH. ArrowBindings entrega Direction; sólo GAME permite mover. BoardPanel representa Board y escala 1x/2x/3x con nearest-neighbor. PixelAssets carga/cacha PNG desde classpath; SoundManager posee clips por ventana, mute y degradación ante dispositivo ausente. No es Singleton ni servicio global del core.

UiCommands contiene callbacks opcionales de Undo/Restart/Save/Load/Next: null mantiene controles deshabilitados. LevelResult sólo transporta datos finales; showVictory muestra esa información sin calcular score ni detectar victoria. Los nombres futuros contemplados por el renderer son pistas de adaptación, no especificaciones del dominio.

[INTEGRACION_EQUIPO](../punto5/INTEGRACION_EQUIPO.md) es el contrato entre módulos; [UML desktop implementado](uml/swing-playable-spike-v1.puml). Los [43 recursos](../punto5/ASSETS_REGISTRO_SHA256.csv) están versionados y el runner agrega src/desktop/resources al classpath; licencias pendientes según [ASSET_POLICY](../assets/ASSET_POLICY.md).

La UI opera en EDT. La carga local acotada es síncrona y puede demorar ante E/S lenta; evaluar cancelación/SwingWorker sólo cuando ese escenario se autorice. Runners core limitados a java.base; desktop usa java.base,java.desktop. Release 17 es provisional, OQ-11 sigue OPEN.

## Diseño integral propuesto, sin implementación

El candidato incluye GameSession, RestorableLevelState, GameMemento/UndoManager, SessionMetrics, cajas especiales, LockObserver/OpenableWall, ScoreStrategy, LevelCatalog, Repository y AudioService. Esos nombres no implican clases presentes; el SoundManager y LevelResult del desktop son implementaciones de presentación con alcance propio.

- Memento, Observer, Strategy, Builder y fronteras candidatas mantienen sus estados de [DECISION_LOG](../decisions/DECISION_LOG.md); no adoptar interfaces ceremoniales.
- SessionMetrics fuera del snapshot es TEAM-PROPOSAL; E09/E10 aceptan comportamientos parciales, no esa clase ni una fórmula.
- La individualidad de resistencia (E02), disposición (E01), cierre (E06) y umbral Undo (E07) ya tienen alcance votado; los detalles permanecen en [OPEN_QUESTIONS](../requirements/OPEN_QUESTIONS.md).
- E04/E08 requieren compatibilidad normativa. No inferir rotura, consecutividad, ocupación de muros o criterios de victoria desde el UML.
- El candidato propone polimorfismo de cajas; herencia/composición sigue abierta. No exigir resistencia a todas las cajas ni distribuir instanceof/getClass por la vista.

ExperimentalFragileParser y ExperimentalFragileLevel representan atributos individuales sin producir un Level jugable. El agregado valida IDs y posiciones únicos, límites, piso/destino vacío y resistencia positiva. Siguiente dominio TXT/niveles: revisar el contrato y especificar su integración futura; sintaxis/rangos/errores siguen sin ratificar. No ampliar mecánicas en mantenimiento documental. [STARUML_STATUS](uml/STARUML_STATUS.md) separa los diagramas implementados, el candidato preservado y el .mdj pendiente. Los informes de [Loading](../experiments/LEVEL_LOADING_R1.md), [Movement](../experiments/MOVEMENT_R1.md) y [Swing V1](../experiments/SWING_VERTICAL_SLICE_V1.md) son antecedentes fechados.

## Patrones y brechas de defensa — 29/10/2026

Revisión documental contra el código integrado por PR #11/#12; [evidencia docente](../decisions/DECISION_LOG.md#evidencia-docente-adicional--29102026). AS_IMPLEMENTED no implica aceptación TEAM.

| Problema | Implementación real | Candidato / brecha de defensa |
|---|---|---|
| Ensamblar filas y publicar nivel válido | LevelBuilder concreto interno, addRow/build por carga | Justificar Builder simplificado frente a construcción directa; sin familia de productos ni jerarquía Creator GoF |
| Separar UI de reglas | GameController delega a Loader/MovementEngine y publica GamePresentation; GameView permite sondas | MVC verificable por dependencias; no transferir reglas al renderer |
| Identidad/resistencia inicial Fragile | ExperimentalFragileLevel separado del Level jugable | Integración runtime pendiente; no duplicar este agregado en R7 |
| Notificar cambios de Lock a muros asociados | No implementado | Observer indicado por docente según ASR; candidato Lock→Walls depende de OQ-07 |
| Deshacer y calcular score | No implementado; UiCommands son callbacks y LevelResult es DTO | Command/Memento a comparar; Strategy depende de fórmula OQ-09 y Undo de OQ-04/05 |
| Audio y adicionales | SoundManager por ventana; sin persistencia operativa | No Singleton GoF; State/Factory/Decorator no están acreditados por ejemplos o nombres |

Cada patrón a defender necesita problema, intención, roles reales, variación, alternativa, consecuencias y prueba del código entregado. Los diagramas candidatos no acreditan esas colaboraciones. R7 permanece preparada y no ejecutada en esta reconciliación.
