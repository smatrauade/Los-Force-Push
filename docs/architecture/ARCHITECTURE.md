# Arquitectura del juego

La aplicación separa lectura de niveles, estado del mundo, reglas de movimiento, sesión y presentación. El núcleo usa Java sin dependencias de Swing. La interfaz coordina eventos discretos sobre el EDT; no necesita un ciclo continuo de simulación.

## Flujo MVC

```text
Flechas / botones de Swing
    -> GameController
    -> LevelProgression / GameSession
    -> RuntimeMovementEngine -> RuntimeState
    <- GamePresentation / RuntimeMoveResult / LevelResult
    -> GameView -> BoardPanel, HUD, animación y sonido
```

El modelo decide movimiento, resistencia, cerrojos, muros y victoria. `GameController` recibe intenciones, delega y publica resultados. `GameView` define la frontera de presentación; `SwingApplication` implementa las pantallas y `BoardPanel` dibuja. Esto permite sustituir la vista por sondas de pruebas sin abrir un `JFrame`.

## Una autoridad del mundo

`RuntimeState` representa terreno, jugador, cajas con identidad/resistencia, cerrojos y asociaciones de muros. Las transiciones producen estados inmutables nuevos. `Board` se utiliza como representación ordinaria y proyección visual; no es una segunda grilla mutable que decida reglas.

`RuntimeMovementEngine.move` aplica colisiones y trayectorias. Un intento bloqueado conserva el estado. Un empuje en hielo puede recorrer varias celdas y cuenta como un movimiento/empuje; la fragilidad se resuelve al terminar el trayecto. Antes de publicar se comprueba que los muros que cierran no queden ocupados. El renderizador recibe el recorrido autoritativo en `RuntimeMoveResult` y no recalcula la física.

La inmutabilidad simplifica Undo y Replay y evita que una animación modifique la partida. Su coste es copiar y validar datos del tablero en las transiciones; los límites de 100×100 acotan la entrada. No se presenta como una optimización para tableros arbitrariamente grandes.

## Carga y validación

`LevelLoader` limita la lectura a 65.536 bytes y aplica UTF-8 estricto. `LevelParser`/`C2LevelParser` procesan niveles ordinarios; `ExperimentalFragileParser` conserva identidad y resistencia; `ExtendedLevelParser` interpreta KeyBox, Lock y muros. Estos nombres son los de las APIs existentes. `LevelBuilder` acumula filas y publica un tablero válido.

El controlador intenta las rutas estrictas de carga sin inferir el formato del nombre del archivo. Las conversiones `RuntimeState.fromExperimentalFragile` y `fromExtended` preservan los datos especiales. La plantilla Fragile sola no representa las cajas. Ante un error de lectura o validación, se conserva la sesión anterior. El [contrato TXT](../requirements/TXT_FORMAT.md) especifica formatos y límites.

## Sesión, Undo, Restart y victoria

`GameSession` conserva el estado inicial/actual, hasta 15 snapshots y métricas por intento. Cada snapshot contiene mundo, movimientos y empujes. Undo consume cinco snapshots y un uso; su total y el límite de tres usos no se restauran desde el snapshot. Restart vuelve al estado inicial y limpia historial y métricas.

`VictoryEvaluator` exige destinos y cajas, igual cantidad de ambos y todos los destinos cubiertos. KeyBox sobre Lock no reemplaza Goal. `LevelProgression` administra el catálogo, conserva resultados terminados y avanza. `ScoreCalculator` calcula el resultado saturado en cero; no se requiere una jerarquía Strategy para una sola fórmula.

## Temas mediante Abstract Factory

`VisualThemeFactory` entrega dos productos coordinados: `UiPalette` y `SpritePresentation`. `VisualThemes` ofrece Original Cobre, Nocturno y Jade. `VisualThemeController` aplica la familia al tablero, Replay, menú y controles; `VisualThemeApplier` restaura colores y bordes al volver al original.

Los temas alternativos transforman imágenes en memoria y conservan alfa. `PixelAssets` invalida la caché por familia. Los PNG del repositorio y el estado de juego no cambian. Frente a condicionales por tema distribuidos en cada componente, esta colaboración concentra la variación; agregar una familia exige definir productos compatibles y mantener sus pruebas.

## Replay mediante Command

`MoveCommand` es un comando inmutable con una `Direction`; `execute(engine, state)` usa el motor como receptor. `ReplayRecorder` registra éxitos y ajusta la secuencia ante Undo/Restart. `ReplayTrace` congela y verifica el recorrido. `ReplayPlayer` ejecuta comandos sobre su propio estado y motor; nunca recibe la sesión activa.

`ReplayPanel` ofrece Play/Pause/Step/Reset/Back. Su timer invalida callbacks al pausar, navegar, reiniciar o cerrar. La verificación tiene coste lineal y un límite de 10.000 movimientos; una traza inválida deshabilita solo Replay. Almacenar snapshots de todos los pasos duplicaría el historial; reproducir sobre la sesión alteraría puntaje y progreso. Un comando concreto alcanza mientras exista un único tipo de acción reproducible.

## Observer y presentación

`LockChangeSubject` compara estados confirmados y publica eventos inmutables de cambios de cerrojos y muros. `GameController` publica después de mostrar el estado; dos suscriptores actualizan avisos y sonido. Un error de presentación no revierte una transición ya confirmada. Las suscripciones se retiran al cerrar.

Los muros son derivados por `RuntimeState`, no por listeners. Así se conserva el mismo resultado al mover, deshacer, reiniciar o reproducir. `BoardPanel` proyecta la animación con un timer cancelable; Undo, Restart, carga exitosa y navegación cancelan recorridos anteriores. `SoundManager` administra clips por ventana y permite continuar sin dispositivo de audio; no es Singleton.

## Principios y consecuencias

La [asignación de responsabilidades](RESPONSIBILITIES.md) detalla SRP, OCP, LSP, ISP, DIP y GRASP sobre clases reales. Las [decisiones](../decisions/DECISION_LOG.md) justifican las alternativas. La carga de archivos locales es síncrona y acotada, por lo que un filesystem lento puede demorar el EDT. No se promete carga asíncrona ni persistencia.

Los [nueve diagramas](uml/pr17-rc-ffa27f9/README.md) muestran colaboraciones seleccionadas. Sus fuentes, PNG y referencias Java tienen verificaciones de integridad; no pretenden enumerar todos los métodos existentes.
