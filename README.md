# Los Force Push — Trabajo Integrador Sokoban

Proyecto académico de Ingeniería en Informática, UADE: **Proceso de Desarrollo de Software**, profesor **Jonathan Leonel Pepe**. La tecnología exigida es **Java, Swing y GUI 2D**. El objetivo es un diseño defendible mediante SOLID, GRASP, MVC y GoF.

## Estado al 2026-10-14

El bootstrap documental y estructural está integrado en `main`: el [PR #1](https://github.com/smatrauade/Los-Force-Push/pull/1) se fusionó el 2026-10-08. El PR #2 de reconciliación se fusionó el 2026-10-14. **La base histórica `1943053ec520ff3d8be0aecb21c2f417f3126380` todavía no contenía clases Java ni build.** El incremento de esta rama es experimental y no contiene un juego ejecutable. La documentación y la estructura inicial del proyecto están creadas; el merge no ratifica las propuestas de diseño. El [estado vivo](docs/continuity/CURRENT_STATE.md) registra el evento histórico y su SHA.

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

```bash
git clone https://github.com/smatrauade/Los-Force-Push.git
cd Los-Force-Push
bash scripts/verify-repo-readonly.sh
```

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

Evidencia R4 (2026-10-14): Linux nativo PASS con JDK 17/26; Windows y macOS nativos NOT_RUN. El runner PowerShell recibió revisión estática y Bash revisión de compatibilidad con macOS; estas revisiones no prueban ejecución en esos sistemas. Ver [comandos, matriz y límites](docs/experiments/LEVEL_LOADING_R1.md). Fuentes: `src/main/java/ar/edu/uade/sokoban/level`; pruebas: `src/test/java`; fixtures: `src/test/resources/levels`. EditorConfig preserva espacios de piso en TXT de niveles y Git fija LF sin recortarlos.

No existe aún un comando para ejecutar el juego. Java/build del producto siguen en OQ-11 OPEN: la compilación directa del spike no los ratifica ni introduce Maven/Gradle. La base histórica reservaba directorios con placeholders; esta rama contiene únicamente las fuentes y pruebas experimentales.

## Movimiento básico experimental

Este incremento contiene el núcleo experimental de movimiento cardinal y empuje de una caja normal, con pruebas reproducibles. El [informe histórico R1](docs/experiments/MOVEMENT_R1.md) describe su contrato y alternativas. Board sigue inmutable y Level conserva su estado inicial. El núcleo es **IMPLEMENTED_EXPERIMENTAL** y **TESTED**; este snapshot no registra ratificación grupal del diseño experimental ni contiene un juego gráfico completo. La implementación, las pruebas, la aceptación del equipo y la integración en la rama principal son estados independientes. La integración efectiva se comprueba mediante el historial Git y el Pull Request correspondiente.

Para ejecutar carga y movimiento con una sola compilación, agregar `--movement` al comando Bash o `-IncludeMovement` al comando PowerShell. Resultado esperado: `RESULT: 40 PASS, 0 FAIL` seguido de `MOVEMENT RESULT: 31 PASS, 0 FAIL`, con salida 0. Sin opción se mantienen los 40 casos de carga. El runner PowerShell rechaza opciones desconocidas y argumentos posicionales antes de iniciar Java, con salida no cero. Conserva las convenciones de PowerShell: nombres sin distinción de mayúsculas y abreviaturas inequívocas como `-IncludeMov`. Para comprobar el contrato CLI con el mismo JDK seleccionado, ejecutar `powershell -NoProfile -File .\scripts\test-level-loading-arguments.ps1` (también admite `pwsh`). La regresión detecta PS5.1/PS7 y comprueba ambos por separado; ver casos y evidencia en la estrategia de pruebas.

| Iteración de movimiento — 2026-10-14 | Java 17 | Java 26 | Procedencia |
|---|---|---|---|
| R1 | 40 + 31 PASS reportados | 40 + 31 PASS reportados | Informe histórico R1 |
| R2 | 40 + 31 PASS reproducidos | NOT_RUN | Revisión independiente; sin defectos funcionales bloqueantes |
| R3 | 40 + 31 PASS reproducidos | 40 + 31 PASS reproducidos | Linux nativo, JDK 17.0.20.1 y 26.0.2, compilación estricta y salida 0 |

La [estrategia de pruebas](docs/testing/TEST_STRATEGY.md#contrato-cli-powershell--runner-r3) distingue suites versionadas, evidencia histórica y la validación nativa Windows del runner. macOS nativo continúa NOT_RUN; compatibilidad por diseño no equivale a ejecución nativa. La versión Java/build definitiva sigue OPEN. El [UML acotado](docs/architecture/uml/movement-core-spike-v1.puml) corresponde a este subconjunto. El backlog conserva la revisión grupal y las validaciones técnicas pendientes; el estado operativo de publicación y PR se consulta en Git/GitHub. No hay hielo, cajas especiales, Undo, victoria, score, Swing ni audio.

## Colaboración y entrega

Seguir [CONTRIBUTING.md](CONTRIBUTING.md): rama de alcance acotado, commits, PR y revisión por otro integrante. No escribir directamente en `main`, usar force-push ni activar GitHub Actions en este alcance.

El repositorio permanece **privado**. La consigna exige uno público para la entrega: el equipo deberá decidir y ejecutar ese cambio más adelante. También faltan README de ejecución, gameplay en video, diagrama de clases StarUML e informe técnico; la entrega institucional deberá incluir a los integrantes en copia.

El bootstrap, la reconciliación documental y este spike fueron preparados y revisados con apoyo de IA (Codex). El equipo debe revisar las propuestas, declarar el uso de IA en el informe y comprender, validar, adaptar y defender individualmente el resultado. Se implementó únicamente el core experimental de carga y movimiento básico; no se volvió a ejecutar ningún spike histórico.
