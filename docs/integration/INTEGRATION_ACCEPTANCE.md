# Matriz de riesgos y aceptación — integración grupal

## I3.B — selector de estilo visual con Abstract Factory (04/11/2026)

Se incorporó un selector de tres familias de presentación en OPCIONES (`ORIGINAL COBRE`, `NOCTURNO`, `JADE`) detrás de `VisualThemeFactory`. La fábrica entrega dos productos coordinados: `UiPalette` (colores de superficie, texto, acento, estado, grilla y marco del tablero) y `SpritePresentation` (transformación de imagen). `VisualThemes` es el catálogo inmutable con las tres familias; `VisualThemeController` concentra el cambio de tema y `VisualThemeApplier` restaura colores y bordes originales al volver al tema base. La familia original usa presentación identidad y conserva los pixeles previos; las alternativas aplican un tinte 55/45 determinista en memoria con alfa intacto. `PixelAssets` invalida su caché al cambiar de familia y nunca escribe los PNG.

La selección es exclusivamente de vista: no altera `RuntimeState`, `GameSession`, `LevelProgression`, Undo, score, TXT ni referencias Lock/DynamicWall, y se aplica en EDT sin recargar imágenes durante `paintComponent`. `VisualThemeTest` aporta 7 escenarios headless (catálogo, sprites, identidad/reversibilidad, ICE/Fragile/Lock, EDT, nulos y hashes de PNG). Regresión: Undo 58, core 258, Desktop 40, Campaña 41, Tema 7 y Assets 44 PASS, `bash scripts/test-integration-i2.sh` exit 0.

`GUI_NATIVE_QA=NOT_RUN`; el selector es candidato a adicional RF-12/OQ-10 y no cierra esa elegibilidad ni la segunda funcionalidad. `TEAM_ACCEPTANCE=NOT_RECORDED`, sin publicación ni merge. [Comandos y límites](../testing/TEST_STRATEGY.md).

## I3.A — aceptación headless de niveles distribuidos (04/11/2026)

Se incorporó `ShippedLevelsIntegrationTest` al runner `scripts/run-swing.sh --test`, y por transitividad al runner I2. Prueba mediante `GameController` real el circuito `demo.txt` → `campaign-b.txt` (victoria, score 993/983, reset y retención histórica), `fragile_demo.txt` (ID/resistencia, solución de tres empujes, score 991 y carga fallida atómica) e `ice_demo.txt` (deslizamiento a Goal, un empuje, score 996). Regresión Java 17 y verificador documental son gates obligatorios antes del commit. Las pruebas son headless: no acreditan animación, interacción visual nativa, derechos de assets ni aceptación TEAM. I3/I4 siguen abiertos y `main` no queda fusionado.

## Checkpoint vigente — I2 publicado en PR #17 Draft (04/11/2026)

I1 + I2 (ICE/Fragile, KeyBox/Lock/Wall, Undo 15/5/3, Restart, score y progresión) y el hotfix de carga `EXPERIMENTAL_FRAGILE` están en `integration/tpo-unificado-2026-11-03` (hito publicado `4d6cb612b38e5ebdb6869bb990301045044e863f`, no SHA permanente; verificar GitHub). Evidencia local reportada: Ubuntu OpenJDK 17.0.20.1, `scripts/test-integration-i2.sh` exit 0; Undo 58, core 258/258, Desktop 40/40, assets 44/44; verificador documental PASS. Sin CI registrada ni retest nativo manual posterior al hotfix. PR Draft, sin merge a `main` ni revisión TEAM (`TEAM_ACCEPTANCE=NOT_RECORDED`). Pendientes: QA GUI, I3/I4, StarUML, licencias, extras RF-12 y entrega. El reset al cambiar de nivel sigue provisional. Los estados «local», «sin publicar» y conteos antiguos de los apartados fechados posteriores son históricos.

INT-05/06/08 tienen evidencia de pruebas locales reportadas; INT-09/10 siguen abiertos. El hotfix Fragile cubre TXT válido/renombrado, rechazo sin mutar estado y regresión de otros formatos; falta repetir GUI nativa después de publicarlo. No confundir pruebas headless con aceptación TEAM.

`LAST_REVIEWED=2026-11-04` · `TESTED_ON_COMBINED_TREE=YES_LOCAL_REPORTED` · `STATUS=I1_I2_IN_PR17_DRAFT_GUI_RETEST_PENDING` · `POLICY=NO_FALSE_POSITIVE`

| ID | Criterio verificable | Dependencias | Fase |
|---|---|---|---|
| INT-01 | Dos Fragiles con IDs/resistencias 2 y 3 conservan ID, posición y resistencia desde TXT hasta runtime tras varios empujes | D-020, contrato TXT, loader runtime | I1 |
| INT-02 | ICE leído de TXT atraviesa `LevelProgression` y se desliza o detiene según E03 sin `IllegalArgumentException` | `Terrain.ICE` y runtime | I1 |
| INT-03 | Fragile resistencia 1 completa deslizamiento antes de romper, pierde 1 resistencia por empuje, nunca provoca reinicio automático no acordado | E04, eventos de animación | I1/I3 |
| INT-04 | KeyBox activa/desactiva Lock y DynamicWalls; pared ocupada no deja transición parcial/inválida | E05/E06/OQ-07 | I1 |
| INT-05 | Undo revierte cinco movimientos y restaura mundo completo (fragile, locks, paredes dinámicas abiertas/cerradas, jugador y métricas); límite de 15 y 3 usos | E07–E09 | I2 (EXECUTED_VERIFIED_LOCAL) |
| INT-06 | Restart restablece estado y contadores desde cuatro métricas no nulas conforme al complemento TEAM con fuente registrada | fuente de ratificación, sesión | I2 (EXECUTED_VERIFIED_LOCAL) |
| INT-07 | KeyBox ocupa Lock y Goal según E05; testigo de puzzle resoluble confirma criterio de victoria | E05, OQ-01 | I1/I3 |
| INT-08 | Al finalizar, puntaje TEAM saturado se comunica a HUD y siguiente nivel; retención histórica de undoUsesTotal sin estado duplicado | ScoreCalculator, LevelProgression | I2 (EXECUTED_VERIFIED_LOCAL) |
| INT-09 | Animación de Fragile sobre ICE representa trayectoria y destrucción aunque no exista en tablero final | BoardPanel, GameView, EDT | I3 |
| INT-10 | Compilación real `--release 17` y regresión core+desktop combinada, scripts Windows/Bash, evidencia y `git diff --check` | I1-I3 completos | I4 |

**Corte histórico I1:** INT-01–04 se ejecutaron sobre el árbol combinado I1 antes del commit (blobs auditados), con 316/316 pruebas de regresión aprobadas; INT-05–10 no quedan acreditados por ello. No se reejecutó la suite después del push. Cada resultado nuevo debe registrar SHA exacto, comando, JDK/SO, fixture, salida, exit code y responsable; actualizar [estrategia de tests](../testing/TEST_STRATEGY.md) y [trazabilidad](../traceability/TRACEABILITY_MATRIX.md) según impacto.

## Riesgos verificables

| Riesgo | Evidencia en source branch | Mitigación, sin decidir por inferencia |
|---|---|---|
| `R-ICE` | `victoria/RuntimeState.Ground` carece de ICE; `fromBoard()` usa `valueOf` | Adaptador/representación unificada y test INT-02 |
| `R-FRAGILE-ID` | `Objetivo2/C2LevelParser` reduce ID a `Cell` con resistencia | Conservar ID en definición y runtime; INT-01 |
| `R-UNDO-DUPLICATE` | Martina: `GameController.history`; Juan: `GameSession` | Un único origen de verdad; INT-05/06 |
| `R-SCORE-COUPLING` | `ScoreCalculator` desktop sin ciclo completo de `LevelProgression` | Exponer resultado completado desde core/adaptador; INT-08 |
| `R-GUI-FRAMES` | Render de animación depende de estado final al romperse Fragile | Testigos de frames intermedios; INT-09 |
| `R-THEME-EXTRA` | Selector visual implementado y probado, pero su elegibilidad como adicional RF-12/OQ-10 sigue abierta y no hay aceptación TEAM | No computar como extra cerrada; revisar elegibilidad y segunda funcionalidad antes de la entrega |
| `R-UML` | R10 `.mdj` en `impaciencia` es DRAFT, no diagrama del árbol unificado | Nuevo diagrama basado en HEAD de integración, validación nativa StarUML |
| `R-EXTRAS-RIGHTS` | RF-12 y licencias requieren aceptación/atribución | Validar funcionalidades y derechos individuales antes de entrega |

**Estados:** `SOURCE_BRANCH_ONLY`, `IN_INTEGRATION_BRANCH`, `IN_MAIN`, `REPORTED_QA`, `EXECUTED_VERIFIED`, `TEAM_ACCEPTED`, `NOT_RUN`. Las reglas no acordadas permanecen en [OPEN_QUESTIONS](../requirements/OPEN_QUESTIONS.md); no generar otro libro de decisiones.

## Evidencia local previa a commit I1.C (03/11/2026)

La matriz superior conserva los **oráculos**. En el worktree auxiliar `integration/i1-santino-stage-2026-11-03`, el usuario ejecutó con OpenJDK 17 las ocho suites core (252/252), Desktop (16/16) y Visual Assets (44/44), todas PASS en la versión **anterior a I1.C**; el postcheck preservó Git. En I1.C los casos INT-01..04 pasan a comprobar expresamente las resistencias **2 y 3**, varios empujes, ICE por `LevelProgression`, rotura sin reinicio y cierre ocupado. Tras I1.C, el usuario **ejecutó** el runner [test-integration-i1.sh](../../scripts/test-integration-i1.sh) con `java`/`javac 17.0.20.1`: core **256/256**, Desktop **16/16**, Visual Assets **44/44**, total **316/316**, exit 0 y `GIT_STATE_PRESERVED=YES`; el verificador documental también pasó (170 archivos, 541 enlaces, 43 assets). Evidencia local reportada, **no** HEAD remoto ni aceptación TEAM. Limitaciones: sin GUI interactiva, sin Windows nativo, sin commit de integración ni aceptación TEAM. [Registro técnico y fase siguiente](I1_LOCAL_VALIDATION_2026-11-03.md).

<!-- I1_PR17_PUBLICATION_2026_11_03 -->

## Publicación I1 — estado verificable

INT-01 a INT-04: `EXECUTED_VERIFIED_PRECOMMIT` sobre el contenido que pasó al merge `6abb8ad003ad9af6642fc1a1e11a91f5d238f2b2` en PR #17 Draft. Resultado reportado: **316/316 PASS**, Java 17.0.20.1 Ubuntu, sin ejecución postcommit adicional. INT-07 y INT-09 (campaña y frames Swing) quedan pendientes. No equivale a `TEAM_ACCEPTED`, `IN_MAIN` ni a completar I2/I3/I4. [Detalle](I1_LOCAL_VALIDATION_2026-11-03.md).

## I2 — avance local 04/11/2026

Auxiliar `integration/i2-martina-stage-2026-11-03`, base `9aebe525` y fuente `efd0fcd2`: integración local verificada antes del commit, estrictamente local sin publicación (`REMOTE_PUSH=NOT_RUN`). `GameSession` conserva el mundo, historial y métricas; `LevelProgression.CompletedLevel` retiene `(victory, metrics, undoUsesTotal)` permitiendo que el puntaje histórico persista tras `advance()` y `restart()`; `ScoreCalculator` calcula la fórmula saturada y `GameController` comunica el resultado a `SwingApplication`, cuyo HUD (`status`) y pantalla de victoria reciben el puntaje. Con JDK 17.0.20.1 se ejecutaron 58 comprobaciones Undo, 258 casos core I1, 36 casos desktop y 44 assets: `bash scripts/test-integration-i2.sh` exit 0. INT-05, INT-06 e INT-08 `PASS` verificados localmente. El reset de métricas al cambiar de nivel es provisional; no hay aceptación del código por el equipo (`TEAM_ACCEPTANCE=NOT_RECORDED`). Siguiente gate: revisión funcional del equipo y verificación remota antes de cualquier publicación.

## I2 — Fragile TXT GUI hotfix (04/11/2026)

- Detectar formato por parsers estrictos existentes: STANDARD, EXTENDED y EXPERIMENTAL_FRAGILE.
- El archivo `fragile_demo.txt` y el mismo contenido con nombre externo arbitrario conservan ID `f1` y resistencia 4 en runtime.
- La carga inválida preserva el estado previamente publicado; regresión de STANDARD, ICE y EXTENDED en `DesktopIntegrationTest`.
- Evidencia: `scripts/test-integration-i2.sh` con JDK 17 y `scripts/verify-repo-readonly.sh` (ejecutar sobre el commit resultante).
- QA gráfica nativa integral, aprobación TEAM, I3/I4 y merge a `main` siguen pendientes.
- La frescura/publicación del PR #17 se comprueba en GitHub; no deducirla del HEAD local.
