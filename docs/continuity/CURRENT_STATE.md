# Estado vivo — Los Force Push

## I3.C — animación ICE/Fragile desde RuntimeMoveResult (04/11/2026)

Se incorporó `BoxAnimation` como proyección inmutable de `RuntimeMoveResult`: conserva `BoxKind`, identidad y resistencia Fragile y el `boxPath` autoritativo. `GameView.animateBox` es un hook default de presentación; `GameController` publica primero el `RuntimeState` final, emite `ICE` una vez cuando `boxPath` supera una celda (evidencia E03 de deslizamiento) y `FRAGILE` una vez al romper, y recién después solicita la animación. `BoardPanel` calcula frames deterministas: orden exacto del path, supresión de la caja final mientras la trayectoria está en curso, última celda + `box_broken` + ausencia para Fragile, y un único `javax.swing.Timer` no bloqueante con generación que invalida callbacks obsoletos. `SwingApplication` difiere la pantalla FINISH solo mientras el tablero anima; las sondas headless siguen recibiendo `showVictory` y el score exacto al volver de `move`. Undo, Restart, carga, pausa/ESC, reemplazo de nivel y cierre cancelan la animación sin mutar el mundo.

`AnimationPresentationTest` aporta 14 escenarios headless: trayectoria ICE exacta sin caja fantasma, Fragile sobre ICE y de una celda, Fragile no roto con ID/resistencia, KeyBox con Lock/DynamicWall, ausencia de animación en bloqueo o movimiento sin empuje, propiedad única de SFX, orden publicación→animación→victoria, diferimiento de victoria, cancelación por Undo/Restart/recarga, cancelación explícita sin callbacks obsoletos, un solo timer en EDT, tema/zoom durante animación y hashes de PNG sin cambios. Regresión: Undo 58, core 258, Desktop 40, Campaña 41, Tema 7, Animación 14 y Assets 44 PASS; `bash scripts/test-integration-i2.sh` exit 0. INT-09 queda con evidencia headless local; `GUI_NATIVE_QA=NOT_RUN`, `TEAM_ACCEPTANCE=NOT_RECORDED`, `REMOTE_PUSH=NOT_RUN` e I4 pendiente. No se modificaron PNG ni se agregaron assets.

## I3.B — selector de estilo visual con Abstract Factory (04/11/2026)

Se incorporó un selector real de tres familias de presentación en OPCIONES: `ORIGINAL COBRE` (`original_cobre`, aspecto previo intacto), `NOCTURNO` (`nocturno`) y `JADE` (`jade`). `VisualThemeFactory` declara la Abstract Factory de vista con dos productos coordinados —`UiPalette` y `SpritePresentation`—; `VisualThemes` aloja el catálogo inmutable y las tres familias concretas; `VisualThemeController` es el único punto de cambio y `VisualThemeApplier` guarda/restaura colores y bordes Swing para que volver al tema original sea exacto. La familia original devuelve el PNG sin transformar; las alternativas aplican un tinte determinista 55/45 que conserva el canal alfa. `PixelAssets` invalida la caché al cambiar de familia y nunca escribe archivos; `BoardPanel` y `PixelTheme.Surface` leen la paleta activa. El cambio es sólo de presentación, se aplica en EDT y no muta `RuntimeState`, `GameSession`, `LevelProgression`, Undo, score, TXT ni referencias Lock/DynamicWall.

`VisualThemeTest` (7 escenarios headless) cubre catálogo estable y diferenciado, sprites alternativos deterministas con alfa intacto, identidad y reversibilidad del tema original, tableros ICE/Fragile/Lock con mundo, métricas y zoom conservados, selección Swing en EDT, entradas nulas sin estado parcial y hashes de PNG sin cambios. Regresión: Undo 58, core 258, Desktop 40, Campaña 41, Tema 7 y Assets 44 PASS, `bash scripts/test-integration-i2.sh` exit 0. `GUI_NATIVE_QA=NOT_RUN`; el selector es candidato a adicional RF-12/OQ-10 y no cierra esa elegibilidad ni la segunda funcionalidad. `TEAM_ACCEPTANCE=NOT_RECORDED`; sin publicación ni merge. [Detalle de aceptación](../integration/INTEGRATION_ACCEPTANCE.md).

## I3.A — aceptación headless de niveles distribuidos (04/11/2026)

Se incorporó `ShippedLevelsIntegrationTest` al runner `scripts/run-swing.sh --test`, y por transitividad al runner I2. Prueba mediante `GameController` real el circuito `demo.txt` → `campaign-b.txt` (victoria, score 993/983, reset y retención histórica), `fragile_demo.txt` (ID/resistencia, solución de tres empujes, score 991 y carga fallida atómica) e `ice_demo.txt` (deslizamiento a Goal, un empuje, score 996). Regresión Java 17 y verificador documental son gates obligatorios antes del commit. Las pruebas son headless: no acreditan animación, interacción visual nativa, derechos de assets ni aceptación TEAM. I3/I4 siguen abiertos y `main` no queda fusionado.

## Checkpoint vigente — I2 publicado en PR #17 Draft (04/11/2026)

I1 + I2 (ICE/Fragile, KeyBox/Lock/Wall, Undo 15/5/3, Restart, score y progresión) y el hotfix de carga `EXPERIMENTAL_FRAGILE` están en `integration/tpo-unificado-2026-11-03` (hito publicado `4d6cb612b38e5ebdb6869bb990301045044e863f`, no SHA permanente; verificar GitHub). Evidencia local reportada: Ubuntu OpenJDK 17.0.20.1, `scripts/test-integration-i2.sh` exit 0; Undo 58, core 258/258, Desktop 40/40, assets 44/44; verificador documental PASS. Sin CI registrada ni retest nativo manual posterior al hotfix. PR Draft, sin merge a `main` ni revisión TEAM (`TEAM_ACCEPTANCE=NOT_RECORDED`). Pendientes: QA GUI, I3/I4, StarUML, licencias, extras RF-12 y entrega. El reset al cambiar de nivel sigue provisional. Los estados «local», «sin publicar» y conteos antiguos de los apartados fechados posteriores son históricos.

## I2 — Fragile TXT GUI hotfix (04/11/2026)

- Detectar formato por parsers estrictos existentes: STANDARD, EXTENDED y EXPERIMENTAL_FRAGILE.
- El archivo `fragile_demo.txt` y el mismo contenido con nombre externo arbitrario conservan ID `f1` y resistencia 4 en runtime.
- La carga inválida preserva el estado previamente publicado; regresión de STANDARD, ICE y EXTENDED en `DesktopIntegrationTest`.
- Evidencia: `scripts/test-integration-i2.sh` con JDK 17 y `scripts/verify-repo-readonly.sh` (ejecutar sobre el commit resultante).
- QA gráfica nativa integral, aprobación TEAM, I3/I4 y merge a `main` siguen pendientes.
- La frescura/publicación del PR #17 se comprueba en GitHub; no deducirla del HEAD local.

## I2 — integración local verificada (04/11/2026)

Rama auxiliar `integration/i2-martina-stage-2026-11-03`; base `9aebe52501bb25d2ccf39781eb515c178dc11983`, fuente Martina `efd0fcd2a1423bd1d5b064458671ec9b149fd054`. Integración y corrección local completadas; estrictamente local sin publicación (`REMOTE_PUSH=NOT_RUN`). Correcciones I2 aplicadas: `LevelProgression.CompletedLevel` almacena `(victory, metrics, undoUsesTotal)` preservando la información para el cálculo del score tras `advance()` y `restart()`; `ScoreCalculator` calcula la fórmula saturada y `GameController` expone el resultado communicating score al HUD (`status`) y pantalla de victoria (`showVictory`); INT-05 (Undo que restaura DynamicWall y Lock abiertos), INT-06 (Restart desde cuatro métricas simultáneamente mayores a cero) e INT-08 (retención histórica de score 987 con Undo y 997 control) `PASS`. En OpenJDK 17.0.20.1 se ejecutaron 58 comprobaciones Undo (`GameSessionUndoTest`), core 258/258, desktop 36/36 y assets 44/44: `bash scripts/test-integration-i2.sh` exit 0. `NATIVE_GUI_QA=NOT_RUN`. La verificación documental pasó; `TEAM_ACCEPTANCE=NOT_RECORDED`; cambio de nivel reinicia métricas como comportamiento provisional. Próximo gate: revisión funcional del equipo y verificación remota antes de cualquier publicación.

AS_OF = 2026-11-01. Git/GitHub prevalece para el HEAD y los PR actuales. Este documento registra eventos y capacidades; no fija un HEAD permanente ni concede permisos de ejecución.

## Incremento victoria — llaves, cerrojos, muros y progresión — 01/11/2026

En la rama `victoria` se implementó un runtime explícito, separado del Board jugable histórico: `ExtendedLevelParser` / `ExtendedLevelDefinition`, `RuntimeState` / `RuntimeMovementEngine`, `VictoryEvaluator`, `LevelCatalog` y `LevelProgression`. `load(Path)` y `loadExperimentalFragile(Path)` no cambian y siguen rechazando la extensión. Hipótesis individuales, no ratificación TEAM de OQ-01/07 ni de una fórmula de score: asociación Lock↔Walls OR, rechazo de muro cerrado ocupado, KeyBox en Lock no es Goal, campaña `demo.txt` → `campaign-b.txt`. Undo, hielo y Fragile jugable permanecen ausentes. TEAM_ACCEPTANCE = NOT_RECORDED.

## Cierre semántico y contractual — 29/10/2026

[D-020](../decisions/DECISION_LOG.md#d-020--decisión-del-responsable-txt--29102026) adopta el subconjunto TXT probado por autoridad expresa del responsable: OQ-06 `RESOLVED_TXT_SCHEMA_BY_MODULE_OWNER`; OQ-08 `PARTIAL_RESOLVED_TXT_SUPPORTED_SUBSET / FUTURE_ENTITIES_OPEN`. E01/E02 se preservan, sin nuevo voto TEAM. [E04](../decisions/DECISION_LOG.md#e04) y E08 tienen regla TEAM elegida; el recuento E04 corregido procede del usuario, sin reinspección de capturas. En aquel corte, gameplay especial y Undo seguían sin implementarse; el estado actual de I1/I2 figura en los checkpoints de esta rama. La aceptación formal del código sigue `TEAM_ACCEPTANCE = NOT_RECORDED`.

Este hito sólo cambia Markdown. QA Java previa por Codex sobre `38e5a2e0ce4dd4688c4eb903dea93a2a3bf54bd6`: 160 PASS/exit 0, con src/scripts idénticos a 5ac2bba; no reejecutada en este cierre. [Comandos, entorno y límites](../testing/TEST_STRATEGY.md#cierre-semántico-documental--29102026). La [matriz de frescura](../requirements/TXT_LEVELS_EXECUTION_PLAN.md#cierre-semántico-del-contrato-soportado--29102026) identifica los documentos revisados y las excepciones conservadas. PR/reviews/HEAD se consultan dinámicamente; no hay permiso de Ready/merge ni SHA de squash predicho.

## Capacidad TXT experimental — desarrollo del 29/10/2026

`LevelLoader.loadExperimentalFragile(Path)` devuelve `ExperimentalFragileLevel` desde filesystem. Comparte un método privado de lectura acotada/UTF-8 estricto con la carga jugable; reutiliza el parser y el agregado existentes. Conserva ID, posición, resistencia y terreno sin convertir cajas frágiles en normales. `load(Path)` y `LevelParser.parse(String)` mantienen su contrato y rechazo de especiales.

El alcance autónomo G2/G3 está terminado: carga de definición inicial y contrato para consumidores. El subconjunto gramatical soportado se adopta por MODULE_OWNER_DECISION en D-020; el gameplay especial continúa ausente. El estado de PR, revisión humana e integración se obtiene de Git/GitHub. [Diseño y matriz de brechas](../requirements/TXT_LEVELS_EXECUTION_PLAN.md#g2--decisión-técnica-del-incremento-29102026). [QA y procedencia](../testing/TEST_STRATEGY.md#incremento-filesystem-fragile--29102026): Loading 40, C2 32, Fragile 41, Movement 31 y Desktop headless 16 PASS. Diez casos nuevos de filesystem; las otras suites conservan sus casos. GUI/manual/audio, Windows/macOS y gameplay Fragile no ensayados en este incremento.

El baseline G0–G1 sobre `8b71712cec673651b8dffe792bdc4ee7c779c1cf` fue **ejecutado localmente por el usuario**, reportado y contrastado con logs/script, no reejecutado por rutina. Los apartados fechados posteriores conservan la historia de cada misión; sus «R7 no ejecutada» describen aquellas reconciliaciones. Siguiente paso: contrastar el contrato soportado con firma/commit real de Model/Movement y definir sólo las extensiones necesarias, sin ampliar gameplay por inferencia. TEAM_ACCEPTANCE = NOT_RECORDED.

## Contrato para trabajo paralelo — 29/10/2026

La [matriz de traspaso TXT→Model/Movement](../requirements/FRAGILE_TXT_CONTRACT_PROPOSAL.md#traspaso-txt-a-modelmovement--29102026) describe datos/errores existentes, ejemplos y preguntas pendientes sobre identidad runtime, hielo, asociaciones y reconstrucción. La [coordinación aportada](SOURCE_REGISTRY.md#coordinación-txt-y-fragilehielo--29102026) informa actividad paralela y un nivel ofrecido; no acredita terminación ni cantidad definitiva de niveles de demo. El hito 529f38d fue documental: código/tests/fixtures/runners conservaron los bytes de 5ac2bba; no hay gameplay nuevo ni interfaz pública impuesta. TEAM_ACCEPTANCE = NOT_RECORDED. [Siguiente integración y responsables por dominio](../../TODO.md#integración-paralela-txt-con-fragilehielo--29102026).

## Integraciones verificadas

Esta tabla selecciona hitos con impacto en producto, decisiones o gobernanza. No pretende enumerar todos los commits de mantenimiento documental; para el historial exhaustivo y el HEAD vigente consultar Git/GitHub. Un nuevo merge solo documental no hace obsoleta por sí misma esta lista.

| PR | Alcance | Commit de merge histórico |
|---|---|---|
| [#1](https://github.com/smatrauade/Los-Force-Push/pull/1) | Bootstrap | ff6fbf170bd9782fb703e1ab4fa1e811ec0782ed |
| [#2](https://github.com/smatrauade/Los-Force-Push/pull/2) | Reconciliación | 1943053ec520ff3d8be0aecb21c2f417f3126380 |
| [#3](https://github.com/smatrauade/Los-Force-Push/pull/3) | Loading y Movement | e8f76cec23de019cd244b6d089bd6de3a24251a3 |
| [#4](https://github.com/smatrauade/Los-Force-Push/pull/4) | Aislamiento de runners | ee83f9ebd30ee1b553c555c89d9ea453131d6678 |
| [#5](https://github.com/smatrauade/Los-Force-Push/pull/5) | Swing V1 | 54c03d4520dfc542c125686e5446687dc07d85db |
| [#6](https://github.com/smatrauade/Los-Force-Push/pull/6) | Rehidratación | 2c2c2e4c6180ca0639e511c480ca5b93cd4e4a01 |
| [#7](https://github.com/smatrauade/Los-Force-Push/pull/7) | Decisiones TEAM | 7d3a30f04285d526709bbbf91e8725c61a187aac |
| [#9](https://github.com/smatrauade/Los-Force-Push/pull/9) | Swing pixel art / Punto 5 | fafae98642cefd38e466c689b3c8a16825773738 |
| [#8](https://github.com/smatrauade/Los-Force-Push/pull/8) | TXT C2-L7 | 565376452affa9b2146569e5da27dbfd1ee9d4f0 |
| [#10](https://github.com/smatrauade/Los-Force-Push/pull/10) | Continuidad y verificador | c6eb755388da142b6c8424d2d46aec2dc03ce622 |
| [#11](https://github.com/smatrauade/Los-Force-Push/pull/11) | Definición experimental Fragile e invariantes | 986ab548e7955752d3542035d6f9d573534b257d |
| [#12](https://github.com/smatrauade/Los-Force-Push/pull/12) | Propuesta documental TXT Fragile, no ratificada | 90ffc4ed91d7ce48d7cecddb37d2f5494b820fc6 |
| [#13](https://github.com/smatrauade/Los-Force-Push/pull/13) | Reconciliación académica, TEAM indirecto y política de continuidad | 65f8ba99de05a00acef401ce4408219b0160d9f5 |

Los PR #8, #9, #10, #11, #12 y #13 están MERGED; los SHA de la tabla identifican eventos históricos, no un HEAD permanente. El merge de #8 tiene padres fafae98642cefd38e466c689b3c8a16825773738 y 289fef6eda97a2bbd5593c74f06382ae6caf433c; conserva sus dos commits y el árbol 1796932307df319e3636daee942cf49920b4eb05. Las ramas remotas de ambos incrementos se retiraron tras verificar integración y consumidores. Sus commits permanecen en main. Al corte posterior al merge de #8 no había otros PR abiertos; consultar GitHub para cambios posteriores y para el estado del mantenimiento documental.

## Software que existe

- [LevelLoader / LevelParser / C2LevelParser](../../src/main/java/ar/edu/uade/sokoban/level): lectura externa UTF-8 estricta, máximo 65.536 bytes, legacy y C2 opt-in; Board inmutable, grillas hasta 100×100, exactamente un jugador.
- [MovementEngine](../../src/main/java/ar/edu/uade/sokoban/movement/MovementEngine.java): movimiento cardinal y empuje de una caja normal. MOVED/PUSHED producen Board nuevo; BLOCKED conserva la misma instancia.
- [RuntimeState](../../src/main/java/ar/edu/uade/sokoban/movement/RuntimeState.java) / [RuntimeMovementEngine](../../src/main/java/ar/edu/uade/sokoban/movement/RuntimeMovementEngine.java): ICE, Fragile, KeyBox, Lock y muros dinámicos.
- [GameSession](../../src/main/java/ar/edu/uade/sokoban/session/GameSession.java) concentra Undo/Restart y métricas; `LevelProgression` coordina victoria y campaña.
- [Desktop](../../src/desktop/java/ar/edu/uade/sokoban/desktop) conecta presentación, Undo/Restart, resultado y siguiente nivel; `ScoreCalculator` calcula score saturado.
- [Recursos](../../src/desktop/resources/assets): 31 PNG, incluidos 29 base y dos variantes opcionales; 12 WAV. [Manifiesto](../punto5/ASSETS_REGISTRO_SHA256.csv), [contrato entre módulos](../punto5/INTEGRACION_EQUIPO.md) y [derechos pendientes](../punto5/USO_IA_Y_LICENCIAS.md).

## Spike local C2-L7 — 2026-10-24

Ese título conserva el ancla histórica; el incremento ya está integrado por PR #8. C2LevelParser es un helper interno sin estado compartido. Header exacto !sokoban-txt 2, [grid] y [attributes] obligatoria vacía; siete aliases y siete formas largas con igual Board. No hay fallback legacy para un encabezado reservado inválido. Atributos y entidades especiales se rechazan antes de publicar Level.

En el corte histórico del spike C2 la gramática seguía EXPERIMENTAL_NOT_RATIFIED; su merge no la ratificó. La adopción posterior por el responsable TXT se registra en D-020 y sólo cubre el subconjunto soportado; OQ-07/11 y extensiones OQ-08 siguen abiertas. No contiene resistencia individual ni asociaciones Lock↔Walls. [Comandos y evidencia](../testing/TEST_STRATEGY.md#spike-experimental-c2-l7--2026-10-24).

## Evidencia y alcance

La auditoría SOKOBAN_POST_PR9_INTEGRATION_R1 del 28/10 ejecutó sobre el árbol 1796932307df319e3636daee942cf49920b4eb05: Loading 40, Movement 31, C2 32, Desktop 16, VisualAssets 44, UI Smoke nativo automatizado 17 y CLI PS5.1/7 18, todos PASS/exit 0. Windows, Temurin 21.0.12.1, release 17, UTF-8 y compilación estricta. También verificó 43 hashes y exportación limpia; Desktop/Assets pasaron con TXT C2 equivalente al demo. Son resultados de esa misión anterior, no una nueva ejecución funcional en este mantenimiento.

[TEST_STRATEGY](../testing/TEST_STRATEGY.md) distingue esa evidencia de las pruebas actuales del verificador y de los resultados históricos. No se afirma QA manual nueva, escucha de altavoces, GUI Linux ni macOS. Los logs/capturas originales no se incorporan a Git.

## Checkpoint mayoritario del equipo — 2026-10-23

Autoridad canónica: [DECISION_LOG](../decisions/DECISION_LOG.md#ratificación-por-mayoría--2026-10-23). Deuda residual: [OPEN_QUESTIONS](../requirements/OPEN_QUESTIONS.md). GAME_RULE_DECISIONS = MAJORITY_ACCEPTED_PARTIAL; TEAM_ACCEPTANCE = NOT_RECORDED para el código. E04/E08 tienen regla TEAM decidida; la revisión del 29/10 corrige su falso estado de elección abierta sin implementar otros módulos. Patrones y UML no se ratifican mediante merges o PASS.

## Estado posterior al merge de PR #5

El merge de #5 y su QA previa son historia: [checkpoint Windows](checkpoints/WINDOWS_PR5_NATIVE_QA_2026-10-21.md) y [JSON](checkpoints/WINDOWS_PR5_NATIVE_QA_2026-10-21.json). Probó el código 0670a4d5137f0e9f1db221866a02c29987024847; el commit documental posterior a722d4e5229e4151e9ca73971e1987e05bc1dac7 no es otro test. Sus estados Draft/premerge no describen el PR actual.

## Estado comprobado y cómo continuar

[README](../../README.md) contiene ejecución desde clon; [TEST_STRATEGY](../testing/TEST_STRATEGY.md), suites y regresión del verificador. No se requieren ZIP de instalación ni assets externos. Java/build definitivo sigue OQ-11 OPEN; los runners provisionales compilan a release 17. [Arquitectura](../architecture/ARCHITECTURE.md) y [UML](../architecture/uml/STARUML_STATUS.md) separan implementación de candidato.

Próximo trabajo TXT/niveles: verificar el contrato soportado frente al consumidor real y especificar extensiones necesarias, respetando E01/E02 y compatibilidad legacy/C2. Requiere especificación y autorización propias; no habilitar especiales por inferencia. En el corte de esta sección quedaban pendientes hielo, Fragile/KeyBox/Lock y victoria/progresión; el estado I1/I2 actual se registra arriba. Persistencia y segunda adicional siguen pendientes. [TODO](../../TODO.md) reúne tareas funcionales y entregables.

## Historia y fuentes

Los informes [Loading](../experiments/LEVEL_LOADING_R1.md), [Movement](../experiments/MOVEMENT_R1.md) y [Swing V1](../experiments/SWING_VERTICAL_SLICE_V1.md) conservan evidencias fechadas. [HISTORICAL_EVIDENCE](../testing/HISTORICAL_EVIDENCE.md) describe A/B sin fuentes recuperadas, no mecánicas disponibles hoy. Cápsulas y checkpoints permanecen inmutables. [SOURCE_REGISTRY](SOURCE_REGISTRY.md) distingue bytes presentes, fuentes externas y transcripciones TEAM; no atribuir lectura directa de originales académicos ausentes.

## Prototipo experimental de atributos individuales

Integrado mediante PR #11; conserva sólo la definición inicial, no gameplay. [Contrato TXT para Fragile](../requirements/FRAGILE_TXT_CONTRACT_PROPOSAL.md): subconjunto soportado MODULE_OWNER_DECIDED; propuestas de integración/extensión diferenciadas.

ExperimentalFragileParser y ExperimentalFragileLevel conservan sus nombres experimentales y una salida no jugable; el contrato TXT soportado está adoptado por D-020; consultar Git/PR para el estado de integración. La plantilla espacial deja vacías las celdas frágiles y la lista inmutable conserva ID, posición y resistencia, sin crear un Level jugable con una caja normal sustituta. El agregado valida IDs y posiciones únicos, límites del tablero, piso/destino vacío, no nulos y resistencia positiva; preserva Board y lista inmutables. Los métodos jugables `LevelLoader.load` / `LevelParser.parse` siguen rechazando el mismo TXT; la entrada explícita experimental se describe al comienzo. [Contrato y límites](../../README.md#prototipo-experimental-de-atributos-individuales); [pruebas](../testing/TEST_STRATEGY.md#prototipo-experimental-de-atributos-individuales). OQ-06 del lector queda resuelta y OQ-08 parcialmente resuelta; sólo extensiones no soportadas siguen abiertas.

## Preparación de entrega — 29/10/2026

Entrega y demo: **miércoles 18/11/2026**, 15–20 minutos; [protocolo y criterios](../requirements/REQUIREMENTS.md#entrega-y-aceptación-docente--29102026). Teams fue aportado por el usuario; el resumen ASR no se verificó acústicamente. [Procedencia](SOURCE_REGISTRY.md#fuentes-académicas-aportadas--29102026).

En el corte de la reconciliación del PR #13, R6 estaba completa mediante PR #12 y R7 **preparada, no ejecutada**: requiere alcance y autorización propios; el prototipo inicial ya existe y no debe duplicarse. La propuesta integrada no cierra OQ-06/OQ-08. [TODO](../../TODO.md) prioriza dependencias y responsables por módulo. Observer sigue sin implementación, con indicación oral docente de incorporarlo en alguna parte del TPO según ASR; Lock→Walls es el candidato, sujeto a OQ-07. No hay Memento/Command ni Strategy de score implementados. TEAM_ACCEPTANCE = NOT_RECORDED.

Esta reconciliación revisa documentación y fuentes contra el código integrado; las suites funcionales citadas son históricas. Java/gameplay/UI: NOT_RUN en esta misión. AGENTS y FRESHNESS_POLICY quedaron sin cambios en la primera reconciliación, pero la política de revisión documental se actualizó después dentro del PR #13; sus gates actuales siguen vigentes; Punto 5 conserva su frontera de presentación y deuda de derechos. No se ejecuta investigación de solver/generador.

## Complemento grupal del PR #13 — 29/10/2026

El [resumen privado aportado](SOURCE_REGISTRY.md#contexto-grupal-privado--complemento-29102026) cuestiona la elegibilidad académica de guardar/recuperar mediante un reporte docente de segunda mano. E11 conserva su voto histórico; [OQ-10](../requirements/OPEN_QUESTIONS.md#elegibilidad-de-adicionales--complemento-29102026) exige confirmar dos adicionales válidas. El selector de estilos es candidato con elegibilidad reportada, sin mayoría ni implementación verificadas. No se declara satisfecho RF-12 ni persistencia obligatoria por Undo/Memento. Las propuestas de áreas/Issues y patrones no son asignaciones ni diseños ratificados. Ese corte conservaba entrega 18/11 y R7 no ejecutada; precede al incremento TXT descrito al comienzo. Las demás fuentes de navegación, política y arquitectura se revisaron sin necesidad de cambio: no se incorpora software ni nueva decisión de patrón.

## I0 — Estado de la rama compartida de integración (corte 03/11/2026)

<!-- I0_BRANCH_AWARE_CONTEXT_2026_11_03 -->

En la rama `integration/tpo-unificado-2026-11-03`, el **punto de partida funcional** es el árbol de `victoria@a7df64172dbb857e7a10709ec9ea85a5f80aacf7` (KeyBox/Lock/Wall y progresión); se agrega contrato documental para integraciones futuras. Al redactar este corte, `Objetivo2`, `feature/undo-restart-score` y `impaciencia` **no** estaban integradas y el PR no existía. Este párrafo es una **fotografía de I0**, no un estado permanentemente verdadero. Para el estado actual y cada hito posterior consultar [README de integración](../integration/README.md), [plan I0–I4](../integration/TEAM_INTEGRATION_PLAN.md), el HEAD real del checkout y el PR remoto. `main` no queda modificado por documentar esta rama.

## I0.1 — Publicación verificada del PR #17 (03/11/2026)

<!-- I0_PR17_PUBLICATION_VERIFIED_2026_11_03 -->

La rama `integration/tpo-unificado-2026-11-03` publicó el PR [#17](https://github.com/smatrauade/Los-Force-Push/pull/17) en estado **Draft**, con origen `victoria@a7df64172dbb857e7a10709ec9ea85a5f80aacf7` y commit documental I0 `50a559a55e1d57afaad68e9f952e552228f45779`. `main=f4a28fd6c1d869fcd6f6189e5cbc2da49bb2f7a2` permanecía intacta al verificar este corte. La implementación de hielo/Fragile de `Objetivo2`, Undo/Restart/score de Martina y la rama individual `impaciencia` **NO** están integradas aún. El PR no aprueba un merge. Las pruebas combinadas permanecen `NOT_RUN`. Esta anotación certifica la publicación I0; los SHA son históricos y deben revalidarse en GitHub. Siguiente etapa: I1 TXT/Runtime/Movement; [plan](../integration/TEAM_INTEGRATION_PLAN.md) y [matriz](../integration/INTEGRATION_ACCEPTANCE.md).

## Checkpoint histórico — worktree auxiliar I1 antes de publicación (03/11/2026)

`integration/i1-santino-stage-2026-11-03` mantiene HEAD base `5af4a586001ecd8da1856483da023ce15eeccfc1` y MERGE_HEAD de `Objetivo2` `ac728a2241d2fae8c4c32b78e29fd4c9bc6228eb`; los tres conflictos Swing están **resueltos en texto** pero no marcados en el índice Git. I1.A/B0/B1 está implementado provisionalmente en el árbol local con **312/312 pruebas JDK17 reportadas antes de I1.C**. I1.C agregó testigos más estrictos, documentos y runner; la ejecución posterior reportada por el usuario aprobó **316/316** pruebas (core 256, Desktop 16, Visual Assets 44), Java/javac 17.0.20.1, `GIT_STATE_PRESERVED=YES`, `bash scripts/verify-repo-readonly.sh` PASS y auditoría read-only de los 38 paths esperados PASS. Son resultados del árbol local aún **sin merge commit**; GUI real y aceptación TEAM continúan pendientes. Ver [evidencia de integración](../integration/I1_LOCAL_VALIDATION_2026-11-03.md). `IN_MAIN=NO`, `IN_PR17_REMOTE=NO`, `TEAM_ACCEPTANCE=NOT_RECORDED`; no reinterpretar las secciones históricas anteriores como estado del worktree actual.

<!-- I1_PR17_PUBLICATION_2026_11_03 -->

## I1 publicado en rama de integración — estado actual 03/11/2026

El PR [#17](https://github.com/smatrauade/Los-Force-Push/pull/17) permanece abierto y Draft, con `integration/tpo-unificado-2026-11-03@6abb8ad003ad9af6642fc1a1e11a91f5d238f2b2`, basado en `main@f4a28fd6c1d869fcd6f6189e5cbc2da49bb2f7a2`, sin modificar `main`. Merge padres `5af4a586` y `ac728a22`, con los dos commits históricos de `Objetivo2` preservados. I1 incorpora TXT/runtime ICE/Fragile, KeyBox/Lock/Wall y reconciliación Swing, no Undo/Score ni animación completa. Prueba local **316/316 PASS** previa al commit sobre los mismos blobs; no reejecutada en el nuevo SHA. [Registro de I1](../integration/I1_LOCAL_VALIDATION_2026-11-03.md). I2–I4 pendientes; `TEAM_ACCEPTANCE=NOT_RECORDED`. Los cortes I0/I1 anteriores son históricos.
