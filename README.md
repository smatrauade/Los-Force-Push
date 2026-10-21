# Los Force Push — Trabajo Integrador Sokoban

Proyecto académico de Ingeniería en Informática, UADE: **Proceso de Desarrollo de Software**, profesor **Jonathan Leonel Pepe**. La tecnología exigida es **Java, Swing y GUI 2D**. El objetivo es un diseño defendible mediante SOLID, GRASP, MVC y GoF.

## Estado al 2026-10-21

**Checkpoint posterior R5:** QA Windows nativa del HEAD PR #5 `0670a4d5137f0e9f1db221866a02c29987024847` con **18/18 escenarios CLI** (PS5.1/PS7), Loading 40 y Movement 31 transitivos, 16/16 Swing headless y GUI real con TXT externos válidos/inválidos, todos **reportados por el agente local con artefactos contrastados**. [Evidencia y límites](docs/continuity/checkpoints/WINDOWS_PR5_NATIVE_QA_2026-10-21.md). Al corte de QA previo a la integración, `main` no contenía Swing y PR #5 estaba Draft; **el estado vigente debe consultarse en Git/GitHub**. Su integración posterior por decisión del autor no equivale a revisión independiente ni a aceptación académica. Las referencias posteriores a Windows histórico/NOT_RUN describen otras iteraciones, no este checkpoint.


El bootstrap documental y estructural está integrado en `main`: el [PR #1](https://github.com/smatrauade/Los-Force-Push/pull/1) se fusionó el 2026-10-08. El PR #2 de reconciliación se fusionó el 2026-10-14. **La base histórica `1943053ec520ff3d8be0aecb21c2f417f3126380` todavía no contenía clases Java ni build.** Esta rama incorpora un primer incremento Swing jugable experimental sobre el core de carga y movimiento; no es el producto completo. La documentación y la estructura inicial del proyecto están creadas; el merge no ratifica las propuestas de diseño. El [estado vivo](docs/continuity/CURRENT_STATE.md) registra el evento histórico y su SHA.

El core se integró mediante el [PR #3](https://github.com/smatrauade/Los-Force-Push/pull/3) el 20/10 y el aislamiento de runners mediante el [PR #4](https://github.com/smatrauade/Los-Force-Push/pull/4) el 21/10. Esta recuperación incorpora sólo Swing V1 sobre la base integrada `ee83f9ebd30ee1b553c555c89d9ea453131d6678`, conservando el código y los runners del core. La rama `feat/swing-playable-notebook-v1` está publicada mediante el [PR #5](https://github.com/smatrauade/Los-Force-Push/pull/5), abierto como Draft contra `main`, sin fusionar y pendiente de revisión humana.

| Área | Estado |
|---|---|
| Requisitos | Normalizados desde el prompt autorizado; PDF original no disponible |
| Arquitectura y UML | Candidatos; requieren revisión del equipo |
| Builder / Memento / Observer / Strategy | `PREFERRED`, no decisiones aceptadas |
| Métricas de Undo fuera del Memento | `TEAM-PROPOSAL`, pendiente de ratificación |
| Spikes Java 21 | Resultados históricos reportados 8/8 y 12/12; no reproducidos aquí |
| TXT, score, dos funcionalidades adicionales | `OPEN` |
| Versión Java / herramienta de build | `OPEN` |
| StarUML, assets, video e informe final | Pendientes |

Consultar siempre el [estado vivo](docs/continuity/CURRENT_STATE.md) y contrastarlo con el HEAD actual. Las cápsulas son snapshots históricos y no reemplazan ese contraste. Este snapshot contiene el [core experimental de carga TXT](docs/experiments/LEVEL_LOADING_R1.md), movimiento cardinal y empuje de una caja normal, pruebas automatizadas, runners Bash/PowerShell y UML/documentación del incremento. La base histórica de carga es `44b3c63b437465b5f54c1f0f5ca5cbadb5def0e5`; no representa un HEAD permanente. El contrato y el diseño siguen sin ratificación del equipo; OQ-08/OQ-11 permanecen abiertas.

## Empezar sin chats anteriores

1. Leer [AGENTS.md](AGENTS.md), el contrato operativo.
2. Leer [CURRENT_STATE.md](docs/continuity/CURRENT_STATE.md).
3. Leer los [requisitos normalizados](docs/requirements/REQUIREMENTS.md) y [preguntas abiertas](docs/requirements/OPEN_QUESTIONS.md).
4. Revisar el [registro de decisiones](docs/decisions/DECISION_LOG.md), la [arquitectura](docs/architecture/ARCHITECTURE.md) y las [responsabilidades](docs/architecture/RESPONSIBILITIES.md).
5. Consultar [trazabilidad](docs/traceability/TRACEABILITY_MATRIX.md), [TODO](TODO.md) y [política de actualización](docs/continuity/FRESHNESS_POLICY.md).

El [índice documental](docs/README.md) enlaza fuentes, evidencia, UML y continuidad. La [cápsula de bootstrap](docs/continuity/capsules/BOOTSTRAP_2026-10-08.md) resume esta integración; la cápsula anterior se preserva como evidencia recibida.

## Requisitos centrales

Niveles leídos realmente desde TXT externos; jugador por flechas; empuje de una sola caja; cajas normal, frágil y llave; destinos, hielo, cerrojos y muros abiertos/cerrados. Undo conserva hasta 15 movimientos y retrocede 5 por uso, con hasta 3 usos consecutivos. Victoria avanza al próximo nivel. HUD muestra movimientos, empujes y nivel, con botones Undo y Restart. El resumen de nivel muestra también usos de Undo y puntaje.

Todos los elementos del tablero usan imágenes y las acciones tienen efectos sonoros. Los assets deben ser de dominio público, con licencia verificada individualmente. Se requieren **dos funcionalidades adicionales** que apliquen patrones; resolver un requisito obligatorio con un patrón no lo convierte automáticamente en adicional.

El profesor podría modificar o escribir un TXT compatible para verificar que los niveles no están hardcodeados. No hace falta medir tiempo ni incorporar animaciones. Las ambigüedades están identificadas como OQ-01 a OQ-11 y permanecen abiertas.

## Clonar y verificar la documentación

El clon siguiente selecciona la rama por defecto `main`. Al control del 2026-10-21 posterior a la publicación, main contiene el core y el aislamiento de los PR #3/#4; Swing V1 está en la rama publicada `feat/swing-playable-notebook-v1` del PR #5 y aún no está integrado en main. Los comandos desktop de esta guía requieren un checkout que contenga el incremento Swing.

```bash
git clone https://github.com/smatrauade/Los-Force-Push.git
cd Los-Force-Push
bash scripts/verify-repo-readonly.sh
```

La rama publicada puede obtenerse directamente con `git clone --branch feat/swing-playable-notebook-v1 https://github.com/smatrauade/Los-Force-Push.git Los-Force-Push-Swing` y entrando en `Los-Force-Push-Swing`. Antes de compilar, verificar el SHA de la rama y del [PR #5](https://github.com/smatrauade/Los-Force-Push/pull/5); el [estado vivo](docs/continuity/CURRENT_STATE.md) registra el último HEAD observado y los dos commits publicados en R4.

Requiere Git, Bash y Python 3 para la auditoría documental; **Python no es una decisión sobre el runtime del juego**. El script inspecciona archivos y Git, no hace fetch, commits ni escrituras. Para verificar un checkout limpio agregar `--require-clean`; usarlo antes de editar o después de un commit exitoso. El modo `--bootstrap` audita exclusivamente la rama original `docs/bootstrap-sokoban-autocontenido-v1` y exige ese nombre de rama; no corresponde a la reconciliación ni al checkout de `main`. No reemplaza el preflight remoto de permisos/PRs.

## Ejecutar las pruebas del incremento experimental

Se necesita un JDK completo 17 o superior. Desde la raíz del checkout:

```bash
# Linux/macOS: Bash 3.2+ y find, sort, mktemp (GNU o BSD).
SPIKE_JDK_HOME='<directorio-del-JDK>' bash scripts/test-level-loading.sh
```

```powershell
# Windows 10/11: Windows PowerShell 5.1 o PowerShell 7.
$env:SPIKE_JDK_HOME = '<directorio-del-JDK>'
powershell -NoProfile -File .\scripts\test-level-loading.ps1
# Con PowerShell 7, reemplazar powershell por pwsh.
```

Ambos runners seleccionan `SPIKE_JDK_HOME` → `JAVA_HOME` → PATH, compilan las mismas fuentes y ejecutan los mismos tests Java; Windows no necesita Bash, WSL, Python ni GNU coreutils. También pueden invocarse desde otro directorio indicando la ruta del script. Las clases y archivos de prueba mutables se crean en un temporal propio fuera del checkout y se eliminan al terminar. Resultado esperado: `RESULT: 40 PASS, 0 FAIL` y código 0; fallos de JDK, compilación, tests o cleanup deben investigarse ante una salida no cero. No se instalan herramientas ni se cambian políticas globales. Si una política Windows impide ejecutar el script, seguir un procedimiento autorizado para scripts confiables o firmados; el runner no cambia ExecutionPolicy.

**Límite de los runners experimentales:** compilan solo los paquetes `level` y `movement` y sus pruebas, con `java.base`. Los componentes Swing (`java.desktop`) quedan fuera de esa compilación. La compilación y las pruebas de *todo* el producto requerirán un build separado cuando el equipo cierre OQ-11; un PASS del core no valida la aplicación gráfica.

Evidencia R4 (2026-10-14): Linux nativo PASS con JDK 17/26; Windows y macOS nativos NOT_RUN. El runner PowerShell recibió revisión estática y Bash revisión de compatibilidad con macOS; estas revisiones no prueban ejecución en esos sistemas. Ver [comandos, matriz y límites](docs/experiments/LEVEL_LOADING_R1.md). Fuentes: `src/main/java/ar/edu/uade/sokoban/level`; pruebas: `src/test/java`; fixtures: `src/test/resources/levels`. EditorConfig preserva espacios de piso en TXT de niveles y Git fija LF sin recortarlos.

En Windows, el incremento Swing se inicia con `powershell -NoProfile -File .\scripts\run-swing.ps1`; usa el mismo JDK seleccionado arriba. `-LevelPath '<archivo.txt>'` permite otro TXT y `-Test` ejecuta las 16 pruebas de integración sin pantalla. También admite pwsh en Windows; el script busca ejecutables `.exe`. [Alcance, comandos y validación](docs/experiments/SWING_VERTICAL_SLICE_V1.md). Java/build del producto siguen en OQ-11 OPEN; estos comandos experimentales no los ratifican ni introducen Maven/Gradle.

## Movimiento básico experimental

Este incremento contiene el núcleo experimental de movimiento cardinal y empuje de una caja normal, con pruebas reproducibles. El [informe histórico R1](docs/experiments/MOVEMENT_R1.md) describe su contrato y alternativas. Board sigue inmutable y Level conserva su estado inicial. El núcleo es **IMPLEMENTED_EXPERIMENTAL** y **TESTED**; este snapshot no registra ratificación grupal del diseño experimental ni contiene un juego gráfico completo. La implementación, las pruebas, la aceptación del equipo y la integración en la rama principal son estados independientes. La integración efectiva se comprueba mediante el historial Git y el Pull Request correspondiente.

Para ejecutar carga y movimiento con una sola compilación, agregar `--movement` al comando Bash o `-IncludeMovement` al comando PowerShell. Resultado esperado: `RESULT: 40 PASS, 0 FAIL` seguido de `MOVEMENT RESULT: 31 PASS, 0 FAIL`, con salida 0. Sin opción se mantienen los 40 casos de carga. El runner PowerShell rechaza opciones desconocidas y argumentos posicionales antes de iniciar Java, con salida no cero. Conserva las convenciones de PowerShell: nombres sin distinción de mayúsculas y abreviaturas inequívocas como `-IncludeMov`. Para comprobar el contrato CLI con el mismo JDK seleccionado, ejecutar `powershell -NoProfile -File .\scripts\test-level-loading-arguments.ps1` (también admite `pwsh`). La regresión detecta PS5.1/PS7 y comprueba ambos por separado; ver casos y evidencia en la estrategia de pruebas.

| Iteración de movimiento — 2026-10-14 | Java 17 | Java 26 | Procedencia |
|---|---|---|---|
| R1 | 40 + 31 PASS reportados | 40 + 31 PASS reportados | Informe histórico R1 |
| R2 | 40 + 31 PASS reproducidos | NOT_RUN | Revisión independiente; sin defectos funcionales bloqueantes |
| R3 | 40 + 31 PASS reproducidos | 40 + 31 PASS reproducidos | Linux nativo, JDK 17.0.20.1 y 26.0.2, compilación estricta y salida 0 |

La [estrategia de pruebas](docs/testing/TEST_STRATEGY.md#contrato-cli-powershell--runner-r3) distingue suites versionadas, evidencia histórica y la validación nativa Windows del runner. macOS nativo continúa NOT_RUN; compatibilidad por diseño no equivale a ejecución nativa. La versión Java/build definitiva sigue OPEN. El [UML acotado](docs/architecture/uml/movement-core-spike-v1.puml) corresponde a este subconjunto. El backlog conserva la revisión grupal y las validaciones técnicas pendientes; el estado operativo de publicación y PR se consulta en Git/GitHub. El Swing experimental descrito abajo añade flechas y presentación. No hay hielo, cajas especiales, Undo, victoria, score ni audio.

## Primer incremento Swing jugable

En Windows, `powershell -NoProfile -File .\scripts\run-swing.ps1` compila con release 17 y abre el TXT versionado [demo.txt](src/desktop/resources/levels/demo.txt). Flechas para mover/empujar, selector Abrir TXT y contadores demostrativos; los bloqueos conservan el estado. Los gráficos son placeholders propios, pendientes de imágenes definitivas y licencias. No hay progresión, victoria, Undo/Restart ni audio. [Guía, arquitectura MVC, pruebas y límites](docs/experiments/SWING_VERTICAL_SLICE_V1.md).

Fuentes en src/desktop/java y tests en src/desktop-test/java conservan sin cambios los runners heredados del PR #4 y su independencia de java.desktop. La validación gráfica Windows es histórica; la validación Linux de esta recuperación se registra por separado en la [guía](docs/experiments/SWING_VERTICAL_SLICE_V1.md#recuperación-linux-r2--2026-10-21). Linux R2: 40 Loading + 31 Movement + 16 desktop headless PASS, cero FAIL y salida 0 con JDK 17/release 17 y C.utf8. GUI interactiva Linux y macOS: NOT_RUN. OQ-01…OQ-11 y la ratificación grupal siguen pendientes.

## Colaboración y entrega

Seguir [CONTRIBUTING.md](CONTRIBUTING.md): rama de alcance acotado, commits, PR y revisión por otro integrante. No escribir directamente en `main`, usar force-push ni activar GitHub Actions en este alcance.

El repositorio es **público**, verificado mediante la API de GitHub el 2026-10-21. Esta recuperación no cambió su visibilidad. También faltan la guía de instalación/ejecución del producto completo, gameplay en video, diagrama de clases StarUML e informe técnico; la entrega institucional deberá incluir a los integrantes en copia.

El bootstrap, la reconciliación documental y este spike fueron preparados y revisados con apoyo de IA (Codex). El equipo debe revisar las propuestas, declarar el uso de IA en el informe y comprender, validar, adaptar y defender individualmente el resultado. El incremento Swing reutiliza ese core sin cambiar sus reglas; no se volvieron a ejecutar los spikes históricos A/B.
