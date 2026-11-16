# Responsabilidades y principios de diseño

Las responsabilidades se asignan al componente que conoce los datos o coordina el caso de uso. El modelo no conoce ventanas, sonido ni colores; la vista no modifica las reglas del tablero.

| Componente | Responsabilidad | Colaboración principal |
|---|---|---|
| `LevelLoader` y parsers de `level` | Leer bytes y validar la definición de un nivel | Publican `Level`, `ExperimentalFragileLevel` o `ExtendedLevelDefinition` |
| `LevelBuilder`, `Board`, `Cell`, `Position` | Construir y proteger valores espaciales válidos | Usados por parsers y proyecciones |
| `RuntimeState` | Representar el mundo completo y consultar cajas, cerrojos y muros | Recibido/devuelto por el motor; proyectado a `Board` |
| `RuntimeMovementEngine` | Resolver movimiento, empuje, hielo, fragilidad y cierre seguro | Produce `RuntimeMoveResult` |
| `GameSession` | Conservar estado inicial/actual, historial y métricas | Delega reglas al motor y restaura snapshots |
| `VictoryEvaluator` | Consultar destinos y cajas para determinar victoria | Usado por progresión/controlador |
| `LevelCatalog`, `LevelProgression`, `DemoCampaign` | Cargar el catálogo y avanzar, preservando resultados | Comparten los mismos cinco TXT entre menú y pruebas |
| `ScoreCalculator`, `LevelResult` | Calcular y transportar el resultado final | Alimentan HUD/resumen sin lógica de puntaje en la vista |
| `GameController` | Coordinar entrada, carga, sesión y publicación | Se comunica con `GameView`, Recorder y Subject |
| `GameView`, `GamePresentation`, `UiCommands` | Definir datos y acciones de la frontera de presentación | Permiten vistas Swing y sondas de prueba |
| `SwingApplication`, `BoardPanel`, `BoxAnimation` | Pantallas, dibujo, entrada y animación del recorrido recibido | Trabajan sobre EDT y cancelan timers obsoletos |
| `VisualThemeFactory`, `VisualThemes` | Proveer familias de productos visuales coherentes | Consumidas por Controller/Applier de temas |
| `MoveCommand`, `ReplayRecorder`, `ReplayTrace`, `ReplayPlayer` | Registrar, validar y ejecutar la repetición aislada | `ReplayPanel` controla un cursor independiente |
| `LockChangeSubject` y sus observadores | Comunicar cambios confirmados a interfaz y sonido | Sin autoridad para decidir el mundo |
| `PixelAssets`, `SoundManager` | Cargar imágenes y administrar clips | Recursos de classpath, sin dependencia del core |

## SOLID

- **SRP:** leer TXT, resolver un empuje, administrar un intento y dibujar tienen motivos de cambio distintos; por eso se separan Loader, motor, sesión y vista.
- **OCP:** las variantes visuales se expresan por `VisualThemeFactory`. Los consumidores usan sus productos sin distribuir condiciones por cada tema. Una nueva familia puede requerir registrarla y probarla; OCP no significa que ningún archivo de composición cambie.
- **LSP:** las familias respetan los contratos de paleta e imagen, incluyendo preservación del canal alfa. Una vista de prueba puede implementar `GameView` sin cambiar la semántica de las acciones. No se inventa una jerarquía de cajas que obligue a todas a tener resistencia.
- **ISP:** `VisualThemeFactory`, `SpritePresentation` y `LockChangeSubject.Observer` definen capacidades concretas. Las operaciones de presentación se mantienen en `GameView`, no se trasladan al núcleo.
- **DIP:** `GameController` recibe `GameView` y la presentación de temas consume `VisualThemeFactory`. No todo vínculo concreto necesita una interfaz; el motor conserva dependencias concretas estables donde no existe una variación útil.

## GRASP

**Information Expert:** `RuntimeState` dispone de cajas, terreno y relaciones para consultar el mundo; `VictoryEvaluator` reúne esa información para evaluar objetivos. `GameSession` conoce historial y métricas para Undo, sin pedir a Swing que los reconstruya.

**Controller:** `GameController` recibe eventos de la interfaz y delega en sesión, carga y progresión. **Creator:** parsers/Builder crean definiciones válidas, y la progresión crea estados runtime desde esas definiciones. **Low Coupling y High Cohesion:** Replay utiliza el motor sin acceder a la partida activa; el core se prueba sin Swing.

**Polymorphism y Protected Variations:** los productos visuales se obtienen por la misma frontera de fábrica. **Indirection:** `GameView` y los eventos Observer separan coordinación de presentación concreta. **Pure Fabrication:** `ScoreCalculator` concentra un cálculo puro que no corresponde al panel visual.

Estos principios describen elecciones y sus límites; no son una afirmación de perfección del diseño. Las alternativas y costes están en [decisiones](../decisions/DECISION_LOG.md) y los ejemplos verificables en [trazabilidad](../traceability/TRACEABILITY_MATRIX.md).
