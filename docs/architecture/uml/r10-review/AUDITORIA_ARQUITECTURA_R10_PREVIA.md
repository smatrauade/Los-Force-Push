# R10 — Auditoría arquitectónica UML ↔ Java (fase de revisión en sandbox)

**Repositorio:** `smatrauade/Los-Force-Push`, rama `impaciencia`
**Commit fuente exacto:** `e7d4c33f3fb17bd3a72bca25ddee1ad37f529087` (31/10/2026)
**Modalidad:** inspección **de sólo lectura** de código y diagramas del commit en GitHub; diagramas de revisión nuevos generados fuera del repositorio.
**Estado:** `PARTIAL_VERIFIED_REVIEW`: correspondencia sustentada para las relaciones examinadas, pendiente revisión integral de métodos no examinados de `SwingApplication` y de tests, compilación/render nativo PlantUML y apertura StarUML `.mdj`.

## 1. Inventario inequívoco

El árbol Git completo (respuesta `truncated=false`) contiene:

| Tipo | Cantidad |
|---|---:|
| Java de producción en `src/main/java` | 30 |
| Java desktop en `src/desktop/java` | 20 |
| Java test core | 9 |
| Java test desktop | 5 |
| PlantUML `.puml` | 4 |
| StarUML `.mdj` | **0** |

Se inspeccionaron los **50 archivos Java de producción** disponibles en el commit (49 mediante contenido íntegro y `SwingApplication.java` mediante varios intervalos de líneas relevantes), junto con los tres `.puml` AS_IMPLEMENTED. Se validaron identidades, jerarquías y colaboraciones principales; **no constituye una verificación AST exhaustiva de todos los métodos ni una nueva ejecución de pruebas**. Las 14 unidades de tests no se analizaron completas en esta fase.

Referencias históricas originales:

- [Loading (PlantUML)](https://github.com/smatrauade/Los-Force-Push/blob/e7d4c33f3fb17bd3a72bca25ddee1ad37f529087/docs/architecture/uml/level-loading-spike-v1.puml)
- [Movement / session (PlantUML)](https://github.com/smatrauade/Los-Force-Push/blob/e7d4c33f3fb17bd3a72bca25ddee1ad37f529087/docs/architecture/uml/movement-core-spike-v1.puml)
- [Swing / extensions (PlantUML)](https://github.com/smatrauade/Los-Force-Push/blob/e7d4c33f3fb17bd3a72bca25ddee1ad37f529087/docs/architecture/uml/swing-playable-spike-v1.puml)
- `sokoban-candidate.puml` es **TO_BE/CANDIDATE**, no versión implementada ni fuente de verdad.

## 2. Diferencias verificadas: diagrama existente → código

| ID | Prioridad | Hallazgo | Código verificado | Resolución en borrador |
|---|---|---|---|---|
| UML-01 | Alta | Las tres fábricas figuran como `CopperTheme`, `NightTheme` y `JadeTheme`: **tipos inexistentes con esos nombres** | `VisualThemes.java` contiene `VisualThemes.Family` (clase privada abstracta que implementa `VisualThemeFactory`) y tres clases privadas `VisualThemes.Copper`, `.Night`, `.Jade` que extienden `Family` | Corregidos nombres y jerarquía, sin inventar clases top-level |
| UML-02 | Media | `UiPalette` y `SpritePresentation` aparecen como productos top-level | Ambos declarados **dentro de** `VisualThemeFactory.java` (`record` e interfaz funcional) | Nombres anidados exactos, tipos concretos |
| UML-03 | Alta | `LockChangeObserver` aparece como interfaz de paquete | La interfaz real es `LockChangeSubject.Observer` | Corregido tipo anidado y realizaciones por dos observadores |
| UML-04 | Alta | Anotación de Swing afirma que Observer no está implementado | `LockChangeSubject`, `GatePresentationObserver`, `LockSoundObserver` y suscripciones reales en `GameController` | Eliminada la afirmación anacrónica; distinguido Observer de mutación del dominio |
| UML-05 | Alta | Loading contiene nota «no Swing integration / no runtime special entities or gameplay» | `LevelProgression` carga EXTENDED y construye `RuntimeState.fromExtended`; `GameController` lo consume | Actualizado alcance; el core de carga sigue independiente de Swing por diseño |
| UML-06 | Alta | Runtime dice que ICE/Lock/muros son exclusivamente programáticos | `ExtendedLevelParser`, `ExtendedLevelDefinition`, `LevelLoader.loadExtended`, `RuntimeState.fromExtended` son una ruta TXT real | Añadido flujo TXT → definición → runtime |
| UML-07 | Media | `GameSession` aparece anotada «Swing not connected» | `GameController` depende de `LevelProgression`; éste usa `GameSession` y publica UI | Actualizada frontera: no dependencia inversa Swing desde core |
| UML-08 | Media | `LevelLoader.loadExtended(Path)` no figura como método | Método público existente, explícito, que delega en `new ExtendedLevelParser().parse(readText(path))` | Incorporado |
| UML-09 | Media | Se desdibuja la naturaleza inmutable de runtime y colecciones | `RuntimeState` es `record` que copia listas, valida lock IDs y muros; `RuntimeState.Box`, `.Lock`, `.DynamicWall` son records anidados | Referencias de valor y listas; no gráfico de objetos mutables |
| UML-10 | Media | Diagrama R1 se titula «no gameplay GUI», aun cuando ya contiene sesión y progresión | `LevelProgression` y `GameController` conectados con Swing R7/R9 | Diagramas renombrados por frontera técnica presente |
| UML-11 | Media | No se evidencia que la UI posee dos tableros | `GameWindow` instancia `board` y `replayBoard` por separado | Cardinalidad 2 en la vista desktop |
| UML-12 | Media | Falta la naturaleza exacta de Command | `MoveCommand` es un **record concreto**, no implementa una interfaz `Command`; `ReplayPlayer` invoca `execute` sobre `RuntimeMovementEngine` | Command concreto, sin interfaz ficticia |
| UML-13 | Media | Evento Observer suele confundirse con `RuntimeMoveResult.LockChange` | `LockChangeSubject` publica su **propio** `Event`/`Delta`, calculado tras transiciones reales | Separados los tipos y responsables |
| UML-14 | Media | `GameWindow` no declara su identidad de clase interna privada | `SwingApplication.GameWindow` es `private static final class` que extiende `JFrame` e implementa `GameView` | Se representa visibilidad y realización |
| UML-15 | Media | El modelo histórico no distingue Guardar/Recuperar de Undo | `GameSession` implementa Undo con `Snapshot` privado y `UndoManager`; `UiCommands` mantiene Save/Load sin callbacks reales | No se dibuja persistencia como implementada |

Los identificadores en esta tabla son **hallazgos de diseño/documentación**, no defectos de ejecución del juego; no se propone alterar Java para acomodar el UML.

## 3. Código → modelo: decisiones defendibles

### TXT y frontera de entrada

`LevelLoader` es un objeto con dependencia inyectada a `LevelParser` para formato básico. Los métodos experimentales de carga especial construyen sus parsers concretos, comparten `readText` (máximo 65.536 bytes, UTF-8 estricto) y devuelven tipos distintos. `ExperimentalFragileLevel` es sólo definición espacial con resistencias, **no** es un `Level` jugable. `ExtendedLevelDefinition` añade terreno ICE, Key, Lock y Wall; `RuntimeState.fromExtended` crea la representación jugable.

`LevelBuilder` es concreto y package-private: construye incrementalmente un `Level` y valida por `Board`. Se justifica como **Builder simplificado**; no existe un `Director`, familia de builders ni interfaces adicionales. Se debe evitar sobreactuar los roles GoF.

### Runtime y reglas

El dominio representa ocupación en `RuntimeState` (record inmutable); sus `BoxKind` son `NORMAL`, `FRAGILE`, `KEY`, **no subclases con herencia ficticia**. Las puertas se abren a partir del estado de KeyBox en Lock y referencias por ID contenidas en `DynamicWall.lockIds`. `RuntimeMovementEngine` devuelve un resultado inmutable, calcula deslizado y resistencia, y rechaza la transición de cierre de muros ocupados. **No** tiene observadores ni mantiene una sesión mutable.

`MovementEngine` con `Board` es ruta heredada de cajas normales, no el motor de campaña R9. No mezclar ambos como si se ejecutaran secuencialmente.

### Memento

`GameSession` es el originator mutable y guarda dos referencias de estado (`initial`, `current`) más métricas. Su `private record Snapshot(...) implements GameMemento` es la representación opaca. `UndoManager` package-private aloja hasta **15** tokens, sin leer sus atributos. La restauración se efectúa en `GameSession`. `undoUsesTotal` queda deliberadamente **fuera** del memento; Undo restaura movimientos/empujes, no elimina penalización. El límite `MAX_UNDO_USES=3` y `UNDO_STEPS=5` son constantes de sesión.

### Strategy y coordinación

`LevelProgression` recibe `ScoreStrategy` por constructor y elige cuándo finalizar después de victoria. `WeightedPenaltyScoreStrategy` implementa la interfaz, como record con configuración de penalizaciones. La fórmula por defecto es `max(0,10000 - 10*moves - 25*pushes - 100*undoUsesTotal)`, implementada con resta saturada para evitar overflow. Es **decisión experimental individual**, no cifra ratificada por el equipo. `LevelProgression` crea su sesión, loader y evaluador; no tiene dependencias de Swing.

### MVC

`SwingApplication.GameWindow` implementa `GameView` y posee los widgets. `GameController` refiere a `GameView`, coordina `LevelProgression`, emite `GamePresentation` (record con `RuntimeState`) y adapta `LevelResult` para presentar el score. `BoardPanel` renderiza sin decidir victoria ni modificar muros. La implementación muestra **separación práctica Model/View/Controller**; no corresponde afirmar que todas las dependencias cumplen DIP: `GameController` todavía crea concreciones como `WeightedPenaltyScoreStrategy`.

### Abstract Factory

`VisualThemeFactory` produce **dos productos relacionados**: `UiPalette` (record anidado) y `SpritePresentation` (interfaz funcional anidada). La clase privada abstracta `VisualThemes.Family` implementa esa interfaz y la extienden `VisualThemes.Copper`, `.Night` y `.Jade`. La familia elegida cambia presentación, no el estado de juego. Las clases con sufijo `Theme` en el UML anterior no existen con esos nombres.

### Command y Replay

`MoveCommand` encapsula `Direction` y tiene `execute(RuntimeMovementEngine,RuntimeState)`. `ReplayRecorder` recopila movimientos aceptados y ajusta la traza ante Undo/Restart; `ReplayTrace` conserva una lista inmutable y estados inicial/final. `ReplayPlayer` posee **otro** motor y cursor mutable, ejecuta los comandos y verifica la equivalencia final. No es un video ni persistencia de partida y no depende del motor Swing para avanzar estado. Es aplicación concreta de la **intención** Command, sin interfaz genérica `Command`.

### Observer

`LockChangeSubject` implementa `AutoCloseable`, mantiene lista de `LockChangeSubject.Observer` y publica `Event` con `Origin` y deltas únicamente después de transiciones confirmadas. `GameController` construye y suscribe `GatePresentationObserver` y `LockSoundObserver`; ambos dependen de `GameView`. El primero solicita repintado y el segundo sonido durante MOVE. **La regla Lock → muros sigue siendo del runtime**, no de Observer; eso evita estado de dominio duplicado.

## 4. Calificación prudente de principios

| Principio / patrón | Alcance defendible | No afirmar |
|---|---|---|
| SRP | Loading separado de runtime, progresión separada de Swing, BoardPanel centrado en render | Que cada clase tenga responsabilidad perfecta |
| OCP | `ScoreStrategy` y familias `VisualThemeFactory` permiten variación | Que todo el sistema sea cerrado para modificación |
| DIP | `GameController` recibe `GameView`; `LevelProgression` recibe `ScoreStrategy` | Que el controlador no dependa de concreciones (`WeightedPenaltyScoreStrategy`) |
| GRASP Controller | `GameController` recibe acciones de UI y delega reglas | Que concentra toda la lógica de negocio |
| GRASP Creator | Objetos se construyen en contextos que los coordinan, por ejemplo Session desde LevelProgression | Que todo `new` es automáticamente Creator |
| Memento | `Snapshot` privado implementa token opaco, UndoManager caretaking | Que existe guardado en disco |
| Strategy | `ScoreStrategy` inyectado y política configurable | Que la fórmula fue aceptada por el equipo |
| Abstract Factory | Familias visuales con paleta y sprites | Que hay clases top-level `CopperTheme` etc. |
| Command | Cada `MoveCommand` ejecuta una dirección sobre motor real | Que existe una interfaz `Command` o macro comandos genéricos |
| Observer | Subject/Observer real para notificación | Que el Subject controla el estado de las puertas |

## 5. Artefactos de esta revisión

Archivos nuevos **externos al repositorio Git**:

1. `01_Carga_TXT_AS_IMPLEMENTED.puml`: límites de carga legacy/C2/Fragile/EXTENDED.
2. `02_Runtime_Movimiento_AS_IMPLEMENTED.puml`: estado, terreno y transiciones inmutables.
3. `03_Sesion_Patrones_AS_IMPLEMENTED.puml`: Memento, Strategy y coordinación.
4. `04_Desktop_Extensiones_AS_IMPLEMENTED.puml`: MVC, Abstract Factory, Command, Observer.
5. `verificar_diagramas.py`: chequeo estático conservador de nombres, extremos de flechas y delimitadores.

El verificador encontró: **98 declaraciones de tipo** (con vistas repetidas) y **121 relaciones** en cuatro archivos, sin alias duplicados ni extremos no declarados. **No** reemplaza a PlantUML: en este sandbox no se identificó compilador PlantUML ni instalación StarUML.

Los diagramas reflejan una revisión de firmas y relaciones seleccionadas; **NO se etiquetan como UML académico final ni como `.mdj` aprobado**. No se modificó el repositorio remoto.

## 6. Pendientes antes de declarar R10 PASS

1. Completar el contraste sistemático de miembros de las 50 fuentes (en particular los intervalos todavía no leídos de `SwingApplication`) y auditar las 14 unidades de prueba para verificar evidencia de cada colaboración, sin confundir inventario de tipos con cobertura de métodos.
2. Validar sintaxis de cada `.puml` con un compilador PlantUML real y corregir errores de layout o sintaxis si aparecen.
3. Justificar cada agregación/composición con constructor, campos y ciclo de vida; evitar inferir ownership sólo por una llamada de método.
4. Seleccionar el conjunto de diagramas de clases legibles para StarUML, basados en el modelo revisado, sin introducir el candidato histórico como implementación.
5. Construir archivo `.mdj` desde un esquema real de StarUML y abrir/guardar/reabrir en la aplicación. Sin ese paso, marcar `STARUML_NATIVE_OPEN=NOT_RUN`.
6. Revalidar con revisión humana los detalles de cardinalidad, firmas y nombres para defensa, sin adjudicar aceptación académica externa.

**Dependencias externas sin cerrar:** `RF12_ACADEMIC_ACCEPTANCE=NOT_CONFIRMED`, `TEAM_ACCEPTANCE=NOT_RECORDED`, `ASSET_RIGHTS=PENDING_EXTERNAL_EVIDENCE`. Ninguna se resuelve mediante un diagrama.

**Siguiente acción de mayor valor:** consolidar la matriz completa de identidad y relaciones, renderizar los PlantUML con el compilador real y preparar la transferencia del modelo canónico a StarUML, donde será obligatoria la apertura/edición nativa antes de dar R10 por terminada.
