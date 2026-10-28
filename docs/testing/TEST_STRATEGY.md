# Estrategia de pruebas y evidencia experimental

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
