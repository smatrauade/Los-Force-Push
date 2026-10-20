# Estrategia de pruebas y evidencia experimental

Revisado 2026-10-14 (carga R1–R4 y movimiento R1–R3 experimentales). El snapshot contiene 40 casos de Level Loading y 31 de movimiento; la base histórica del bootstrap precede a esas suites. La tabla siguiente conserva el **plan de aceptación del producto completo**, no resultados ni negación de la cobertura parcial ejecutable descrita después. Herramienta/framework y Java definitivos dependen de OQ-11. Core se prueba sin Swing.

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

## Pruebas ejecutables del experimento local R1

La tabla anterior conserva los gates de producto final. El incremento de carga ensayó un contrato **experimental** sin cerrar OQ-08/OQ-11. [LEVEL_LOADING_R1](../experiments/LEVEL_LOADING_R1.md) registra las ejecuciones históricas de Level Loading R3 de 38 casos y R4 de 40 casos PASS / 0 FAIL en JDK 17 y JDK 26 con archivos reales, UTF-8/EOL, dimensiones, jugadores, errores, determinismo, no publicación parcial, aislamiento e independencia de Swing. Comando: `SPIKE_JDK_HOME='<directorio-del-JDK>' bash scripts/test-level-loading.sh`.

Runner sin dependencias ni assertions desactivables: falla con código no cero si compila con warnings o si falla un caso. Fixtures mutables y clases se crean en un temporal propio fuera del checkout. En esta rama sí hay tests de carga ejecutables; no hay tests productivos de gameplay. La base histórica precede al incremento: reproducir con las fuentes de esta rama. El runner también admite JAVA_HOME si SPIKE_JDK_HOME no está definido. No se ejecutan A/B históricos ni se alteran sus registros.

Level Loading R3 conservó los 30 casos originales, reforzó el 25 y agregó 31–38. La causa de tamaño se distingue de estructura; al desactivar exclusivamente el guard de bytes en una copia temporal, 25/38 fallaron con salida no cero. Ancho/alto/rectangularidad tempranos, nulos y columna negativa están cubiertos. Es evidencia experimental, sin ratificación de OQ-08/OQ-11 ni reproducción de A/B históricos.

Level Loading R4 preservó los 38 casos y agregó 39 (archivo externo con nombre Unicode/espacios, LF/CRLF, piso significativo y recarga) y 40 (diagnóstico estable bajo localización de formato distinta, restaurada al terminar). Linux ejecutó la suite con JDK 17/26, desde otra ubicación y una copia del checkout/TMPDIR con espacios/Unicode; JDK 17 también con `JAVA_TOOL_OPTIONS=-Dfile.encoding=US-ASCII`. El mecanismo de argfile UTF-8 sin BOM y rutas absolutas entre comillas del runner Windows se comprobó con javac en Linux; esto no ejecuta PowerShell ni Windows. Controles negativos del runner Unix: JDK seleccionado inexistente, TMPDIR dentro del checkout y fixture erróneo producen salida no cero; sus temporales propios se limpian.

Windows usa `powershell -NoProfile -File scripts/test-level-loading.ps1` o `pwsh` con el mismo archivo; no duplica tests Java ni necesita Python/Bash. En la evidencia histórica de carga R4, Windows/macOS nativos estaban NOT_RUN; la validación posterior de Windows figura en el contrato CLI al final. macOS nativo continúa pendiente. La revisión estática PowerShell 5.1/7 y Bash 3.2/GNU/BSD no sustituye pruebas nativas. [Instrucciones y matriz](../experiments/LEVEL_LOADING_R1.md).

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

Esta evidencia mantiene RF-03/RF-04 PARTIAL: input por flechas y hielo siguen pendientes, al igual que las demás mecánicas. La implementación experimental, las pruebas, la ratificación del equipo y la integración en main son estados separados. Los controles pendientes de aquella validación R3 se distinguen de la validación posterior del runner Windows registrada abajo. La revisión grupal y macOS nativo continúan pendientes; ampliar funcionalidades requiere autorización y decisiones pertinentes. Java/build continúa OPEN.

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
