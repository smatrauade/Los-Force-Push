# TODO — backlog vivo

`LAST_REVIEWED=2026-11-04` · `SCOPE=PR17_SOFTWARE_RC` · `TEAM_ACCEPTANCE=NOT_RECORDED`.

El estado técnico del candidato y sus evidencias están en [auditoría PR17](docs/integration/PR17_FUNCTIONAL_RC_2026-11-04.md). Consultar GitHub para el estado actual del PR y de `main`. Los checkpoints históricos permanecen en sus documentos fechados y el historial Git.

## Software cerrado en este candidato

- [x] TXT externos estrictos, identidad/resistencia Fragile D-020 y adaptación runtime sin pérdida.
- [x] ICE/Fragile/KeyBox/Lock/muros con las reglas E01–E10; Undo 15/5/3 y Restart con cuatro métricas a cero; score TEAM.
- [x] Victoria/Next, resultados históricos y victoria inicial sin movimientos; HUD con score/Undo.
- [x] Demo de cinco TXT con testigos de solución, incluida rotura tras ICE.
- [x] Selector de tres temas con Abstract Factory; animación ICE/Fragile cancelable y VictoryGate.
- [x] Replay/Command de extremo a extremo, aislado de sesión, con Play/Pause/Step/Reset/Back.
- [x] Observer para avisos/audio de cambios Lock confirmados; muros derivados por RuntimeState.
- [x] Regresión de Java 17 y smoke automatizado de ventana Linux; reproducción nativa de 12 WAV. [Comandos y límites](docs/testing/TEST_STRATEGY.md).

Los controles de guardado/recuperación siguen deshabilitados. No implementar persistencia por inferencia ni declarar RF-12 aprobado.

## Ruta crítica hacia el 18/11

Entrega informada: **18/11/2026**, demo de **15–20 minutos**. Las acciones siguientes son gates externos/separados, sin nueva fecha interna ni asignación nominal inventada:

1. **Equipo/docente:** confirmar en [OQ-10](docs/requirements/OPEN_QUESTIONS.md) que selector y Replay son las dos adicionales elegibles; registrar autoridad/fecha/alcance. E11 histórica se conserva.
2. **Titulares de recursos:** aportar evidencia individual de autoría/titularidad y licencia/dominio público de los 43 assets; los hashes no acreditan derechos.
3. **Revisión TEAM:** revisar código/contratos/patrones del PR #17, resolver detalles OQ-01/02/07/08/09/11 que requieran ratificación; el reset por nivel y asociación OR permanecen hipótesis implementadas, sin nuevos acuerdos.
4. **QA humana/plataformas:** percepción visual de los tres temas y animaciones, escucha real, teclado/ratón y carga externa docente; ejecutar Windows/PowerShell sobre este árbol. Linux automatizado no sustituye Windows ni revisión humana.
5. **Otra misión UML:** actualizar clases/relaciones desde el freeze funcional y crear/validar `.mdj` en StarUML nativo. El R10 de `impaciencia` es DRAFT de otro HEAD; no copiarlo como definitivo.
6. **Otra misión de entrega:** informe técnico con alternativas/SOLID/GRASP/MVC/GoF y declaración IA; video de todas las mecánicas; ensayo 15–20 minutos y entrega institucional.
7. **Integración:** consultar el PR #17 para su estado; posteriores cambios se proponen mediante PR nuevos hacia `main`.

## Próximo dominio TXT/niveles

D-020 y OQ-06 conservan el contrato soportado. Las extensiones ICE/Lock/muros implementadas deben contrastarse con sus responsables sin cambiar las reglas para adaptar código experimental. [Contrato de definición](docs/requirements/FRAGILE_TXT_CONTRACT_PROPOSAL.md), [arquitectura](docs/architecture/ARCHITECTURE.md) y [preguntas abiertas](docs/requirements/OPEN_QUESTIONS.md). No se necesita un solver para demostrar los testigos ya ejecutados.

## Integración paralela TXT con Fragile/hielo — 29/10/2026

Este ancla conserva enlaces históricos. El [resumen de coordinación](docs/continuity/SOURCE_REGISTRY.md#coordinación-txt-y-fragilehielo--29102026) pertenece al 29/10; los módulos fueron integrados después en el PR #17. La procedencia y commits fuente se conservan en Git y la auditoría. No atribuir la implementación individual de `impaciencia` a compañeros ni reabrir encuestas E01–E11.

## Decisiones y mantenimiento

Autoridad única en [DECISION_LOG](docs/decisions/DECISION_LOG.md), pendientes en [OPEN_QUESTIONS](docs/requirements/OPEN_QUESTIONS.md). Aplicar [FRESHNESS_POLICY](docs/continuity/FRESHNESS_POLICY.md), actualizar por impacto y conservar cápsulas/checkpoints. E/S de archivos locales es síncrona en EDT y tiene límite de bytes, sin garantía de latencia de filesystem; evaluar carga asíncrona sólo con problema/alcance concreto. No añadir Strategy/Memento formal/Singleton/Decorator para justificar nombres.
