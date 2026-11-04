# Integración compartida del TPO — plan operativo I0–I4

`LAST_REVIEWED=2026-11-03` · `DOMAIN=GIT_TEAM_INTEGRATION_COORDINATION` · `INTEGRATION_BRANCH=integration/tpo-unificado-2026-11-03` · `TARGET=main` · `REVIEW_MODE=DRAFT` · `MERGE_AUTHORIZATION=NOT_GRANTED`

## Intención y fuente de verdad

Unificar **las contribuciones grupales**, conservando autoría y evitando descartar trabajo de otros. `main` mantiene la línea canónica; las ramas son fuentes independientes; `impaciencia` solo provee ejemplos individuales de diseño, sin autoridad para copiar decisiones, puntajes, contratos TXT o UML a `main` automáticamente.

Antes de intervenir: [AGENTS](../../AGENTS.md) → [rehidratación](../continuity/REHYDRATION_INDEX.md) → [requisitos](../requirements/REQUIREMENTS.md), [decisiones](../decisions/DECISION_LOG.md), [preguntas](../requirements/OPEN_QUESTIONS.md), [proveniencia](../continuity/SOURCE_REGISTRY.md), [trazabilidad](../traceability/TRACEABILITY_MATRIX.md), [pruebas](../testing/TEST_STRATEGY.md), [TODO](../../TODO.md), [frescura](../continuity/FRESHNESS_POLICY.md), [estado de ramas](BRANCH_SNAPSHOT_2026-11-03.md). El repositorio remoto vigente prevalece sobre cualquier SHA histórico.

La consigna exige Java/Swing 2D, TXT externos, Undo 15/5/3, Restart, KeyBox/Lock/muros, Fragile/hielo, victoria y score, dos adicionales con patrones, assets de dominio público, README, video, StarUML e informe; la defensa y entrega están previstas para el 18/11/2026. [REQUISITOS](../requirements/REQUIREMENTS.md) establece el alcance oficial; **este plan no crea requisitos nuevos**.

## Política de ramas y publicaciones

- I0 parte de `victoria` (`main` antecesor), en worktree **aislado** con almacén Git compartido, sin actualizar `main` o la rama `victoria`.
- Abrir un único PR **Draft** con base `main`. Su título/cuerpo indican expresamente que I0 contiene la contribución `victoria` y documentación de coordinación, **NO** Santino/Martina ni las funciones completas.
- I1, I2, I3 e I4 agregan commits separados por dominio técnico sobre la **misma** rama compartida y mismo PR. Antes de cada fase volver a leer contratos y HEAD, comparar diff y colisiones.
- Prohibidos merge automático, force-push, rebase irreversible, `git reset --hard`, `git clean -fd` o elegir `-X ours/theirs` para resolver conflictos semánticos.
- No mezclar cambios ejecutables con actos de documentación de bajo riesgo; ni actualizar decisiones TEAM sin evidencia de ratificación.

## Fases y gates

| Hito | Dominio técnico único | Verificación requerida | Estado al crear este plan |
|---|---|---|---|
| **I0** | Coordinación Git + continuidad | branch remota, PR Draft, docs versionados, SHA remoto, main intacta | PUBLISHED_DRAFT_VERIFIED — PR #17; `main` intacta al corte |
| **I1** | TXT + `RuntimeState` + movimiento | INT-01 a INT-04, identidad Fragile, terreno ICE, E03/E04/Lock; 0 regresiones | NOT_RUN |
| **I2** | Sesión + Undo + Restart + score | INT-05/06/08, Undo 15/5/3, contadores, fórmula TEAM saturada, no duplicidad | IN_PR17_DRAFT; LOCAL_TESTS_PASS |
| **I3** | Swing + campaña + presentación | INT-09, GUI/EDT, HUD, audio, fin y Next, animación de rotura | PARTIAL_LOCAL — I3.A/I3.B headless; animación y QA GUI pendientes |
| **I4** | QA de release y entregables | INT-01–10 ejecutados sobre HEAD combinado, Java 17, Windows/PowerShell, StarUML final, derechos, video, informe, revisión | NOT_RUN |

La [matriz de aceptación](INTEGRATION_ACCEPTANCE.md) contiene oráculos, riesgos y pruebas. Cada avance produce `SOURCE_HEAD`, `CHANGED_FILES`, `TESTS_EXECUTED`, `DECISIONS_AFFECTED`, `DOCS_REFRESHED`, `UNVERIFIED` y `NEXT_GATE`. El historial previo es `REPORTED_QA` hasta ejecutarse en el SHA combinado.

## Contrato y autoridad técnica

- E03: hielo hasta última casilla válida; E04: Fragile agota resistencia, termina el deslizamiento y luego rompe (sin auto-Restart implícito).
- E05: KeyBox sobre Lock no sustituye destino Goal; detalles OQ-01 permanecen abiertos.
- E06: cierre al desactivar Lock, bordes de ocupación OQ-07 requieren especificación y pruebas.
- E07/E08/E09: Undo cinco pasos solo con >=5 disponibles, máximo tres consecutivos; reinicio de presupuesto solo Restart o cambio de nivel; usos contabilizados fuera del memento.
- E10: penalizar movimientos/empujes/Undo. La fórmula `max(0, 1000 - moves - 2*pushes - 10*undoUsesTotal)` y reset de cuatro contadores fueron informados como complemento TEAM en la rama de sesión; **verificar autoridad/versionado de esa ratificación antes de imponerlos al runtime integrado**. Cálculo sin overflow de `long`.
- D-020: identidad y resistencia Fragile pertenecen a definición TXT adoptada por responsable del módulo. No normalizar todo hacia `Cell` sin ID ni eliminar entrada experimental estable.
- OQ-10: dos adicionales elegibles, y derechos de assets, siguen sin aprobación final.

### Incompatibilidades de contratos confirmadas en código (corte de fuentes)

`victoria` carece de `RuntimeState.Ground.ICE` y `BoxKind.FRAGILE`; `fromBoard` convierte `Ground.valueOf(cell.terrain().name())` y extrae sólo `Occupant.BOX` → riesgo de excepción y pérdida de Fragile al mezclar con `Objetivo2`.

`Objetivo2` conserva ID local del parser C2 durante validación, pero construye `Cell(terrain, FRAGILE_BOX, resistance)`: el record de salida no conserva el ID. No afirmar integración de contrato D-020 por solo parsear atributos.

`feature/undo-restart-score` implementa historial y score en `GameController`; `victoria` lo concentra en `GameSession`/`LevelProgression`. Un merge textual podría duplicar las autoridades de sesión. La corrección de overflow de Martina **sí está** en el HEAD de su rama y no debe reportarse como error vigente.

## Rehidratación en un chat nuevo

1. Identificar remoto y rama: `git status --short --branch`, `git remote -v`, `git rev-parse HEAD`; comparar contra GitHub, PRs y HEADs de los módulos.
2. Leer `AGENTS.md`, once documentos obligatorios, este directorio, API y tests que se vayan a cambiar.
3. Separar IMPLEMENTADO_EN_LA_RAMA, EN_MAIN, REPORTADO y PENDIENTE; nunca concluir que el PR Draft ya integró tres módulos.
4. Ejecutar `python3 scripts/check-repo-context.py --local` para validación estructural; `--remote` solo cuando exista conectividad y se necesite la comparación del estado remoto.
5. Una vez cerrados QA, aceptación TEAM y derechos, solicitar revisión y autorización expresa antes de cambiar Draft→Ready o fusionar a `main`.

**Fuentes públicas y bibliografía:** [SOURCE_REGISTRY](../continuity/SOURCE_REGISTRY.md) (documentos docentes por procedencia, sin distribuir originales), [REQUISITOS](../requirements/REQUIREMENTS.md) (baseline), [ARCHITECTURE](../architecture/ARCHITECTURE.md) (MVC/GRASP/SOLID/GoF), [STARUML_STATUS](../architecture/uml/STARUML_STATUS.md) y fuentes Java. Se consultaron como *contexto privado* comunicaciones del equipo; no publicar su exportación, teléfonos, capturas o transcripciones completas.

## Checkpoint histórico local I1.C — anterior al merge commit

I1.A resolvió texto Swing; I1.B0 protege la identidad TXT; I1.B1 añadió runtime ICE/Fragile y eventos de trayectoria. En el estado **local sin commit**, el usuario reportó 312/312 PASS con JDK 17 antes del ajuste I1.C. La suite I1.C especifica resistencias 2/3, varios empujes y validaciones de `LevelProgression`, sesión y muro ocupado. Su ejecución posterior con JDK17 local reportó **316/316 PASS** (256 core, 16 Desktop, 44 assets), más verificación documental y auditoría de contenido read-only PASS; el índice Git conserva tres rutas `UU` sin resolver formalmente. Ver [auditoría I1](I1_LOCAL_VALIDATION_2026-11-03.md) y [runner reproducible](../../scripts/test-integration-i1.sh). `I1=LOCAL_PENDING_REVIEW`; I2-I4 continúan pendientes; los commits fuente siguen sin integrarse en el PR hasta crear/promover un merge commit de dos padres supervisado.

<!-- I1_PR17_PUBLICATION_2026_11_03 -->

## Checkpoint vigente I1 — publicación comprobada (03/11/2026)

- `SOURCE_HEAD=ac728a2241d2fae8c4c32b78e29fd4c9bc6228eb`; `INTEGRATION_HEAD=6abb8ad003ad9af6642fc1a1e11a91f5d238f2b2`; padres `5af4a586` y `ac728a22`.
- PR #17 **open/Draft**, dirigido a `main@f4a28fd6c1d869fcd6f6189e5cbc2da49bb2f7a2`; autoría fuente preservada, sin force push ni aceptación del equipo.
- `TESTS_EXECUTED=316/316_PASS_PRECOMMIT` en Ubuntu/JDK17 sobre el árbol después incorporado, verificación de 38 rutas/índice PASS. No se reejecutó Java tras la publicación.
- `I1=PUBLISHED_IN_INTEGRATION_BRANCH`, `I2/I3/I4=NOT_RUN`, `IN_MAIN=NO`, `TEAM_ACCEPTANCE=NOT_RECORDED`.
- Riesgos pendientes: historia Undo/Restart/Score de Martina, animación ICE/Fragile I3, pruebas de plataformas, licencias, StarUML y entrega. La sección anterior es un checkpoint histórico, no un gate activo.

Siguiente gate: comparar el HEAD de `feature/undo-restart-score` y contratos de sesión con `RuntimeState`/`GameSession`; integrar I2 en un worktree nuevo o aislado, con pruebas antes de promover al mismo PR.

## Checkpoint vigente — I2 publicado en PR #17 Draft (04/11/2026)

I1 + I2 (ICE/Fragile, KeyBox/Lock/Wall, Undo 15/5/3, Restart, score y progresión) y el hotfix de carga `EXPERIMENTAL_FRAGILE` están en `integration/tpo-unificado-2026-11-03` (hito publicado `4d6cb612b38e5ebdb6869bb990301045044e863f`, no SHA permanente; verificar GitHub). Evidencia local reportada: Ubuntu OpenJDK 17.0.20.1, `scripts/test-integration-i2.sh` exit 0; Undo 58, core 258/258, Desktop 40/40, assets 44/44; verificador documental PASS. Sin CI registrada ni retest nativo manual posterior al hotfix. PR Draft, sin merge a `main` ni revisión TEAM (`TEAM_ACCEPTANCE=NOT_RECORDED`). Pendientes: QA GUI, I3/I4, StarUML, licencias, extras RF-12 y entrega. El reset al cambiar de nivel sigue provisional. Los estados «local», «sin publicar» y conteos antiguos de los apartados fechados posteriores son históricos.

## I2 — avance local 04/11/2026

Auxiliar `integration/i2-martina-stage-2026-11-03`, base `9aebe525` y fuente `efd0fcd2`: integración local verificada antes del commit, sin publicación. `GameSession` conserva el mundo, historial y métricas; `LevelProgression` coordina finalización y nivel; `GameController` conecta Undo y el resultado calculado por `ScoreCalculator`. Con JDK 17 se ejecutaron 40 comprobaciones Undo, 256 casos core I1, 35 casos desktop y 44 assets: `bash scripts/test-integration-i2.sh` exit 0; JDK 17.0.20.1. La primera ejecución terminó exit 1 por whitespace en el índice y fue corregida. El reset al cambiar de nivel es provisional; no hay aceptación del código por el equipo. Siguiente gate: revisión funcional del equipo y verificación remota antes de cualquier publicación.

## I3 — avance local 04/11/2026

I3.A incorporó `ShippedLevelsIntegrationTest` al runner desktop y por transitividad al I2: `demo.txt` → `campaign-b.txt` (victoria, score 993/983, reset y retención), `fragile_demo.txt` (ID/resistencia, tres empujes, score 991 y fallo atómico) e `ice_demo.txt` (deslizamiento a Goal, score 996). I3.B incorporó el selector real `ORIGINAL COBRE`/`NOCTURNO`/`JADE` sobre `VisualThemeFactory` (Abstract Factory de vista), con restauración exacta del tema original, sprites tintados en memoria con alfa preservado, caché invalidada por familia y `VisualThemeTest` de 7 escenarios headless. I3.C incorporó la animación de presentación desde `RuntimeMoveResult`: `BoxAnimation`, `GameView.animateBox`, orden publicación→SFX→animación→victoria, frames deterministas de ICE y Fragile, supresión de caja fantasma, diferimiento de FINISH y cancelación segura por Undo/Restart/recarga/ESC/cierre. `AnimationPresentationTest` aporta 14 escenarios headless; no se agregaron PNG ni se modificaron hashes. Regresión del árbol local: Undo 58, core 258, Desktop 40, Campaña 41, Tema 7, Animación 14 y Assets 44 PASS (`scripts/test-integration-i2.sh`, exit 0, JDK 17). INT-09 queda con evidencia headless, no QA nativa; siguen abiertos la QA GUI nativa, I4 y la elegibilidad del selector como adicional RF-12/OQ-10. `TEAM_ACCEPTANCE=NOT_RECORDED`; no hay publicación ni merge por este hito.
