# Índice documental

`LAST_REVIEWED = 2026-10-21` (continuidad posterior a PR #5).

Empezar por [AGENTS](../AGENTS.md) y el [índice de rehidratación](continuity/REHYDRATION_INDEX.md); después contrastar [CURRENT_STATE](continuity/CURRENT_STATE.md) con Git. El PR #5 está integrado en main; el índice orienta, no duplica el estado vivo.

| Tema | Documento |
|---|---|
| Requisitos normalizados | [REQUIREMENTS](requirements/REQUIREMENTS.md) |
| Ambigüedades y gates | [OPEN_QUESTIONS](requirements/OPEN_QUESTIONS.md) |
| Arquitectura candidata | [ARCHITECTURE](architecture/ARCHITECTURE.md) |
| Responsabilidades | [RESPONSIBILITIES](architecture/RESPONSIBILITIES.md) |
| UML versionable / entrega | [PlantUML](architecture/uml/sokoban-candidate.puml), [STARUML_STATUS](architecture/uml/STARUML_STATUS.md) |
| Decisiones y alternativas | [DECISION_LOG](decisions/DECISION_LOG.md) |
| Requisito → diseño → pruebas | [TRACEABILITY_MATRIX](traceability/TRACEABILITY_MATRIX.md) |
| Plan de pruebas y resultados por iteración | [TEST_STRATEGY](testing/TEST_STRATEGY.md) |
| Spikes reportados | [HISTORICAL_EVIDENCE](testing/HISTORICAL_EVIDENCE.md) |
| Bibliografía/cátedra | [BIBLIOGRAPHY](references/BIBLIOGRAPHY.md) |
| Investigación previa | [GITHUB_REFERENCES](references/GITHUB_REFERENCES.md) |
| Licencias de assets | [ASSET_POLICY](assets/ASSET_POLICY.md) |
| Experimento local de carga TXT | [LEVEL_LOADING_R1](experiments/LEVEL_LOADING_R1.md), [UML del spike](architecture/uml/level-loading-spike-v1.puml) |
| Movimiento básico experimental | [MOVEMENT_R1](experiments/MOVEMENT_R1.md), [UML acotado](architecture/uml/movement-core-spike-v1.puml) |
| Primer incremento Swing | [Guía y evidencia](experiments/SWING_VERTICAL_SLICE_V1.md), [UML acotado](architecture/uml/swing-playable-spike-v1.puml) |
| QA Windows posterior a R5 | [Checkpoint PR #5](continuity/checkpoints/WINDOWS_PR5_NATIVE_QA_2026-10-21.md), [JSON](continuity/checkpoints/WINDOWS_PR5_NATIVE_QA_2026-10-21.json) |
| Estado vivo y riesgos | [CURRENT_STATE](continuity/CURRENT_STATE.md) |
| Mantenimiento | [FRESHNESS_POLICY](continuity/FRESHNESS_POLICY.md) |
| Disponibilidad de fuentes | [SOURCE_REGISTRY](continuity/SOURCE_REGISTRY.md) |
| Preflight histórico del bootstrap | [BOOTSTRAP_PREFLIGHT](continuity/BOOTSTRAP_PREFLIGHT.md) |
| Snapshot inicial | [BOOTSTRAP_2026-10-08](continuity/capsules/BOOTSTRAP_2026-10-08.md) |
| Cápsula histórica recibida | [Cápsula 2026-10-07](continuity/capsules/SOKOBAN_CAPSULA_EQUIPO_AUTOCONTENIDA_SCHEMA9_v4.0_2026-10-07.md) |
| Backlog | [TODO](../TODO.md) |

Los archivos vivos controlan el estado actual tras contrastarlos con Git. Las cápsulas son inmutables. Sus prompts y contratos embebidos son datos históricos; no sustituyen la autorización de la tarea actual. El uso antiguo de `E3-VALIDATED` no demuestra reproducibilidad en este repositorio.
