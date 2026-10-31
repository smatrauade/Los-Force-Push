# Los Force Push — Sokoban

Prototipo de Sokoban en **Java y Swing**, desarrollado para el trabajo integrador de Procesos de Desarrollo de Software de UADE. El jugador recorre un tablero 2D y empuja cajas normales hacia destinos. El proyecto explora SOLID, GRASP, MVC y patrones GoF.

**Estado:** se puede ejecutar una porción experimental con carga de niveles y movimiento básico; el TPO está incompleto. Hielo, cajas especiales jugables, Undo, Restart, victoria y puntaje todavía no están integrados en el juego Swing. La aceptación del código por el equipo no está registrada.

## Inicio rápido

### Requisitos

- Un **JDK completo 17 o superior** con `java` y `javac` compatibles; los runners compilan con `--release 17`. La versión y el sistema de build definitivos siguen abiertos ([OQ-11](docs/requirements/OPEN_QUESTIONS.md)).
- Un entorno gráfico de escritorio para abrir Swing. Git permite clonar el repositorio.
- Windows: PowerShell 5.1 o 7. Linux/macOS: Bash y utilidades `find`, `sort` y `mktemp`.

No se requieren Maven, Gradle ni bibliotecas Java externas. Sprites, sonidos y demo están en el repositorio; no hace falta descargar ZIP ni instalar Python/Pillow para jugar. Para el verificador documental son necesarios Git, Bash y Python 3.9 o superior; son herramientas opcionales para ejecutar el juego.

Clonar y entrar al directorio (con acceso al repositorio):

```bash
git clone https://github.com/smatrauade/Los-Force-Push.git
cd Los-Force-Push
```

El clon obtiene la rama predeterminada del remoto. `impaciencia` es una rama experimental local y no se presupone publicada.

### Windows — PowerShell

Desde la raíz del proyecto, comprobar ambas herramientas y ejecutar:

```powershell
java -version
javac -version
powershell -NoProfile -File ./scripts/run-swing.ps1
```

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

En el menú, elegir **NUEVA PARTIDA / DEMO** para abrir el [nivel incluido](src/desktop/resources/levels/demo.txt), o **ABRIR NIVEL TXT** para seleccionar un archivo externo compatible. Las flechas mueven al jugador y empujan una caja normal; `Esc` pausa, `M` controla el sonido y `+`/`-` cambian el zoom. Undo, Restart, guardar/cargar partida y siguiente nivel necesitan servicios todavía ausentes; sus controles permanecen deshabilitados sin callbacks operativos. El HUD muestra nivel y contadores demostrativos; la pantalla final no detecta victoria ni calcula score.

También se puede iniciar con un nivel externo como argumento, reemplazando la ruta por un archivo propio:

```powershell
powershell -NoProfile -File ./scripts/run-swing.ps1 -LevelPath 'C:/niveles/mi nivel.txt'
```

```bash
bash scripts/run-swing.sh '/ruta/al/nivel.txt'
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

La sección `[attributes]` debe estar vacía en la ruta jugable; se rechazan entidades especiales. La carga inválida informa el error y no reemplaza el nivel anterior. Los detalles de formato y límites están en el [contrato TXT](docs/requirements/FRAGILE_TXT_CONTRACT_PROPOSAL.md).

## Capacidades y pruebas

El core implementa carga legacy/C2-L7, tablero inmutable y movimiento cardinal con empuje de una caja normal. Swing ofrece menú, pausa, opciones, ayuda, HUD, sprites y sonidos. La definición experimental Fragile descrita abajo **no** habilita cajas frágiles jugables. Quedan pendientes hielo, KeyBox, Lock y muros dinámicos, sesión y Undo/Restart, victoria/progresión, score y dos adicionales elegibles.

Para comprobar el runner de escritorio sin abrir ventana, desde la raíz:

```bash
bash scripts/run-swing.sh --test
```

En Windows: `powershell -NoProfile -File ./scripts/run-swing.ps1 -Test`. La [estrategia de pruebas](docs/testing/TEST_STRATEGY.md) incluye comandos y suites independientes para C2 y Fragile, plataformas y límites. Una ejecución histórica reportó 160 casos PASS de Loading, C2, Fragile, Movement y Desktop headless sobre el código identificado allí; no certifica el TPO completo, gameplay especial ni aceptación grupal. Las pruebas GUI, manuales y audio requieren sus propias evidencias.

## Carga TXT extendida del core

La rama incorpora `LevelLoader.loadExtended(Path)` y `RuntimeState.fromExtended(...)` para definiciones individuales con hielo, Fragile, KeyBox, Lock y muros. Es una API del core, **no está conectada a Swing**. La gramática no ratifica OQ-08; ver [contrato y ejemplos](docs/requirements/IMPACIENCIA_RUNTIME_TXT_CONTRACT.md) y [pruebas](docs/testing/TEST_STRATEGY.md).

## Prototipo experimental de atributos individuales

`LevelLoader.loadExperimentalFragile(Path)` lee una definición C2 con Fragile y devuelve `ExperimentalFragileLevel`: una plantilla espacial inmutable y una lista de ID, posición y resistencia inicial. Conserva piso/destino, pero **no devuelve un nivel jugable**; no enviar la plantilla sola al motor como si incluyera las cajas. La carga jugable ordinaria sigue rechazando Fragile.

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

Este README pertenece a `impaciencia`, rama individual de experimentación creada desde el baseline oficial de `main`. Sus cambios no reemplazan automáticamente decisiones ni trabajo del equipo. La rama puede existir sólo localmente: consultar Git y [su estado de continuidad](docs/continuity/IMPACIENCIA_STATE.md) antes de continuar o intentar obtenerla. El estado de `main` y sus integraciones se consulta en Git/GitHub y [CURRENT_STATE](docs/continuity/CURRENT_STATE.md).

## Contexto académico

La entrega informada es el **18/11/2026**, con demo en vivo de 15–20 minutos y explicación de patrones. La consigna normalizada exige repositorio público con código y README de ejecución, video de gameplay, StarUML e informe técnico. La explicación oral aportada por el solicitante también describe cómo descargar, ejecutar y conocer dependencias; procede de ASR sin verificación acústica. [Requisitos y entrega](docs/requirements/REQUIREMENTS.md#entrega-y-aceptación-docente--29102026) · [procedencia de fuentes](docs/continuity/SOURCE_REGISTRY.md) · [pendientes de entrega](TODO.md).
