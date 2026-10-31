# Continuidad experimental — impaciencia

`AS_OF = 2026-10-31` · `DOMAIN = SOKOBAN_EXPERIMENTAL_BRANCH_CONTINUITY`

## Identidad y autoridad

`impaciencia` es una rama individual local para avanzar experimentalmente hacia el TPO Sokoban completo. Parte de `main` en `f3f91f92829b7e8e4bd3c0c07e3af4ffb0beb142`, árbol `830a86d24c8683f275e520d1fbe86a8f019b2b75`; ese commit integra el PR #15. El cierre Git histórico del 31/10 dejó un respaldo externo al árbol y esta rama local desde ese baseline. La existencia de ese respaldo no sustituye verificar Git. Obtener la rama y el HEAD vigentes con `git branch --show-current` y `git rev-parse HEAD`; consultar `git log --oneline` para los incrementos posteriores. La rama no reemplaza automáticamente el trabajo oficial ni implica aceptación del equipo.

[AGENTS](../../AGENTS.md) fija jerarquía de fuentes y reglas operativas; [REQUIREMENTS](../requirements/REQUIREMENTS.md) normaliza la consigna y aclaraciones con su [procedencia](SOURCE_REGISTRY.md); [DECISION_LOG](../decisions/DECISION_LOG.md) conserva las decisiones y [OPEN_QUESTIONS](../requirements/OPEN_QUESTIONS.md) sus pendientes. El [contrato TXT](../requirements/FRAGILE_TXT_CONTRACT_PROPOSAL.md) especifica el subconjunto adoptado. [CURRENT_STATE](CURRENT_STATE.md) registra capacidades y eventos oficiales; este documento sólo organiza el estado y la continuidad de la rama experimental. Ante discrepancias, contrastar fuentes y Git, sin convertir una hipótesis en autoridad.

## Estado y evidencia

R5 agrega `GameSession` individual con Memento, Undo 15/5/3 y Restart; Swing no la consume. [Decisiones y plan](IMPACIENCIA_MASTERPLAN.md) · [pruebas](../testing/TEST_STRATEGY.md).

R4 agrega carga externa C2 extendida individual hacia RuntimeState; [contrato y límites](../requirements/IMPACIENCIA_RUNTIME_TXT_CONTRACT.md). Swing sigue usando la ruta anterior y la extensión no ratifica OQ-08.

R3 agrega KeyBox, Lock y muros dinámicos al runtime individual con asociaciones y cierre atómico; no añade TXT ni integración Swing. [Decisiones y plan](IMPACIENCIA_MASTERPLAN.md); [pruebas](../testing/TEST_STRATEGY.md).

R2 implementa runtime inmutable individual y movimiento normal/Fragile con hielo programático; no integra Swing ni amplía TXT. Diseño, hipótesis, fases y criterios en [IMPACIENCIA_MASTERPLAN](IMPACIENCIA_MASTERPLAN.md); pruebas en [TEST_STRATEGY](../testing/TEST_STRATEGY.md).

- Heredado: carga externa legacy y C2-L7, Board inmutable, movimiento cardinal con una caja normal y prototipo Swing con HUD, sprites y audio. La ruta jugable rechaza entidades especiales. PR #15 integra `LevelLoader.loadExperimentalFragile(Path)`: definición inicial inmutable con ID, posición, resistencia y terreno, no un nivel jugable. Su plantilla sola no representa las cajas frágiles.
- Incrementos experimentales posteriores al baseline en R1: continuidad documental, sin capacidad de juego nueva. Para determinar incrementos posteriores, comparar `git log f3f91f9..HEAD` y `git diff f3f91f9..HEAD`, y contrastar implementación y pruebas efectivas.
- Incompletos en el juego integrado: conexión de hielo y Fragile a Swing; integración de KeyBox, Lock y muros dinámicos; integración Swing de sesión, Undo/Restart; victoria, progresión y score; integración de servicios Swing; dos adicionales elegibles; QA integral y entregables. La existencia de callbacks o recursos no demuestra mecánicas. `FULL_TPO = INCOMPLETE`; `TEAM_ACCEPTANCE = NOT_RECORDED`.
- QA Java de R1: `NOT_RUN`, por ser documental. QA histórica reportada sobre `38e5a2e0ce4dd4688c4eb903dea93a2a3bf54bd6`: Loading 40, C2 32, Fragile 41, Movement 31 y Desktop headless 16, 160 PASS; no demuestra gameplay completo ni aceptación. [Entorno, comandos y límites](../testing/TEST_STRATEGY.md#cierre-semántico-documental--29102026). Cada incremento futuro identifica su propia evidencia y SHA.
- Dependencias: contrato y firma real del consumidor Model/Movement, preservación de ID/resistencia/terreno, extensiones TXT y comportamiento no especificado; OQ-01/02/04/05/07/08/09/10/11 conservan detalles pendientes. Java/build global sigue abierto; derechos de recursos, StarUML, informe, video, revisión y defensa también requieren trabajo. El nivel ofrecido por un compañero no está verificado. Ver [TODO oficial](../../TODO.md) y [fuentes](SOURCE_REGISTRY.md).

## Gobernanza

Las reglas TEAM E01–E11 se heredan exclusivamente con el alcance y la procedencia de [DECISION_LOG](../decisions/DECISION_LOG.md#ratificación-por-mayoría--2026-10-23). E04 establece completar el deslizamiento antes de romper Fragile; su recuento 4/3 procede de la corrección del usuario, sin reinspección de capturas. E08 establece que sólo Restart o cambio de nivel reinician el límite de tres Undo. Ninguna de estas reglas demuestra implementación o aprobación docente adicional.

[D-020](../decisions/DECISION_LOG.md#d-020--decisión-del-responsable-txt--29102026) adopta por autoridad del responsable TXT el subconjunto soportado: OQ-06 resuelta para ese lector y OQ-08 parcialmente resuelta; las entidades futuras siguen abiertas. No es voto TEAM ni aprobación del código. Las decisiones individuales que una misión posterior necesite ensayar deben quedar como hipótesis reversibles y con límites explícitos; no se declara ratificada una decisión TEAM, académica, de arquitectura o de elegibilidad por implementarla aquí. No se resuelven OQ por inferencia ni se reabren votos por defecto.

## Backlog experimental propuesto

Este orden prepara misiones independientes; no autoriza su ejecución. Cada una requiere alcance vigente, preflight, contratos, pruebas pertinentes y revisión de solapamientos.

| Misión | Objetivo y dependencia principal |
|---|---|
| R2 | Evaluar contratos TXT → Model/Movement, consumidor real, preservación de datos y brechas antes de implementar. |
| R3 | Núcleo individual de KeyBox, Lock y muros dinámicos; hipótesis y Observer pendientes de integración. |
| R4 | Extensión TXT individual y adaptación a runtime implementadas; OQ-07/08 permanecen abiertas oficialmente. |
| R5 | Sesión individual, Undo 15/5/3 y Restart implementados; bordes oficiales y conexión Swing pendientes. |
| R6 | Victoria, progresión y score; respetar OQ-01/09 y no inventar fórmula oficial. |
| R7 | Integración Swing y HUD con servicios reales y estado completo. |
| R8 | Dos adicionales elegibles, con selección y patrones justificados; OQ-10 requiere confirmación. |
| R9 | QA integral y entregables en misiones independientes: pruebas y niveles; QA visual/sonora y licencias; StarUML e informe; video, README y ensayo de defensa. |

## Sincronización con main

Antes de cada incremento importante, comprobar estado y HEAD local, consultar el SHA de `main` remoto y PRs relevantes en lectura, y comparar con el baseline o último punto común mediante `git merge-base`, `git log` y `git diff`. Una referencia remota local puede estar desactualizada; verificarla contra el remoto cuando sea posible. Si no se puede verificar o hay cambios no reconciliados, detener el incremento afectado.

Analizar diferencias por contratos, invariantes, comportamiento, pruebas y decisiones, además de archivos: cambios en rutas distintas pueden solaparse semánticamente. Identificar avances de compañeros y sus dependencias antes de incorporarlos; no sobrescribirlos. No hacer merge ni rebase automático. Cualquier incorporación requiere alcance y autorización propios, base exacta, análisis de solapamiento y validación pertinente.

## Rehidratación y próximo gate

Para un agente nuevo: [AGENTS](../../AGENTS.md) → [REHYDRATION_INDEX](REHYDRATION_INDEX.md) → este estado → fuentes normativas y decisiones vinculadas → código y pruebas del dominio autorizado. Completar las once lecturas íntegras obligatorias de AGENTS, verificar Git/GitHub y aplicar [FRESHNESS_POLICY](FRESHNESS_POLICY.md). Las cápsulas son historia, no autorización; no se necesita este chat ni archivos temporales para reconstruir la continuidad.

R3 extiende el consumidor individual con mecánicas dinámicas, sin incorporar el trabajo de compañeros. R4 integra la carga externa individual con ese runtime; la siguiente misión requiere autorización y comparación con main, y no se inicia automáticamente.
