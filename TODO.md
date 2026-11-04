# TODO — backlog vivo

## I3.D — Replay/Command (implementación individual adaptada)

- I3.D.1: núcleo Replay/Command puro en `src/main/java/ar/edu/uade/sokoban/replay` y suite propia; confirmar gate JDK17 + regresión total y estado Git antes de publicación.
- I3.D.2: conectar únicamente movimientos exitosos en `GameController`; reconciliar Undo de cinco, Restart, carga válida y cambio de nivel. No duplicar `GameSession` ni modificar score.
- I3.D.3: pantalla Replay independiente en Swing (Play/Pause/Step/Reset, temporizador EDT, temas/zoom y navegación) compatible con I3.C `VictoryGate`.
- I3.D.4/I3.D.5: pruebas de integración/UI y continuidad, auditoría/promo al mismo PR Draft bajo gate independiente. `impaciencia` es implementación individual de origen, no decisión/aceptación TEAM.
- RF-12/OQ-10: Replay/Command permanece **candidato**; solicitar confirmación académica y del equipo antes de contarlo como segunda funcionalidad. No sustituye los entregables ni la QA humana.

## I3.C — animación ICE/Fragile (04/11/2026)

Se incorporó `BoxAnimation` como proyección inmutable de `RuntimeMoveResult`, el hook default `GameView.animateBox` y frames deterministas en `BoardPanel`: orden exacto del `boxPath`, supresión de la caja final durante la trayectoria y, para Fragile, última celda + `box_broken` + ausencia. `GameController` publica el runtime final, emite `ICE`/`FRAGILE` una vez y luego anima; `SwingApplication` difiere FINISH solo mientras el tablero anima. Undo, Restart, recarga, ESC/cierre y reemplazo de nivel cancelan sin callbacks obsoletos. La animación no muta `RuntimeState` ni recalcula física y conserva `BoxKind`, ID/resistencia Fragile y metadatos Lock/DynamicWall.

`AnimationPresentationTest` aporta 14 escenarios headless y se ejecuta desde `scripts/run-swing.sh --test` y `scripts/test-integration-i2.sh`. Regresión: Undo 58, core 258, Desktop 40, Campaña 41, Tema 7, Animación 14 y Assets 44 PASS, runner exit 0. No se agregaron PNG ni se alteraron hashes. INT-09 queda con evidencia headless; `GUI_NATIVE_QA=NOT_RUN`, `TEAM_ACCEPTANCE=NOT_RECORDED`, `REMOTE_PUSH=NOT_RUN` e I4 pendiente. Siguiente gate: QA GUI nativa y revisión funcional del equipo. [Evidencia y límites](docs/testing/TEST_STRATEGY.md).

## I3.B — selector de estilo visual con Abstract Factory (04/11/2026)

Se incorporó un selector de tres familias visuales en OPCIONES —`ORIGINAL COBRE`, `NOCTURNO` y `JADE`— sobre `VisualThemeFactory` (Abstract Factory de vista: `UiPalette` + `SpritePresentation`), catálogo inmutable en `VisualThemes`, coordinación única en `VisualThemeController` y restauración exacta de colores/bordes en `VisualThemeApplier`. Los PNG no se modifican: la familia original los devuelve intactos y las alternativas generan en memoria un tinte determinista 55/45 con alfa preservado; `PixelAssets` invalida la caché por familia. El cambio no altera `RuntimeState`, `GameSession`, `LevelProgression`, Undo, score, TXT ni referencias Lock/DynamicWall. `VisualThemeTest` aporta 7 escenarios headless; la regresión completa queda en Undo 58, core 258, Desktop 40, Campaña 41, Tema 7 y Assets 44 PASS con `bash scripts/test-integration-i2.sh` exit 0.

El selector es un candidato a adicional RF-12/OQ-10: su elegibilidad, la segunda funcionalidad y la aceptación TEAM siguen `OPEN`/`NOT_RECORDED`. `GUI_NATIVE_QA=NOT_RUN`, sin publicación ni merge. Siguiente gate: revisión funcional del equipo y QA GUI nativa. [Evidencia y límites](docs/testing/TEST_STRATEGY.md).

## I3.A — aceptación headless de niveles distribuidos (04/11/2026)

Se incorporó `ShippedLevelsIntegrationTest` al runner `scripts/run-swing.sh --test`, y por transitividad al runner I2. Prueba mediante `GameController` real el circuito `demo.txt` → `campaign-b.txt` (victoria, score 993/983, reset y retención histórica), `fragile_demo.txt` (ID/resistencia, solución de tres empujes, score 991 y carga fallida atómica) e `ice_demo.txt` (deslizamiento a Goal, un empuje, score 996). Regresión Java 17 y verificador documental son gates obligatorios antes del commit. Las pruebas son headless: no acreditan animación, interacción visual nativa, derechos de assets ni aceptación TEAM. I3/I4 siguen abiertos y `main` no queda fusionado.

Revisado 04/11/2026 para el estado de I2 publicado en PR #17; conservar cortes históricos. [Estado integrado y registro de merges](docs/continuity/CURRENT_STATE.md). TEAM_ACCEPTANCE = NOT_RECORDED. Las prioridades son planificación propuesta por módulo, no nuevas decisiones de reglas ni asignaciones personales.

## Checkpoint I2 en PR #17 (04/11/2026)

I1+I2 y el hotfix Fragile TXT están publicados en el Draft PR #17, hito `4d6cb612b38e5ebdb6869bb990301045044e863f`. Pruebas locales JDK17 reportadas: Undo 58, core 258, Desktop 40 y assets 44, todas PASS. Pendientes: retest GUI nativo posterior al hotfix, revisión TEAM, I3/I4 y entrega. El runner I1 valida Desktop 40/40 pero conserva la salida resumen obsoleta `DESKTOP_TOTAL=36/36`; ajustar en una ejecución validada posterior. No equivale a merge en `main`.

## Base disponible

| Estado | Alcance | Evidencia y límite |
|---|---|---|
| IMPLEMENTED_VERIFIED / PARTIAL | UTF-8 externo, legacy/C2-L7, Board inmutable y LevelBuilder concreto | Código integrado; Loading 40/C2 32 históricos; especiales jugables rechazados |
| IMPLEMENTED_PARTIAL | Movement cardinal, caja normal, KeyBox/Lock/muros y victoria | Movement 31 histórico más Extended/Dynamic/Completion de esta rama; hielo/Fragile runtime incorporados en I1; Undo pendiente I2 |
| IMPLEMENTED_VERIFIED / PARTIAL | Swing pixel art, menú/HUD/pausa/opciones/zoom, sprites/SFX | Desktop 16, Assets 44, UI 17 y CLI 18 históricos; callbacks pendientes no son servicios |
| IMPLEMENTED / MODULE_OWNER_DECIDED_SUPPORTED_SUBSET | Definición Fragile inmutable desde String/Path; contrato soportado D-020 | Fragile 41 en parser; runtime I1 publicado; OQ-06 resuelta, OQ-08 extensiones pendientes |
| PARTIAL | 31 PNG (29 base + dos opcionales) y 12 WAV versionados | Manifiesto disponible; revisión de derechos pendiente |
| IN_PR17_DRAFT / TEAM_REVIEW_PENDING | Undo y Restart en GameSession y LevelProgression | Historial limitado a 15 snapshots; cada uso de Undo retrocede 5 movimientos exitosos, se permiten hasta 3 usos consecutivos, restaura el tablero, `moves` y `pushes`, y acumula `undoUsesTotal`; `Restart` restaura el estado inicial y reinicia las cuatro métricas del intento; la carga de un nivel nuevo también reinicia el contador de usos consecutivos; 58 casos ejecutados en GameSessionUndoTest |
| IN_PR17_DRAFT / TEAM_REVIEW_PENDING | Cálculo de score por intento y retención histórica | Puntaje inicial 1000; −1 por movimiento, −2 adicionales por cada push, −10 por cada uso de Undo; puntaje mínimo 0; fórmula `max(0, 1000 - moves - 2 * pushes - 10 * undoUsesTotal)`; `ScoreCalculator` realiza el cálculo, `LevelProgression.CompletedLevel` retiene `undoUsesTotal`, y `LevelResult` expone las estadísticas y el puntaje; `Restart` reinicia los contadores sin aplicar penalizaciones; score conservado tras `advance()` y `restart()` |

IMPLEMENTED_VERIFIED describe presencia contrastada en código; no una nueva ejecución funcional. [Comandos, fechas y límites de QA](docs/testing/TEST_STRATEGY.md).

## Ruta crítica hacia el 18/11

Entrega docente: **miércoles 18/11/2026**, demo en vivo **15–20 minutos** y correo formal. [Protocolo y entregables](docs/requirements/REQUIREMENTS.md#entrega-y-aceptación-docente--29102026). P0 bloquea cierre funcional o entrega; P1 integra y comprueba; P2 mejora fuera de ruta crítica. Las fechas internas siguientes son **objetivos propuestos, no acuerdos ni plazos docentes adicionales**; revisar viabilidad con el equipo ante cada incremento.

| Prioridad / objetivo interno | Estado y trabajo | Responsable por módulo | Dependencia | Criterio de aceptación / siguiente prueba |
|---|---|---|---|---|
| P0 / 02/11 | OPEN_DEPENDENCY: extensiones TXT e integración; Java/build global | TXT + consumidores/integración | OQ-08 extensiones; OQ-07/11 | Subconjunto soportado ya decidido D-020; obtener contrato consumidor, preservar E01/E02 y legacy/C2 |
| P0 / 02/11 | I1_I2_PUBLISHED_DRAFT: Fragile/hielo y Undo/Restart/Score integrados; QA GUI posterior al hotfix pendiente | Model/Movement y sesión | E04/E08 resueltas; OQ-03/04 bordes operativos | Implementar y probar las reglas elegidas con casos antes/después; no reabrir encuestas ni rediseñar Undo desde TXT |
| P0 / 06/11 | IMPLEMENTED_PARTIAL: KeyBox/Lock/muros, victoria y campaña; hielo/Fragile runtime incorporados en I1; animación I3.C headless; QA GUI pendiente | Model/Movement + sesión | OQ-01/07 hipótesis individuales; OQ-02/08 abiertas | Preservar identidad/resistencia; casos de cierre ocupado y E05 ejecutados en esta rama |
| P0 / 06/11 | PARTIAL: Lock abre muros asociados por consulta derivada, no Observer GoF | Model/arquitectura | Indicación ASR docente, D-010 y OQ-07 | Roles y notificaciones a asociados, aislamiento entre grupos y restauración Undo pendientes |
| P0 / 09/11 | IN_PR17_DRAFT: Restart de nivel, Undo 15/5/3 y métricas oficiales | Sesión/score | OQ-04/05/09; estado especial | Historial 15, restaurar bloques 5, límites 3 y bordes 4/5 verificados localmente en GameSessionUndoTest (58 casos) |
| P0 / 09/11 | IN_PR17_DRAFT: victoria/progresión integradas con score oficial y retención | Sesión/niveles/score | OQ-01 hipótesis; OQ-09 ratificada | Goal/KeyBox y progresión probados; CompletedLevel retiene undoUsesTotal y LevelResult comunica fórmula TEAM; 987 y 997 probados |
| P0 / 10/11 | TEAM_DECISION_REQUIRED: confirmar dos adicionales elegibles y su selección | Equipo de producto/arquitectura e interfaz | OQ-10; reporte docente indirecto y E11 histórica | No computar guardar automáticamente; selector candidato con elegibilidad reportada, sin mayoría ni implementación verificadas. Confirmar dos extras y demostrar patrón real, sin imponer Strategy/Abstract Factory |
| P1 / 12/11 | PARTIAL: conectar servicios a UiCommands/renderer | Swing e integración | Servicios operativos y DTO acordado | Disponibilidad real de botones; render de especiales, sin reglas ni resistencia inferida desde sprites |
| P1 / 13/11 | NOT_IMPLEMENTED: aceptación integral de niveles propios | TXT/QA/Movement | Contrato y mecánicas integrados | TXT externo editado→Board→UI; error conserva estado; cada puzzle tiene secuencia solución y resistencia coherente |
| P0 / 13/11 | DELIVERY_REQUIRED: revisión de derechos de assets | Recursos/equipo | Titularidad y política de dominio público | Registro individual verificable; no inferir CC0 de hash u origen IA |
| P0 / 15/11 | DELIVERY_REQUIRED: StarUML, informe técnico, README reproducible y video | Arquitectura/documentación/integración | Código entregado y patrones defendibles | .mdj abre y corresponde al código; informe justifica alternativas/SOLID/GRASP/MVC/GoF y declara IA; video/README reproducibles |
| P1 / 16/11 | DELIVERY_REQUIRED: QA manual y ensayo de defensa | QA y todos los módulos | Build final, fixtures resolubles | Demo 15–20 min con funcionalidades efectivas y explicación de patrones; escucha de audio real; cada integrante comprende su trabajo |
| P0 / 18/11 | DELIVERY_REQUIRED: presentación y correo | Coordinación de entrega | Todos los entregables revisados | Un integrante envía a jpepe@uade.edu.ar con copia al resto, nombre del equipo, apellido/nombre de todos y enlace público |
| P2 / sin compromiso preentrega | NOT_IMPLEMENTED: mejoras de E/S cancelable y QA macOS pendiente | Integración/Swing/QA | Necesidad y entorno disponibles | Autorización y prueba específica; no extrapolar del smoke Windows |
| P2 / posterior a entrega | NOT_IMPLEMENTED: solver/generación opcional | TXT/niveles | Fuera de consigna y ruta crítica | Evaluar sólo con alcance propio; no dependencia para validar puzzles manualmente |

## Próximo dominio TXT/niveles

[Plan de ejecución TXT/niveles, una rama y un PR final](docs/requirements/TXT_LEVELS_EXECUTION_PLAN.md). Gates graduales y documentación alineada con el estado que se aprobaría; el plan conserva su origen documental y registra el alcance autónomo implementado; Git/GitHub determina el estado del PR y su integración.

R6 está completa por PR #12. **Alcance autónomo TXT terminado:** carga externa de definición experimental `LevelLoader.loadExperimentalFragile(Path)`, compartiendo lectura UTF-8/límite de bytes. QA del incremento: Loading 40, C2 32, Fragile 41 y Movement 31; Desktop headless 16, sin cambios Swing. [Identificación del contenido y comandos](docs/testing/TEST_STRATEGY.md#incremento-filesystem-fragile--29102026).

OQ-06 del lector ya resuelta por el responsable TXT; OQ-08 parcialmente resuelta para el subconjunto soportado. Pendiente: acordar con Model/Movement identidad/resistencia runtime, snapshots, casos antes/después y datos para extensiones. El [contrato TXT](docs/requirements/FRAGILE_TXT_CONTRACT_PROPOSAL.md) delimita la salida disponible. Siguiente tarea segura: contrastar el contrato y ejemplos con la firma/commit real del consumidor, sin activar especiales en `Level`, sin reimplementar este loader y sin contar el fixture de definición como puzzle jugable resuelto. La promoción única exige revisión humana del código/contrato y gate técnico; consultar el PR y Git para saber si se completó. La autorización de publicación no sustituye esa revisión.

## Integración paralela TXT con Fragile/hielo — 29/10/2026

[Procedencia anónima](docs/continuity/SOURCE_REGISTRY.md#coordinación-txt-y-fragilehielo--29102026). Un integrante reporta trabajo en esas mecánicas y ofreció un nivel de prueba; no se ha verificado una entrega ni su implementación. Cantidad/estructura de niveles de demo siguen propuestas. TXT no sustituye ese nivel ni implementa su motor.

| Estado / tarea | Responsable por dominio | Bloqueo y siguiente evidencia |
|---|---|---|
| DOCUMENTED: entrada/salida actual y limitaciones | TXT/niveles | [Matriz de interfaces y ejemplos](docs/requirements/FRAGILE_TXT_CONTRACT_PROPOSAL.md#traspaso-txt-a-modelmovement--29102026); código base 5ac2bba |
| OPEN_PENDING_TEAM: compatibilidad con consumidor | Fragile/hielo + TXT/integración | Recibir firma real y commit del consumidor; contrastar ID, coordenadas, terreno y resistencia inicial sin imponer API |
| OPEN_DEPENDENCY: representación hielo y compatibilidad runtime | Hielo/Model + TXT | Datos/combinaciones y sintaxis futura OQ-08; E04 ya elegida, rango inicial TXT ya decidido en OQ-06 |
| OFFERED_NOT_VERIFIED: nivel de prueba | Integrante que lo ofreció + QA/TXT/Model | Esperar archivo/commit y mecánicas utilizadas; acordar contrato antes de cargar entidades no soportadas; solución y resistencia por validar |
| PENDING: futuras asociaciones y restauración | Cerrojos/muros y sesión con TXT | OQ-07 y contrato runtime; no atribuir estas tareas al desarrollador de hielo por inferencia |

Siguiente integración segura: revisar el constructor real del consumidor y el nivel cuando se aporten, usando las preguntas del contrato. Este hito no justifica cambio Java independiente (`CODE_CHANGE=NOT_JUSTIFIED`): las brechas restantes son de acuerdo entre módulos, no un fallo nuevo demostrado del parser. No enviar mensajes ni editar otras ramas sin autorización.

## Decisiones y mantenimiento

E01–E11 se conservan únicamente en [DECISION_LOG](docs/decisions/DECISION_LOG.md); pendientes en [OPEN_QUESTIONS](docs/requirements/OPEN_QUESTIONS.md). No reabrir acuerdos por defecto. E04 permanece TEAM_DECIDED, con implementación pendiente en Model/Movement. E08 está implementada en el módulo de Undo/Restart, conforme a las reglas ratificadas. OQ-06 = MODULE_OWNER_DECIDED; OQ-08 = MODULE_OWNER_DECIDED parcial, extensiones abiertas. Java/build global sigue OPEN. Revisar patrones por intención, roles reales y alternativa; SoundManager no es Singleton, callbacks no acreditan Command y el DTO LevelResult no calcula score.

Mantener [arquitectura](docs/architecture/ARCHITECTURE.md), UML implementado y candidato separados; actualizar diagramas en misión autorizada, sin convertirlos en evidencia de clases ausentes. [Derechos y assets](docs/assets/ASSET_POLICY.md), [contratos Punto 5](docs/punto5/INTEGRACION_EQUIPO.md) y [FRESHNESS_POLICY](docs/continuity/FRESHNESS_POLICY.md) siguen vigentes. Revisar humanos/código, pruebas y documentos en cada incremento; conservar cápsulas y checkpoints históricos. Este backlog no concede permiso de implementación o publicación.

## Contexto grupal y tareas de adicionales — complemento 29/10/2026

[Fuente y límites](docs/continuity/SOURCE_REGISTRY.md#contexto-grupal-privado--complemento-29102026). Las cinco áreas del 24/10 fueron una propuesta organizativa, con un ofrecimiento para Undo/Restart/puntaje; no hay asignación nominal completa. Los responsables por módulo de la tabla son planificación, no atribuciones personales. La consulta actual a GitHub no encontró Issues; su uso fue propuesto, no ejecutado.

- **P0 / sin nueva fecha interna:** confirmar en OQ-10 la elegibilidad académica de las dos adicionales. E11 queda preservada; guardar/recuperar deja de descontarse automáticamente del requisito de dos extras.
- **Candidato pendiente:** selector de temas/sprites, TEAM_PROPOSAL / TEACHER_REPORTED_ELIGIBILITY / IMPLEMENTATION_NOT_VERIFIED. Definir aceptación y patrón real tras confirmar el alcance; no tratar conformidad individual ni encuesta incompleta como mayoría.
- **Persistencia:** conservar como propuesta histórica del equipo con contrato pendiente, sin transformarla en obligación docente ni equipararla a snapshots Memento para Undo. Cualquier implementación requiere alcance propio.
- **Patrones:** sugerencias de Factory Method/Abstract Factory del 29/10 no son decisiones ratificadas ni autorizan refactors. La segunda funcionalidad y el cómputo final de ambas siguen pendientes.

El parcial no tiene fecha exacta confirmada aquí. La pregunta sobre 1/11 carece de contexto de voces omitidas: no es deadline. Continúa **18/11/2026** como entrega oficial; las fechas internas de la tabla anterior son sólo propuestas. El estado actual de R7 se describe en «Próximo dominio TXT/niveles»; este complemento conserva su contexto documental anterior.

## Integración grupal — backlog I0–I4 (corte 03/11/2026)

<!-- I0_BRANCH_AWARE_CONTEXT_2026_11_03 -->

| Fase | Entrega | Criterio de cierre | Estado inicial |
|---|---|---|---|
| I0 | Rama compartida y PR Draft documentado | PR Draft con autoría, SHA, documentación viva y `main` intacta | DONE — PR #17 Draft publicado, `main` intacta al corte |
| I1 | TXT/runtime/movimiento integrado | INT-01–04 (IDs Fragile, ICE, Lock/Wall) sobre árbol I1 auditado | PUBLISHED_DRAFT — `6abb8ad`, 316/316 pruebas precommit |
| I2 | Sesión/Undo/Restart/Score | INT-05/06/08, fórmula ratificada y sin duplicidad | DONE_LOCAL — 58 Undo, 258 core, 36 desktop, 44 assets en JDK17; no publicado |
| I3 | Swing/campaña/animación | INT-09, controles, HUD, victoria y audio | PARTIAL_LOCAL — I3.A/I3.B/I3.C headless; QA GUI nativa pendiente |
| I4 | QA/entregables y revisión TEAM | INT-01–10, Java17, derechos, StarUML del código integrado, informe/video | NOT_RUN |

Detalle: [plan vivo de integración](docs/integration/TEAM_INTEGRATION_PLAN.md). No duplicar el estado real de los commits en esta fotografía: actualizar este TODO **por impacto** al cerrar cada fase. Ningún PR Draft concede autorización de merge.

## Checkpoint histórico I1.C — antes de la publicación (03/11/2026)

- [x] Ejecutado `bash scripts/test-integration-i1.sh` tras I1.C: JDK 17.0.20.1, core **256/256**, Desktop **16/16**, Visual Assets **44/44**, exit 0 y `GIT_STATE_PRESERVED=YES`; evidencia reportada por el usuario (03/11/2026).
- [ ] Auditar diff completo, tres resoluciones Swing, contratos TXT/Fragile, ausencia de estado duplicado y documentación viva. [Evidencia y fuentes](docs/integration/I1_LOCAL_VALIDATION_2026-11-03.md).
- [ ] Resolver **con autorización** el índice `UU` de los tres archivos, crear el merge commit de **dos padres** preservando los dos commits originales `Objetivo2` y volver a probar contra el HEAD confirmado.
- [ ] Promover sólo ese merge validado al **mismo PR Draft #17** tras verificar SHA remoto; nunca a `main` automáticamente ni sin revisión humana.
- [ ] I2 (Undo/Restart/Score), I3 (GUI/animación), I4 (QA/entregables/TEAM) siguen sin cerrar.

<!-- I1_PR17_PUBLICATION_2026_11_03 -->

## Estado actual del backlog de integración (03/11/2026)

- [x] I1: conflictos Swing reconciliados, 38 rutas auditadas, merge de dos padres `6abb8ad003ad9af6642fc1a1e11a91f5d238f2b2` incorporado al mismo PR #17 Draft; commits originales preservados.
- [x] Validación previa al commit: JDK 17, 316/316 pruebas PASS y verificación documental; sin repetir tests post-push.
- [x] I2: reconciliado Undo 15/5/3, Restart y score de Martina en GameSession, LevelProgression, GameController y ScoreCalculator; INT-05, INT-06 e INT-08 corregidos y verificados localmente (58 Undo, 258 core, 36 desktop, 44 assets en JDK 17); no publicado.
- [ ] I3: HUD, campaña y GUI interactiva; animación ICE/Fragile cubierta localmente por I3.C.
- [ ] I4: pruebas en HEAD final, Windows, licencias, StarUML, informe, video y aceptación del equipo.

Las tareas sin marcar del checkpoint histórico anterior pertenecen a su corte anterior al merge; no son el estado actual. No fusionar PR #17 a `main` sin aceptación expresa.

## I2 — avance local 04/11/2026

Auxiliar `integration/i2-martina-stage-2026-11-03`, base `9aebe525` y fuente `efd0fcd2`: integración local verificada antes del commit, estrictamente local sin publicación (`REMOTE_PUSH=NOT_RUN`). `GameSession` conserva el mundo, historial y métricas; `LevelProgression.CompletedLevel` retiene `(victory, metrics, undoUsesTotal)` permitiendo que el score histórico persista tras `advance()` y `restart()`; `ScoreCalculator` calcula la fórmula saturada y `GameController` expone el resultado a `SwingApplication`, cuyo HUD y pantalla de victoria reciben el puntaje. Con JDK 17.0.20.1 se ejecutaron 58 comprobaciones Undo, 258 casos core I1, 36 casos desktop y 44 assets: `bash scripts/test-integration-i2.sh` exit 0. El reset de métricas al cambiar de nivel es provisional; no hay aceptación del código por el equipo (`TEAM_ACCEPTANCE=NOT_RECORDED`). Siguiente gate: revisión funcional del equipo y verificación remota antes de cualquier publicación.

## I2 — Fragile TXT GUI hotfix (04/11/2026)

- Detectar formato por parsers estrictos existentes: STANDARD, EXTENDED y EXPERIMENTAL_FRAGILE.
- El archivo `fragile_demo.txt` y el mismo contenido con nombre externo arbitrario conservan ID `f1` y resistencia 4 en runtime.
- La carga inválida preserva el estado previamente publicado; regresión de STANDARD, ICE y EXTENDED en `DesktopIntegrationTest`.
- Evidencia: `scripts/test-integration-i2.sh` con JDK 17 y `scripts/verify-repo-readonly.sh` (ejecutar sobre el commit resultante).
- QA gráfica nativa integral, aprobación TEAM, I3/I4 y merge a `main` siguen pendientes.
- La frescura/publicación del PR #17 se comprueba en GitHub; no deducirla del HEAD local.
