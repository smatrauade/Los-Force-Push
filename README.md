# Los Force Push — Sokoban

## UML PR #17 — PlantUML verificado (04/11/2026)

La arquitectura del candidato está documentada en [nueve diagramas editables PlantUML y PNG](docs/architecture/uml/pr17-rc-ffa27f9/README.md). Se verificó la generación PNG con el motor oficial PlantUML 1.2026.9. Esto facilita revisión y presentación, pero **no acredita automáticamente** el archivo StarUML solicitado por la consigna.

Sokoban 2D en **Java/Swing** para el TPO de Procesos de Desarrollo de Software (UADE). Esta rama integra los aportes de movimiento, cajas especiales, cerrojos, sesión y presentación del equipo. Consultar el [PR #17](https://github.com/smatrauade/Los-Force-Push/pull/17) para conocer el estado de integración; la aceptación del equipo sigue pendiente. [Guía para el equipo](docs/integration/PR17_TEAM_HANDOFF.md).

El candidato incluye TXT externos, movimiento cardinal, cajas normales/Fragile/KeyBox, ICE, Lock/muros dinámicos, Undo 15/5/3, Restart, victoria/progresión, HUD y score TEAM, tres temas visuales, animaciones ICE/Fragile y Replay independiente. [Auditoría y gates pendientes](docs/integration/PR17_FUNCTIONAL_RC_2026-11-04.md).

## Inicio rápido

Se necesita un **JDK completo 17 o superior** con `java` y `javac` compatibles y un escritorio gráfico. Los scripts compilan con `--release 17`, UTF-8, `-Xlint:all -Werror`. La selección definitiva de toolchain sigue [OQ-11](docs/requirements/OPEN_QUESTIONS.md). No se necesitan Maven, Gradle ni bibliotecas externas; imágenes, WAV y niveles están en el clon. El audio depende del dispositivo disponible. Observer publica cambios Lock confirmados para avisos y SFX; los muros siguen derivados por el modelo.

Para usar la versión consolidada después de integrar el PR, clonar la rama predeterminada:

```bash
git clone https://github.com/smatrauade/Los-Force-Push.git
cd Los-Force-Push
java -version
javac -version
```

Windows (PowerShell 5.1 o 7):

```powershell
powershell -NoProfile -File ./scripts/run-swing.ps1
# Alternativa PowerShell 7:
pwsh -NoProfile -File ./scripts/run-swing.ps1
```

El runner Windows selecciona `SPIKE_JDK_HOME`, luego `JAVA_HOME` y finalmente PATH. Para seleccionar un JDK en la sesión:

```powershell
$env:SPIKE_JDK_HOME = 'C:/ruta/al/JDK'
powershell -NoProfile -File ./scripts/run-swing.ps1
```

Linux/macOS (Bash):

```bash
bash scripts/run-swing.sh
```

El runner Bash usa PATH. Seleccionar allí la pareja `java`/`javac` del JDK. Requiere `find`, `sort` y `mktemp`; compila en un temporal externo con recursos en classpath. Usar un checkout sin espacios: el argfile Bash actual no entrecomilla las rutas fuente. Los comandos Windows/macOS son instrucciones de ejecución; no implican QA nativa actual en esas plataformas.

## Uso del juego

**NUEVA PARTIDA / DEMO** abre el mismo catálogo validado por las pruebas:

| Orden | TXT distribuido | Mecánica / testigo de solución |
|---|---|---|
| 1 | [demo.txt](src/desktop/resources/levels/demo.txt) | Caja normal; → → → |
| 2 | [campaign-b.txt](src/desktop/resources/levels/campaign-b.txt) | KeyBox, Lock y apertura/cierre de muros; → ← ↓ ↓ → → ↑ → ↑ ← ← |
| 3 | [ice_demo.txt](src/desktop/resources/levels/ice_demo.txt) | Hielo; ↓ → |
| 4 | [fragile_demo.txt](src/desktop/resources/levels/fragile_demo.txt) | Resistencia individual 4→1; → → → |
| 5 | [fragile-ice-demo.txt](src/desktop/resources/levels/fragile-ice-demo.txt) | Fragile completa el deslizamiento y rompe; → ↓ → → → → → |

Esperar el fin de la animación antes de la siguiente flecha. **SIGUIENTE NIVEL** aparece habilitado tras victoria si existe otro TXT. El último nivel conserva su resultado y no ofrece un Next inexistente. Continuar desde menú vuelve al resumen si el nivel ya terminó.

- Flechas: caminar/empujar una caja, sin tirar. El jugador no se desliza.
- `Z` / **DESHACER**: vuelve cinco movimientos exitosos, historial de 15, hasta tres usos por intento. Requiere cinco disponibles; moverse después no recupera usos.
- `R` / **REINICIAR**: estado inicial y las cuatro métricas a cero.
- `Esc`: pausa/volver; `M`: sonido; `+`/`-`: zoom. OPCIONES: `ORIGINAL COBRE`, `NOCTURNO`, `JADE`.
- HUD: nivel, movimientos, empujes, usos de Undo y score. Fórmula: `max(0, 1000 - moves - 2*pushes - 10*undoUsesTotal)`.

**VER REPLAY**, desde el resumen, ofrece Play/Pause/Step/Reset/Back. Usa otro tablero y cursor: no modifica la partida, score ni progresión. ESC/Back/navegación/cierre cancelan su timer. Registra sólo movimientos exitosos de la rama final tras Undo/Restart. Una traza inconsistente o un intento de más de 10.000 movimientos no ofrece Replay; la victoria sigue funcionando. Al avanzar se ofrece sólo la nueva traza completada. Los tres temas y zoom se comparten con la presentación; Replay avanza por pasos discretos sin SFX.

**GUARDAR PARTIDA / CARGAR PARTIDA** permanecen deshabilitados y no están implementados. **ABRIR TXT** inicia un nivel nuevo; no restaura una partida. La persistencia no se agrega como obligación supuesta de Undo ni cuenta automáticamente como adicional.

## TXT externos

Elegir **ABRIR NIVEL TXT** o iniciar con una ruta:

```bash
bash scripts/run-swing.sh '/ruta/al/nivel.txt'
```

```powershell
powershell -NoProfile -File ./scripts/run-swing.ps1 -LevelPath 'C:/niveles/mi nivel.txt'
```

Archivo UTF-8 estricto, sin BOM, máximo 65.536 bytes; dimensiones hasta 100×100 y exactamente un jugador. Una entrada inválida informa error y conserva el nivel previo, sus métricas y Replay. Reiniciar no relee el archivo; para una edición externa, guardar y volver a abrirlo.

Legacy usa `#` pared, espacio piso, `.` destino, `@` jugador, `$` caja, `+` jugador en destino y `*` caja en destino. C2 usa tokens separados por un espacio ASCII, cabecera y secciones:

```text
!sokoban-txt 2
[grid]
# # # # #
# @ $ . #
# # # # #
[attributes]
```

C2 ordinario admite aliases, formas largas e ICE (`{ice|empty}`, `{ice|player}`, `{ice|box}`). Fragile se carga por la definición dedicada, preservando ID y resistencia; Key/Lock/Wall por la extensión explícita. El controlador detecta la ruta mediante los parsers estrictos, sin depender del nombre del archivo. Los TXT distribuidos sirven de ejemplos ejecutables. [Contratos y límites efectivos](docs/integration/PR17_FUNCTIONAL_RC_2026-11-04.md).

## Prototipo experimental de atributos individuales

El nombre de la API se conserva: `LevelLoader.loadExperimentalFragile(Path)` devuelve plantilla espacial y lista inmutable de ID/posición/resistencia inicial. El runtime se construye con **ambas** partes mediante `RuntimeState.fromExperimentalFragile`; la plantilla sola no representa las cajas. `LevelLoader.load(Path)` rechaza Fragile para evitar pérdida de identidad. D-020 adopta el subconjunto piso/Goal de [este contrato](docs/requirements/FRAGILE_TXT_CONTRACT_PROPOSAL.md); ICE y Lock/muros tienen extensiones implementadas que no constituyen ratificación global de OQ-07/OQ-08.

## Capacidades y pruebas

Con JDK 17, desde la raíz:

```bash
bash scripts/test-replay-i3d1.sh
bash scripts/test-integration-i2.sh
bash scripts/verify-repo-readonly.sh --require-clean
git diff --check
```

Sólo desktop headless: `bash scripts/run-swing.sh --test` o `powershell -NoProfile -File ./scripts/run-swing.ps1 -Test`. Ventana real requerida: `bash scripts/run-swing.sh --ui-test` o PowerShell `-UiTest`. Audio nativo opcional en Bash: `bash scripts/run-swing.sh --audio-test` (comprueba reproducción por dispositivo, sin acreditar escucha humana). El verificador documental requiere Git, Bash y Python 3.9+; no es dependencia del juego.

[TEST_STRATEGY](docs/testing/TEST_STRATEGY.md) y [auditoría RC](docs/integration/PR17_FUNCTIONAL_RC_2026-11-04.md) identifican comandos, contenido probado, conteos y límites. Headless no acredita percepción visual ni escucha; QA automatizada Linux no certifica Windows, macOS ni aceptación humana.

## Documentación técnica

[AGENTS](AGENTS.md) → [REHYDRATION_INDEX](docs/continuity/REHYDRATION_INDEX.md) → [CURRENT_STATE](docs/continuity/CURRENT_STATE.md) orientan el ingreso al proyecto. Consultar [requisitos](docs/requirements/REQUIREMENTS.md), [preguntas abiertas](docs/requirements/OPEN_QUESTIONS.md), [decisiones E01–E11 y D-020](docs/decisions/DECISION_LOG.md), [arquitectura](docs/architecture/ARCHITECTURE.md), [responsabilidades](docs/architecture/RESPONSIBILITIES.md), [trazabilidad](docs/traceability/TRACEABILITY_MATRIX.md), [integración](docs/integration/README.md), [TODO](TODO.md) y [fuentes](docs/continuity/SOURCE_REGISTRY.md). [Índice documental](docs/README.md) y [CONTRIBUTING](CONTRIBUTING.md).

## Contexto académico

Entrega informada: **18/11/2026**, demo 15–20 minutos. El software es candidato para revisión; RF-12/OQ-10 requiere confirmar la elegibilidad de selector + Replay. Los [43 assets](docs/punto5/ASSETS_REGISTRO_SHA256.csv) tienen integridad registrada, pero [titularidad/licencias](docs/assets/ASSET_POLICY.md) siguen pendientes. [StarUML](docs/architecture/uml/STARUML_STATUS.md), informe, video, declaración de IA y revisión/defensa del equipo requieren sus propios gates. No se declara cumplida la entrega ni se autoriza Ready/merge.
