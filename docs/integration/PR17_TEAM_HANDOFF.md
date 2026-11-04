# Guía de transferencia — Sokoban PR #17

La versión integrada en `main` reúne los aportes del equipo y las decisiones documentadas en el [PR #17](https://github.com/smatrauade/Los-Force-Push/pull/17). Esta guía sirve para ejecutar la campaña, recorrer las responsabilidades y preparar la puesta en común, el ensayo y los ajustes que surjan.

La historia preserva las contribuciones de `victoria` (KeyBox/Lock y progresión), `Objetivo2` (ICE/Fragile) y `feature/undo-restart-score` (Undo/Restart/score). Los temas, Replay y Observer incluyen adaptaciones selectivas de `impaciencia`, rama individual divergente; no atribuirla globalmente al equipo.

## Ejecutar y recorrer la campaña

El PR #17 está fusionado a `main`; clonar el repositorio por defecto. Se requiere un JDK completo 17 o superior; los runners compilan con `--release 17`, UTF-8 y lint estricto. No se necesitan Maven ni Gradle.

```bash
git clone https://github.com/smatrauade/Los-Force-Push.git
cd Los-Force-Push
java -version
javac -version
bash scripts/run-swing.sh
```

En Windows PowerShell: `powershell -NoProfile -File ./scripts/run-swing.ps1`. En Bash usar un checkout sin espacios; el runner usa `java` y `javac` de PATH. En Windows puede seleccionarse un JDK con `SPIKE_JDK_HOME`. Los comandos no certifican QA Windows actual.

Desde **NUEVA PARTIDA / DEMO**, los cinco TXT son [demo](../../src/desktop/resources/levels/demo.txt), [campaign-b](../../src/desktop/resources/levels/campaign-b.txt), [ice_demo](../../src/desktop/resources/levels/ice_demo.txt), [fragile_demo](../../src/desktop/resources/levels/fragile_demo.txt) y [fragile-ice-demo](../../src/desktop/resources/levels/fragile-ice-demo.txt). Cubren caja normal, KeyBox/Lock/muros, hielo, resistencia Fragile y rotura tras deslizar. Flechas mueven, Z deshace cinco movimientos válidos (máximo tres usos), R reinicia, Esc pausa, M cambia sonido y +/- zoom. El HUD y resultado muestran métricas y score TEAM. Replay en el resumen ofrece Play/Pause/Step/Reset/Back sin modificar la sesión. Guardar/cargar partidas sigue deshabilitado.

TXT externo: `bash scripts/run-swing.sh '/ruta/al/nivel.txt'` o `powershell -NoProfile -File ./scripts/run-swing.ps1 -LevelPath 'C:/niveles/nivel.txt'`. Ver sintaxis y soluciones de los cinco niveles en [README](../../README.md).

Headless Bash: `bash scripts/test-integration-i2.sh` y `bash scripts/test-replay-i3d1.sh`. Windows: `powershell -NoProfile -File ./scripts/run-swing.ps1 -Test`. Integridad documental: `bash scripts/verify-repo-readonly.sh --require-clean` (Git, Bash y Python). No confundir suites ni atribuirles ejecución Windows que no ocurrió.

## Arquitectura y patrones

`level` valida TXT; `RuntimeState` y `RuntimeMovementEngine` conservan mundo y transiciones; `GameSession` es autoridad de Undo y métricas; `LevelProgression` administra victoria/avance. `GameController` coordina; `GameView` separa la vista; `SwingApplication`/`BoardPanel` presentan; Replay tiene cursor y tablero propios. MVC separa modelo, coordinación y presentación; GRASP asigna responsabilidad a quienes poseen la información; SOLID se aplica mediante fronteras de presentación y cambios de tema sin cambiar reglas. [Arquitectura](../architecture/ARCHITECTURE.md) y [responsabilidades](../architecture/RESPONSIBILITIES.md).

- **Abstract Factory:** [VisualThemeFactory](../../src/desktop/java/ar/edu/uade/sokoban/desktop/VisualThemeFactory.java), [VisualThemes](../../src/desktop/java/ar/edu/uade/sokoban/desktop/VisualThemes.java), [VisualThemeController](../../src/desktop/java/ar/edu/uade/sokoban/desktop/VisualThemeController.java) y [VisualThemeApplier](../../src/desktop/java/ar/edu/uade/sokoban/desktop/VisualThemeApplier.java) coordinan paleta y sprites de tres temas. Alternativa: condicionales en cada widget, con más duplicación. Coste: clases de familia y restauración. Evidencia: `VisualThemeTest`.
- **Command:** [MoveCommand](../../src/main/java/ar/edu/uade/sokoban/replay/MoveCommand.java), [ReplayRecorder](../../src/main/java/ar/edu/uade/sokoban/replay/ReplayRecorder.java), [ReplayTrace](../../src/main/java/ar/edu/uade/sokoban/replay/ReplayTrace.java) y [ReplayPlayer](../../src/main/java/ar/edu/uade/sokoban/replay/ReplayPlayer.java): acciones exitosas se reproducen con motor y estado aislados. Alternativa: snapshots por paso, con más memoria. Coste: verificación lineal y límite 10.000. Evidencia: Replay core y tests de controlador/presentación. Los callbacks UI no son este Command.
- **Observer:** [LockChangeSubject](../../src/desktop/java/ar/edu/uade/sokoban/desktop/LockChangeSubject.java) publica cambios confirmados desde [GameController](../../src/desktop/java/ar/edu/uade/sokoban/desktop/GameController.java) a presentación/audio. Alternativa: llamadas directas repetidas en cada transición; coste: gestión de suscriptores. `RuntimeState` deriva los muros; listeners no controlan física. Evidencia: `LockObserverTest`.

Builder es simplificado. `ScoreCalculator` no acredita Strategy, `SoundManager` no es Singleton y los snapshots de Undo no acreditan Memento GoF formal. No declarar Decorator o Prototype por nombres.

## UML y evidencia

[Índice de nueve vistas, fuentes y PNG](../architecture/uml/pr17-rc-ffa27f9/README.md); [manifiesto](../architecture/uml/pr17-rc-ffa27f9/RENDER_GATE.json) y trazabilidad CSV/TSV en la misma carpeta. Verificar con `python3 -B scripts/verify-uml-pr17-readonly.py --root "$PWD" --verify-java`. Las vistas representan colaboraciones seleccionadas del código congelado `ffa27f9`. PlantUML y PNG no acreditan por sí solos el requisito académico de StarUML.

La [auditoría funcional](PR17_FUNCTIONAL_RC_2026-11-04.md) registra el código `0bbc60e`, comandos y resultados Linux/JDK17: Replay 20, Undo 58, core 258 y suites desktop; GUI automatizada Linux 17+7 y 12 clips por dispositivo. Son ejecuciones anteriores, no nuevas pruebas de esta guía; no acreditan escucha, revisión humana ni Windows.

## Pendientes y contribución

Por rol: arquitectura debe preparar `.mdj` y QA nativa StarUML; titulares deben documentar derechos de 43 assets (RF-11); equipo/docente debe confirmar elegibilidad de selector y Replay (RF-12/OQ-10); QA debe validar visualmente, escuchar audio y probar Windows; equipo debe preparar informe y declaración IA, video, defensa y entrega. Otras dimensiones OQ permanecen en [OPEN_QUESTIONS](../requirements/OPEN_QUESTIONS.md). Ningún merge concede esas aprobaciones.

Para un cambio posterior: actualizar `main` desde el remoto, crear una rama `feature/...`, implementar y ejecutar las pruebas pertinentes, y abrir un PR nuevo hacia `main`. Conservar ramas históricas y procedencia; no trabajar directamente en main.

Cada integrante debe poder clonar y ejecutar el juego, cargar un TXT, recorrer la campaña, mostrar Undo/Restart/score y Replay, explicar MVC y los tres patrones con sus participantes y alternativas, y distinguir las pruebas automáticas de las validaciones humanas pendientes.
