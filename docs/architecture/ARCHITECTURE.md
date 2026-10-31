# Arquitectura: implementación y candidato

Revisado 29/10/2026: contrato TXT soportado adoptado por su responsable; historia de PR #8/#9 preservada. La implementación sigue siendo experimental, sin ratificación del diseño por los PASS ni por los merges. El [UML integral v0.5](uml/sokoban-candidate.puml) se conserva como CANDIDATE; no es el diagrama completo del software actual.

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

[LevelLoader](../../src/main/java/ar/edu/uade/sokoban/level/LevelLoader.java) limita lectura a 65.536 bytes y decodifica UTF-8 estricto. [LevelParser](../../src/main/java/ar/edu/uade/sokoban/level/LevelParser.java) selecciona legacy o el helper interno sin estado [C2LevelParser](../../src/main/java/ar/edu/uade/sokoban/level/C2LevelParser.java) mediante el prefijo reservado. Las firmas públicas LevelLoader.load(Path), LevelParser.parse(String) y Level(Board) no cambian. El código expone `LevelLoader.loadExperimentalFragile(Path) → ExperimentalFragileLevel`: un método privado `readText` centraliza la política de bytes/UTF-8 de ambas entradas; el parser experimental interpreta y el agregado existente valida la definición. No se agrega clase, interfaz, estado compartido ni patrón GoF. [Comparación con alternativas](../requirements/TXT_LEVELS_EXECUTION_PLAN.md#g2--decisión-técnica-del-incremento-29102026).

C2 valida header/secciones, tokens ASCII (cuota del subconjunto soportado de 128 caracteres), dimensiones lógicas hasta 100×100 y [attributes] vacía. Sólo ofrece las siete combinaciones existentes; rechaza atributos/especiales. parse(String) no impone el techo de bytes de Loader. No hay caché compartida, fallback ante header inválido ni Level parcial.

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
- E04/E08 tienen regla TEAM decidida; implementación y bordes pertenecen a Model/Movement y sesión. No inferir rotura, consecutividad, ocupación de muros o criterios de victoria desde el UML.
- El candidato propone polimorfismo de cajas; herencia/composición sigue abierta. No exigir resistencia a todas las cajas ni distribuir instanceof/getClass por la vista.

ExperimentalFragileParser y ExperimentalFragileLevel representan atributos individuales sin producir un Level jugable. El agregado valida IDs y posiciones únicos, límites, piso/destino vacío y resistencia positiva. Siguiente trabajo TXT/niveles: contrastar el consumidor real con el contrato soportado, adoptado por MODULE_OWNER_DECISION en [D-020](../decisions/DECISION_LOG.md#d-020--decisión-del-responsable-txt--29102026). Sintaxis/rango/validación del lector actual resueltos; extensiones especiales e integración runtime OPEN_DEPENDENCY. No ampliar mecánicas en mantenimiento documental. [STARUML_STATUS](uml/STARUML_STATUS.md) separa los diagramas implementados, el candidato preservado y el .mdj pendiente. Los informes de [Loading](../experiments/LEVEL_LOADING_R1.md), [Movement](../experiments/MOVEMENT_R1.md) y [Swing V1](../experiments/SWING_VERTICAL_SLICE_V1.md) son antecedentes fechados.

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

Cada patrón a defender necesita problema, intención, roles reales, variación, alternativa, consecuencias y prueba del código entregado. Los diagramas candidatos no acreditan esas colaboraciones. Esa reconciliación no ejecutó R7. El incremento posterior 5ac2bba implementó la entrada filesystem de definición experimental; gameplay e integración runtime siguen pendientes.


## Runtime individual R2 — 31/10/2026

`RuntimeState` conserva terreno, jugador y cajas normales/Fragile inmutables; adapta `Level` o `ExperimentalFragileLevel` sin sustituir Fragile por BOX. `RuntimeMovementEngine` calcula transición y `RuntimeMoveResult` expone caja, destino, distancia y rotura. El motor `MovementEngine` y Swing no se modifican ni consumen este runtime. El hielo sólo se construye programáticamente. La decisión de un valor inmutable sin proyección Board evita dos ocupaciones divergentes y se aparta de relaciones mutables del UML candidato, que sigue TO_BE. [Opciones, límites e hipótesis](../continuity/IMPACIENCIA_MASTERPLAN.md).


## Mundo dinámico individual R3 — 31/10/2026

`RuntimeState` agrega KeyBox, Lock y DynamicWall inmutables y asociaciones explícitas. La apertura se deriva de ocupación final por KeyBox; se rechaza atómicamente un cierre con ocupante. `RuntimeMoveResult.lockChanges()` informa cambios efectivos. Esta colaboración no implementa Observer GoF: no hay suscripción ni observadores; [alternativas y límites](../continuity/IMPACIENCIA_MASTERPLAN.md). El UML candidato sigue TO_BE, Swing/TXT no consumen el modelo y OQ-07/08 permanecen abiertas.
