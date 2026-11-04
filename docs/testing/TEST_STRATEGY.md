# Estrategia de pruebas y evidencia experimental

## I3.D.1 — Replay/Command puro, sin GUI (04/11/2026)

Ejecutar `bash scripts/test-replay-i3d1.sh` desde el repositorio con OpenJDK 17. El runner compila el **core completo** y la nueva prueba con `--release 17 --limit-modules java.base -encoding UTF-8 -Xlint:all -Werror`, luego ejecuta `ReplayIntegrationTest`. Suite propia: **20 comprobaciones** (Command, bloqueo, ICE, Fragile, KeyBox/Lock/DynamicWall, Undo 5, Restart, 10.001 operaciones, trazas divergentes/alteradas, métricas, inmutabilidad, cursor aislado y reset). Exigir `REPLAY RESULT: 18 PASS, 0 FAIL`, exit 0 y preservar HEAD/worktree.

Al cerrar el candidato en la notebook, volver a ejecutar `bash scripts/test-integration-i2.sh` (Undo/Core/Desktop/Campaña/Temas/Animación/Assets) y `bash scripts/verify-repo-readonly.sh --require-clean` sobre el commit candidato; en sandbox sin remoto, el verificador precisa un overlay temporal de configuración Git de **solo lectura**. No declarar nuevas cifras ni QA gráfica/Windows/TEAM hasta ejecutar esos controles sobre el commit exacto y registrar SHA, actor, JDK, exit y logs. La compilación aislada del sandbox de ChatGPT con contratos auxiliares no acredita el árbol integrado completo.

## I3.C — animación ICE/Fragile (04/11/2026)

Nueva suite headless [AnimationPresentationTest](../../src/desktop-test/java/ar/edu/uade/sokoban/desktop/AnimationPresentationTest.java), ejecutada por `bash scripts/run-swing.sh --test` y por transitividad por `bash scripts/test-integration-i1.sh`/`test-integration-i2.sh`. Cubre 14 escenarios: orden exacto de `RuntimeMoveResult.boxPath` sobre ICE; ausencia de caja fantasma en el destino final mediante comparación de píxeles por celda; Fragile resistencia 1 sobre ICE con última celda, `box_broken` y ausencia; Fragile de una sola celda; Fragile resistencia 2 con ID/resistencia originales; KeyBox sobre ICE con Lock/DynamicWall intactos; bloqueo y movimientos sin empuje sin animación; `ICE`/`FRAGILE` una vez por transición sin duplicar `PUSH`/`GOAL`; publicación del runtime final antes de animar y victoria headless inmediata; diferimiento de FINISH durante la animación; cancelación por Undo, Restart y recarga; cancelación explícita sin callbacks obsoletos; un único `javax.swing.Timer` no bloqueante en EDT; tema/zoom durante la animación con mundo y frames conservados y tema original restaurado; y hashes SHA-256 de los PNG sin cambios.

La suite no reemplaza las suites existentes: `DesktopIntegrationTest` continúa en 40 casos, `ShippedLevelsIntegrationTest` en 41 y `DesktopVisualAssetsTest` en 44. Evidencia local con OpenJDK 17.0.20.1, release 17, UTF-8, `-Xlint:all -Werror`, `java.awt.headless=true`: Undo 58, core 258, Desktop 40, Campaña 41, Tema 7, Animación 14 y Assets 44 PASS; `bash scripts/test-integration-i2.sh` exit 0 y `GIT_STATE_PRESERVED=YES`. El runner exige `ANIMATION RESULT: [1-9][0-9]* PASS, 0 FAIL`, de modo que una suite vacía o fallida rompe el gate.

Límites: la suite pinta componentes en `BufferedImage` y avanza frames de forma determinista; no abre una ventana interactiva ni acredita percepción visual, audio audible, Windows/macOS ni aceptación TEAM. `GUI_NATIVE_QA=NOT_RUN`; INT-09 no queda cerrado por QA nativa. [Aceptación y riesgos](../integration/INTEGRATION_ACCEPTANCE.md).

## I3.B — selector de estilo visual (04/11/2026)

Nueva suite headless [VisualThemeTest](../../src/desktop-test/java/ar/edu/uade/sokoban/desktop/VisualThemeTest.java), ejecutada por `bash scripts/run-swing.sh --test` y por transitividad por `bash scripts/test-integration-i1.sh`/`test-integration-i2.sh`. Cubre siete escenarios: tres familias estables y diferenciadas; sprites alternativos deterministas de 32×32 con canal alfa idéntico al PNG; tema original idéntico byte a byte al PNG y reversible tras alternar; cambio de familia sobre tableros ICE, Fragile y Lock conservando `RuntimeState`, movimientos, pushes, Undo y zoom; selección Swing aplicada en EDT sobre tablero, botones, etiquetas y `PixelTheme.Surface`; rechazo de entradas nulas sin estado a medias ni caché invalidada; y comparación de hashes SHA-256 de los PNG antes/después, que deben permanecer iguales.

La suite no reemplaza las suites existentes: `DesktopIntegrationTest` continúa en 40 casos, `ShippedLevelsIntegrationTest` en 41 y `DesktopVisualAssetsTest` en 44. Evidencia local con OpenJDK 17.0.20.1, release 17, UTF-8, `-Xlint:all -Werror`, `java.awt.headless=true`: Undo 58, core 258, Desktop 40, Campaña 41, Tema 7 y Assets 44 PASS; `bash scripts/test-integration-i2.sh` exit 0 y `GIT_STATE_PRESERVED=YES`. Los umbrales de los runners exigen `THEME RESULT: [1-9][0-9]* PASS, 0 FAIL`, de modo que una suite vacía o fallida rompe el gate.

Límites: la suite pinta componentes en `BufferedImage`, no abre una ventana interactiva; no acredita percepción visual, audio, Windows/macOS ni aceptación TEAM. El selector es candidato a adicional RF-12/OQ-10 y no cierra la elegibilidad de extras. `GUI_NATIVE_QA=NOT_RUN`. [Aceptación y riesgos](../integration/INTEGRATION_ACCEPTANCE.md).

## I3.A — aceptación headless de niveles distribuidos (04/11/2026)

Se incorporó `ShippedLevelsIntegrationTest` al runner `scripts/run-swing.sh --test`, y por transitividad al runner I2. Prueba mediante `GameController` real el circuito `demo.txt` → `campaign-b.txt` (victoria, score 993/983, reset y retención histórica), `fragile_demo.txt` (ID/resistencia, solución de tres empujes, score 991 y carga fallida atómica) e `ice_demo.txt` (deslizamiento a Goal, un empuje, score 996). Regresión Java 17 y verificador documental son gates obligatorios antes del commit. Las pruebas son headless: no acreditan animación, interacción visual nativa, derechos de assets ni aceptación TEAM. I3/I4 siguen abiertos y `main` no queda fusionado.

## Incremento victoria — 2026-11-01

Rama `victoria`. Core `java.base`, desktop `java.base,java.desktop`, Temurin 17.0.16, `--release 17`. Comandos: `powershell -NoProfile -File ./scripts/test-level-loading.ps1 -IncludeMovement` y `powershell -NoProfile -File ./scripts/run-swing.ps1 -Test`. Resultados: Loading 40, Movement 31, Extended 26, DynamicWorld 6, Completion 36, C2 32, Fragile 41, Desktop 16, VisualAssets 44, todos PASS/exit 0. GUI manual y audio audible: NOT_RUN. No cierra OQ ni TEAM_ACCEPTANCE.

## QA combinado PR 8 y PR 9 — 2026-10-28

**Evidencia anterior a este mantenimiento**, ejecutada en SOKOBAN_POST_PR9_INTEGRATION_R1 por Codex sobre la referencia de merge de prueba 99783067a51dea8867924a629daf7fb017cae5ff y su árbol 1796932307df319e3636daee942cf49920b4eb05. Padres: main fafae98642cefd38e466c689b3c8a16825773738 y PR #8 289fef6eda97a2bbd5593c74f06382ae6caf433c. El merge real de #8, 565376452affa9b2146569e5da27dbfd1ee9d4f0, conserva exactamente ese árbol; la equivalencia se comprobó en R2, no se atribuye otra ejecución funcional al merge.

Windows nativo, Temurin java/javac 21.0.12.1, --release 17, -encoding UTF-8, -Xlint:all -Werror; core sólo java.base y desktop java.base,java.desktop. Exportación limpia de Git, sin clases previas, ZIP originales ni rutas privadas. Los 130 archivos exportados coincidieron con blobs usando core.autocrlf=false.

| Comando/procedimiento ejecutado en R1 | Resultado | Exit |
|---|---|---|
| powershell -NoProfile -File scripts/test-level-loading.ps1 -IncludeMovement | Loading 40/40 y Movement 31/31 | 0 |
| Compilación core estricta y C2LevelLoadingTest, con fixtures src/test/resources/levels | C2 32/32 | 0 |
| powershell -NoProfile -File scripts/run-swing.ps1 -Test | Desktop 16/16, VisualAssets 44/44 | 0 |
| powershell -NoProfile -File scripts/run-swing.ps1 -UiTest | UI Smoke 17/17, ventana Windows automatizada | 0 |
| powershell -NoProfile -File scripts/test-level-loading-arguments.ps1 | CLI 18/18 entre PS5.1 y PS7 | 0 |
| SHA-256 del manifiesto, cabeceras PNG y decodificación WAV | 43/43; 31 PNG y 12 WAV | 0 |
| Suites DesktopIntegrationTest y DesktopVisualAssetsTest con demo equivalente escrito como C2 | Repetición 16/16 y 44/44; Loader → GameController → Board/render | 0 |

Los 44 controles de VisualAssets son 29 sprites base + 12 WAV + tres controles de panel/escala. Las dos variantes opcionales se incluyen en el manifiesto de 43 recursos y su validación de formato/hash. Las repeticiones C2 y CLI no suman nuevos casos Java únicos.

NOT_RUN en R1: interacción manual nueva, escucha de altavoces, GUI Linux y macOS. Xvfb no estaba instalado. La reproducción no prueba derechos CC0 ni ratifica gramática, patrones, OQ o aceptación grupal. Logs/capturas completos no están versionados; el registro normalizado identifica comandos, árbol y límites.

## Ratificación funcional 01/11/2026 — Undo, Restart y score

**Fuente histórica de Martina:** esta sección describe su rama original, no la arquitectura o ejecución del árbol combinado I2. La evidencia actual está al final de este documento.

`TEAM_ACCEPTANCE = NOT_RECORDED`.

La ratificación del 01/11/2026 dejó fijadas las reglas funcionales que coinciden con la implementación actual de `GameController`, `ScoreCalculator`, `LevelResult` y la conexión de `UiCommands`, pero sin convertir esas reglas en aceptación formal del equipo. Esta sección conserva la distinción entre: (a) reglas ratificadas, (b) implementación efectiva en código, y (c) escenarios de prueba realmente cubiertos por la suite actual.

### Regla ratificada y su reflejo en la implementación

- Undo retrocede cinco movimientos exitosos por uso; en `GameController.undo()`, si `canUndo()` es true, se extraen los últimos cinco snapshots del historial (`history.removeLast()`) y se reutiliza el último de esos snapshots removidos como estado restaurado. Eso vuelve al estado de cinco movimientos exitosos antes del intento actual.
- Se conserva un historial máximo de 15 snapshots; `history` usa `ArrayDeque` y elimina el más antiguo cuando supera `HISTORY_LIMIT = 15` durante `move()`.
- Se permiten hasta tres usos consecutivos de Undo; `MAX_UNDO_USES = 3` y `canUndo()` usa `consecutiveUndoUses < MAX_UNDO_USES`.
- Undo restaura el tablero, `moves` y `pushes`; los tests `UNDO restores state from five moves ago` y `UNDO restores pushes counter` verifican ese comportamiento en `DesktopIntegrationTest`.
- `undoUsesTotal` acumula los usos de Undo durante el intento; `GameController.getLevelResult()` expone el valor calculado, y el test `GameController exposes current statistics and score` valida la propagación de `undoUses` en `LevelResult`.
- Un movimiento normal no reinicia el límite de usos consecutivos; `move()` no borra `consecutiveUndoUses`.
- Cada uso de Undo incrementa `consecutiveUndoUses`; `undo()` hace `consecutiveUndoUses++` después de restaurar el snapshot.
- `restart()` y `load()` reinician ese contador; `restart()` pone `consecutiveUndoUses = 0` y `load()` también limpia la sesión antes de renderizar un nivel nuevo.
- Restart restaura el estado inicial y reinicia `moves`, `pushes`, `undoUsesTotal` y `consecutiveUndoUses`; el test `RESTART restores initial state and resets counters` verifica ese comportamiento.
- El score parte de 1000 puntos, descuenta 1 por movimiento, 2 adicionales por empuje y 10 por uso de Undo, con un mínimo de cero; la fórmula la ejecuta `ScoreCalculator.calculate()`, y `GameController.getLevelResult()` expone el resultado calculado.
- Restart no agrega una penalización; `restart()` vuelve a `initial` y limpia contadores/historial, pero no ejecuta una penalización de score adicional.

### Implementación efectiva actual

La implementación vigente y verificable queda en:

- `GameController`: `history`, `move()`, `undo()`, `restart()`, `canUndo()`, `refreshUiCommands()` y `getLevelResult()`, así como la actualización de `undoUsesTotal` y `consecutiveUndoUses`.
- `ScoreCalculator`: calcula el score con `INITIAL_SCORE = 1000`, penalidades por movimiento, empuje y Undo, y limita el resultado a un mínimo de cero mediante `applyPenalty()`.
- `LevelResult`: registro para `levelName`, `moves`, `pushes`, `undoUses`, `score`.
- `UiCommands`: contrato de callbacks `undo` y `restart` que `GameController.refreshUiCommands()` crea y que `SwingApplication.configureUiCommands()` usa para habilitar/deshabilitar los botones.

La conexión real del UI está confirmada en el código de Swing: `SwingApplication.configureUiCommands(UiCommands)` activa `undoButton` y `restartButton` según `value.undo()` y `value.restart()`, y `installKeys()` enlaza la tecla `Z` con `commands.undo()` y la tecla `R` con `commands.restart()`. El smoke test `DesktopUiSmokeTest` valida que esos callbacks se habilitan y se ejecutan desde la vista.

### Evidencia y alcance de cobertura real

Los resultados generales `DESKTOP RESULT: 30 PASS, 0 FAIL` y `VISUAL ASSETS RESULT: 44 PASS, 0 FAIL` son evidencia válida de integración general del desktop y de assets, pero no reemplazan la cobertura específica de cada regla de Undo, Restart y score.

La suite actual con casos específicos que sí existen en `DesktopIntegrationTest` es la siguiente:

- `UNDO restores state from five moves ago`: verifica el retroceso de cinco movimientos exitosos y la restauración del tablero y del contador de pushes.
- `UNDO restores pushes counter`: verifica la restauración del número de empujes.
- `UNDO can be used only three times`: verifica el límite de tres usos consecutivos y la prohibición del cuarto Undo.
- `RESTART restores initial state and resets counters`: verifica que resetee el tablero, `moves`, `pushes`, `undoUsesTotal` y `consecutiveUndoUses`.
- `UNDO is unavailable with fewer than five moves`: verifica que no se ejecute Undo cuando no hay cinco movimientos disponibles.
- `BLOCKED movement is not added to undo history`: verifica que un movimiento bloqueado no se agregue al historial y que el Undo posterior restaura el estado válido previo.
- `SCORE starts at 1000 points`: caso base.
- `SCORE penalizes each move`: aplica la penalidad por movimiento.
- `SCORE applies additional penalty for pushes`: aplica la penalidad extra por empuje.
- `SCORE penalizes Undo uses`: verifica la penalidad por Undo.
- `SCORE never goes below zero`: limita el valor a un mínimo de cero.
- `SCORE handles maximum moves without overflow`: resguardo de overflow por movimientos extremos.
- `SCORE handles maximum pushes without overflow`: resguardo de overflow por empujes extremos.
- `SCORE handles maximum Undo uses without overflow`: resguardo de overflow por usos extremos de Undo.
- `SCORE rejects negative statistics`: validación de entrada negativa.
- `GameController exposes current statistics and score`: valida la propagación de `moves`, `pushes`, `undoUses` y `score` desde el controlador hasta `LevelResult`.

No existe un caso nombrado que ejerza explícitamente la regla de que un movimiento normal no reinicia el límite de usos consecutivos ni un test aislado de `Restart no agrega penalización`; esa semántica está implementada en `GameController` y puede inferirse de la lógica, pero no dispone de una prueba específica con nombre separado en la suite actual. Del mismo modo, no se cierran aquí cuestiones pendientes sobre el efecto del cambio de nivel sobre otras métricas; esta sección solo documenta la regla ratificada del score y la implementación real de Undo/Restart en el intento vigente.

### Ejecución del conjunto relevante

Los casos anteriores se ejecutan como parte del runner Swing del repositorio. En este worktree quedó registrado el comando real `bash scripts/run-swing.sh --test`, y el resultado global se usa como evidencia de integración del desktop, sin presentarlo como prueba individual de cada regla.

## Verificador del repositorio — mantenimiento R2, 2026-10-28

Base integrada: 565376452affa9b2146569e5da27dbfd1ee9d4f0, árbol funcional citado arriba. Esta misión cambia documentación/UML y verificación, **cero archivos del juego, tests Java, recursos o runners funcionales**. El SHA final de mantenimiento se obtiene del historial/PR; no se inserta dentro del documento que lo contiene.

El verificador es de sólo lectura. Selecciona Python 3.9+ ya instalado, primero python3 y luego python; necesita Git y Bash. Inspecciona indexados y untracked, incluidos ignorados, para que .gitignore no oculte residuos. Mantiene UTF-8 estricto, escaneo de secretos, enlaces, cuotas de 10 MiB y allowlist histórica de bootstrap. PNG/WAV se admiten sólo en las rutas autorizadas, con nombre/hash/manifiesto únicos, formato válido y detección de faltantes/no registrados. PNG verifica chunks/CRC y descompresión acotada; WAV verifica RIFF/PCM y longitud. El CSV no acredita licencias.

~~~bash
bash scripts/verify-repo-readonly.sh
# Tras commit, en checkout limpio:
bash scripts/verify-repo-readonly.sh --require-clean
python3 -B scripts/test-verify-repo-readonly.py
~~~

Git Bash/Windows puede usar python. Desde PowerShell, seleccionar el Bash real si PATH apunta a WSL:

~~~powershell
python -B ./scripts/test-verify-repo-readonly.py --bash 'C:/Program Files/Git/bin/bash.exe'
~~~

La [regresión versionada](../../scripts/test-verify-repo-readonly.py) ejecuta el script real en copias Git temporales: **16 escenarios, dos positivos y 14 negativos**. Cubre PNG truncado/CRC corrupto con hash actualizado, WAV inválido con hash actualizado, hash distinto, manifiesto duplicado, faltante/no registrado, binario fuera de ruta/desconocido, texto UTF-8 inválido, enlace roto, residuo ignorado y patrón sintético de secreto. Prueba además el bootstrap histórico (exportación del merge #1 con el verificador actual trasplantado) y el rechazo de un untracked fuera de ese alcance. No modifica históricos ni utiliza secretos reales.

Ejecución nueva R2: **16/16 PASS, exit 0 en Git Bash/Windows con Python 3.10 y en Ubuntu 24.04 WSL con Bash 5.2, Git 2.43 y Python 3.12.3**. Cada negativo exige exit no cero y la categoría de error esperada; los positivos exigen exit 0. No es una nueva ejecución de las suites funcionales. macOS del verificador: NOT_RUN; las dependencias son biblioteca estándar Python/Git/Bash.

Bash -n y el parseo AST de ambos bloques Python: PASS. PlantUML instalado, con Java 21 y -checkonly sobre los dos diagramas modificados: exit 0. Se comprobó sintaxis; no se afirma render nuevo ni validación en StarUML. Las fuentes/tests Java, recursos, runners funcionales, manifiesto y snapshots históricos permanecen idénticos a la base integrada.

## Lectura de los registros anteriores

Los apartados siguientes conservan el plan de producto y las evidencias de sus fechas. Los estados operativos antiguos de ramas/PR y las menciones a placeholders/audio ausente corresponden a esos baselines. Para capacidades integradas actuales ver [CURRENT_STATE](../continuity/CURRENT_STATE.md), [README](../../README.md) y [contratos Punto 5](../punto5/INTEGRACION_EQUIPO.md).

Revisado 2026-10-21 (recuperación Swing R2 y revisión de consistencia R3). El snapshot contiene 40 casos de Level Loading y 31 de movimiento; la base histórica del bootstrap precede a esas suites. La tabla siguiente conserva el **plan de aceptación del producto completo**, no resultados ni negación de la cobertura parcial ejecutable descrita después. Herramienta/framework y Java definitivos dependen de OQ-11. Core se prueba sin Swing.

| Área | Casos previstos | Gate |
|---|---|---|
| Carga | TXT válido creado por otra persona; modificar carácter y recargar; archivo faltante; símbolo desconocido; filas/dimensiones; cero/varios jugadores; metadata/resistencia y IDs inválidos | OQ-06/07/08; gramática ratificada |
| Movimiento | Libre; pared; límites; muros cerrados/abiertos; una caja; dos cajas; no tirar; intento inválido sin mutación | RF-03/04 |
| Hielo | Franja corta/larga; primer espacio normal libre; jugador no desliza; pared/caja/muro al salir | OQ-02 para salida bloqueada |
| Fragile | Decremento por empuje; cero; sin resistencia negativa; interacción con hielo; Undo/Restart | OQ-03/06 |
| Key/Lock | Llave activa, normal no activa; muros asociados; retirar llave; varios cerrojos; Undo restaura muros y suscripciones | OQ-01/07 |
| Undo | N-5/N-10/N-15; historial >15; cuarto uso; <5; nuevo movimiento/intento inválido; fragile/muros; snapshots aislados; Restart/métricas | OQ-04/05 y D-008 |
| Victoria/progreso | Todas las cajas; llave/cerrojo; próximo nivel y final del catálogo | OQ-01 y decisión de catálogo |
| Score | Ejemplos tabulados; determinismo; límites; Undo si penalización ratificada | OQ-09 |
| Extras | Pruebas positivas/bordes/invalidez y evidencia del patrón | OQ-10 |
| Presentación | Flechas, imágenes de todo elemento, HUD, botones, resumen, audio | Build ejecutable y assets auditados |

Una mecánica se cierra con regla documentada, ejemplo positivo, caso límite, invalidación/transición atómica, implementación identificable y evidencia ejecutada. Un nombre de clase o UML no cumple aceptación.

Para comparar Builder y construcción directa, mantener mismo contrato TXT e invariantes, probar entradas válidas/inválidas y evaluar cohesión, claridad, coste de abstracción y defensa. No elegir el patrón porque el spike compila.

La evidencia [histórica](HISTORICAL_EVIDENCE.md) no sustituye estas pruebas. Una ejecución nueva registra fecha, commit, entorno/comando, casos, resultado y limitaciones. Manual Swing cubre representación/interacción, no reemplaza pruebas de reglas.

Para documentación actual: `bash scripts/verify-repo-readonly.sh`, enlaces/rutas, archivos requeridos, checks Git, identidad de históricos, revisión manual de autoridad/decisiones/evidencia. No instalar CI ni asumir toolchain Java.

## Spike experimental C2-L7 — 2026-10-24

`EXPERIMENTAL_NOT_RATIFIED`, rama experimental `spike/level-txt-c2-l7-r1`, base `5bf6b6524251aee00788be4ae37ade623f4dc745`. La ejecución inicial siguiente se realizó localmente antes del commit, sobre el contenido modificado real del worktree; SHA final en la entrega y correspondencia de blobs comprobada al confirmar. No atribuir las pruebas al HEAD antiguo sin sus modificaciones ni inferir el estado posterior de publicación de esta evidencia inicial.

Ubuntu/Linux, OpenJDK `java`/`javac` 17.0.20.1, release 17, C.utf8, UTF-8, `-Xlint:all -Werror`: **Loading 40/40, Movement 31/31, C2 32/32, Desktop headless 16/16 PASS**, compilaciones y suites con salida 0. Core sólo `java.base`; desktop se compiló aparte con `java.base,java.desktop` y `java.awt.headless=true`. La JVM emitió un aviso de bloqueo de su archivo de métricas `hsperfdata` durante las compilaciones simultáneas; no fue un warning de fuente de javac ni un fallo de compilación/test. Temporales propios de clases/argfiles/tests eliminados. No se ejecutaron Windows, macOS ni GUI interactiva; no se ratifican gramática, Java/build o código.

Los 40 tests históricos y los runners se conservan. `C2LevelLoadingTest` es una suite **independiente**: compilarla no la ejecuta y no cambia los totales 40/31 esperados por la regresión PowerShell. Desde la raíz, con `SPIKE_JDK_HOME` apuntando a un JDK completo existente:

```bash
export SPIKE_JDK_HOME='<directorio-del-JDK-verificado>'
LC_ALL=C.utf8 bash scripts/test-level-loading.sh --movement
(
    set -euo pipefail
    c2_temp="$(mktemp -d /tmp/sokoban-c2-l7.XXXXXXXX)"
    trap 'find "$c2_temp" -type f -delete; find "$c2_temp" -depth -type d -empty -delete' EXIT
    mkdir "$c2_temp/classes"
    find src/main/java/ar/edu/uade/sokoban/level src/main/java/ar/edu/uade/sokoban/movement \
        src/test/java/ar/edu/uade/sokoban/level src/test/java/ar/edu/uade/sokoban/movement \
        -name '*.java' -type f | LC_ALL=C sort > "$c2_temp/sources"
    "$SPIKE_JDK_HOME/bin/javac" --release 17 --limit-modules java.base \
        -encoding UTF-8 -Xlint:all -Werror -d "$c2_temp/classes" @"$c2_temp/sources"
    "$SPIKE_JDK_HOME/bin/java" --limit-modules java.base -Djava.io.tmpdir="$c2_temp" \
        -cp "$c2_temp/classes" ar.edu.uade.sokoban.level.C2LevelLoadingTest src/test/resources/levels
)
```

Desktop: se usó el [procedimiento Linux existente](../experiments/SWING_VERTICAL_SLICE_V1.md#recuperación-linux-r2--2026-10-21), sin modificar Swing ni su suite. C2 prueba aliases/formas largas, tokenización, dimensiones lógicas, límite experimental de 128 caracteres ASCII por token, fallos tardíos, UTF-8/BOM por Loader y cargas independientes. `parse(String)` conserva la ausencia de techo de bytes; Loader sigue limitando 65.536. Header/versión inválidos no vuelven a legacy; `[attributes]` debe estar vacío. Ninguna entidad especial se degrada a BOX/FLOOR.

Revisión técnica por Codex del 24/10: ejecución nueva sobre los blobs de `0195f0e97e2bd4edde422f9acf4586591d75642c`, Ubuntu/Linux, mismo JDK 17.0.20.1 y opciones estrictas anteriores. Runner Bash `--movement`: Loading 40/40 y Movement 31/31; compilación core y ejecución explícita de `C2LevelLoadingTest`: 32/32; procedimiento Linux desktop y `DesktopIntegrationTest` headless: 16/16. Todas las compilaciones y ejecuciones salieron con código 0, sin warnings, secuencialmente y con temporales externos propios eliminados. Sólo se corrigió el alcance temporal de las afirmaciones documentales sobre publicación; fuentes, tests, recursos y runners se conservaron idénticos al SHA probado. No se ejecutaron Windows, macOS ni GUI interactiva; resultados y revisión por IA no sustituyen revisión humana ni ratificación de C2.

## Pruebas ejecutables del experimento local R1

La tabla anterior conserva los gates de producto final. El incremento de carga ensayó un contrato **experimental** sin cerrar OQ-08/OQ-11. [LEVEL_LOADING_R1](../experiments/LEVEL_LOADING_R1.md) registra las ejecuciones históricas de Level Loading R3 de 38 casos y R4 de 40 casos PASS / 0 FAIL en JDK 17 y JDK 26 con archivos reales, UTF-8/EOL, dimensiones, jugadores, errores, determinismo, no publicación parcial, aislamiento e independencia de Swing. Comando: `SPIKE_JDK_HOME='<directorio-del-JDK>' bash scripts/test-level-loading.sh`.

Runner sin dependencias ni assertions desactivables: falla con código no cero si compila con warnings o si falla un caso. Fixtures mutables y clases se crean en un temporal propio fuera del checkout. En esta rama sí hay tests de carga ejecutables; no hay tests productivos de gameplay. La base histórica precede al incremento: reproducir con las fuentes de esta rama. El runner también admite JAVA_HOME si SPIKE_JDK_HOME no está definido. No se ejecutan A/B históricos ni se alteran sus registros.

Level Loading R3 conservó los 30 casos originales, reforzó el 25 y agregó 31–38. La causa de tamaño se distingue de estructura; al desactivar exclusivamente el guard de bytes en una copia temporal, 25/38 fallaron con salida no cero. Ancho/alto/rectangularidad tempranos, nulos y columna negativa están cubiertos. Es evidencia experimental, sin ratificación de OQ-08/OQ-11 ni reproducción de A/B históricos.

Level Loading R4 preservó los 38 casos y agregó 39 (archivo externo con nombre Unicode/espacios, LF/CRLF, piso significativo y recarga) y 40 (diagnóstico estable bajo localización de formato distinta, restaurada al terminar). Linux ejecutó la suite con JDK 17/26, desde otra ubicación y una copia del checkout/TMPDIR con espacios/Unicode; JDK 17 también con `JAVA_TOOL_OPTIONS=-Dfile.encoding=US-ASCII`. El mecanismo de argfile UTF-8 sin BOM y rutas absolutas entre comillas del runner Windows se comprobó con javac en Linux; esto no ejecuta PowerShell ni Windows. Controles negativos del runner Unix: JDK seleccionado inexistente, TMPDIR dentro del checkout y fixture erróneo producen salida no cero; sus temporales propios se limpian.

Windows usa `powershell -NoProfile -File scripts/test-level-loading.ps1` o `pwsh` con el mismo archivo; no duplica tests Java ni necesita Python/Bash. En la evidencia histórica de carga R4, Windows/macOS nativos estaban NOT_RUN; la validación posterior de Windows figura en el contrato CLI al final. macOS nativo continúa pendiente. La revisión estática PowerShell 5.1/7 y Bash 3.2/GNU/BSD no sustituye pruebas nativas. [Instrucciones y matriz](../experiments/LEVEL_LOADING_R1.md).

## Aislamiento del core frente a la GUI — PR #4 integrado

Los runners `scripts/test-level-loading.sh` y `scripts/test-level-loading.ps1` solo
compilan recursivamente fuentes Java de `level` y `movement` en `src/main/java`
y `src/test/java`, con `--limit-modules java.base`. No compilan por accidente
una futura interfaz Swing de `src/main/java/ar/edu/uade/sokoban/ui` u otros
paquetes ajenos a estas dos suites. Las pruebas de CLI del runner PowerShell
siguen invocando el mismo runner, sin modificar sus nueve expectativas.

**Contrato del gate:** fallo de compilación o test en `level`/`movement` implica
exit code distinto de cero. Un PASS demuestra únicamente el core ensayado,
no compila ni prueba el producto completo. Incorporar un build y tests de
integración/GUI por separado cuando se cierre OQ-11. La partición por paquete
es deliberadamente acotada: una dependencia futura del core fuera de esos
paquetes debe revisarse en conjunto con sus tests y con este runner.

**Prueba de aislamiento:** agregar solamente en una copia descartable
`src/main/java/ar/edu/uade/sokoban/ui/Preview.java` que importe `javax.swing.JPanel`;
ejecutar ambas suites con `java.base` y verificar que sigan pasando. Luego
introducir un error de compilación en una clase `level` de otra copia
independiente y verificar que el runner falle. No incorporar estos probes al
repositorio. El PR #4 fue fusionado el 21/10. El [checkpoint Windows del PR #5](../continuity/checkpoints/WINDOWS_PR5_NATIVE_QA_2026-10-21.md) registra posteriormente la regresión nativa del árbol combinado, 18/18 escenarios CLI PS5.1/PS7; no confundirla con el runner original ni con una reejecución durante el mantenimiento documental.

## Movimiento básico R1 — evidencia 2026-10-14

[Suite MovementTest](../../src/test/java/ar/edu/uade/sokoban/movement/MovementTest.java), [contrato, casos y resultados](../experiments/MOVEMENT_R1.md). Ejecutar `SPIKE_JDK_HOME='<JDK>' bash scripts/test-level-loading.sh --movement` o `powershell -NoProfile -File scripts/test-level-loading.ps1 -IncludeMovement` (pwsh también admitido). Sin opción se mantienen los 40 casos de carga. Una compilación estricta, dos suites sobre las mismas clases, sólo java.base; cada error produce exitcode no cero.

El informe histórico R1 reporta ejecución Linux en JDK 17/26: 40 PASS de carga y 31 PASS de movimiento, cero fallos. Casos con tableros completos esperados, cuatro direcciones, empujes/bloqueos, destinos, no arrastre, secuencias independientes/deterministas, TXT externos modificados, nulos, Board máximo y 2.664 transiciones acotadas verificando invariantes. Los 40 tests de carga permanecen intactos. R1 reporta además repetición con Java 17 en copia Unicode/espacios, TMPDIR personalizado, invocación externa y charset US-ASCII. Su contrafactual sin empujes produjo 11 fallos de movimiento; errores de compilación/opciones también fueron detectados, con cleanup. Windows/macOS nativos NOT_RUN; los runners tienen revisión de compatibilidad por diseño. No se reproducen los spikes A/B históricos.

### Revisión independiente R2 y validación R3 — 2026-10-14

R2 revisó código, tests, UML, invariantes y documentación; no identificó defectos funcionales bloqueantes. Reprodujo en Linux/JDK 17.0.20.1 los 40 casos de carga y 31 de movimiento, con salida 0, y ambas suites con copia Unicode/espacios y charset US-ASCII. Java 26 no fue ejecutado en R2; ello no es una restricción de las fuentes ni del runner. Desactivar empujes sólo en copia temporal reprodujo 20 PASS / 11 FAIL de movimiento, 40 PASS de carga y salida 1. El caso 30 siguió pasando en esa mutación: su objetivo es preservar invariantes, no determinar todos los outcomes.

El caso 30 contiene 666 tableros 3×3 (9 posiciones del jugador, 0–2 cajas y dos variantes de terreno), cada uno probado en cuatro direcciones: 2.664 transiciones dentro de un único caso. Verifica terrenos/destinos, cajas, jugador único, dimensiones, identidad en bloqueo y desplazamiento cardinal en éxito. No exige por sí solo el Kind correcto para cada intento ni todas las posiciones finales de las cajas; la preservación del estado anterior se comprueba allí por hash. Los casos golden comparan Kind, Board completo e identidad. Estas limitaciones menores no se confundieron con un defecto demostrado del motor.

Como auditoría adicional, R2 ejecutó un oráculo ASCII independiente en temporales propios: todas las 150.000 transiciones de los 37.500 tableros válidos 2×3 del alfabeto experimental, con jugador en piso/destino y cuatro direcciones. Comparó outcome, Board completo, identidad, determinismo y estado anterior: 35.000 MOVED, 4.000 PUSHED y 111.000 BLOCKED, sin fallos. Esa herramienta fue temporal y no está versionada: no suma casos a MovementTest, no cubre todos los tamaños y no es un comando reproducible de este snapshot. R3 no repitió la enumeración masiva ni alteró fuentes/tests Java.

| Validación R3 sobre el incremento | Level Loading | Movimiento | Salida |
|---|---|---|---|
| Linux nativo, JDK 17.0.20.1 | 40 PASS / 0 FAIL | 31 PASS / 0 FAIL | 0 |
| Linux nativo, JDK 26.0.2 | 40 PASS / 0 FAIL | 31 PASS / 0 FAIL | 0 |
| Windows nativo | NOT_RUN | NOT_RUN | — |
| macOS nativo | NOT_RUN | NOT_RUN | — |

Comando para cada JDK: `SPIKE_JDK_HOME='<directorio-del-JDK>' bash scripts/test-level-loading.sh --movement`. Ambos compilaron con `--release 17 --limit-modules java.base -encoding UTF-8 -Xlint:all -Werror`, sin warnings, y ejecutaron con sólo java.base. Los temporales propios del runner se eliminaron. PlantUML comprobó sintaxis y renderizó el UML de movimiento; se contrastaron sus firmas y relaciones con las clases.

Aquella evidencia del core mantiene RF-03/RF-04 PARTIAL: no validaba input por flechas ni hielo. El incremento Swing posterior agrega las flechas; hielo y las demás mecánicas siguen pendientes. La implementación experimental, las pruebas, la ratificación del equipo y la integración en main son estados separados. Los controles pendientes de aquella validación R3 se distinguen de la validación posterior del runner Windows registrada abajo. La revisión grupal y macOS nativo continúan pendientes; ampliar funcionalidades requiere autorización y decisiones pertinentes. Java/build continúa OPEN.

## Contrato CLI PowerShell — RUNNER R3

El runner `scripts/test-level-loading.ps1` conserva dos modos: sin argumentos ejecuta 40 casos de Loading; `-IncludeMovement` ejecuta esos 40 y los 31 de Movement. El binding avanzado de PowerShell rechaza opciones desconocidas y argumentos posicionales antes de iniciar `java`/`javac`, compilar o ejecutar suites. El código de salida debe ser distinto de cero y no debe emitirse un resumen PASS. Los nombres no distinguen mayúsculas; una abreviatura inequívoca como `-IncludeMov` es válida, mientras que `-IncludeMovment` no lo es.

Regresión permanente, sin framework ni dependencias adicionales, con el mismo JDK completo seleccionado sólo en la sesión/proceso:

```powershell
$env:SPIKE_JDK_HOME = '<directorio-del-JDK-verificado>'
powershell -NoProfile -File .\scripts\test-level-loading-arguments.ps1
# Alternativamente: pwsh -NoProfile -File .\scripts\test-level-loading-arguments.ps1
```

El script detecta Windows PowerShell 5.1 y PowerShell 7, informa cada uno por separado y marca el que no esté instalado como NOT_RUN_NOT_INSTALLED. Ejecuta únicamente el runner real como subproceso, nunca se invoca a sí mismo. Por shell comprueba nueve casos:

| Válidos | Inválidos |
|---|---|
| Sin argumentos | `-IncludeMovment` |
| `-IncludeMovement` | `-UnknownOption` |
| `-includemovement` | Texto posicional inesperado |
| `-IncludeMov` | Texto adicional tras `-IncludeMovement` |
| | `-IncludeMovement -UnknownOption` |

Combina exit code real, totales y casos individuales de las suites, presencia/ausencia de ejecución Java y diagnóstico que identifica el argumento rechazado. Los controles válidos exigen JDK funcional: un fallo por JDK ausente no satisface la regresión. Los negativos no deben iniciar Java ni crear temporales de compilación. La regresión sale con código no cero si falla una expectativa; con ambos shells disponibles se esperan `CLI RESULT: 18 PASS, 0 FAIL` y salida 0. Sus temporales propios se crean fuera del checkout y se limpian al terminar.

Validación nativa Windows del runner R3 (2026-10-14), Temurin JDK 21.0.12.1, compilación `--release 17`, UTF-8, `-Xlint:all -Werror` y sólo `java.base`:

| Shell real | Loading | Movement | Contrato CLI |
|---|---|---|---|
| Windows PowerShell 5.1.26100.9444 | 40 PASS / 0 FAIL | 31 PASS / 0 FAIL | 9 PASS / 0 FAIL |
| PowerShell 7.6.5 | 40 PASS / 0 FAIL | 31 PASS / 0 FAIL | 9 PASS / 0 FAIL |

Las ejecuciones válidas terminaron en 0; los cinco casos inválidos por shell fueron rechazados antes de iniciar Java. La sensibilidad se comprueba ejecutando esta misma regresión sobre un snapshot aislado con el runner original: debe conservar los controles válidos y detectar los negativos aceptados incorrectamente, con salida no cero. Esto no cambia HEAD ni reescribe la evidencia histórica de RUNNER-01.

La validación incluye rutas con espacios/`ñ`, TEMP/TMP alternativos, CWD externo y preservación byte a byte de TXT. Se distingue la eliminación del temporal propio del runner de una carpeta vacía `hsperfdata` que puede crear HotSpot; no se altera el JDK para ocultarla. macOS nativo sigue NOT_RUN. Las matrices anteriores describen sus iteraciones Linux históricas; esta sección registra la validación posterior del contrato Windows. El checklist combinado Windows/macOS sigue pendiente mientras falte macOS. Esta corrección no ratifica arquitectura, formato TXT, versión/build del producto ni reglas de juego; la revisión grupal continúa pendiente.

## Integración Swing V1 — 2026-10-15

[Guía reproducible y matriz acotada](../experiments/SWING_VERTICAL_SLICE_V1.md). Fuentes en src/desktop/java y 16 casos en [DesktopIntegrationTest](../../src/desktop-test/java/ar/edu/uade/sokoban/desktop/DesktopIntegrationTest.java), sin framework adicional. Ejecutar con el mismo JDK seleccionado: powershell -NoProfile -File scripts/run-swing.ps1 -Test; admite pwsh en Windows, ya que busca ejecutables .exe. Compilación estricta release 17 con java.base y java.desktop; el runner core de aquel original estaba limitado a java.base. La recuperación R2 conserva los runners heredados del PR #4 con selección por paquetes.

Casos automáticos: demo real, TXT externo modificado/recargado, cuatro direcciones, empuje a piso/destino, dos bloqueos sin publicación, bindings reales en EDT, correspondencia/inmutabilidad del Board mostrado, contadores/recarga y errores atómicos. La pintura BufferedImage en EDT es headless y sólo comprueba que la vista puede consumir el modelo; no certifica una interfaz visual.

Windows 25H2 x64 / Temurin 21.0.12.1: core 40 Loading + 31 Movement PASS y salida 0, una vez en PS5.1; 16 tests desktop PASS y salida 0 en PS5.1/PS7.6.5. Exportación del índice, rutas Unicode/espacios, TEMP/TMP y CWD externos: PASS, sin residuos del runner (hsperfdata vacío de JVM separado). Interacción gráfica nativa observada por separado: Right, Right, Right, Up, Up; último intento bloqueado, posición (1,5), caja en destino (2,6), movimientos 4/empujes 2, sin cambios tras bloqueo. Capturas externas de entrega. Comando de GUI: powershell -NoProfile -File scripts/run-swing.ps1; cierre con salida 0.

En la evidencia original del 15/10, Linux/macOS Swing estaban NOT_RUN; la recuperación Linux R2 se registra por separado a continuación. El placeholder gráfico no satisface imágenes definitivas/SFX y no hay pruebas de mecánicas fuera del alcance. Los tests no ratifican Java/build, TXT, reglas abiertas ni diseño; revisión grupal pendiente.

## Recuperación integrada Linux — SWING_RECOVERY_R2, 2026-10-21

Base `ee83f9ebd30ee1b553c555c89d9ea453131d6678` con #3/#4 integrados, más el delta exclusivo del original `1303a33748e129f3d8a55ab2b1799518aaa8df5d` y documentación reconciliada. [Comandos reproducibles, entorno y límites](../experiments/SWING_VERTICAL_SLICE_V1.md#recuperación-linux-r2--2026-10-21). Java/Swing, tests, fixtures y scripts heredados sin modificaciones; sólo se recompilan y ejecutan las suites existentes, sin sumar casos.

Linux x86_64, OpenJDK/java/javac 17.0.20.1, release 17, LC_ALL=C.utf8: **Loading 40 PASS / 0 FAIL; Movement 31 PASS / 0 FAIL; Desktop 16 PASS / 0 FAIL**, salida 0 en compilación y suites. Ejecución real antes del commit con -encoding UTF-8 -Xlint:all -Werror; temporales propios eliminados. Logs, comandos y SHA final en el informe externo de recuperación. GUI interactiva Linux y macOS: NOT_RUN. Windows no se repite; sus resultados anteriores son históricos. Core usa java.base; desktop se compila por separado con java.base,java.desktop y se prueba con java.awt.headless=true. La pintura en BufferedImage y los bindings en EDT no certifican una ventana interactiva.


## Revisión de consistencia Swing R3 — 2026-10-21

Antes de editar documentación, se exportó el commit `99b81c5aa04dd6c6a19cc72f42c766ce53551bbd` con `git archive`; sus 67 archivos coincidieron con los blobs del commit. En esa copia sin metadatos Git se ejecutaron el runner core y el bloque Linux desktop versionado en la [guía](../experiments/SWING_VERTICAL_SLICE_V1.md#recuperación-linux-r2--2026-10-21), sustituyendo únicamente el directorio del JDK instalado. No se requirieron bundle, logs QA, clases previas ni rutas personales.

Linux x86_64, OpenJDK/java/javac 17.0.20.1, release 17, LC_ALL=C.utf8: **Loading 40 PASS / 0 FAIL; Movement 31 PASS / 0 FAIL; Desktop 16 PASS / 0 FAIL**, compilación y ejecuciones con salida 0. Se verificaron java.base/java.desktop, UTF-8, -Xlint:all -Werror y cleanup de temporales propios. GUI interactiva Linux y macOS: NOT_RUN. No se repitió Windows.

R3 sólo corrige documentación. La evidencia de esa exportación se conserva para el contenido final tras demostrar identidad de fuentes, tests, recursos y runners. `git archive HEAD` no incluye staging ni cambios locales; no se utiliza una exportación del HEAD anterior para certificar código nuevo. El verificador documental necesita Git: se ejecuta en el worktree real, con `--require-clean` sólo cuando esté limpio. La validación de enlaces/consistencia se repite sobre la documentación corregida; no equivale a ratificar decisiones académicas.

## QA Windows nativa del PR #5, posterior a R5 (2026-10-21)

[Checkpoint](../continuity/checkpoints/WINDOWS_PR5_NATIVE_QA_2026-10-21.md) y [JSON](../continuity/checkpoints/WINDOWS_PR5_NATIVE_QA_2026-10-21.json). HEAD **del código probado** `0670a4d5137f0e9f1db221866a02c29987024847` sobre `main=ee83f9ebd30ee1b553c555c89d9ea453131d6678`; no confundir con Windows del incremento original de 15/10.

- Una invocación de `scripts/test-level-loading-arguments.ps1` en Windows nativo: PS5.1 y PS7, 9 casos por shell, **18/18 PASS**, exit 0. Loading 40 y Movement 31 son **casos únicos** cubiertos transitivamente; repetición deliberada por contratos CLI; no hubo core standalone adicional. Los casos CLI negativos con exit 1 eran **esperados**.
- Una invocación de `scripts/run-swing.ps1 -Test`: **16/16 headless PASS**, exit 0, JDK 21 y `--release 17`, compilación estricta sin warnings.
- GUI observada realmente por agente con capturas: movimiento, empujes, bloqueo, contadores, carga de TXT externo Unicode válido, error por inválido y conservación del nivel previo; cierre exit 0. No inferir interacción de pintura headless.
- Dos worktrees históricos y contenido del clone reportados intactos; GitHub sin cambio de HEAD. Logs/capturas originales permanecen fuera de Git; esta rama contiene resumen/trazabilidad, no una nueva reejecución.
- `NOT_RUN`: GUI Linux/macOS, macOS nativo y aceptación del producto completo. Al corte de aquella QA, PR #5 estaba Draft; su merge posterior está en [CURRENT_STATE](../continuity/CURRENT_STATE.md). Siguen pendientes ratificación académica, Java/build definitivos, `.mdj` StarUML y OQ-01…OQ-11.

## Prototipo experimental de atributos individuales

Sobre la rama spike basada en c6eb755388da142b6c8424d2d46aec2dc03ce622, Windows y Temurin 21.0.12.1, compilación UTF-8 --release 17 --limit-modules java.base -Xlint:all -Werror: ExperimentalFragileParserTest 22/22 PASS, exit 0. Compilación independiente en temporal nuevo; LevelLoadingTest 40, MovementTest 31, C2LevelLoadingTest 32, DesktopIntegrationTest 16 y DesktopVisualAssetsTest 44 también PASS, exit 0. Las suites existentes conservan sus contadores. Desktop usa java.base,java.desktop. No se ejecutó GUI manual ni se cambió Swing.

El test independiente está en src/test/java/ar/edu/uade/sokoban/level/ExperimentalFragileParserTest.java. Para reproducirlo, compilar las fuentes level/movement y sus tests en un directorio temporal con las opciones estrictas indicadas y ejecutar ar.edu.uade.sokoban.level.ExperimentalFragileParserTest. El runner core compila esta clase pero no la ejecuta automáticamente. El mismo TXT especial se rechaza por `LevelLoader.load(Path)` jugable; `loadExperimentalFragile(Path)` permite cargar la definición no jugable desde el incremento filesystem posterior. La representación no habilita mecánicas.

Windows, desde la raíz y con SPIKE_JDK_HOME apuntando a un JDK completo:

~~~powershell
$owned = Join-Path ([IO.Path]::GetTempPath()) ('sokoban-fragile-' + [Guid]::NewGuid())
New-Item -ItemType Directory -Path $owned | Out-Null
try {
    $sources = Get-ChildItem src/main/java,src/test/java -Recurse -Filter '*.java' | Sort-Object FullName
    $lines = [string[]]@($sources | ForEach-Object { '"' + $_.FullName.Replace('\','/') + '"' })
    $argfile = Join-Path $owned 'sources'
    [IO.File]::WriteAllLines($argfile, $lines, (New-Object Text.UTF8Encoding($false)))
    & "$env:SPIKE_JDK_HOME/bin/javac.exe" '-J-Dfile.encoding=UTF-8' --release 17 --limit-modules java.base -encoding UTF-8 -Xlint:all -Werror -d $owned ('@' + $argfile)
    if ($LASTEXITCODE -ne 0) { throw 'Compilación fallida' }
    & "$env:SPIKE_JDK_HOME/bin/java.exe" --limit-modules java.base -cp $owned ar.edu.uade.sokoban.level.ExperimentalFragileParserTest
    if ($LASTEXITCODE -ne 0) { throw 'Prueba fallida' }
} finally {
    Remove-Item -LiteralPath $owned -Recurse -Force
}
~~~

## Validación de invariantes Fragile R5 — 2026-10-28

Sobre el contenido actualizado del PR #11, basado en c6eb755388da142b6c8424d2d46aec2dc03ce622: Windows 11, Temurin 21.0.12.1, release 17, UTF-8, -Xlint:all -Werror. Compilación desde fuentes en temporales nuevos. Loading 40/40, Movement 31/31, C2 32/32, Fragile 31/31, Desktop 16/16, VisualAssets 44/44 y UI Smoke automatizado 17/17: PASS, exit 0. Las 22 pruebas anteriores son evidencia previa, no otro resultado de esta corrección.

El agregado valida directamente nulos, ID ASCII único, posiciones únicas dentro del Board, piso/destino sin ocupante y resistencia positiva; List.copyOf y Board conservan inmutabilidad. Los nueve casos nuevos cubren construcción directa inválida y límites, incluyendo pared, jugador y caja normal. El parser y el modelo comparten la expresión del ID. El comando de la sección anterior ejecuta también estos casos. GUI manual y escucha de audio no fueron ejecutadas. No se ratifica OQ-06/OQ-08 ni se habilita Fragile jugable.

## Aceptación para demo y TXT docente — plan 29/10/2026

[Autoridad y límites](../requirements/REQUIREMENTS.md#entrega-y-aceptación-docente--29102026). Todos los casos siguientes están PLAN / NOT_RUN en esta misión; las suites históricas conservan fecha, baseline y conteos anteriores.

1. Crear TXT externo con la convención documentada y cargar por LevelPath/selector. Comparar el Board y la representación Swing con el archivo.
2. Cambiar válidamente una pared por caja, conservar dimensiones/jugador y recargar el mismo archivo; comprobar posición y representación nuevas, sin recompilar ni hardcode. LevelLoadingTest y DesktopIntegrationTest contienen antecedentes de recarga; el ensayo docente/manual completo debe registrarse sobre la versión final.
3. Introducir símbolo inválido o archivo ausente: diagnóstico legible y proyección previa intacta. No aceptar Fragile jugable por pasar su parser experimental.
4. Para niveles propios finales, registrar secuencia solución y empujes/resistencia; probar puzzles especiales sólo tras reglas e integración. No se requiere solver.
5. Ensayar demo de 15–20 minutos: funcionalidades efectivas, explicación detallada de roles/patrones y alternativa justificada; QA visual y audible real. Revisar los entregables por separado, incluido StarUML.

Regresión funcional futura según cambios: Loading 40, Movement 31, C2 32, Fragile 31, Desktop 16, VisualAssets 44, UI Smoke 17 y CLI 18 son conteos históricos separados, no ejecuciones de esta reconciliación. JAVA_REGRESSION = NOT_RUN_DOCS_ONLY; UML y GUI = NOT_RUN. Las validaciones documentales actuales se registran en el PR con comando, exit code y commit final, evitando un SHA autorreferencial.

## Incremento filesystem Fragile — 29/10/2026

**Baseline aportado, no reejecutado por rutina:** `8b71712cec673651b8dffe792bdc4ee7c779c1cf`, ejecución local del usuario `run.vNBarLPb`: compilación estricta, Loading 40, C2 32, Fragile 31 y Movement 31 PASS. Se contrastaron logs completos, metadata, mapa F01–F14, script R2 y ausencia de cambio funcional posterior al SHA. El resumen sólo se genera tras salida cero del compilador y suites; los logs no contienen campos numéricos separados de exit. [Procedencia y límites](../continuity/SOURCE_REGISTRY.md#evidencia-técnica-local-txt--29102026).

**Ejecución nueva por Codex:** Ubuntu 26.04.1 LTS, Linux x86_64, OpenJDK **java y javac 17.0.20.1**, release 17; se seleccionaron ambos binarios del mismo JDK, sin utilizar el java 25 predeterminado. Compilación core con `--limit-modules java.base -encoding UTF-8 -Xlint:all -Werror`. Pruebas realizadas sobre el contenido nuevo del worktree basado en el SHA anterior, antes de crear su commit; no atribuirlas al baseline sin sus cambios. Identificación reproducible del código: todo `src`/`scripts` conserva los blobs del baseline excepto estos tres:

| Ruta | Git blob ensayado |
|---|---|
| src/main/java/ar/edu/uade/sokoban/level/LevelLoader.java | 4f7c3202f1473c4ac7c8ed83c7b497ba7e437e3e |
| src/test/java/ar/edu/uade/sokoban/level/ExperimentalFragileParserTest.java | f423ec607527d82defe4efccc12d81a50c6eccbe |
| src/test/resources/levels/experimental-fragile.txt | b2d8929b5f1db35ae5dd70299870a00cc4a8eef9 |

El commit que incorpora estos blobs se localiza con `git log -1 --format=%H -- src/main/java/ar/edu/uade/sokoban/level/LevelLoader.java`; comprobar blobs con `git rev-parse <commit>:<ruta>`. Esta identificación evita inventar un SHA futuro/autorreferencial. El informe de entrega y los logs externos registran además el HEAD final comprobado antes de publicar; un squash posterior requiere su propio gate, no hereda ese SHA.

| Comando / clase real | Resultado | Exit |
|---|---|---|
| javac core con las opciones estrictas anteriores | PASS, sin warnings | 0 |
| ar.edu.uade.sokoban.level.ExperimentalFragileParserTest src/test/resources/levels | 41 PASS / 0 FAIL | 0 |
| ar.edu.uade.sokoban.level.LevelLoadingTest src/test/resources/levels | 40 PASS / 0 FAIL | 0 |
| ar.edu.uade.sokoban.level.C2LevelLoadingTest src/test/resources/levels | 32 PASS / 0 FAIL | 0 |
| ar.edu.uade.sokoban.movement.MovementTest src/test/resources/levels | 31 PASS / 0 FAIL | 0 |
| javac desktop con java.base,java.desktop, mismas opciones estrictas | PASS, sin warnings | 0 |
| ar.edu.uade.sokoban.desktop.DesktopIntegrationTest src/desktop/resources/levels/demo.txt | 16 PASS / 0 FAIL, headless | 0 |

Los **31 casos Fragile previos se conservan**, sin modificar expectativas. Los diez casos nuevos son:

| Caso | Qué comprueba |
|---|---|
| 32 | Fixture externo dos IDs/resistencias 3/12, piso/Goal, normal Box en Goal, inmutabilidad y rechazo jugable |
| 33 | Path Unicode/espacios, edición de posición/terreno/resistencia y pared→caja, recarga LF/CRLF/mixtos, declaraciones invertidas y resultado anterior intacto |
| 34 | Error tardío con línea 6, UTF-8 inválido, referencia publicada intacta y recuperación |
| 35 | UTF-8 malformado y truncado rechazado antes de interpretar |
| 36 | 65.537 bytes con UTF-8 inválido: prevalece el diagnóstico de tamaño |
| 37 | Exactamente 65.536 bytes llega a validación estructural, no se rechaza por tamaño |
| 38 | Menos de 65.536 caracteres Java pero 65.537 bytes: rechazo por bytes |
| 39 | Archivo ausente y Path nulo conservan categorías explícitas |
| 40 | No se normaliza BOM ni se acepta vacío/legacy en la entrada experimental |
| 41 | C2 ordinario en ambas entradas, lista especial vacía y llamadas intercaladas independientes |

Reproducción desde la raíz de un clon, sin script ni logs privados. `SPIKE_JDK_HOME` debe apuntar a un JDK completo existente. Sólo se crean/eliminan temporales propios fuera del checkout:

```bash
(
    set -euo pipefail
    qa_dir="$(mktemp -d /tmp/sokoban-txt-qa.XXXXXXXX)"
    trap 'find "$qa_dir" -type f -delete; find "$qa_dir" -depth -type d -empty -delete' EXIT
    mkdir "$qa_dir/classes" "$qa_dir/desktop"
    git rev-parse HEAD
    "$SPIKE_JDK_HOME/bin/java" -version
    "$SPIKE_JDK_HOME/bin/javac" -version
    find src/main/java src/test/java -name '*.java' -type f | LC_ALL=C sort > "$qa_dir/sources"
    "$SPIKE_JDK_HOME/bin/javac" --release 17 --limit-modules java.base \
        -encoding UTF-8 -Xlint:all -Werror -d "$qa_dir/classes" @"$qa_dir/sources"
    for suite in level.LevelLoadingTest level.C2LevelLoadingTest \
                 level.ExperimentalFragileParserTest movement.MovementTest; do
        "$SPIKE_JDK_HOME/bin/java" --limit-modules java.base -Dfile.encoding=UTF-8 \
            -Djava.io.tmpdir="$qa_dir" -cp "$qa_dir/classes" \
            "ar.edu.uade.sokoban.$suite" src/test/resources/levels
    done
    find src/main/java src/desktop/java src/desktop-test/java -name '*.java' -type f \
        | LC_ALL=C sort > "$qa_dir/desktop-sources"
    "$SPIKE_JDK_HOME/bin/javac" --release 17 --limit-modules java.base,java.desktop \
        -encoding UTF-8 -Xlint:all -Werror -d "$qa_dir/desktop" @"$qa_dir/desktop-sources"
    "$SPIKE_JDK_HOME/bin/java" --limit-modules java.base,java.desktop \
        -Dfile.encoding=UTF-8 -Djava.awt.headless=true -Djava.io.tmpdir="$qa_dir" \
        -cp "$qa_dir/desktop:src/desktop/resources" \
        ar.edu.uade.sokoban.desktop.DesktopIntegrationTest src/desktop/resources/levels/demo.txt
)
```

Fragile acepta opcionalmente el directorio de fixtures; sin argumento usa `src/test/resources/levels` relativo al directorio de trabajo. Los runners core existentes siguen compilando pero **no ejecutando** C2/Fragile; sus comandos independientes son necesarios. Los resultados de Desktop son regresión del consumidor existente de `load`, no prueba de representación/gameplay Fragile. Las suites conservadas no suman casos nuevos por repetirlas después del commit para comprobar el HEAD publicado.

`NOT_RUN` en este incremento: GUI interactiva/UI Smoke, VisualAssets Java, audio audible, Windows/macOS, tests PowerShell/CLI, gameplay Fragile, solución de puzzle y ratificación del equipo. El verificador comprueba separadamente integridad de los 43 assets, sin certificar derechos. No se ejecuta enumeración masiva adicional ni se modifica Movement. La gramática/rango siguen experimentales y OQ-06/OQ-08 no se cierran.

Controles del mismo incremento: `git diff --check`, `bash scripts/verify-repo-readonly.sh` y `JAVA_TOOL_OPTIONS=-Djava.awt.headless=true plantuml -checkonly docs/architecture/uml/level-loading-spike-v1.puml`, todos exit 0. Verificador: 137 archivos, 401 enlaces relativos, 43 PNG/WAV registrados con formato/hash válidos. PlantUML sólo comprueba sintaxis; no se ejecutó render nuevo ni StarUML. La regresión del verificador no se repitió porque su implementación no cambió.

## Revisión del contrato para trabajo paralelo — 29/10/2026

Verificación de evidencia anterior: los logs completos y el registro de comandos del incremento filesystem identifican una reejecución posterior al commit sobre **5ac2bba9a1d495a5da9e1ad998857fbe00613840**, por Codex, Ubuntu 26.04.1/Linux x86_64, java/javac 17.0.20.1: Loading 40, C2 32, Fragile 41, Movement 31 y Desktop headless 16 PASS; compilaciones estrictas y suites con exit 0. Son 160 casos entre cinco suites, repetidos para verificar aquel HEAD; no son 160 casos nuevos de esta revisión. Los tres blobs de la tabla anterior coinciden y no hay diferencias posteriores en src/scripts.

`JAVA_QA_THIS_STEP=NOT_RUN_DOCS_ONLY`. Se reutiliza esa evidencia verificada; no se vuelven a ejecutar suites por documentar el contrato. Los ejemplos de traspaso describen el fixture existente de Fragile 32 y combinaciones ordinarias cubiertas por C2/Fragile 41; sus resultados se contrastaron con código y oráculos existentes, sin atribuirles ejecución nueva. GUI manual, gameplay Fragile, Windows/macOS y aceptación del compañero no se verifican en este hito. Los comandos de reproducción siguen en la sección filesystem; continuar no requiere acceder a los logs externos originales.

## Cierre semántico documental — 29/10/2026

Evidencia previa verificada al preparar este cierre: ejecución por Codex sobre **38e5a2e0ce4dd4688c4eb903dea93a2a3bf54bd6**, Ubuntu 26.04.1 LTS/Linux x86_64, java/javac **17.0.20.1** del mismo JDK. Se contrastaron registro de comandos, salida por caso y resumen de exit: compilación estricta core/desktop y Loading 40, C2 32, Fragile 41, Movement 31, Desktop headless 16, todos PASS/exit 0. Son **160 casos anteriores, no nuevos ni reejecutados en el cierre documental**. El bloque Bash de la sección filesystem es el procedimiento versionado ejecutado; mantiene release 17, UTF-8, -Xlint:all -Werror, java.base y desktop separado.

La adopción del contrato por D-020 no cambia comportamiento: `JAVA_QA=PREVIOUS_QA_IDENTICAL_BLOBS`. Se exige comprobar desde la raíz:

```bash
git diff --check
bash scripts/verify-repo-readonly.sh
git diff --exit-code 5ac2bba9a1d495a5da9e1ad998857fbe00613840 -- src scripts
# Después del commit, checkout limpio:
bash scripts/verify-repo-readonly.sh --require-clean
```

La comparación de blobs debe terminar con exit 0; no es una ejecución Java. El informe/PR registra HEAD documental y resultados nuevos del verificador, enlaces/anclas y revisión semántica. Los logs anteriores son externos, pero el clon conserva identificación del código, casos individuales, entorno, comandos y límites para reproducir. `MODULE_TXT_QA_BASELINE=JAVA_17`; `OQ11_PROJECT_BUILD=OPEN`. GUI manual/UI Smoke, audio audible, VisualAssets Java, Windows/macOS, gameplay Fragile y código del compañero: NOT_RUN en este cierre; no se acredita CI ni aceptación humana.

Las frases «no se ratifica OQ-06/OQ-08» de los apartados de pruebas anteriores describen sus hitos: el cierre posterior D-020 adopta el esquema soportado con autoridad MODULE_OWNER, sin convertir los PASS históricos en una votación o aprobación del código.

Validación documental ejecutada por Codex antes del commit sobre el worktree de cierre basado en 38e5a2e: `git diff --check`, `bash scripts/verify-repo-readonly.sh` y comparación de src/scripts anterior, **exit 0** cada uno. Verificador: **137 archivos, 431 enlaces relativos y 43 assets con formato/SHA-256 válidos**. Control adicional de fragmentos locales: **156 anclas** válidas en los 16 Markdown afectados, incluidos E01–E11; IDs OQ-01…11 preservados. Contraste de filas oficiales, recuentos ajenos a la corrección, fixture contra ejemplo y secciones no TXT de INTEGRACION_EQUIPO: PASS. Son controles de documentación/integridad, sin compilar, ejecutar Java ni verificar derechos o fuentes privadas originales.

## I1 — Gate local de integración con JDK17 (03/11/2026)

En el worktree auxiliar, el usuario ejecutó las ocho suites core con `javac`/`java 17.0.20.1`: Loading 40, Movement 37, Extended 26, Dynamic 6, Completion 36, C2 36, Experimental Fragile 41, Runtime ICE/Fragile 30; **252/252**. Desktop headless **16/16** y Visual Assets **44/44**, totales **312/312**, con postcheck independiente preservando fuente, refs e índice. Esa corrida es **anterior al refuerzo de INT-01..04 de I1.C**. El usuario ejecutó **después de I1.C** el comando autocontenido versionado `bash scripts/test-integration-i1.sh`: Loading 40, Movement 37, Extended 26, Dynamic 6, Completion 36, C2 36, Experimental Fragile 41, Runtime ICE/Fragile 34 (**256/256 core**), Desktop headless **16/16**, Visual Assets **44/44**; total **316/316 PASS**, `RESULTADO_GLOBAL=PASS_I1C_LOCAL_TESTS_316`, exit 0 y `GIT_STATE_PRESERVED=YES`. Verificador documental: **170 archivos, 541 enlaces, 43 assets PASS**. Evidencia del worktree local sin commit, no del PR ni de `main`. JAVA 17 pareja, UTF-8, `--release 17`, `-Xlint:all -Werror`; compilación y recursos originales del checkout. GUI real, audio audible, Windows/macOS, ramas remotas y aceptación TEAM: NOT_RUN/NOT_RECORDED. [Procedencia y límites I1](../integration/I1_LOCAL_VALIDATION_2026-11-03.md).

<!-- I1_PR17_PUBLICATION_2026_11_03 -->

## I1 publicado en PR #17 — alcance de QA al 03/11/2026

La rama `integration/tpo-unificado-2026-11-03` recibió el merge de dos padres `6abb8ad003ad9af6642fc1a1e11a91f5d238f2b2`. Los **316/316 casos** (256 core, 16 Desktop headless, 44 Visual Assets) se ejecutaron con `java`/`javac 17.0.20.1` **en el árbol local inmediatamente anterior al commit**, y los blobs resultantes fueron auditados antes del push. Para reproducir: `bash scripts/test-integration-i1.sh` y `bash scripts/verify-repo-readonly.sh`. No atribuir a `6abb8ad0` una reejecución no efectuada. GUI interactiva, Windows nativo y QA de I2–I4 siguen `NOT_RUN`.

## I2 — avance local 04/11/2026

Auxiliar `integration/i2-martina-stage-2026-11-03`, base `9aebe525` y fuente `efd0fcd2`: integración local verificada antes del commit, estrictamente local sin publicación (`REMOTE_PUSH=NOT_RUN`). `GameSession` conserva el mundo, historial y métricas; `LevelProgression.CompletedLevel` retiene `(victory, metrics, undoUsesTotal)` permitiendo que el score histórico persista tras `advance()` y `restart()`; `ScoreCalculator` calcula la fórmula saturada acordada y `GameController` expone el resultado a `SwingApplication`, cuyo HUD (`status`) y pantalla de victoria reciben el puntaje. Con OpenJDK 17.0.20.1 se ejecutaron **58 comprobaciones Undo** (`GameSessionUndoTest`), **258 casos core I1** (Loading 40, Movement 37, Extended 26, Dynamic 6, Completion 38, C2 36, Fragile 41, Runtime_I1C 34), **36 casos desktop** (`DesktopIntegrationTest`) y **44 assets** (`DesktopVisualAssetsTest`): `bash scripts/test-integration-i2.sh` terminó con exit 0. Casos INT-05 (Undo que restaura DynamicWall y Lock abiertos), INT-06 (Restart desde cuatro contadores estrictamente no nulos) e INT-08 (retención histórica de score 987 y control 997 persistentes tras advance y restart) `PASS`. `NATIVE_GUI_QA=NOT_RUN`. El reset de métricas al cambiar de nivel es provisional; `TEAM_ACCEPTANCE=NOT_RECORDED`. Siguiente gate: revisión funcional del equipo y verificación remota antes de cualquier publicación.

## I2 — Fragile TXT GUI hotfix (04/11/2026)

- Detectar formato por parsers estrictos existentes: STANDARD, EXTENDED y EXPERIMENTAL_FRAGILE.
- El archivo `fragile_demo.txt` y el mismo contenido con nombre externo arbitrario conservan ID `f1` y resistencia 4 en runtime.
- La carga inválida preserva el estado previamente publicado; regresión de STANDARD, ICE y EXTENDED en `DesktopIntegrationTest`.
- Evidencia: `scripts/test-integration-i2.sh` con JDK 17 y `scripts/verify-repo-readonly.sh` (ejecutar sobre el commit resultante).
- QA gráfica nativa integral, aprobación TEAM, I3/I4 y merge a `main` siguen pendientes.
- La frescura/publicación del PR #17 se comprueba en GitHub; no deducirla del HEAD local.
