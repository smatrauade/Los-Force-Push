# Integración grupal — punto de entrada (I0, I1, I2 e I3 local)

## RC funcional — 04/11/2026

[Auditoría funcional PR17](PR17_FUNCTIONAL_RC_2026-11-04.md): diagnóstico previo, Replay conectado y pruebas nuevas, incrementos de hardening y gates de publicación. Las notas I3 local siguientes son históricas; Git define qué está publicado.

`DOMAIN=TEAM_INTEGRATION_AND_REPOSITORY_CONTINUITY` · `STATUS=I0_I1_I2_PUBLISHED_PR17_DRAFT_I3_LOCAL` · `CUT=2026-11-04`

## I3 local — niveles distribuidos, selector visual y animación ICE/Fragile (04/11/2026)

Además de I2 publicado, el árbol local incorpora I3.A (`ShippedLevelsIntegrationTest`, aceptación headless de `demo.txt`/`campaign-b.txt`, `fragile_demo.txt` e `ice_demo.txt`), I3.B (selector `ORIGINAL COBRE`/`NOCTURNO`/`JADE` con Abstract Factory de vista, `VisualThemeTest` 7 escenarios) e I3.C (animación de presentación desde `RuntimeMoveResult` con `BoxAnimation`, frames deterministas de ICE/Fragile, diferimiento de FINISH y cancelación segura, `AnimationPresentationTest` 14 escenarios). La regresión completa del árbol es Undo 58, core 258, Desktop 40, Campaña 41, Tema 7, Animación 14 y Assets 44 PASS con `scripts/test-integration-i2.sh`. `GUI_NATIVE_QA=NOT_RUN`, `TEAM_ACCEPTANCE=NOT_RECORDED`; INT-09 cuenta solo con evidencia headless y el selector es candidato a adicional RF-12/OQ-10 sin cerrar su elegibilidad. Sin publicación ni merge.

## Checkpoint vigente — I2 publicado en PR #17 Draft (04/11/2026)

I1 + I2 (ICE/Fragile, KeyBox/Lock/Wall, Undo 15/5/3, Restart, score y progresión) y el hotfix de carga `EXPERIMENTAL_FRAGILE` están en `integration/tpo-unificado-2026-11-03` (hito publicado `4d6cb612b38e5ebdb6869bb990301045044e863f`, no SHA permanente; verificar GitHub). Evidencia local reportada: Ubuntu OpenJDK 17.0.20.1, `scripts/test-integration-i2.sh` exit 0; Undo 58, core 258/258, Desktop 40/40, assets 44/44; verificador documental PASS. Sin CI registrada ni retest nativo manual posterior al hotfix. PR Draft, sin merge a `main` ni revisión TEAM (`TEAM_ACCEPTANCE=NOT_RECORDED`). Pendientes: QA GUI, I3/I4, StarUML, licencias, extras RF-12 y entrega. El reset al cambiar de nivel sigue provisional. Los estados «local», «sin publicar» y conteos antiguos de los apartados fechados posteriores son históricos.

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

[PR #17](https://github.com/smatrauade/Los-Force-Push/pull/17) abierto como **Draft** el 03/11/2026, `main` sin cambios al corte y HEAD inicial de integración `50a559a55e1d57afaad68e9f952e552228f45779`. El bloque `BRANCH_SNAPSHOT_2026-11-03.md` es histórico e inmutable, anterior a abrir el PR. I0 completado al corte histórico; I1 se publicó posteriormente, I2-I4 siguen pendientes. No fusionar el PR sin revisión y aceptación.

## I1 — publicado en el PR Draft #17

La reconciliación I1.A/I1.B0/I1.B1 y el testigo I1.C quedaron incorporados mediante merge de dos padres `6abb8ad003ad9af6642fc1a1e11a91f5d238f2b2` y publicados en el **mismo PR #17 Draft**. Consultar [evidencia y límites I1](I1_LOCAL_VALIDATION_2026-11-03.md), y ejecutar `bash scripts/test-integration-i1.sh` antes de cualquier commit. El PR #17 continúa siendo el único vehículo previsto para la integración grupal; este documento no autoriza promoverlo.

<!-- I1_PR17_PUBLICATION_2026_11_03 -->

### Validación y límites actuales

Pruebas locales previas al commit: **316/316 PASS** (JDK17, core 256, Desktop headless 16, assets 44), blobs auditados y ascendencia preservada. `main` permanece sin modificar. No se afirma reejecución tras la publicación, GUI interactiva ni aceptación TEAM. I2 Undo/Restart/Score, I3 GUI y I4 QA/entrega siguen abiertos. [Evidencia técnica](I1_LOCAL_VALIDATION_2026-11-03.md).
