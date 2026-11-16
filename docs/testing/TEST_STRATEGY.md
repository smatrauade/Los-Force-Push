# Pruebas y verificación

El proyecto usa programas de prueba Java con comprobaciones explícitas y runners que propagan el código de salida. El core se puede verificar sin Swing; las pruebas de ventana requieren escritorio gráfico. Los verificadores documentales necesitan Git, Bash y Python 3.9+; estas herramientas adicionales no son requisitos para jugar.

## Evidencia Windows del 13/11/2026

La ejecución histórica usó Windows 11 x64, JDK 26.0.1 y compilación `--release 17`, UTF-8 y `-Xlint:all -Werror`. Código probado/grabado: `ee2681148f8dde7425968b61b19b631e5532bb04`. Esta referencia identifica la evidencia y no fija el HEAD actual.

| Runner | Resultado histórico |
|---|---|
| `powershell -NoProfile -File .\scripts\test-level-loading.ps1 -IncludeMovement` | Core: 147 comprobaciones aprobadas, exit 0 |
| `powershell -NoProfile -File .\scripts\run-swing.ps1 -Test` | Desktop: 177 comprobaciones aprobadas, exit 0 |
| `powershell -NoProfile -File .\scripts\run-swing.ps1 -UiTest` | Ventana automatizada: 24 comprobaciones aprobadas, exit 0 |

Las [salidas Core](../../entregables/evidencia/core.txt), [Desktop](../../entregables/evidencia/desktop.txt) y [UI](../../entregables/evidencia/ui.txt) se conservan sin editar. El [registro de ejecución](../../entregables/VALIDACION_WINDOWS.md) detalla suites y campaña. No se suman repeticiones como nuevos casos ni se presentan estas cifras como pruebas ejecutadas por editar documentación.

El video documenta cinco niveles terminados, temas, Replay, Undo, Restart y apertura de TXT externo. La captura fue automatizada; señal AAC y decodificación correcta no equivalen a escucha humana. Compilar a release 17 con JDK 26 no acredita una ejecución nativa sobre JDK 17.

## Ejecución de pruebas

Desde la raíz del clon y con un JDK completo 17+ seleccionado según el README:

```powershell
powershell -NoProfile -File .\scripts\test-level-loading.ps1 -IncludeMovement
powershell -NoProfile -File .\scripts\run-swing.ps1 -Test
```

Para pruebas de ventana, ejecutar `powershell -NoProfile -File .\scripts\run-swing.ps1 -UiTest` en un escritorio. Los runners Bash disponibles son `test-level-loading.sh`, `test-integration-i1.sh`, `test-integration-i2.sh` y `test-replay-i3d1.sh`; sus nombres conservan la evolución del proyecto, sin cambiar su propósito de prueba. Los runners Bash de integración y Replay exigen específicamente Java 17 en sus controles de entorno. El runner Swing Bash ofrece `--test`, `--ui-test` y `--audio-test`. No ejecutar suites completas por una modificación exclusivamente editorial.

## Cobertura por responsabilidad

| Área | Ejemplos de pruebas |
|---|---|
| TXT | `LevelLoadingTest`, `C2LevelLoadingTest`, `ExperimentalFragileParserTest`, `ExtendedLevelTest`: sintaxis, límites, IDs, atributos, UTF-8, recarga y errores |
| Movimiento | `MovementTest`, `DynamicWorldTest`, `RuntimeIceFragileTest`: bloqueo, hielo, resistencia, rotura y cierre seguro |
| Sesión | `GameSessionUndoTest`, `CompletionTest`: historial 15/5/3, métricas, reinicio, victoria y avance |
| Replay | `ReplayIntegrationTest`, `ReplayControllerTest`, `ReplayPresentationTest`: comandos, trazas inválidas, aislamiento, límites y cancelación |
| Presentación | `VisualThemeTest`, `AnimationPresentationTest`, `DesktopVisualAssetsTest`: familias coherentes, alfa, timers y recursos |
| Campaña y eventos | `ShippedLevelsIntegrationTest`, `ReleaseCampaignTest`, `LockObserverTest`, `ControllerBoundaryTest`: catálogo, soluciones, avisos y victoria al cargar |

## Integridad documental

```bash
bash scripts/verify-repo-readonly.sh --require-clean
python -B scripts/check-repo-context.py --local
python -B scripts/verify-uml-pr17-readonly.py --root . --verify-java
python -B scripts/test-verify-repo-readonly.py
```

En Windows, utilizar Git Bash (no WSL) y una instalación Python 3.9+ ya disponible. Se puede indicar su ruta con `--bash` en la regresión. El verificador inspecciona estructura Git, documentos y enlaces locales, secretos, residuos, formatos y hashes de los 43 assets, nueve pares UML y archivos de entrega. Los modos de integridad no compilan el juego ni evalúan aceptación humana, licencias o formato nativo de otro editor.

La regresión altera únicamente copias temporales y exige que fallen hashes corruptos, PNG/WAV inválidos, manifiestos incoherentes, enlaces rotos, archivos no autorizados y secretos sintéticos. Las rutas y anchors se validan sobre el árbol actual, sin depender de bitácoras o ramas históricas. `git diff --check` y `git diff --cached --check` completan la revisión de espacios.

## Lanzadores de defensa — 16/11/2026

Sobre la base `04cfceb5fa1e56e319254d39bcb989472172bc32` y el incremento documental de entrega: `bash -n` para los tres lanzadores Bash, dos pruebas negativas aisladas (modo inválido y TXT ausente), verificador de repositorio y regresión del verificador: 31 escenarios PASS, exit 0. Los negativos incluyen PPTX sin una de sus nueve diapositivas y MDJ sin sus nueve diagramas aun actualizando el hash.

En Ubuntu, con la pareja java/javac OpenJDK 17.0.21.0.3, se ejecutaron `bash INICIAR_JUEGO.sh` y `bash DEMO_CARGAR_TXT.sh`. Ambas ventanas abrieron y cerraron normalmente (exit 0). Una sonda temporal en EDT comprobó componentes visibles: menú con NUEVA PARTIDA/DEMO y modo TXT con BoardPanel, DEMO.TXT, contadores cero y score 1000. La conexión X11 requirió acceso fuera del sandbox. La captura del escritorio falló; no se acredita evaluación visual humana ni escucha. No se repitieron suites de gameplay. Los lanzadores CMD/PowerShell se revisaron estáticamente; doble clic Windows NOT_RUN en esta misión.

El manifiesto plano exige 38 archivos, incluido StarUML; los binarios académicos históricos y las fuentes/PNG UML conservan bytes. La [guía de operación](../demo/DEFENSA_OPERACION.md) describe requisitos y el ensayo pendiente en el equipo/proyector final.
