# Índice documental

## Versión integrada en main — 04/11/2026

Punto de entrada a la [auditoría de la versión integrada y sus pendientes](integration/PR17_FUNCTIONAL_RC_2026-11-04.md): Replay Swing, campaña de cinco TXT, Observer de presentación y regresión Java 17. [README de ejecución](../README.md), [TODO vigente](../TODO.md), [arquitectura](architecture/ARCHITECTURE.md) y [contrato entre módulos](punto5/INTEGRACION_EQUIPO.md). Las secciones fechadas siguientes describen hitos anteriores; sus pendientes de gameplay no describen la versión integrada. El PR #17 se fusionó a main en `6e884226054284981486efb6bef65c43bc5c9fda`; las decisiones del grupo se conservan en su registro y la puesta en común permitirá revisar la versión integrada.

Revisado 29/10/2026. Empezar por [AGENTS](../AGENTS.md) → [rehidratación](continuity/REHYDRATION_INDEX.md) → [CURRENT_STATE](continuity/CURRENT_STATE.md). La reconciliación del PR #13 ya está integrada; no deducir el HEAD de fechas o cápsulas, contrastarlo con Git/GitHub.

| Tema | Documento |
|---|---|
| Ejecutar desde clon | [README](../README.md), [guía Punto 5](punto5/GUIA_INSTALACION_GITHUB_DESKTOP.md) |
| Requisitos y deuda residual | [REQUIREMENTS](requirements/REQUIREMENTS.md), [OPEN_QUESTIONS](requirements/OPEN_QUESTIONS.md) |
| Acuerdos TEAM, decisiones del responsable TXT y alternativas | [DECISION_LOG](decisions/DECISION_LOG.md) |
| Implementación frente a candidato | [ARCHITECTURE](architecture/ARCHITECTURE.md), [RESPONSIBILITIES](architecture/RESPONSIBILITIES.md), [STARUML_STATUS](architecture/uml/STARUML_STATUS.md) |
| Contratos entre módulos | [INTEGRACION_EQUIPO](punto5/INTEGRACION_EQUIPO.md) |
| Pruebas, QA combinado y verificador | [TEST_STRATEGY](testing/TEST_STRATEGY.md), [TRACEABILITY_MATRIX](traceability/TRACEABILITY_MATRIX.md), [checklist Punto 5](punto5/CHECKLIST_PRUEBAS.md) |
| Recursos y licencias | [ASSET_POLICY](assets/ASSET_POLICY.md), [manifiesto](punto5/ASSETS_REGISTRO_SHA256.csv), [IA/derechos](punto5/USO_IA_Y_LICENCIAS.md) |
| Estado, fuentes y mantenimiento | [CURRENT_STATE](continuity/CURRENT_STATE.md), [SOURCE_REGISTRY](continuity/SOURCE_REGISTRY.md), [FRESHNESS_POLICY](continuity/FRESHNESS_POLICY.md), [TODO](../TODO.md) |
| Antecedentes técnicos | [BIBLIOGRAPHY](references/BIBLIOGRAPHY.md), [GITHUB_REFERENCES](references/GITHUB_REFERENCES.md), [HISTORICAL_EVIDENCE](testing/HISTORICAL_EVIDENCE.md) |

Los experimentos, checkpoints y cápsulas fechados conservan evidencia histórica. Sus comandos/prompts embebidos no autorizan tareas ni sustituyen la ruta principal. El candidato integral no representa toda la implementación; las mecánicas ausentes se localizan en TODO/OQ.

## Entrega y reconciliación académica — 29/10/2026

**Corte histórico:** las afirmaciones de implementación siguientes describen el 29/10; el runtime se integró posteriormente en el PR #17.

PR #11 y #12 están integrados; Fragile permanece no jugable. El contrato soportado se adoptó posteriormente por el responsable TXT en D-020; integración runtime y extensiones siguen pendientes. Para el **18/11/2026**, leer [entrega y demo](requirements/REQUIREMENTS.md#entrega-y-aceptación-docente--29102026), [fuentes Teams/ASR](continuity/SOURCE_REGISTRY.md#fuentes-académicas-aportadas--29102026), [brechas de patrones](architecture/ARCHITECTURE.md#patrones-y-brechas-de-defensa--29102026) y [ruta crítica](../TODO.md#ruta-crítica-hacia-el-1811). El código ofrece carga de definición experimental y contrato TXT; la integración runtime sigue pendiente. Para trabajo paralelo, consultar el [traspaso a Model/Movement](requirements/FRAGILE_TXT_CONTRACT_PROPOSAL.md#traspaso-txt-a-modelmovement--29102026).

## Integración grupal por ramas — 03/11/2026

<!-- I0_BRANCH_AWARE_CONTEXT_2026_11_03 -->

[Entrada de integración](integration/README.md) · [Plan I0–I4](integration/TEAM_INTEGRATION_PLAN.md) · [Criterios INT-01–10](integration/INTEGRATION_ACCEPTANCE.md) · [Frescura por rama](continuity/BRANCH_AWARE_FRESHNESS.md). Estas fuentes distinguen estado histórico/branch-only de código realmente integrado en `main`.
