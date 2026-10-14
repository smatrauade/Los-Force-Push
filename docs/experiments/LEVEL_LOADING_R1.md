# Carga de niveles R1–R4 — incremento experimental

`AS_OF = 2026-10-14`, `STATUS = EXPERIMENTAL`, base integrada `1943053ec520ff3d8be0aecb21c2f417f3126380` (merge PR #2). Incremento experimental disponible en esta rama, pendiente de adopción por el equipo. No es implementación productiva ratificada. OQ-08/OQ-11 siguen OPEN; D-011 sigue PREFERRED.

## Contrato TXT candidato antes del parser

| Criterio | Una capa | Dos capas |
|---|---|---|
| Edición manual / profesor | Un carácter por celda; compacto | Dos mapas que deben mantener dimensiones y alineación |
| Gramática y parser | Siete símbolos; superposiciones explícitas | Separadores y dos alfabetos; validación cruzada |
| Terrain / Occupant | Se decodifica cada símbolo en dos valores | Separación visible en el archivo |
| Sobre destinos | `+` jugador y `*` caja | Destino en terreno y ocupante en otra capa |
| Extensión | Combinaciones nuevas aumentan el alfabeto | Facilita combinaciones de futuros terrenos y cajas |
| Fragile / Key / Ice / Lock / Walls | Insuficiente sin nuevas decisiones | Tampoco resuelve resistencia, IDs, asociaciones o cierre |
| Metadata / IDs | No necesaria en el subconjunto | Sección aparte si las reglas futuras la justifican |
| Tests / mantenimiento | Fixtures cortos; errores por posición | Más fixtures para validar relaciones entre capas |

Se elige **una capa provisional** por facilidad de escritura manual para el subconjunto autorizado. No es una gramática definitiva ni evidencia de aceptación docente. Las dos capas siguen siendo alternativa para futuras combinaciones; no se introducen metadatos artificiales.

| Símbolo | Terrain | Occupant |
|---|---|---|
| `#` | WALL | EMPTY |
| espacio ASCII | FLOOR | EMPTY |
| `.` | GOAL | EMPTY |
| `@` | FLOOR | PLAYER |
| `$` | FLOOR | BOX |
| `+` | GOAL | PLAYER |
| `*` | GOAL | BOX |

Hipótesis experimentales explícitas: UTF-8 estricto, sin BOM; LF o CRLF (también mezclados), un salto final opcional; CR aislado inválido. Espacios iniciales/finales significativos: no se recorta, rellena ni ignora ninguna fila. Sin comentarios, encabezados, tabs, líneas vacías ni otros símbolos. Rectángulo de 1–100 filas y 1–100 columnas, archivo de hasta 65.536 bytes, exactamente un jugador. Los límites son protección del experimento, no requisitos oficiales. Cajas y destinos pueden tener conteos distintos o cero: no se decide victoria, solvencia, cerramiento o alcanzabilidad. Coordenadas del modelo desde cero; diagnóstico textual desde uno.

Archivo faltante / UTF-8 inválido: IOException explícita. Texto vacío, tamaño excesivo, filas inconsistentes, símbolos inválidos o cantidad de jugadores inválida: LevelFormatException. No se devuelve Level ante error. El parser no conserva estado entre cargas.

## Construcción y modelo propuestos

Comparación sobre exactamente el mismo contrato y las mismas invariantes:

| Fuerza | Construcción directa | Builder concreto interno |
|---|---|---|
| Cohesión / GRASP | Parser interpreta y conserva filas intermedias | Parser interpreta; Builder acumula pasos y publica el producto |
| Acoplamiento | Parser conoce Board y Level | Parser conoce Builder; Builder conoce el producto |
| Clases / complejidad | Una clase menos; opción válida y simple | Una clase adicional y estado transitorio por carga |
| Invariantes / testabilidad | Board valida el agregado | El mismo Board valida; no se duplican reglas |
| Variación real | Suficiente para una única representación | No se demostró necesidad de múltiples builders |
| Legibilidad / UML / defensa | Flujo directo fácil de seguir | Inicio, filas y build delimitan el ensamblado y la publicación |
| Extensión | Extraer ensamblado cuando haga falta | Ofrece frontera para pasos futuros; no los implementa |

Se ensaya un **LevelBuilder concreto, interno y por carga**, sin interfaz ni SokobanLevelBuilder duplicado ni Director adicional. Beneficio acotado: encapsular el acumulador y su cierre; coste: una clase y copias defensivas. La construcción directa puede cumplir las mismas garantías y sigue siendo alternativa. Compilar/pruebas no ratifican GoF ni D-011; Builder permanece PREFERRED para revisión del equipo. No hay Factory Method, Abstract Factory ni Prototype.

Position es valor de coordenadas; Terrain/Occupant son enums limitados al subconjunto, no una decisión de jerarquía para cajas. Cell combina ambos y evita ocupantes en paredes. Board conserva filas inmutables, valida forma/límites/jugador, consulta celdas y conteos (Information Expert). Level es el producto que contiene Board. LevelParser interpreta sintaxis; LevelBuilder ensambla (Creator); LevelLoader lee bytes externos, limita tamaño y decodifica UTF-8 antes de delegar. Una única representación inmutable, sin aliases al acumulador ni cache del loader. No se duplican Board ni posiciones mutables. Sin reglas de movimiento, victoria o UI. Responsabilidades separadas (SRP); sin herencia artificial ni contratos LSP de cajas especiales todavía ausentes. No se agrega una interfaz para una sola implementación.

El UML integral v0.5 continúa como candidato histórico intacto; el [diagrama del experimento](../architecture/uml/level-loading-spike-v1.puml) describe sólo los tipos realmente creados. En R3 se comprobaron sintaxis, renderizado y correspondencia con el código mediante PlantUML y Graphviz, incluidos accessors de records y herencia de la excepción. No representa la arquitectura final ni completa StarUML.

## Toolchain provisional

Para compilar y ejecutar este experimento se necesita un JDK completo 17 o superior. En Linux/macOS se usa Bash 3.2+ y utilidades `find`, `sort`, `mktemp` (GNU/BSD); en Windows 10/11, PowerShell 5.1 o 7 con el runner nativo, sin WSL/Git Bash/Python. El programa y las pruebas dependen sólo de Java estándar; PlantUML/Graphviz son herramientas auxiliares para el diagrama. Compilación: `--release 17 --limit-modules java.base -encoding UTF-8 -Xlint:all -Werror`, sin librerías externas. La elección definitiva de Java/build del producto sigue en OQ-11 OPEN.

Maven aportaría convenciones, ciclo de build/tests y reproducibilidad del equipo, con coste de configuración y plugins. Script javac/Java basta aquí, pero exige JDK explícito y un runner propio. Gradle añade flexibilidad que no se necesita en este alcance. La decisión posterior debe fijar target/runtime, sistema de build, instrucciones desde checkout limpio y tests mantenibles; no se introduce ninguno como decisión definitiva.

## Verificación

Evidencia histórica R1/R2 del 2026-10-14: 30 casos PASS con JDK 26.0.2. Evidencia histórica R3 de esa misma fecha: 38 casos PASS con JDK 17.0.20.1 y JDK 26.0.2. Base histórica anterior al incremento: `1943053ec520ff3d8be0aecb21c2f417f3126380`. Runners: [Unix](../../scripts/test-level-loading.sh) y [PowerShell](../../scripts/test-level-loading.ps1), pruebas: [LevelLoadingTest.java](../../src/test/java/ar/edu/uade/sokoban/level/LevelLoadingTest.java). Reproducir sobre las fuentes y fixtures de esta rama; identificar la revisión exacta en el informe de revisión/entrega, sin autorreferenciar el SHA del commit que contiene este documento.

Desde la raíz del checkout, seleccionando el directorio de un JDK completo:

```bash
SPIKE_JDK_HOME='<directorio-del-JDK>' bash scripts/test-level-loading.sh
# Alternativa equivalente, si SPIKE_JDK_HOME no está definido:
JAVA_HOME='<directorio-del-JDK>' bash scripts/test-level-loading.sh
```

Selección del runner: SPIKE_JDK_HOME → JAVA_HOME → herramientas en PATH. Si javac/Java compatibles están en PATH, basta `bash scripts/test-level-loading.sh`. No se alteran las variables globales ni se instalan dependencias automáticamente. El script compila fuentes main/test con las opciones indicadas, ejecuta `java --limit-modules java.base` y usa un directorio temporal propio fuera del checkout que elimina al terminar. No instala, descarga ni modifica Git.

Salidas históricas R3, cada una con compilación sin warnings (`-Werror`) y ejecución sin java.desktop:

| Compilador/runtime | Resultado |
|---|---|
| JDK 17.0.20.1 | `RESULT: 38 PASS, 0 FAIL` |
| JDK 26.0.2 | `RESULT: 38 PASS, 0 FAIL` |

| Casos ejecutados | Evidencia |
|---|---|
| 01–05 | Dos fixtures TXT reales, dimensiones/celdas/conteos distintos, editar copia externa y recargar, jugador/caja sobre destino |
| 06–12 | Archivo faltante, vacío, símbolo con diagnóstico, cero/varios jugadores, filas irregulares; error no sustituye el Level previamente publicado y una carga válida posterior se recupera |
| 13–15 | UTF-8 y LF/CRLF/mixtos/sin salto final, igualdad determinista con agregados distintos, ejecución sin java.desktop y sin javax.swing.JPanel |
| 16–23 | UTF-8 malformado, Unicode fuera del alfabeto, BOM, CR aislado, fila vacía, segundo salto final, espacios preservados y tab rechazado |
| 24–30 | Dimensiones máximas/excesivas, límite de bytes, copias defensivas e inmutabilidad, coordenadas/celda válidas, invariantes de construcción directa, cierre del Builder, ausencia de regla implícita de cajas/destinos/cerramiento |
| 31–34 | Frontera de bytes y prioridad de errores de ancho, altura y rectangularidad antes de interpretar símbolos |
| 35–38 | Nulos públicos/anidados, columna negativa y diferencia entre bytes UTF-8 y caracteres |

R3 tuvo 38 casos nombrados, varios con más de una aserción; R4 conserva esos casos y agrega dos. El conteo no representa requisitos completos. No se reutilizan los 8/8 y 12/12 históricos. No se probó gameplay, solubilidad, concurrencia, Swing, catálogo ni nuevas entidades. Preparado con apoyo de IA; requiere revisión y comprensión del equipo.

## Correcciones verificadas R3

- Test 25 exige el diagnóstico estable `File exceeds 65536 bytes`, no cualquier LevelFormatException. El test 31 prueba que 65.536 bytes llegan a la validación estructural; el 38 diferencia bytes UTF-8 de cantidad de caracteres. Un tablero válido máximo de 100×100 con CRLF y salto final ocupa 10.200 bytes: el techo de 65.536 es defensa de lectura, no una dimensión alcanzable del tablero válido. No se cambiaron límites.
- Parser recorre filas sin split global. Antes de crear celdas verifica altura, ancho y rectangularidad con MAX_DIMENSION existente. La forma de cada fila precede a la interpretación de sus símbolos; un exceso de altura precede a interpretar la fila 101. Board mantiene las invariantes finales para construcción directa. No se agregan reglas de gameplay.
- Casos 32–34 verifican esa prioridad de diagnóstico; 35–36 verifican nulos públicos/anidados y 37 columna negativa. Nulos son errores del llamador y producen NullPointerException; errores de formato y de I/O conservan las categorías documentadas.
- Contrafactual controlado: copiar únicamente fuentes, tests, fixtures y runner a un temporal nuevo; eliminar allí sólo el bloque `if (bytes.length > MAX_FILE_BYTES)` del Loader y ejecutar el mismo runner con JDK 17. Resultado real: código 1, 36 PASS y fallos esperados en 25/38. La versión original quedó intacta y se eliminó la copia temporal.

## Validación reproducible del UML

Requiere PlantUML y Graphviz, independientes del runtime del programa. Desde la raíz del checkout:

```bash
JAVA_TOOL_OPTIONS=-Djava.awt.headless=true plantuml -checkonly docs/architecture/uml/level-loading-spike-v1.puml
JAVA_TOOL_OPTIONS=-Djava.awt.headless=true plantuml -tpng -failfast2 -o '<directorio-temporal-fuera-del-checkout>' docs/architecture/uml/level-loading-spike-v1.puml
```

Ejecución R3: PlantUML 1.2020.02 / Graphviz 14.1.2, ambos comandos código 0; PNG real de 1293×1397 píxeles revisado contra tipos, accessors y responsabilidades. Se eliminaron los artefactos temporales. Esta evidencia no convierte el UML integral candidato en diseño ratificado ni sustituye el entregable StarUML.

## Portabilidad R4 — 2026-10-14

El mismo core y `LevelLoadingTest.java` se utilizan en todos los sistemas. Path/Files y UTF-8 explícito evitan concatenación de separadores, comandos del SO y charset implícito. Locale.ROOT fija los diagnósticos de formato; las causas de IOException procedentes del sistema operativo pueden variar. No se depende de permisos POSIX, symlinks, orden de enumeración, Swing ni normalización de nombres Unicode. Crear y cargar un Path concreto evita asumir equivalencia de mayúsculas/minúsculas o de distintas normalizaciones Unicode entre filesystems.

El runner Unix obtiene la raíz desde su ubicación, admite TMPDIR externo y usa mktemp GNU/BSD con template explícito; su cleanup sólo actúa sobre el temporal creado. No usa readlink/realpath GNU, sort -z, sed -i, mapfile ni sintaxis posterior a Bash 3.2. Windows obtiene la raíz de PSScriptRoot y usa GetTempPath/Guid para un directorio nuevo; cleanup con LiteralPath, limitado a ese directorio. El argfile se escribe en UTF-8 sin BOM, con rutas entre comillas y separadores compatibles con javac; `-J-Dfile.encoding=UTF-8` fija su decodificación en JDK 17. Ambos runners rechazan un temporal dentro del checkout y propagan fallos del compilador/tests. No instalan ni alteran configuraciones.

Windows, desde la raíz del checkout:

```powershell
$env:SPIKE_JDK_HOME = '<directorio-del-JDK>'
powershell -NoProfile -File .\scripts\test-level-loading.ps1
# PowerShell 7: pwsh -NoProfile -File .\scripts\test-level-loading.ps1
# Alternativa: JAVA_HOME si SPIKE_JDK_HOME no está definido, o JDK completo en PATH.
```

En Linux/macOS usar los comandos Bash anteriores. Desde otro directorio, indicar la ruta al script: ambos descubren fuentes y fixtures por su propia ubicación. Las variables mostradas son selecciones del proceso/sesión, no cambios globales. Una política Windows que bloquee scripts se atiende mediante el procedimiento local autorizado para scripts confiables o firmados; no se solicita cambiar ExecutionPolicy global ni el runner lo modifica. Un error de compilación, test o limpieza produce salida no cero, no un PASS. No hay runner de juego ni Maven/Gradle adoptado.

| Plataforma / control | Resultado R4 | Alcance |
|---|---|---|
| Linux nativo, JDK 17.0.20.1 | 40 PASS / 0 FAIL | Compilación estricta y ejecución real |
| Linux nativo, JDK 26.0.2 | 40 PASS / 0 FAIL | Compilación release 17 y ejecución real en 26 |
| Linux, checkout y TMPDIR con espacios/Unicode, invocación externa | PASS en ambos JDK | Copia temporal controlada; worktree no movido |
| Linux, Java 17 con charset predeterminado US-ASCII | 40 PASS / 0 FAIL | Fuentes/TXT con UTF-8 explícito; diagnóstico estable |
| Mecanismo de argfile PowerShell, javac en Linux con charset US-ASCII | 40 PASS / 0 FAIL | UTF-8 sin BOM, rutas absolutas Unicode/espacios; no ejecuta PowerShell |
| Runner PowerShell 5.1/7 | Revisión estática PASS | JDK/args/exitcode/cleanup; no ejecución del script |
| Bash 3.2 / utilidades BSD macOS | Revisión de compatibilidad PASS | Sintaxis y operaciones portables; no ejecución en macOS |
| Windows 10/11 nativo | NOT_RUN | Requiere host Windows y los mismos tests |
| macOS nativo | NOT_RUN | Requiere host macOS y los mismos tests |

Casos nuevos: 39 carga, cambia y recarga un nombre de archivo con espacios/Unicode fuera de fixtures, verifica piso final y LF/CRLF; 40 verifica diagnóstico ASCII bajo localización de formato árabe y restaura la localización. Todos los 38 anteriores se preservan. Controles negativos del runner Unix comprobaron JDK explícito inexistente, TMPDIR dentro del checkout y fixture inválido: salida no cero y ningún temporal residual. En un checkout/copia con espacios y Unicode se ejecutaron ambos JDK desde otra ubicación y con TMPDIR externo personalizado. El resultado esperado de la suite actual es `RESULT: 40 PASS, 0 FAIL`.

EditorConfig conserva espacios finales únicamente en TXT bajo directorios levels. Atributos Git acotados fijan LF para fuentes Java, runners y niveles; el parser sigue probando CRLF/BOM con bytes generados por los tests. Los espacios significativos no se recortan ni se convierten en excepción global de whitespace. La regla de la cápsula histórica v4 permanece intacta.

Próxima evidencia: ejecutar runners y 40 casos en Windows (PowerShell 5.1/7) y macOS con JDK completo, checkout y temporales con espacios/Unicode, invocación desde otra ubicación; registrar exitcode y ausencia de residuos. La compatibilidad diseñada no demuestra ejecución nativa. Revisión grupal del contrato/Builder y eventual publicación son pasos separados; OQ-08/OQ-11 siguen OPEN y D-011 PREFERRED.
