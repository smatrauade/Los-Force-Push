# TODO — backlog vivo

Revisado 29/10/2026. [Estado integrado y registro de merges](docs/continuity/CURRENT_STATE.md). TEAM_ACCEPTANCE = NOT_RECORDED. Las prioridades son planificación propuesta por módulo, no nuevas decisiones de reglas ni asignaciones personales.

## Base disponible

| Estado | Alcance | Evidencia y límite |
|---|---|---|
| IMPLEMENTED_VERIFIED / PARTIAL | UTF-8 externo, legacy/C2-L7, Board inmutable y LevelBuilder concreto | Código integrado; Loading 40/C2 32 históricos; especiales jugables rechazados |
| IMPLEMENTED_VERIFIED / PARTIAL | Movement cardinal y caja normal | Movement 31 histórico; hielo/Fragile/KeyBox/Lock/muros dinámicos ausentes |
| IMPLEMENTED_VERIFIED / PARTIAL | Swing pixel art, menú/HUD/pausa/opciones/zoom, sprites/SFX | Desktop 16, Assets 44, UI 17 y CLI 18 históricos; callbacks pendientes no son servicios |
| EXPERIMENTAL_NOT_RATIFIED | Definición Fragile inmutable con identidad/resistencia; contrato candidato #12 | Fragile 31 histórico; no gameplay, OQ-06/OQ-08 pendientes |
| PARTIAL | 31 PNG (29 base + dos opcionales) y 12 WAV versionados | Manifiesto disponible; revisión de derechos pendiente |

IMPLEMENTED_VERIFIED describe presencia contrastada en código; no una nueva ejecución funcional. [Comandos, fechas y límites de QA](docs/testing/TEST_STRATEGY.md).

## Ruta crítica hacia el 18/11

Entrega docente: **miércoles 18/11/2026**, demo en vivo **15–20 minutos** y correo formal. [Protocolo y entregables](docs/requirements/REQUIREMENTS.md#entrega-y-aceptación-docente--29102026). P0 bloquea cierre funcional o entrega; P1 integra y comprueba; P2 mejora fuera de ruta crítica. Las fechas internas siguientes son **objetivos propuestos, no acuerdos ni plazos docentes adicionales**; revisar viabilidad con el equipo ante cada incremento.

| Prioridad / objetivo interno | Estado y trabajo | Responsable por módulo | Dependencia | Criterio de aceptación / siguiente prueba |
|---|---|---|---|---|
| P0 / 02/11 | TEAM_DECISION_REQUIRED: completar detalles TXT y Java/build | TXT/niveles e integración | OQ-06/08/11 | Registrar alcance ratificado y ejemplos válidos/inválidos; preservar E01/E02 y legacy/C2 |
| P0 / 02/11 | BLOCKED_BY_OQ: compatibilidad Fragile/hielo y límite Undo | Reglas/Movement y sesión | OQ-03/04, consigna y E04/E08 | Casos antes/después sin reinterpretar votos; consulta docente si persiste conflicto |
| P0 / 06/11 | NOT_IMPLEMENTED: hielo, Fragile jugable, KeyBox/Lock y muros dinámicos | Model/Movement + TXT | OQ-01/02/06/07/08 | Preservar identidad/resistencia y terrenos; casos de bordes, cierre ocupado y rechazo atómico |
| P0 / 06/11 | NOT_IMPLEMENTED: Observer con candidato Lock→Walls | Model/arquitectura | Indicación ASR docente, D-010 y OQ-07 | Roles y notificaciones a asociados, aislamiento entre grupos y restauración probados |
| P0 / 09/11 | NOT_IMPLEMENTED: Undo/Restart y métricas | Sesión/score | OQ-04/05/09; estado especial | Historial 15, restaurar bloques 5, límites 3 y bordes 4/5; justificar Command o Memento |
| P0 / 09/11 | NOT_IMPLEMENTED: victoria/progresión y score | Sesión/niveles/score | OQ-01/09, fórmula del equipo | Goal/KeyBox y progresión probados; LevelResult recibe score real; Strategy justificada |
| P0 / 10/11 | TEAM_DECISION_REQUIRED: confirmar dos adicionales elegibles y su selección | Equipo de producto/arquitectura e interfaz | OQ-10; reporte docente indirecto y E11 histórica | No computar guardar automáticamente; selector candidato con elegibilidad reportada, sin mayoría ni implementación verificadas. Confirmar dos extras y demostrar patrón real, sin imponer Strategy/Abstract Factory |
| P1 / 12/11 | PARTIAL: conectar servicios a UiCommands/renderer | Swing e integración | Servicios operativos y DTO acordado | Disponibilidad real de botones; render de especiales, sin reglas ni resistencia inferida desde sprites |
| P1 / 13/11 | NOT_IMPLEMENTED: aceptación integral de niveles propios | TXT/QA/Movement | Contrato y mecánicas integrados | TXT externo editado→Board→UI; error conserva estado; cada puzzle tiene secuencia solución y resistencia coherente |
| P0 / 13/11 | DELIVERY_REQUIRED: revisión de derechos de assets | Recursos/equipo | Titularidad y política de dominio público | Registro individual verificable; no inferir CC0 de hash u origen IA |
| P0 / 15/11 | DELIVERY_REQUIRED: StarUML, informe técnico, README reproducible y video | Arquitectura/documentación/integración | Código entregado y patrones defendibles | .mdj abre y corresponde al código; informe justifica alternativas/SOLID/GRASP/MVC/GoF y declara IA; video/README reproducibles |
| P1 / 16/11 | DELIVERY_REQUIRED: QA manual y ensayo de defensa | QA y todos los módulos | Build final, fixtures resolubles | Demo 15–20 min con funcionalidades efectivas y explicación de patrones; escucha de audio real; cada integrante comprende su trabajo |
| P0 / 18/11 | DELIVERY_REQUIRED: presentación y correo | Coordinación de entrega | Todos los entregables revisados | Un integrante envía a jpepe@uade.edu.ar con copia al resto, nombre del equipo, apellido/nombre de todos y enlace público |
| P2 / sin compromiso preentrega | NOT_IMPLEMENTED: mejoras de E/S cancelable y QA macOS pendiente | Integración/Swing/QA | Necesidad y entorno disponibles | Autorización y prueba específica; no extrapolar del smoke Windows |
| P2 / posterior a entrega | NOT_IMPLEMENTED: solver/generación opcional | TXT/niveles | Fuera de consigna y ruta crítica | Evaluar sólo con alcance propio; no dependencia para validar puzzles manualmente |

## Próximo dominio TXT/niveles

R6 está completa por PR #12. **R7 está preparada, NO ejecutada**. Su gate previo es revisar alcance autorizado contra ExperimentalFragileParser/ExperimentalFragileLevel ya existentes y la [propuesta TXT](docs/requirements/FRAGILE_TXT_CONTRACT_PROPOSAL.md), sin duplicar modelos ni presentar hipótesis como decisiones. Acordar dependencias e interfaz con Model/Movement antes de carga jugable; no habilitar especiales hasta preservar identidad/resistencia. Esta misión documental no ejecuta ni autoriza R7.

## Decisiones y mantenimiento

E01–E11 se conservan únicamente en [DECISION_LOG](docs/decisions/DECISION_LOG.md); pendientes en [OPEN_QUESTIONS](docs/requirements/OPEN_QUESTIONS.md). No reabrir acuerdos por defecto. E04/E08 siguen CONFLICT_REVIEW_REQUIRED; Java/build OPEN. Revisar patrones por intención, roles reales y alternativa; SoundManager no es Singleton, callbacks no acreditan Command y el DTO LevelResult no calcula score.

Mantener [arquitectura](docs/architecture/ARCHITECTURE.md), UML implementado y candidato separados; actualizar diagramas en misión autorizada, sin convertirlos en evidencia de clases ausentes. [Derechos y assets](docs/assets/ASSET_POLICY.md), [contratos Punto 5](docs/punto5/INTEGRACION_EQUIPO.md) y [FRESHNESS_POLICY](docs/continuity/FRESHNESS_POLICY.md) siguen vigentes. Revisar humanos/código, pruebas y documentos en cada incremento; conservar cápsulas y checkpoints históricos. Este backlog no concede permiso de implementación o publicación.

## Contexto grupal y tareas de adicionales — complemento 29/10/2026

[Fuente y límites](docs/continuity/SOURCE_REGISTRY.md#contexto-grupal-privado--complemento-29102026). Las cinco áreas del 24/10 fueron una propuesta organizativa, con un ofrecimiento para Undo/Restart/puntaje; no hay asignación nominal completa. Los responsables por módulo de la tabla son planificación, no atribuciones personales. La consulta actual a GitHub no encontró Issues; su uso fue propuesto, no ejecutado.

- **P0 / sin nueva fecha interna:** confirmar en OQ-10 la elegibilidad académica de las dos adicionales. E11 queda preservada; guardar/recuperar deja de descontarse automáticamente del requisito de dos extras.
- **Candidato pendiente:** selector de temas/sprites, TEAM_PROPOSAL / TEACHER_REPORTED_ELIGIBILITY / IMPLEMENTATION_NOT_VERIFIED. Definir aceptación y patrón real tras confirmar el alcance; no tratar conformidad individual ni encuesta incompleta como mayoría.
- **Persistencia:** conservar como propuesta histórica del equipo con contrato pendiente, sin transformarla en obligación docente ni equipararla a snapshots Memento para Undo. Cualquier implementación requiere alcance propio.
- **Patrones:** sugerencias de Factory Method/Abstract Factory del 29/10 no son decisiones ratificadas ni autorizan refactors. La segunda funcionalidad y el cómputo final de ambas siguen pendientes.

El parcial no tiene fecha exacta confirmada aquí. La pregunta sobre 1/11 carece de contexto de voces omitidas: no es deadline. Continúa **18/11/2026** como entrega oficial; las fechas internas de la tabla anterior son sólo propuestas. R7 no se ejecuta.
