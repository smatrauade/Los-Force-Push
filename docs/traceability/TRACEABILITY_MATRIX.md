# Trazabilidad de requisitos y pruebas

La matriz relaciona el alcance académico con el código presente y los oráculos de prueba. Los resultados de ejecución se consultan en [pruebas](../testing/TEST_STRATEGY.md); nombrar una suite no implica haberla ejecutado de nuevo.

| Requisito | Código responsable | Pruebas y evidencia |
|---|---|---|
| RF-01: mundo y entidades | `Board`, `RuntimeState`, definiciones en `level` | `ExtendedLevelTest`, `RuntimeIceFragileTest`, `DynamicWorldTest` |
| RF-02: TXT externo | `LevelLoader`, parsers, `GameController` | `LevelLoadingTest`, `C2LevelLoadingTest`, `ExperimentalFragileParserTest`; carga externa en video |
| RF-03: caminar/empujar | `RuntimeMovementEngine`, `GameSession` | `MovementTest`, `DynamicWorldTest`, `DesktopIntegrationTest` |
| RF-04: hielo | `RuntimeMovementEngine`, `RuntimeMoveResult` | `RuntimeIceFragileTest`, `AnimationPresentationTest`; `ice_demo.txt` |
| RF-05: fragilidad individual | `ExperimentalFragileLevel`, `RuntimeState`, motor | Parser Fragile, `RuntimeIceFragileTest`; `fragile_demo.txt`, `fragile-ice-demo.txt` |
| RF-06: llaves/cerrojos/muros | `ExtendedLevelParser`, `RuntimeState`, motor | `ExtendedLevelTest`, `DynamicWorldTest`, `LockObserverTest`; `campaign-b.txt` |
| RF-07: Undo | `GameSession` | `GameSessionUndoTest`: disponibilidad, snapshots, restauración de métricas y límite de usos |
| RF-08: victoria/campaña | `VictoryEvaluator`, `LevelProgression`, `DemoCampaign` | `CompletionTest`, `ReleaseCampaignTest`, `ControllerBoundaryTest` |
| RF-09: resumen/score | `ScoreCalculator`, `LevelResult`, `LevelProgression.CompletedLevel` | `DesktopIntegrationTest`, `ReleaseCampaignTest` |
| RF-10: HUD/controles | `SwingApplication`, `GameView`, `UiCommands` | `DesktopUiSmokeTest`, `ReleaseUiSmokeTest` |
| RF-11: imágenes/sonido | `PixelAssets`, `SoundManager`, recursos de classpath | `DesktopVisualAssetsTest`, `NativeAudioSmokeTest`; [inventario de 43 archivos](../punto5/ASSETS_REGISTRO_SHA256.csv) |
| RF-12: temas y Replay | `VisualThemeFactory` y colaboración; `MoveCommand` y colaboración | `VisualThemeTest`, `ReplayIntegrationTest`, `ReplayControllerTest`, `ReplayPresentationTest` |
| RT-01: Java/Swing | Core y desktop separados, runners PS1/Bash | [Requisitos y ejecución](../../README.md) |
| RT-02: materiales de entrega | README, informe, portada, video, diagramas | [Índice con formatos disponibles](../../entregables/README.md) y manifiesto |
| RT-03: diseño y defensa | MVC, principios y patrones con participantes reales | [Arquitectura](../architecture/ARCHITECTURE.md), [responsabilidades](../architecture/RESPONSIBILITIES.md), [decisiones](../decisions/DECISION_LOG.md) |
| RT-04: declaración de IA | Informe técnico y procedencia de recursos | [Declaración](../punto5/USO_IA_Y_LICENCIAS.md) e informe |

La comprobación de integridad de RF-11 identifica bytes y formatos; no constituye una certificación jurídica. La matriz tampoco atribuye aprobación docente ni envío de correo. La consigna y el alcance del producto se presentan en [requisitos](../requirements/REQUIREMENTS.md).

Fuentes: [core](../../src/main/java/ar/edu/uade/sokoban), [desktop](../../src/desktop/java/ar/edu/uade/sokoban/desktop), [tests core](../../src/test/java/ar/edu/uade/sokoban), [tests desktop](../../src/desktop-test/java/ar/edu/uade/sokoban/desktop).
