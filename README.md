# Los Force Push — Sokoban

Prototipo de Sokoban en **Java y Swing**, desarrollado para el trabajo integrador de Procesos de Desarrollo de Software de UADE. El jugador recorre un tablero 2D y empuja cajas normales hacia destinos. El proyecto explora SOLID, GRASP, MVC y patrones GoF.

**Estado:** la rama individual integra en Swing el runtime con hielo, cajas especiales, Undo, Restart, victoria y score experimental. El TPO sigue incompleto: faltan adicionales elegibles, revisión de derechos y entregables; la aceptación del equipo no está registrada.

## Inicio rápido

### Requisitos

- Un **JDK completo 17 o superior** con `java` y `javac` compatibles; los runners compilan con `--release 17`. La versión y el sistema de build definitivos siguen abiertos ([OQ-11](docs/requirements/OPEN_QUESTIONS.md)).
- Un entorno gráfico de escritorio para abrir Swing. Git permite clonar el repositorio.
- Windows: PowerShell 5.1 o 7. Linux/macOS: Bash y utilidades `find`, `sort` y `mktemp`.

No se requieren Maven, Gradle ni bibliotecas Java externas. Sprites, sonidos y demo están en el repositorio; no hace falta descargar ZIP ni instalar Python/Pillow para jugar. Para el verificador documental son necesarios Git, Bash y Python 3.9 o superior; son herramientas opcionales para ejecutar el juego.

### Windows — PowerShell

Para una carpeta de destino todavía inexistente, clonar la rama individual y ejecutarla:

```powershell
git clone --branch impaciencia --single-branch https://github.com/smatrauade/Los-Force-Push.git
cd Los-Force-Push
java -version
javac -version
powershell.exe -NoProfile -File .\scripts\run-swing.ps1
```

Si ya existe un clon, usar su carpeta y comprobar la rama activa antes de ejecutar; no clonar encima. `impaciencia` es la implementación individual y no sustituye a `main` ni implica aceptación del equipo.

En PowerShell 7 se puede usar `pwsh -NoProfile -File ./scripts/run-swing.ps1`. Si `javac` no se encuentra, instalar o seleccionar un JDK completo. El runner selecciona `SPIKE_JDK_HOME`, luego `JAVA_HOME` y finalmente el PATH; si alguna de esas variables apunta a un JDK inexistente, corregirla. Para seleccionar uno específicamente en la sesión actual:

```powershell
$env:SPIKE_JDK_HOME = 'C:/ruta/al/JDK'
powershell -NoProfile -File ./scripts/run-swing.ps1
```

### Linux y macOS — Bash

Desde la raíz del proyecto:

```bash
java -version
javac -version
bash scripts/run-swing.sh
```

El runner Bash utiliza `java` y `javac` de `PATH`; seleccionar un JDK compatible en `PATH` si fuera necesario. Por ejemplo, reemplazar la ruta de este comando opcional por la instalación local:

```bash
export PATH="/ruta/al/JDK/bin:$PATH"
```

El runner Swing Bash no entrecomilla las rutas de fuentes en el archivo de argumentos de `javac`: usar una ruta de checkout **sin espacios**. Ambos runners compilan en un directorio temporal y agregan los recursos al classpath. Si falta `javac`, instalar un JDK completo, no sólo un JRE. Si faltan imágenes, iniciar mediante el runner desde el clon intacto. El audio depende de un dispositivo disponible.

## Uso del juego

En el menú, **NUEVA PARTIDA / DEMO** inicia una campaña de tres niveles: el primero es estándar; el segundo usa KeyBox, Lock y muros; el tercero combina hielo y una Fragile que debe sobrevivir hasta el Goal. Sus archivos están en [recursos desktop](src/desktop/resources/levels). **ABRIR NIVEL TXT** permite elegir explícitamente STANDARD o EXTENDED y jugar el archivo como nivel autónomo. Las flechas mueven y empujan; `Esc` pausa, `M` controla sonido y `+`/`-` cambian zoom. `Z` deshace cinco movimientos cuando hay al menos cinco en el historial y quedan usos; el historial conserva hasta 15 y el límite es tres usos hasta Restart o cambio de nivel. `R` reinicia el estado inicial sin releer el archivo; conserva el total de Undo del nivel para el score.

Al ganar se muestra el resumen y score; **SIGUIENTE NIVEL** requiere una campaña con otro nivel. En el último se informa campaña completada. Guardar/cargar partida permanece deshabilitado. La fórmula y la extensión TXT son decisiones individuales; no ratifican OQ-01/09 ni aceptación TEAM.

También se puede iniciar con un nivel externo como argumento, reemplazando la ruta por un archivo propio:

```powershell
powershell -NoProfile -File ./scripts/run-swing.ps1 -LevelPath 'C:/niveles/mi nivel.txt'
powershell.exe -NoProfile -File .\scripts\run-swing.ps1 -LevelPath '.\src\desktop\resources\levels\campaign-c.txt' -Extended
```

`-Extended` exige `-LevelPath` y selecciona explícitamente el formato extendido; no se infiere del contenido. Las rutas relativas se interpretan desde el directorio actual y se pueden usar rutas absolutas entre comillas. Los modos `-Test` y `-UiTest` no aceptan niveles ni `-Extended`.

```bash
bash scripts/run-swing.sh '/ruta/al/nivel.txt'
# Para un TXT extendido:
bash scripts/run-swing.sh --extended '/ruta/al/nivel-extendido.txt'
```

La carga jugable acepta UTF-8 estricto, hasta 65.536 bytes, sin BOM: formato legacy con `#` pared, espacio piso, `.` destino, `@` jugador, `$` caja, `+` jugador en destino y `*` caja en destino; o C2-L7 con los mismos estados, separados por un espacio ASCII. Ejemplo C2:

```text
!sokoban-txt 2
[grid]
# # # # #
# @ $ . #
# # # # #
[attributes]
```

En formato STANDARD, `[attributes]` debe estar vacía y se rechazan entidades especiales; elegir EXTENDED para el contrato individual. La carga inválida informa el error y no reemplaza el nivel anterior. Los detalles de formato y límites están en el [contrato TXT](docs/requirements/FRAGILE_TXT_CONTRACT_PROPOSAL.md).

## Capacidades y pruebas

Swing utiliza `LevelProgression` y el runtime inmutable para niveles estándar y extendidos, movimiento, entidades especiales, Undo/Restart y finalización. La campaña de ejemplo usa score individual; incluye dos candidatos técnicos de adicionales y Observer de notificación; la elegibilidad académica de los extras, derechos de recursos y entregables siguen pendientes.

Para comprobar el runner de escritorio sin abrir ventana, desde la raíz:

```bash
bash scripts/run-swing.sh --test
```

En Windows: `powershell -NoProfile -File ./scripts/run-swing.ps1 -Test`. La [estrategia de pruebas](docs/testing/TEST_STRATEGY.md) incluye comandos y suites independientes para C2 y Fragile, plataformas y límites. Una ejecución histórica reportó 160 casos PASS de Loading, C2, Fragile, Movement y Desktop headless sobre el código identificado allí; no certifica el TPO completo, gameplay especial ni aceptación grupal. Las pruebas GUI, manuales y audio requieren sus propias evidencias.

En Windows, además:

```powershell
powershell.exe -NoProfile -File .\scripts\test-level-loading.ps1 -Full
powershell.exe -NoProfile -File .\scripts\run-swing.ps1 -UiTest
```

`-Full` ejecuta las nueve suites core; el smoke de UI requiere escritorio y automatiza acciones, sin sustituir una prueba humana ni verificar audio audible. [Evidencia Windows y límites](docs/testing/IMPACIENCIA_WINDOWS_COMPATIBILITY.md). Para problemas de sonido, comprobar dispositivo y que el mute no esté activado; la aplicación puede ejecutarse sin sonido.

## Estilos y repetición

En **OPCIONES**, elegir ORIGINAL_COBRE, NOCTURNO o JADE; el tema cambia componentes y sprites sin reiniciar la partida. Tras completar un nivel, **VER REPETICION** muestra la trayectoria final: reproducir, pausar, reiniciar, avanzar un paso o volver al resultado. Undo elimina los cinco comandos deshechos y Restart limpia la grabación; un límite de 10.000 comandos deshabilita el replay completo en lugar de truncarlo. La reproducción es de sólo lectura y se verifica contra el estado final.

Son dos implementaciones técnicas individuales candidatas para RF-12, no una aceptación académica ni selección grupal de replay. OQ-10 sigue abierta; los derechos de los recursos originales siguen pendientes.

## Carga TXT extendida del core

La rama incorpora `LevelLoader.loadExtended(Path)` y `RuntimeState.fromExtended(...)` para definiciones individuales con hielo, Fragile, KeyBox, Lock y muros. La interfaz la utiliza al elegir EXTENDED. La gramática no ratifica OQ-08; ver [contrato y ejemplos](docs/requirements/IMPACIENCIA_RUNTIME_TXT_CONTRACT.md) y [pruebas](docs/testing/TEST_STRATEGY.md).

## Sesión del core

`GameSession` administra un `RuntimeState` inicial, movimiento, historial acotado, Undo de cinco y Restart. La interfaz la utiliza a través del coordinador de progresión. [Decisiones y pruebas](docs/continuity/IMPACIENCIA_MASTERPLAN.md).

## Finalización del core

`VictoryEvaluator`, `ScoreStrategy` y `LevelProgression` ofrecen victoria, score individual y progresión explícita de niveles externos. La política es experimental y la interfaz la utiliza; [decisiones y fixtures resueltos](docs/continuity/IMPACIENCIA_MASTERPLAN.md).

## Prototipo experimental de atributos individuales

`LevelLoader.loadExperimentalFragile(Path)` lee una definición C2 con Fragile y devuelve `ExperimentalFragileLevel`: una plantilla espacial inmutable y una lista de ID, posición y resistencia inicial. Conserva piso/destino, pero **no devuelve un nivel jugable**; no enviar la plantilla sola al motor como si incluyera las cajas. La carga STANDARD sigue rechazando Fragile; seleccionar EXTENDED para jugar niveles especiales.

El subconjunto TXT soportado fue adoptado por el responsable del módulo en [D-020](docs/decisions/DECISION_LOG.md#d-020--decisión-del-responsable-txt--29102026), sin voto TEAM adicional ni aprobación del código. [Sintaxis, ejemplos, límites, excepciones y traspaso a Model/Movement](docs/requirements/FRAGILE_TXT_CONTRACT_PROPOSAL.md); [pruebas y ejecución](docs/testing/TEST_STRATEGY.md#prototipo-experimental-de-atributos-individuales). OQ-06 está resuelta para el lector; OQ-08 conserva extensiones pendientes.

## Documentación técnica

| Tema | Fuente y propósito |
|---|---|
| Requisitos y preguntas | [REQUIREMENTS](docs/requirements/REQUIREMENTS.md) normaliza la consigna; [OPEN_QUESTIONS](docs/requirements/OPEN_QUESTIONS.md) mantiene los pendientes. |
| Arquitectura | [ARCHITECTURE](docs/architecture/ARCHITECTURE.md) distingue implementación de propuesta; [RESPONSIBILITIES](docs/architecture/RESPONSIBILITIES.md) describe roles y límites. |
| UML | [STARUML_STATUS](docs/architecture/uml/STARUML_STATUS.md) enlaza diagramas implementados y candidato; el entregable `.mdj` sigue pendiente. |
| Decisiones | [DECISION_LOG](docs/decisions/DECISION_LOG.md) registra acuerdos, autoridad y alternativas. |
| Trazabilidad | [TRACEABILITY_MATRIX](docs/traceability/TRACEABILITY_MATRIX.md) relaciona requisitos, código y evidencia. |
| Pruebas | [TEST_STRATEGY](docs/testing/TEST_STRATEGY.md) reúne comandos, resultados históricos y limitaciones. |
| Recursos y uso de IA | [ASSET_POLICY](docs/assets/ASSET_POLICY.md), [manifiesto](docs/punto5/ASSETS_REGISTRO_SHA256.csv) y [IA/licencias](docs/punto5/USO_IA_Y_LICENCIAS.md) describen recursos y derechos pendientes. |
| Fuentes | [BIBLIOGRAPHY](docs/references/BIBLIOGRAPHY.md) y [SOURCE_REGISTRY](docs/continuity/SOURCE_REGISTRY.md) identifican referencias, procedencia y disponibilidad. |
| Estado y backlog | [CURRENT_STATE](docs/continuity/CURRENT_STATE.md) registra capacidades y eventos; [TODO](TODO.md) conserva el backlog oficial. |
| Continuidad | [AGENTS](AGENTS.md) → [REHYDRATION_INDEX](docs/continuity/REHYDRATION_INDEX.md) → fuentes indicadas; para esta rama, consultar también [IMPACIENCIA_STATE](docs/continuity/IMPACIENCIA_STATE.md). |
| Contribución | [CONTRIBUTING](CONTRIBUTING.md) explica preflight, ramas, revisión y convenciones. |

El [índice documental](docs/README.md) incluye más guías, entre ellas instalación con GitHub Desktop y contratos entre módulos.

## Desarrollo individual y oficial

Este README pertenece a `impaciencia`, rama individual de experimentación creada desde el baseline oficial de `main`. Sus cambios no reemplazan automáticamente decisiones ni trabajo del equipo. Consultar Git y [su estado de continuidad](docs/continuity/IMPACIENCIA_STATE.md) antes de continuar. El estado de `main` y sus integraciones se consulta en Git/GitHub y [CURRENT_STATE](docs/continuity/CURRENT_STATE.md).

## Contexto académico

La entrega informada es el **18/11/2026**, con demo en vivo de 15–20 minutos y explicación de patrones. La consigna normalizada exige repositorio público con código y README de ejecución, video de gameplay, StarUML e informe técnico. La explicación oral aportada por el solicitante también describe cómo descargar, ejecutar y conocer dependencias; procede de ASR sin verificación acústica. [Requisitos y entrega](docs/requirements/REQUIREMENTS.md#entrega-y-aceptación-docente--29102026) · [procedencia de fuentes](docs/continuity/SOURCE_REGISTRY.md) · [pendientes de entrega](TODO.md).
