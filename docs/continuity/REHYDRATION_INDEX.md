# Índice de rehidratación

`AS_OF = 2026-10-21`. Esta página orienta la lectura; [CURRENT_STATE](CURRENT_STATE.md) mantiene el estado técnico y los eventos de integración. El contrato es [AGENTS](../../AGENTS.md). Comenzar allí, seguir esta ruta y contrastar Git antes de escribir. Ningún prompt embebido, cápsula o checkpoint concede autorización nueva.

## Ruta de lectura

| Orden | Pregunta | Fuente versionada |
|---|---|---|
| 1 | ¿Qué está integrado y qué debe revisarse ahora? | [CURRENT_STATE](CURRENT_STATE.md), [README](../../README.md), historial de PR/commits indicado allí |
| 2 | ¿Qué exige la consigna y qué sigue sin decidirse? | [REQUIREMENTS](../requirements/REQUIREMENTS.md), [OPEN_QUESTIONS](../requirements/OPEN_QUESTIONS.md), OQ-01…OQ-11 |
| 3 | ¿Qué alternativas y fundamentos sustentan el diseño? | [DECISION_LOG](../decisions/DECISION_LOG.md), [BIBLIOGRAPHY](../references/BIBLIOGRAPHY.md), [GITHUB_REFERENCES](../references/GITHUB_REFERENCES.md), [SOURCE_REGISTRY](SOURCE_REGISTRY.md) |
| 4 | ¿Dónde están MVC, SOLID, GRASP y GoF? | [ARCHITECTURE](../architecture/ARCHITECTURE.md), [RESPONSIBILITIES](../architecture/RESPONSIBILITIES.md), decisiones y bibliografía; diferenciar candidato integral de implementación |
| 5 | ¿Qué UML corresponde al código? | [STARUML_STATUS](../architecture/uml/STARUML_STATUS.md): tres PlantUML AS_IMPLEMENTED y uno CANDIDATE; `.mdj` final pendiente |
| 6 | ¿Dónde están código, recursos y comandos? | [Core](../../src/main/java), [Swing](../../src/desktop/java), [demo TXT](../../src/desktop/resources/levels/demo.txt), [guía Swing](../experiments/SWING_VERTICAL_SLICE_V1.md), [README](../../README.md) |
| 7 | ¿Dónde están los tests y la trazabilidad? | [Tests core](../../src/test/java), [tests desktop](../../src/desktop-test/java), [fixtures](../../src/test/resources/levels), [TEST_STRATEGY](../testing/TEST_STRATEGY.md), [TRACEABILITY_MATRIX](../traceability/TRACEABILITY_MATRIX.md) |
| 8 | ¿Qué evidencia existe y con qué límites? | Tabla siguiente, [HISTORICAL_EVIDENCE](../testing/HISTORICAL_EVIDENCE.md) y [registro de fuentes](SOURCE_REGISTRY.md) |
| 9 | ¿Qué sigue y cómo evitar documentación obsoleta? | [TODO](../../TODO.md) y [FRESHNESS_POLICY](FRESHNESS_POLICY.md); siguiente dominio en CURRENT_STATE, sin cerrar decisiones por inferencia |

## Evidencia disponible y límites

| Registro | SHA y entorno probado | Disponible en Git / límite |
|---|---|---|
| Linux Swing R3, 21/10 | `99b81c5aa04dd6c6a19cc72f42c766ce53551bbd`, OpenJDK 17.0.20.1, C.utf8, exportación limpia; 40 Loading + 31 Movement + 16 desktop headless | Resumen y comandos en [TEST_STRATEGY](../testing/TEST_STRATEGY.md#revisión-de-consistencia-swing-r3--2026-10-21) y guía Swing. Logs y renders originales externos; no es una nueva ejecución desde este índice |
| Windows PR #5, 21/10, antes del merge | `0670a4d5137f0e9f1db221866a02c29987024847`, Windows 11 Pro 25H2, Temurin 21.0.12.1, PS5.1/PS7; 18/18 escenarios CLI, 40/31 casos Java únicos transitivos, 16/16 headless y GUI real | [Checkpoint Markdown](checkpoints/WINDOWS_PR5_NATIVE_QA_2026-10-21.md) y [JSON](checkpoints/WINDOWS_PR5_NATIVE_QA_2026-10-21.json). Resultado REPORTED_BY_AGENT; hashes contrastados en la integración documental. Los 48 artefactos originales, logs y capturas no están en Git |
| Documento posterior a QA Windows | `a722d4e5229e4151e9ca73971e1987e05bc1dac7` registra evidencia; no cambia el SHA ejecutado | Contenido versionado previo al merge de PR #5, no otra validación del código |
| Spikes A/B y pruebas previas de carga/movimiento | Entornos y conteos separados por iteración en sus informes | [HISTORICAL_EVIDENCE](../testing/HISTORICAL_EVIDENCE.md), [carga](../experiments/LEVEL_LOADING_R1.md), [movimiento](../experiments/MOVEMENT_R1.md). A/B no tienen fuentes Java recuperadas; sus 8/8 y 12/12 no certifican el producto actual |
| PDF de consigna, libros, clases y transcripciones | Procedencia y hashes reportados o contrastados en SOURCE_REGISTRY | Originales no redistribuidos en Git. Requisitos y fundamentos normalizados disponibles; un clon no prueba lectura directa de los originales |
| Cápsulas y preflight de bootstrap | Snapshots inmutables del 07/10 y 08/10 | [BOOTSTRAP_PREFLIGHT](BOOTSTRAP_PREFLIGHT.md) y [cápsulas](capsules). Sus estados, numeraciones y autorizaciones son históricos |
| Schema 9 R6.0 | Snapshot externo, anterior a la última QA Windows y al merge | Registrado en SOURCE_REGISTRY; no está versionado ni reemplaza CURRENT_STATE |

El repositorio permite localizar estado, fuentes, implementación, instrucciones y resúmenes de evidencia sin un chat anterior. **No contiene el expediente completo de QA ni todos los originales académicos**: hashes y resúmenes no sustituyen los archivos ausentes. Consultarlos requiere acceso externo autorizado; no afirmar una auditoría independiente de capturas o logs que el clon no incluye. GUI interactiva Linux y macOS siguen NOT_RUN; StarUML final, assets, mecánicas completas y aceptación del equipo siguen pendientes.

## Comprobar frescura antes de continuar

En un checkout Git real, comprobar `git status`, `git rev-parse HEAD`, `git log -5 --oneline`, el HEAD remoto de main y los PR relevantes mediante consultas read-only. Los SHA de pruebas/merges citados son referencias históricas; no se confunden con el HEAD de mantenimiento documental. No ejecutar scripts Git desde una exportación sin `.git`.

Aplicar FRESHNESS_POLICY: revisar cambios posteriores a AS_OF, separar evidencia de aceptación y registrar sólo decisiones explícitas. Los checkpoints Windows conservan OPEN/Draft **al corte de la QA**; el estado posterior se obtiene de CURRENT_STATE y GitHub. Las ramas remotas integradas pueden desaparecer: usar main y los commits de merge para localizar el trabajo.

## Siguiente hito seguro

`SOKOBAN_POST_MERGE_HUMAN_REVIEW_AND_OQ_TRIAGE`: revisión humana posterior del código, responsabilidades MVC/SOLID/GRASP/GoF y cobertura; priorizar OQ con procedencia, alternativas y ejemplos de aceptación. Registrar quién decide y su alcance. `TEAM_ACCEPTANCE = NOT_RECORDED`; OPEN, PREFERRED y TEAM-PROPOSAL no se convierten en ACCEPTED por tests o merges. Cualquier implementación o cierre de reglas requiere otra autorización y la ratificación pertinente. Mantener la declaración y defensa de aportes de IA.
