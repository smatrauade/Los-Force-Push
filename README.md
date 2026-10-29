# Los Force Push — Sokoban

Trabajo integrador de **Procesos de Desarrollo de Software, UADE**, profesor Jonathan Leonel Pepe. Java, Swing y GUI 2D; diseño y defensa con SOLID, GRASP, MVC y GoF.

## Entrega y defensa

**Miércoles 18/11/2026: demo en vivo de 15–20 minutos y entrega formal.** Mostrar funcionalidades implementadas y explicar detalladamente los patrones aplicados. Un integrante envía a jpepe@uade.edu.ar, con copia al resto, nombre del equipo, apellido y nombre de cada desarrollador, todos los entregables y enlace al repositorio público. No es necesario mostrar código ni diagramas durante la exposición; **StarUML sigue siendo entregable**, junto con video, informe técnico y README ejecutable. [Autoridad y procedencia](docs/requirements/REQUIREMENTS.md#entrega-y-aceptación-docente--29102026); [prioridades](TODO.md).

## Estado integrado

Revisado el **29/10/2026**. [PR #9](https://github.com/smatrauade/Los-Force-Push/pull/9) integra la interfaz pixel art y [PR #8](https://github.com/smatrauade/Los-Force-Push/pull/8) integra TXT C2-L7. También están MERGED el [PR #10](https://github.com/smatrauade/Los-Force-Push/pull/10) de continuidad, #11 de definición Fragile experimental y [#12](https://github.com/smatrauade/Los-Force-Push/pull/12) del contrato candidato. Fragile continúa sin gameplay y el contrato no está ratificado. El merge de #8 es 565376452affa9b2146569e5da27dbfd1ee9d4f0; identifica ese evento, no un HEAD permanente. Consultar [CURRENT_STATE](docs/continuity/CURRENT_STATE.md) y Git/GitHub antes de continuar.

| Área | Implementado | Pendiente |
|---|---|---|
| Loading | TXT externo UTF-8 estricto, legacy de siete símbolos, C2-L7 opt-in y prototipo aislado de atributos Fragile; Board inmutable | Entidades especiales jugables y gramática final pendientes |
| Movement | Cuatro direcciones, una caja normal, piso/destino y bloqueos atómicos | Hielo, Fragile, KeyBox, Lock y muros dinámicos |
| Swing / Punto 5 | Menú, juego, HUD, pausa, opciones, ayuda, zoom, flechas, sprites y SFX | Conectar Undo, Restart, persistencia, victoria/progresión y score a servicios reales |
| Recursos | 31 PNG (29 base y dos variantes opcionales), 12 WAV versionados | Revisión de derechos/licencias del equipo |
| Entrega | Fuentes, suites y runners reproducibles | StarUML .mdj, informe, video y defensa; aceptación del código NOT_RECORDED |

Los botones pendientes permanecen deshabilitados hasta recibir callbacks operativos. La pantalla final recibe LevelResult; no detecta victoria ni calcula puntaje. Los recursos y sonidos de entidades futuras no implementan sus mecánicas.

## Empezar y compilar desde un clon

~~~bash
git clone https://github.com/smatrauade/Los-Force-Push.git
cd Los-Force-Push
git rev-parse HEAD
~~~

Se necesita un **JDK completo 17 o superior**, con java y javac compatibles. Los runners usan release 17, UTF-8, -Xlint:all y -Werror, sin Maven/Gradle ni librerías Java externas. La elección definitiva de Java/build sigue en OQ-11 OPEN.

Windows PowerShell 5.1 o 7:

~~~powershell
$env:SPIKE_JDK_HOME = '<directorio-del-JDK-completo>'
powershell -NoProfile -File ./scripts/run-swing.ps1
powershell -NoProfile -File ./scripts/run-swing.ps1 -LevelPath 'C:/niveles/mi nivel.txt'
powershell -NoProfile -File ./scripts/test-level-loading.ps1 -IncludeMovement
powershell -NoProfile -File ./scripts/run-swing.ps1 -Test
powershell -NoProfile -File ./scripts/run-swing.ps1 -UiTest
powershell -NoProfile -File ./scripts/test-level-loading-arguments.ps1
~~~

Se puede reemplazar powershell por pwsh. Los runners PowerShell seleccionan SPIKE_JDK_HOME, luego JAVA_HOME y luego PATH. Compilan fuera del checkout y limpian sus temporales; no cambian políticas ni configuración global.

Linux/macOS, Bash y utilidades estándar find, sort y mktemp:

~~~bash
export SPIKE_JDK_HOME='<directorio-del-JDK-completo>'
export PATH="$SPIKE_JDK_HOME/bin:$PATH"
bash scripts/run-swing.sh
bash scripts/run-swing.sh '/ruta/nivel.txt'
bash scripts/test-level-loading.sh --movement
bash scripts/run-swing.sh --test
bash scripts/run-swing.sh --ui-test
~~~

El runner Swing Bash usa java/javac de PATH; su argfile no entrecomilla las rutas de fuentes. Usarlo desde una ruta de checkout **sin espacios**. El runner core sí admite espacios/Unicode. La prueba UI necesita un escritorio; Xvfb es una alternativa Linux si ya está instalado. Una ventana automatizada no sustituye QA manual ni escucha de audio.

El menú abre el [demo TXT](src/desktop/resources/levels/demo.txt). Flechas mueven/empujan; ESC pausa, M controla audio y +/- cambia zoom. Los runners agregan [src/desktop/resources](src/desktop/resources) al classpath. No hace falta descargar ZIP, regenerar imágenes ni instalar Python/Pillow para jugar.

## TXT y pruebas

Legacy: # pared, espacio piso, . destino, @ jugador, $ caja, + jugador en destino y * caja en destino. C2-L7 requiere este encabezado y secciones, con un espacio ASCII entre tokens:

~~~text
!sokoban-txt 2
[grid]
# # # # #
# @ $ . #
# # # # #
[attributes]
~~~

C2 admite los siete aliases y sus formas largas terrain|occupant entre llaves. La sección attributes debe estar vacía: atributos y entidades especiales se rechazan explícitamente. Loader limita archivos a 65.536 bytes; grillas hasta 100×100, sin BOM. Es EXPERIMENTAL_NOT_RATIFIED: no cierra OQ-06/07/08/11.

Resultados esperados: Loading 40, Movement 31, C2 32, Desktop 16, VisualAssets 44 y UI Smoke 17. **C2 es una suite independiente**: el runner core la compila, pero no la ejecuta. [TEST_STRATEGY](docs/testing/TEST_STRATEGY.md) contiene su comando separado, la regresión del verificador y la procedencia de cada ejecución.

## Verificar el repositorio

La auditoría documental necesita Git, Bash y Python 3.9+ estándar; no son dependencias del juego:

~~~bash
bash scripts/verify-repo-readonly.sh
# Sólo para un checkout sin cambios:
bash scripts/verify-repo-readonly.sh --require-clean
python3 -B scripts/test-verify-repo-readonly.py
~~~

En Git Bash/Windows el verificador selecciona un python3 funcional o python. Para la regresión, usar python si ése es el nombre instalado, e indicar --bash con la ruta de Git Bash si PATH resuelve Bash a WSL. En PowerShell:

~~~powershell
python -B ./scripts/test-verify-repo-readonly.py --bash 'C:/Program Files/Git/bin/bash.exe'
~~~

El verificador no usa red, no escribe, no compila y no modifica Git. Valida texto UTF-8, secretos, enlaces, residuos (incluidos ignorados) y PNG/WAV autorizados contra el [manifiesto de 43 recursos](docs/punto5/ASSETS_REGISTRO_SHA256.csv). No valida licencias por comprobar hashes.

## Continuidad y colaboración

Leer [AGENTS](AGENTS.md) → [índice de rehidratación](docs/continuity/REHYDRATION_INDEX.md) → requisitos/preguntas → decisiones → arquitectura → pruebas/TODO. Los [contratos del Punto 5](docs/punto5/INTEGRACION_EQUIPO.md) describen los puntos de extensión reales; [ASSET_POLICY](docs/assets/ASSET_POLICY.md) conserva la deuda de licencias.

El próximo paso TXT/niveles es revisar el contrato experimental con el equipo y especificar su integración futura con el modelo jugable, sin cerrar sintaxis ni rangos por inferencia. No habilitar entidades especiales ni resolver E04/E08 por inferencia.

[DECISION_LOG](docs/decisions/DECISION_LOG.md) registra los acuerdos TEAM; [OPEN_QUESTIONS](docs/requirements/OPEN_QUESTIONS.md) conserva los detalles abiertos. Los votos no ratifican el código, patrones ni UML. Seguir [CONTRIBUTING](CONTRIBUTING.md), trabajar en ramas acotadas y declarar/revisar el apoyo de IA. Cápsulas, checkpoints y experimentos fechados son historia, no instrucciones actuales.

## Prototipo experimental de atributos individuales

Para evaluar sintaxis, validaciones y los detalles pendientes de E01/E02 sin atribuir aprobación del equipo, consultar la [propuesta de contrato TXT Fragile](docs/requirements/FRAGILE_TXT_CONTRACT_PROPOSAL.md).

ExperimentalFragileParser produce ExperimentalFragileLevel: un Board plantilla con celdas frágiles vacías y una lista inmutable de ID, posición y resistencia inicial. No es un Level jugable. Se conserva piso o destino y no se convierte Fragile en caja normal.

~~~text
!sokoban-txt 2
[grid]
# # # # #
# @ {floor|fragile:f1} . #
# # # # #
[attributes]
fragile f1 resistance=3
~~~

También se admite {goal|fragile:f1}. IDs experimentales: ASCII [a-z][a-z0-9_]{0,31}; cada referencia y declaración aparece exactamente una vez. Resistencia decimal positiva entre 1 y 2147483647; texto de hasta 65536 caracteres. Son límites técnicos del prototipo, no decisiones del equipo. No se presupone un máximo de 3. El orden de declaraciones no afecta el resultado. El parser existente valida grilla, tokens y dimensiones. LevelParser.parse y LevelLoader.load siguen rechazando Fragile. El agregado valida IDs y posiciones únicos, coordenadas dentro del tablero y celdas vacías de piso o destino; rechaza nulos y resistencias no positivas. Copia la colección y conserva el Board inmutable. OQ-06 y OQ-08 siguen abiertas en sus detalles. [Pruebas y ejecución](docs/testing/TEST_STRATEGY.md#prototipo-experimental-de-atributos-individuales).
