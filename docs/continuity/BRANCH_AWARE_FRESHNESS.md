# Protocolo de frescura por rama y continuidad de agentes

`LAST_REVIEWED=2026-11-03` · `TYPE=LIVE_MAINTENANCE_PROTOCOL` · `TRIGGERS=EACH_INCREMENT,BEFORE_PR,AFTER_PR,AT_HANDOFF,WEEKLY_DURING_ACTIVE_INTEGRATION`

Este protocolo **amplía** [FRESHNESS_POLICY](FRESHNESS_POLICY.md), no la reemplaza. El defecto que corrige es confundir la documentación histórica de `main` con los avances reales alojados exclusivamente en ramas de integrantes. **No** actualizar en `main` una capacidad hasta que su código haya sido fusionado y verificado allí.

## Lectura del estado — precedencia

1. `git remote -v`, `git branch --show-current`, `git rev-parse HEAD`, `git ls-remote --heads origin` y PRs remotos (fuente de estado vivo; la fecha de un archivo no los sustituye).
2. `AGENTS.md` y [rehidratación](REHYDRATION_INDEX.md) y, si existe `docs/integration/`, su [README](../integration/README.md).
3. [CURRENT_STATE](CURRENT_STATE.md), [TODO](../../TODO.md), [REQUISITOS](../requirements/REQUIREMENTS.md), [DECISION_LOG](../decisions/DECISION_LOG.md), [OPEN_QUESTIONS](../requirements/OPEN_QUESTIONS.md), [TRACEABILITY](../traceability/TRACEABILITY_MATRIX.md), [TEST_STRATEGY](../testing/TEST_STRATEGY.md), [ARCHITECTURE](../architecture/ARCHITECTURE.md), [STARUML_STATUS](../architecture/uml/STARUML_STATUS.md), [SOURCE_REGISTRY](SOURCE_REGISTRY.md).
4. Un documento con SHA/fecha históricos es *fotografía*, no instrucción de checkout ni declaración del HEAD actual.

## Matriz obligatoria de actualización por impacto

| Evento | Archivo/s vivos a revisar | Qué registrar | No hacer |
|---|---|---|---|
| Nuevo commit de compañero | `docs/integration/BRANCH_SNAPSHOT_*` como **nuevo** corte; `docs/integration/TEAM_INTEGRATION_PLAN.md` y matriz si cambia un contrato | SHAs actuales, comparativa, archivos afectados, estado «SOURCE_BRANCH_ONLY» | Reescribir una fotografía vieja ni atribuir código a `main` |
| Nuevo commit de integración I1/I2/I3 | `docs/continuity/CURRENT_STATE.md`, `README.md`, `TODO.md`, `TRACEABILITY_MATRIX.md`, `TEST_STRATEGY.md`, `ARCHITECTURE.md`, `RESPONSIBILITIES.md`, UML | capacidad real, dueño, contrato, pruebas ejecutadas y pendientes | Tratar un test reportado de otra rama como test integrado |
| Decisión TEAM nueva/ajuste autorizado | `DECISION_LOG.md`, `OPEN_QUESTIONS.md`, `REQUIREMENTS.md` si afecta norma, trazabilidad | autoridad, fecha, alcance, fuente, resultado de consenso | Inventar votación o reabrir dimensión ya decidida |
| PR Draft creado/cerrado/merge | `docs/integration/README.md` y plan; `CURRENT_STATE`/`TODO` si cambia capacidad; índice | URL PR, base/head, estado Draft/Ready/Merged, SHA, QA actual | Afirmar `main` integrado mientras Draft |
| Asset/licencia nueva | `ASSET_POLICY`, manifiesto, `USO_IA_Y_LICENCIAS.md`, `SOURCE_REGISTRY` | archivo, hash, procedencia y licencia por asset | Publicar material privado o afirmar CC0 por origen genérico |
| StarUML actualizado | `STARUML_STATUS.md`, UML versionado, informe y matriz de trazabilidad | SHA de código, editor/versión, apertura-guardado-reapertura real | Confundir JSON válido con StarUML aprobado |
| Cambio en comandos de ejecución | `README`, `TEST_STRATEGY`, `CURRENT_STATE` si afecta uso | comando reproducible para Windows/Ubuntu/JDK | Confiar en comandos históricos rotos |
| Final de turno/chat | archivos afectados, `TODO`, estado/PR y siguiente gate | `STATUS`, `VERIFIED`, `NOT_RUN`, próximo paso autocontenido | Depender del sandbox, chats o rutas personales |

**Cadencia manual:** después de cada commit/PR, al iniciar un agente nuevo, antes de crear PR o preparar merge y **al menos una vez por semana durante la integración activa**. No se ha configurado automatización periódica. No modificar documentos sin impacto real sólo por fecha; **sí registrar comprobación `UNCHANGED_VERIFIED` y motivo**.

## Registro inicial de documentos por revisar (corte 03/11/2026)

Esta tabla **no** convierte en incorrecta la documentación de `main`: indica dónde hay estado por rama, cabeceras antiguas y disparadores de actualización. Los apéndices I0 se agregan en la rama compartida, no en `main`.

| Documento | Corte observado | Situación y siguiente actualización por impacto |
|---|---|---|
| `AGENTS.md` | 29/10 | Política todavía aplicable, pero sin ruta al PR compartido: enlazar `docs/integration/`; revisar cambios de autorización o dominio por misión. |
| `README.md` | `main`: 29/10; `victoria`: 01/11 | Ambos correctos **en su rama**; no describir ICE/Undo grupal hasta I1/I2/I3. Agregar índice de integración, después actualizar ejecución real. |
| `REHYDRATION_INDEX.md` | `main`: 29/10; `victoria`: 01/11 | Agregar ruta I0 y un protocolo de consulta de HEAD remoto. Reconciliar frases antiguas al cerrar cada hito, sin borrar su contexto histórico. |
| `CURRENT_STATE.md` | `main`: 29/10; `victoria`: 01/11 | La capacidad publicada varía según el checkout; registrar I0 explícitamente como rama compartida, I1/I2/I3 cuando sus tests existan. |
| `TODO.md` | 29/10 | Prioridades históricas previas a integración; agregar backlog I0–I4 y revisar cierres por commit, sin reinterpretar prioridades TEAM. |
| `FRESHNESS_POLICY.md` | 29/10 | Ya exige revisión por hito y semanal. Agregar separación por rama y el comando verificable, **sin sustituirla**. |
| `SOURCE_REGISTRY.md` | hasta 29/10 | Registra bibliografía/procedencia y faltantes privados. Actualizar sólo ante fuente nueva verificable; no importar ZIP de conversaciones. |
| `DECISION_LOG.md` + `OPEN_QUESTIONS.md` | 29/10 en `main` | E01–E11 y D-020 son autoridad canónica. Fórmula/reset informados por rama de sesión requieren cotejo con su evidencia antes de trasladarse a contrato oficial. |
| `ARCHITECTURE.md` + `RESPONSIBILITIES.md` + UML | 28–29/10 | Describen implementación/candidato del corte. Actualizarlos tras I1/I2/I3 y modelo final; `.mdj` individual R10 de `impaciencia` **no** valida StarUML grupal. |
| `TRACEABILITY_MATRIX.md` + `TEST_STRATEGY.md` | 29/10 en `main` | Mantener pruebas antiguas fechadas; agregar resultados **ejecutados en HEAD combinado** y casos INT-01–10 según corresponda. |

No editar cabeceras de autoridad o cierres históricos para aparentar actualidad. La corrección de fondo es **citar correctamente rama, SHA, estado y evidencia**, y actualizar únicamente los apartados vivos afectados.

## Comando de revisión y criterio fail-close

Desde la raíz de checkout (sin escritura):

```bash
python3 scripts/check-repo-context.py --local
python3 scripts/check-repo-context.py --remote
```

`--local` valida la estructura y los enlaces locales de esta capa de integración, y enumera fechas de contexto; `--remote` consulta GitHub vía `git ls-remote origin` y contrasta el snapshot histórico. Un SHA distinto indica `REMOTE_DRIFT`: requiere reauditoría y un **nuevo corte**, no es licencia para forzar pushes. Si falta conexión, reportar `REMOTE_UNKNOWN`, nunca PASS.

## Bibliografía, privacidad y autarquía documental

- Las fuentes docentes se normalizan en [REQUISITOS](../requirements/REQUIREMENTS.md) y tienen procedencia/disponibilidad en [SOURCE_REGISTRY](SOURCE_REGISTRY.md); no redistribuir PDFs, grabaciones ni ASR íntegro si no hay permiso.
- La bibliografía técnica y las decisiones de diseño se anclan en [ARCHITECTURE](../architecture/ARCHITECTURE.md), [RESPONSIBILITIES](../architecture/RESPONSIBILITIES.md) y [DECISION_LOG](../decisions/DECISION_LOG.md). Registrar bibliografía nueva con título, edición, enlace, sección aplicada, acceso y consecuencias; no inventar edición o página.
- Las comunicaciones privadas del grupo pueden motivar una comprobación, pero el artefacto público debe reflejar **hechos técnicos despersonalizados**; no exportar números, capturas o conversaciones.
- Un agente nuevo con **sólo el clon** debe poder saber: estado de la rama correcta, fuente de autoridad, qué cambió, quién lo implementó según commit, qué está probado, qué falta y siguiente gate.

Si un link está roto, las fuentes normativas no están claras, o se detecta divergencia material, `FRESHNESS_GATE=BLOCKED` para la publicación afectada; preparar corrección acotada, sin tocar `main` ni usar fuerza.
