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
| **I0** | Coordinación Git + continuidad | branch remota, PR Draft, docs versionados, SHA remoto, main intacta | PREPARADO, PR NO PUBLICADO |
| **I1** | TXT + `RuntimeState` + movimiento | INT-01 a INT-04, identidad Fragile, terreno ICE, E03/E04/Lock; 0 regresiones | NOT_RUN |
| **I2** | Sesión + Undo + Restart + score | INT-05/06/08, Undo 15/5/3, contadores, fórmula TEAM saturada, no duplicidad | NOT_RUN |
| **I3** | Swing + campaña + presentación | INT-09, GUI/EDT, HUD, audio, fin y Next, animación de rotura | NOT_RUN |
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
