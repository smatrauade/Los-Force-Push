# R10 — Modelo de clases y trazabilidad UML ↔ Java (sandbox, revisión técnica)

**Proyecto:** Los Force Push · UADE · Procesos de Desarrollo de Software
**Repositorio:** https://github.com/smatrauade/Los-Force-Push/tree/impaciencia
**Fuente inmutable:** `e7d4c33f3fb17bd3a72bca25ddee1ad37f529087`
**Corte:** 31 de octubre de 2026
**Resultado:** `PARTIAL_VERIFIED_UML_MODEL` — sin validación de apertura/guardado en StarUML.

## 1. Qué se realizó realmente

1. Verificación de GitHub: `origin/impaciencia` en `e7d4c33f3fb17bd3a72bca25ddee1ad37f529087` y `origin/main` en `f4a28fd6c1d869fcd6f6189e5cbc2da49bb2f7a2`, ambos observados sin modificaciones durante la auditoría.
2. Inspección de las **50 fuentes Java de producción** mediante el conector GitHub en la revisión anterior; lectura íntegra de 49 y de fragmentos estructurales seleccionados de `SwingApplication.java`. En esta continuación se confirmó código y diseño relevante con la evidencia conservada. No se afirma AST exhaustivo ni reejecución local Java.
3. Análisis de los 3 PlantUML `AS_IMPLEMENTED` de origen; `sokoban-candidate.puml` se mantiene separado como TO_BE, no se usa como implementación.
4. Corrección de 15 discrepancias históricas documentadas en `AUDITORIA_ARQUITECTURA_R10_PREVIA.md`. Ninguna requirió modificar gameplay.
5. Nueve vistas de UML en PlantUML: resumen general, carga TXT, runtime, sesión/patrones, desktop completo y cuatro vistas docentes enfocadas (MVC, Abstract Factory, Command, Observer).
6. Generación local de un **borrador nativo StarUML `.mdj`** siguiendo el formato JSON y la separación `Model`/`View` documentados por StarUML y contrastando un ejemplo oficial `Book.mdj`. No se ha abierto ese archivo con StarUML. Incluye `82` elementos de clase/interfaz/enum —incluye tipos anidados y externos JDK— y `185` objetos de relación (pueden representar el mismo par con propósitos/etiquetas distintos).
7. Comprobación **independiente de estructura JSON**, identidades únicas, contención parent, referencias modelo-vista y extremos: `2421` objetos con ID, `3755` referencias resueltas, **0 colgantes**. Esta comprobación no acredita compatibilidad de serialización o renderizado por la app.
8. Diagramas de previsualización `.svg`/`.png` generados con **Graphviz DOT**. Son mapas de conectividad (flechas simplificadas), *no* render de PlantUML ni evidencia de semántica visual StarUML.

## 2. Fuentes y autoridad

- Consigna docente aportada, `PDS - TI - Prof Pepe.pdf`: entregable "Diagrama de clases en StarUML"; evaluación SOLID, GRASP, MVC, GoF; dos funcionalidades adicionales asociadas a patrones. No requiere afirmar identidad entre diagramas candidatos y código experimental.
- Clase de UML aportada, `Introducción al Lenguaje de Modelado Unificado (UML).pdf`: distingue asociaciones estructurales, dependencias, generalización, agregación y composición. Las decisiones gráficas deben respetar esa semántica.
- Código Java del commit indicado (fuente más fuerte para hechos AS_IMPLEMENTED).
- Evidencia Windows R9W.1, `docs/testing/IMPACIENCIA_WINDOWS_COMPATIBILITY.md` y README de la rama: regresiones reportadas sobre Windows 11 con JDK 26 `--release 17`; no se reejecutaron en este sandbox.
- Documentos `STARUML_STATUS.md`, `IMPACIENCIA_MASTERPLAN.md`, `ARCHITECTURE.md`, `DECISION_LOG.md`, `OPEN_QUESTIONS.md`, `REQUIREMENTS.md`: se usaron para separar hechos, propuestas y decisiones.
- Formato `.mdj`: [documentación StarUML del modelo/vista](https://docs.staruml.io/v3/user-guide/basic-concepts) y [ejemplo de autoría StarUML Book.mdj](https://github.com/staruml/staruml-dev-docs/blob/master/samples/Book.mdj).

## 3. Modelo canónico y flujo de ejecución

**Ruta real de TXT y juego:** `LevelLoader.load/loadExtended` → `LevelParser` o `ExtendedLevelParser` → `Level`/`ExtendedLevelDefinition` → `RuntimeState.fromLevel/fromExtended` → `LevelProgression` → `GameSession` → `RuntimeMovementEngine` → `RuntimeMoveResult` → `GameController` → `GameView`/`GamePresentation` → `BoardPanel`.

Las transiciones no modifican `BoardPanel` para decidir el estado: el runtime es el único autor de las reglas de ocupación. `MovementEngine` legacy existe, pero no es el motor principal del gameplay extendido. La instancia `GameWindow` es privada y anidada en `SwingApplication`.

**Regla de victoria:** `VictoryEvaluator` comprueba que existan Goals y cajas y que el número de cajas, goals y goals cubiertos coincida. La fórmula de score saturada por defecto es `max(0, 10000 − 10*moves − 25*pushes − 100*undoUsesTotal)`. Ambas son decisiones experimentales de la rama individual, sin ratificación docente/grupal.

## 4. Patrones: intención, roles y prueba

| Patrón | Roles concretos y motivación | Cota de la afirmación |
|---|---|---|
| **Builder simplificado** | `LevelParser`/`C2LevelParser` configuran `LevelBuilder.addRow/build`; éste construye `Level` usando `Board` validado. | No existe Director ni jerarquía de builders; alternativa construcción directa. |
| **Memento** | Originator: `GameSession`. Token: `GameMemento` package-private. Snapshot real: `GameSession.Snapshot` privado. Caretaker: `UndoManager` (historial hasta 15). | `undoUsesTotal` NO está en snapshot; Undo no equivale a persistencia en disco. |
| **Strategy** | `ScoreStrategy.calculate(LevelStats)` inyectada en `LevelProgression`; implementación `WeightedPenaltyScoreStrategy`. | Política sustituible, pero fórmula actual no ratificada. |
| **Abstract Factory** | `VisualThemeFactory` produce par compatible `UiPalette` + `SpritePresentation`. `VisualThemes.Family` concreta parcialmente la factory; `Copper`, `Night`, `Jade` heredan de Family. | Las tres clases son *privadas anidadas*, no `CopperTheme`/`NightTheme`/`JadeTheme`. |
| **Command** | `MoveCommand(Direction)` encapsula `execute(engine,state)`; `ReplayRecorder` guarda comandos; `ReplayTrace` congela trayectoria; `ReplayPlayer` invoca con motor propio. | Sin `Command` interface ni comandos deshacer persistidos. |
| **Observer** | `LockChangeSubject.Observer`; implementadores `GatePresentationObserver`, `LockSoundObserver`; `GameController` suscribe y notifica transiciones confirmadas. | Observer notifica a la vista/audio, no gobierna aperturas de muros ni es el tipo `LockChange` del motor. |
| **MVC** | `GameController` adapta eventos de `GameView` y delega en `LevelProgression`; `GamePresentation` es record de sólo lectura; `BoardPanel` pinta. | No etiquetar el sistema entero como DIP puro: el controlador crea implementaciones concretas de scoring. |

**GRASP y SOLID defendibles:** Controller, Creator (asignado contextualmente, no todo `new`), cohesión y acoplamiento por fronteras; SRP por separación loading/runtime/desktop; OCP de políticas de score/temas; DIP de `GameView` y `ScoreStrategy`. No afirmar satisfacción universal de SOLID.

## 5. Relaciones y modelado del formato Java

- `Class--Component` puede expresar posesión por campo y ciclo de vida; se modela como composición sólo cuando el propietario crea y retiene realmente la instancia (`GameSession`→`UndoManager`, `GameWindow`→dos `BoardPanel`). Es una *interpretación de diseño*, no recolección de basura exclusiva ni permiso para eliminar alias.
- Los `record` inmutables que contienen listas (`RuntimeState`→`Box`, `Lock`, `DynamicWall`) se modelan mediante asociación y multiplicidad; no representar una jerarquía de subtipos para `BoxKind`.
- `RuntimeState.DynamicWall` guarda `lockIds : Set<String>`: es una referencia por identificador, **no** campo de objeto `RuntimeState.Lock`.
- `GameController` conoce `LevelProgression`, y `LevelProgression` conoce `GameSession`; no invertir esa dependencia desde el core hacia Swing.
- Los tipos anidados aparecen calificados con el nombre externo (p. ej. `LockChangeSubject.Observer`), para no confundirlos con tipos top-level. El archivo `.mdj` generado conserva las identidades FQN en `documentation`; la representación nativa de propiedad de miembros anidados debe revisarse con StarUML.
- `UiCommands.save` y `loadSaved` son slots sin implementación operativa; no se dibuja un repositorio de partidas persistidas ficticio.

## 6. Cobertura bidireccional de clases

`COBERTURA_JAVA_UML.md` comprueba mediante inventario independiente de GitHub que el proyecto UML cubre **50/50 archivos Java de producción**, sin paths ausentes ni inventados. Los 82 tipos UML incluyen tipos anidados y 4 JDK. Esta prueba **no significa 100% de atributos, operaciones ni asociaciones**, ni validación nativa StarUML.

## 7. Controles y límites

| Gate | Resultado |
|---|---|
| HEAD remoto GitHub y `main` preservada | VERIFICADO, sólo lectura |
| Revisión firma/identidad de clases centrales | VERIFICADO POR INSPECCIÓN, no AST 100% |
| PlantUML: delimitadores y extremos | PASS del generador y verificador estático |
| `.mdj`: JSON válido | PASS (Python `json`) |
| `.mdj`: IDs únicos, parent y refs | PASS (2421 IDs / cero colgantes) |
| `.mdj`: modelos de clases y relaciones | DRAFT — `82` tipos y `185` relaciones |
| StarUML: abrir, editar, guardar, reabrir | **NOT_RUN — gate obligatorio antes de entrega** |
| Compilador PlantUML real | NOT_RUN; las previsualizaciones usan Graphviz |
| Java unit tests ejecutados aquí | NOT_RUN; se conserva evidencia anterior Windows |
| `RF12_ACADEMIC_ACCEPTANCE` | NOT_CONFIRMED |
| `TEAM_ACCEPTANCE` | NOT_RECORDED |
| `ASSET_RIGHTS` | PENDING_EXTERNAL_EVIDENCE |
| Repositorio escrito por esta misión | NO; todos los archivos sólo en sandbox |

El hecho de que un modelo `.mdj` sea un JSON bien formado **no** garantiza que StarUML lo abra ni que los diagramas se rendericen correctamente. Por eso el nombre es `DRAFT` y `R10` no se declara PASS.

## 8. Plan recomendado de cierre (dominio UML únicamente)

1. **Sandbox completado:** selección de diseño, corrección de identidad, cuatro diagramas principales, cuatro focalizados, matriz de relaciones, borrador `.mdj`, verificador autónomo y previsualizaciones.
2. **Una sola comprobación local mínima:** abrir `Los_Force_Push_R10_AS_IMPLEMENTED_DRAFT.mdj` desde StarUML; inspeccionar las nueve vistas, incluida la identidad de interfaces y las asociaciones; probar editar un nombre temporal, deshacer, guardar como copia, cerrar y reabrir. No confiar en confirmación textual sin apertura real. Si falla, informar pantalla de error + log; no rehacer modelos manualmente.
3. **Consolidación aquí:** integrar errores observados, corregir el `.mdj` desde el sandbox hasta verificar apertura; cuando los diagramas estén validados, preparar cambios Git limitados a UML/documentación y revisar diff antes de publicación.
4. **No iniciar R11** antes de aprobar el UML. Licencias/elección extras/QA humano se gestionan por separado.

## 9. Estado compacto para continuidad

```text
MISSION=SOKOBAN_IMPACIENCIA_R10_UML_SANDBOX
RESULT=PARTIAL_VERIFIED_UML_MODEL
SOURCE_SHA=e7d4c33f3fb17bd3a72bca25ddee1ad37f529087
UML_MODEL_TYPES=82
UML_UNIQUE_RELATIONSHIPS=185
UML_DIAGRAMS=9
STARUML_MDJ_JSON=PASS
STARUML_IDS_AND_REFS=PASS
STARUML_NATIVE_OPEN=NOT_RUN
PLANTUML_NATIVE_RENDER=NOT_RUN
GRAPHVIZ_PREVIEWS=PASS
GITHUB_MODIFIED=false
MAIN_MODIFIED=false
NEXT_GATE=STARUML_NATIVE_OPEN_SAVE_REOPEN
```
