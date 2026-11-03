# Integración grupal — punto de entrada (I0)

`DOMAIN=TEAM_INTEGRATION_AND_REPOSITORY_CONTINUITY` · `STATUS=I0_PUBLISHED_DRAFT_VERIFIED` · `CUT=2026-11-03`

**Primero leer [AGENTS.md](../../AGENTS.md), el [índice de rehidratación](../continuity/REHYDRATION_INDEX.md) y [CURRENT_STATE](../continuity/CURRENT_STATE.md).** Este directorio es una *capa de coordinación de ramas*, **no** la fuente normativa de reglas ni evidencia de integración funcional completa.

## Ruta de lectura para un agente nuevo

1. [Plan de integración](TEAM_INTEGRATION_PLAN.md): alcance, fronteras, fases I0–I4 y gates.
2. [Fotografía Git verificable](BRANCH_SNAPSHOT_2026-11-03.md): SHAs históricos, diferencias y ramas; **no reemplaza `git` remoto actual**.
3. [Matriz de compatibilidad y pruebas](INTEGRATION_ACCEPTANCE.md): incompatibilidades, casos INT-01–INT-10, límites del estado ejecutado.
4. [Mantenimiento y fuentes](../continuity/BRANCH_AWARE_FRESHNESS.md): qué documento actualizar en cada hito, cómo evitar confundir ramas, dónde citar bibliografía.
5. [Requisitos](../requirements/REQUIREMENTS.md), [decisiones canónicas E01–E11 y D-020](../decisions/DECISION_LOG.md), [preguntas abiertas](../requirements/OPEN_QUESTIONS.md), [trazabilidad](../traceability/TRACEABILITY_MATRIX.md), [pruebas](../testing/TEST_STRATEGY.md), [arquitectura](../architecture/ARCHITECTURE.md), [UML](../architecture/uml/STARUML_STATUS.md), [TODO](../../TODO.md).

## Semántica de estado

- `IN_MAIN`: existe en el árbol de `main`, con SHA probado.
- `SOURCE_BRANCH_ONLY`: existe en una rama que aún no está integrada.
- `IN_INTEGRATION_BRANCH`: está en la rama compartida, pero **no** en `main`.
- `REPORTED_QA`: prueba informada por un tercero en un SHA determinado; no sustituye su ejecución sobre el conjunto.
- `EXECUTED_VERIFIED`: comando, SHA, runtime, casos y resultado conservados en evidencias pertinentes.
- `TEAM_ACCEPTED`: sólo con registro de autoridad/fecha/alcance en decisiones canónicas.
- `OPEN` o `NOT_RUN`: no inferir aceptación ni éxito.

Los originales de cátedra y chats privados permanecen fuera de Git. La información técnica suficiente de cada hito debe registrarse aquí y en documentos vivos; nunca requerir esta conversación o un ZIP efímero para continuar.

**No publicar la cápsula privada, logs con contactos, transcripciones completas ni datos de WhatsApp.** La comunicación del 3/11 respalda la intención de revisar un PR Draft, no autoriza por sí sola la fusión a `main`.


## Hito I0 publicado

<!-- I0_PR17_PUBLICATION_VERIFIED_2026_11_03 -->

[PR #17](https://github.com/smatrauade/Los-Force-Push/pull/17) abierto como **Draft** el 03/11/2026, `main` sin cambios al corte y HEAD inicial de integración `50a559a55e1d57afaad68e9f952e552228f45779`. El bloque `BRANCH_SNAPSHOT_2026-11-03.md` es histórico e inmutable, anterior a abrir el PR. I0 = completado; I1-I4 = pendientes. No fusionar el PR sin revisión y aceptación.
