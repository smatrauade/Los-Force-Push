# Matriz de riesgos y aceptación — integración grupal

`LAST_REVIEWED=2026-11-03` · `TESTED_ON_COMBINED_BRANCH=NO` · `STATUS=TEST_DESIGN_ONLY` · `POLICY=NO_FALSE_POSITIVE`

| ID | Criterio verificable | Dependencias | Fase |
|---|---|---|---|
| INT-01 | Dos Fragiles con IDs/resistencias 2 y 3 conservan ID, posición y resistencia desde TXT hasta runtime tras varios empujes | D-020, contrato TXT, loader runtime | I1 |
| INT-02 | ICE leído de TXT atraviesa `LevelProgression` y se desliza o detiene según E03 sin `IllegalArgumentException` | `Terrain.ICE` y runtime | I1 |
| INT-03 | Fragile resistencia 1 completa deslizamiento antes de romper, pierde 1 resistencia por empuje, nunca provoca reinicio automático no acordado | E04, eventos de animación | I1/I3 |
| INT-04 | KeyBox activa/desactiva Lock y DynamicWalls; pared ocupada no deja transición parcial/inválida | E05/E06/OQ-07 | I1 |
| INT-05 | Undo revierte cinco movimientos y restaura mundo completo (fragile, locks, paredes, jugador y métricas); límite de 15 y 3 usos | E07–E09 | I2 |
| INT-06 | Restart restablece estado y contadores conforme al complemento TEAM con fuente registrada | fuente de ratificación, sesión | I2 |
| INT-07 | KeyBox ocupa Lock y Goal según E05; testigo de puzzle resoluble confirma criterio de victoria | E05, OQ-01 | I1/I3 |
| INT-08 | Al finalizar, puntaje TEAM saturado se comunica a HUD y siguiente nivel; no queda estado histórico duplicado | ScoreCalculator, LevelProgression | I2/I3 |
| INT-09 | Animación de Fragile sobre ICE representa trayectoria y destrucción aunque no exista en tablero final | BoardPanel, GameView, EDT | I3 |
| INT-10 | Compilación real `--release 17` y regresión core+desktop combinada, scripts Windows/Bash, evidencia y `git diff --check` | I1-I3 completos | I4 |

**Estado:** estos casos **no** fueron ejecutados sobre la rama combinada. Su existencia documenta el oráculo, no un PASS. Cada resultado nuevo debe registrar SHA exacto, comando, JDK/SO, fixture, salida, exit code y responsable; actualizar [estrategia de tests](../testing/TEST_STRATEGY.md) y [trazabilidad](../traceability/TRACEABILITY_MATRIX.md) según impacto.

## Riesgos verificables

| Riesgo | Evidencia en source branch | Mitigación, sin decidir por inferencia |
|---|---|---|
| `R-ICE` | `victoria/RuntimeState.Ground` carece de ICE; `fromBoard()` usa `valueOf` | Adaptador/representación unificada y test INT-02 |
| `R-FRAGILE-ID` | `Objetivo2/C2LevelParser` reduce ID a `Cell` con resistencia | Conservar ID en definición y runtime; INT-01 |
| `R-UNDO-DUPLICATE` | Martina: `GameController.history`; Juan: `GameSession` | Un único origen de verdad; INT-05/06 |
| `R-SCORE-COUPLING` | `ScoreCalculator` desktop sin ciclo completo de `LevelProgression` | Exponer resultado completado desde core/adaptador; INT-08 |
| `R-GUI-FRAMES` | Render de animación depende de estado final al romperse Fragile | Testigos de frames intermedios; INT-09 |
| `R-UML` | R10 `.mdj` en `impaciencia` es DRAFT, no diagrama del árbol unificado | Nuevo diagrama basado en HEAD de integración, validación nativa StarUML |
| `R-EXTRAS-RIGHTS` | RF-12 y licencias requieren aceptación/atribución | Validar funcionalidades y derechos individuales antes de entrega |

**Estados:** `SOURCE_BRANCH_ONLY`, `IN_INTEGRATION_BRANCH`, `IN_MAIN`, `REPORTED_QA`, `EXECUTED_VERIFIED`, `TEAM_ACCEPTED`, `NOT_RUN`. Las reglas no acordadas permanecen en [OPEN_QUESTIONS](../requirements/OPEN_QUESTIONS.md); no generar otro libro de decisiones.

## Evidencia local previa a commit I1.C (03/11/2026)

La matriz superior conserva los **oráculos**. En el worktree auxiliar `integration/i1-santino-stage-2026-11-03`, el usuario ejecutó con OpenJDK 17 las ocho suites core (252/252), Desktop (16/16) y Visual Assets (44/44), todas PASS en la versión **anterior a I1.C**; el postcheck preservó Git. En I1.C los casos INT-01..04 pasan a comprobar expresamente las resistencias **2 y 3**, varios empujes, ICE por `LevelProgression`, rotura sin reinicio y cierre ocupado. Tras I1.C, el usuario **ejecutó** el runner [test-integration-i1.sh](../../scripts/test-integration-i1.sh) con `java`/`javac 17.0.20.1`: core **256/256**, Desktop **16/16**, Visual Assets **44/44**, total **316/316**, exit 0 y `GIT_STATE_PRESERVED=YES`; el verificador documental también pasó (170 archivos, 541 enlaces, 43 assets). Evidencia local reportada, **no** HEAD remoto ni aceptación TEAM. Limitaciones: sin GUI interactiva, sin Windows nativo, sin commit de integración ni aceptación TEAM. [Registro técnico y fase siguiente](I1_LOCAL_VALIDATION_2026-11-03.md).
