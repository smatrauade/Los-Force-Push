# Índice de rehidratación

AS_OF = 2026-10-29. Leer primero [AGENTS](../../AGENTS.md); luego seguir esta ruta y contrastar Git/GitHub. [CURRENT_STATE](CURRENT_STATE.md) registra eventos y capacidades, no un HEAD permanente. PR #8 (C2-L7), #9 (Punto 5), #10 (continuidad) y #11 (Fragile experimental), además de #12 (contrato candidato no ratificado) y [#13](https://github.com/smatrauade/Los-Force-Push/pull/13) (reconciliación académica y política de continuidad), están integrados.

## Ruta principal

| Orden | Pregunta | Fuente |
|---|---|---|
| 1 | ¿Qué está integrado y cómo se ejecuta? | [CURRENT_STATE](CURRENT_STATE.md), [README](../../README.md) |
| 2 | ¿Qué exige la consigna y qué sigue abierto? | [REQUIREMENTS](../requirements/REQUIREMENTS.md), [OPEN_QUESTIONS](../requirements/OPEN_QUESTIONS.md) |
| 3 | ¿Qué decidió el equipo? | [DECISION_LOG](../decisions/DECISION_LOG.md#ratificación-por-mayoría--2026-10-23); único registro E01–E11 |
| 4 | ¿Qué está implementado frente a propuesto? | [ARCHITECTURE](../architecture/ARCHITECTURE.md), [RESPONSIBILITIES](../architecture/RESPONSIBILITIES.md), [STARUML_STATUS](../architecture/uml/STARUML_STATUS.md) |
| 5 | ¿Dónde están código y recursos? | [Loading/C2 y prototipo Fragile](../../src/main/java/ar/edu/uade/sokoban/level), [Movement](../../src/main/java/ar/edu/uade/sokoban/movement), [Swing](../../src/desktop/java/ar/edu/uade/sokoban/desktop), [recursos desktop](../../src/desktop/resources) |
| 6 | ¿Cómo se integran los módulos? | [INTEGRACION_EQUIPO](../punto5/INTEGRACION_EQUIPO.md), [guía desde clon](../punto5/GUIA_INSTALACION_GITHUB_DESKTOP.md) |
| 7 | ¿Dónde están pruebas y evidencia? | [TEST_STRATEGY](../testing/TEST_STRATEGY.md), [tests core](../../src/test/java), [tests desktop](../../src/desktop-test/java), [TRACEABILITY_MATRIX](../traceability/TRACEABILITY_MATRIX.md) |
| 8 | ¿Qué recursos existen y qué derechos faltan? | [Manifiesto de 43 recursos](../punto5/ASSETS_REGISTRO_SHA256.csv), [ASSET_POLICY](../assets/ASSET_POLICY.md), [IA/licencias](../punto5/USO_IA_Y_LICENCIAS.md) |
| 9 | ¿Qué sigue y cómo mantener la información? | [TODO](../../TODO.md), [FRESHNESS_POLICY](FRESHNESS_POLICY.md), [SOURCE_REGISTRY](SOURCE_REGISTRY.md) |
| 10 | ¿Qué contrato TXT se propone para Fragile y qué falta ratificar? | [FRAGILE_TXT_CONTRACT_PROPOSAL](../requirements/FRAGILE_TXT_CONTRACT_PROPOSAL.md), [OPEN_QUESTIONS](../requirements/OPEN_QUESTIONS.md) |

Plan de TXT/niveles y promoción única, con checkpoints históricos separados del estado técnico: [TXT_LEVELS_EXECUTION_PLAN](../requirements/TXT_LEVELS_EXECUTION_PLAN.md). Define gates controlables, una sola promoción y revisión de frescura previa al merge. No ratifica sintaxis ni reemplaza decisiones del equipo.

El código presente expone `LevelLoader.loadExperimentalFragile(Path)` para leer la definición con UTF-8 estricto y límite de bytes compartidos. Leer el [diseño G2 y estado G3](../requirements/TXT_LEVELS_EXECUTION_PLAN.md#g2--decisión-técnica-del-incremento-29102026) y la [evidencia del incremento](../testing/TEST_STRATEGY.md#incremento-filesystem-fragile--29102026). No atribuir su publicación o integración a una fecha: comprobar Git/PR.

El prototipo ExperimentalFragileParser representa atributos individuales en una definición inmutable separada; el C2 jugable acepta sólo las siete combinaciones actuales y rechaza atributos/especiales. El siguiente dominio TXT/niveles requiere revisar el contrato y su integración futura con el equipo. E01/E02 no fijan sintaxis ni rangos. E04/E08 requieren revisión normativa. Java/build, varios contratos y TEAM_ACCEPTANCE del código siguen pendientes según sus fuentes canónicas.

## Procedencia y evidencia histórica

- QA combinado del 28/10, anterior a este mantenimiento: árbol 1796932307df319e3636daee942cf49920b4eb05, Windows/Temurin 21.0.12.1/release 17. Loading 40, Movement 31, C2 32, Desktop 16, Assets 44, UI nativa automatizada 17, CLI 18 y hashes 43: PASS. [Comandos, alcance y límites](../testing/TEST_STRATEGY.md).
- QA Windows PR #5 del 21/10: [checkpoint](checkpoints/WINDOWS_PR5_NATIVE_QA_2026-10-21.md) y [JSON](checkpoints/WINDOWS_PR5_NATIVE_QA_2026-10-21.json). Sus 48 artefactos originales no están en Git. Draft/premerge describe aquel corte.
- Linux R2/R3 y pruebas previas: [TEST_STRATEGY](../testing/TEST_STRATEGY.md) e informes fechados de [Loading](../experiments/LEVEL_LOADING_R1.md), [Movement](../experiments/MOVEMENT_R1.md) y [Swing V1](../experiments/SWING_VERTICAL_SLICE_V1.md). No son ejecuciones actuales.
- A/B: [HISTORICAL_EVIDENCE](../testing/HISTORICAL_EVIDENCE.md), sin fuentes Java recuperadas; no certifican mecánicas del producto actual.
- Consigna, materiales de cátedra, libros y transcripciones: [SOURCE_REGISTRY](SOURCE_REGISTRY.md), [BIBLIOGRAPHY](../references/BIBLIOGRAPHY.md) y [referencias GitHub históricas](../references/GITHUB_REFERENCES.md). Un clon no contiene todos sus originales.
- Encuestas del 23/10: transcripción aportada por el solicitante, originales privados externos; no afirmar auditoría independiente de capturas.

Las cápsulas y checkpoints se consultan sólo para historia y procedencia; no forman parte de las instrucciones principales. No heredan permisos ni representan HEAD. Sus bytes permanecen inmutables. No afirmar QA manual nueva, salida audible ni validación macOS por el smoke automatizado.

## Comprobar frescura

Consultar git status, git rev-parse HEAD, historial, HEAD remoto y PRs antes de actuar. Los commits citados identifican integraciones/pruebas históricas. Las ramas remotas fusionadas pueden desaparecer sin perder esos commits. El verificador documental requiere un checkout Git; no ejecutarlo desde una exportación sin .git.

Aplicar [FRESHNESS_POLICY](FRESHNESS_POLICY.md): registrar cambios vivos afectados, ejecución nueva frente a histórica y siguiente gate. No crear otra cápsula ni reabrir acuerdos por defecto. StarUML final, licencias, mecánicas restantes y aceptación del equipo siguen pendientes.

## Entrega y siguiente gate

Para el **18/11/2026**, leer [entrega y aceptación docente](../requirements/REQUIREMENTS.md#entrega-y-aceptación-docente--29102026), [fuentes Teams/ASR](SOURCE_REGISTRY.md#fuentes-académicas-aportadas--29102026), [prioridades por módulo](../../TODO.md#ruta-crítica-hacia-el-1811) y [patrones reales frente a candidatos](../architecture/ARCHITECTURE.md#patrones-y-brechas-de-defensa--29102026). Demo de 15–20 minutos; no mostrar diagramas no elimina StarUML. R6 completa; el alcance autónomo TXT de R7 ofrece carga de definición experimental y contrato, sin gameplay. Antes de integración runtime revisar contrato, dependencias OQ y autorización específica; este índice no es una orden de implementación.
