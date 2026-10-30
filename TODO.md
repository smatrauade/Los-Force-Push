# TODO — backlog vivo

Revisado 29/10/2026. [Estado integrado y registro de merges](docs/continuity/CURRENT_STATE.md). TEAM_ACCEPTANCE = NOT_RECORDED. Las prioridades son planificación propuesta por módulo, no nuevas decisiones de reglas ni asignaciones personales.

## Base disponible

| Estado | Alcance | Evidencia y límite |
|---|---|---|
| IMPLEMENTED_VERIFIED / PARTIAL | UTF-8 externo, legacy/C2-L7, Board inmutable y LevelBuilder concreto | Código integrado; Loading 40/C2 32 históricos; especiales jugables rechazados |
| IMPLEMENTED_VERIFIED / PARTIAL | Movement cardinal y caja normal | Movement 31 histórico; hielo/Fragile/KeyBox/Lock/muros dinámicos ausentes |
| IMPLEMENTED_VERIFIED / PARTIAL | Swing pixel art, menú/HUD/pausa/opciones/zoom, sprites/SFX | Desktop 16, Assets 44, UI 17 y CLI 18 históricos; callbacks pendientes no son servicios |
| IMPLEMENTED / MODULE_OWNER_DECIDED_SUPPORTED_SUBSET | Definición Fragile inmutable desde String/Path; contrato soportado D-020 | Fragile 41, sin gameplay; OQ-06 resuelta, OQ-08 extensiones pendientes |
| PARTIAL | 31 PNG (29 base + dos opcionales) y 12 WAV versionados | Manifiesto disponible; revisión de derechos pendiente |

IMPLEMENTED_VERIFIED describe presencia contrastada en código; no una nueva ejecución funcional. [Comandos, fechas y límites de QA](docs/testing/TEST_STRATEGY.md).

## Ruta crítica hacia el 18/11

Entrega docente: **miércoles 18/11/2026**, demo en vivo **15–20 minutos** y correo formal. [Protocolo y entregables](docs/requirements/REQUIREMENTS.md#entrega-y-aceptación-docente--29102026). P0 bloquea cierre funcional o entrega; P1 integra y comprueba; P2 mejora fuera de ruta crítica. Las fechas internas siguientes son **objetivos propuestos, no acuerdos ni plazos docentes adicionales**; revisar viabilidad con el equipo ante cada incremento.

| Prioridad / objetivo interno | Estado y trabajo | Responsable por módulo | Dependencia | Criterio de aceptación / siguiente prueba |
|---|---|---|---|---|
| P0 / 02/11 | OPEN_DEPENDENCY: extensiones TXT e integración; Java/build global | TXT + consumidores/integración | OQ-08 extensiones; OQ-07/11 | Subconjunto soportado ya decidido D-020; obtener contrato consumidor, preservar E01/E02 y legacy/C2 |
| P0 / 02/11 | TEAM_DECIDED / IMPLEMENTATION_PENDING_OTHER_MODULE: Fragile/hielo y límite Undo | Model/Movement y sesión | E04/E08 resueltas; OQ-03/04 bordes operativos | Implementar y probar las reglas elegidas con casos antes/después; no reabrir encuestas ni rediseñar Undo desde TXT |
| P0 / 06/11 | NOT_IMPLEMENTED: hielo, Fragile jugable, KeyBox/Lock y muros dinámicos | Model/Movement + TXT | OQ-01/02/07/08; contrato TXT soportado | Preservar identidad/resistencia y terrenos; casos de bordes, cierre ocupado y rechazo atómico |
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

[Plan de ejecución TXT/niveles, una rama y un PR final](docs/requirements/TXT_LEVELS_EXECUTION_PLAN.md). Gates graduales y documentación alineada con el estado que se aprobaría; el plan conserva su origen documental y registra el alcance autónomo implementado; Git/GitHub determina el estado del PR y su integración.

R6 está completa por PR #12. **Alcance autónomo TXT terminado:** carga externa de definición experimental `LevelLoader.loadExperimentalFragile(Path)`, compartiendo lectura UTF-8/límite de bytes. QA del incremento: Loading 40, C2 32, Fragile 41 y Movement 31; Desktop headless 16, sin cambios Swing. [Identificación del contenido y comandos](docs/testing/TEST_STRATEGY.md#incremento-filesystem-fragile--29102026).

OQ-06 del lector ya resuelta por el responsable TXT; OQ-08 parcialmente resuelta para el subconjunto soportado. Pendiente: acordar con Model/Movement identidad/resistencia runtime, snapshots, casos antes/después y datos para extensiones. El [contrato TXT](docs/requirements/FRAGILE_TXT_CONTRACT_PROPOSAL.md) delimita la salida disponible. Siguiente tarea segura: contrastar el contrato y ejemplos con la firma/commit real del consumidor, sin activar especiales en `Level`, sin reimplementar este loader y sin contar el fixture de definición como puzzle jugable resuelto. La promoción única exige revisión humana del código/contrato y gate técnico; consultar el PR y Git para saber si se completó. La autorización de publicación no sustituye esa revisión.

## Integración paralela TXT con Fragile/hielo — 29/10/2026

[Procedencia anónima](docs/continuity/SOURCE_REGISTRY.md#coordinación-txt-y-fragilehielo--29102026). Un integrante reporta trabajo en esas mecánicas y ofreció un nivel de prueba; no se ha verificado una entrega ni su implementación. Cantidad/estructura de niveles de demo siguen propuestas. TXT no sustituye ese nivel ni implementa su motor.

| Estado / tarea | Responsable por dominio | Bloqueo y siguiente evidencia |
|---|---|---|
| DOCUMENTED: entrada/salida actual y limitaciones | TXT/niveles | [Matriz de interfaces y ejemplos](docs/requirements/FRAGILE_TXT_CONTRACT_PROPOSAL.md#traspaso-txt-a-modelmovement--29102026); código base 5ac2bba |
| OPEN_PENDING_TEAM: compatibilidad con consumidor | Fragile/hielo + TXT/integración | Recibir firma real y commit del consumidor; contrastar ID, coordenadas, terreno y resistencia inicial sin imponer API |
| OPEN_DEPENDENCY: representación hielo y compatibilidad runtime | Hielo/Model + TXT | Datos/combinaciones y sintaxis futura OQ-08; E04 ya elegida, rango inicial TXT ya decidido en OQ-06 |
| OFFERED_NOT_VERIFIED: nivel de prueba | Integrante que lo ofreció + QA/TXT/Model | Esperar archivo/commit y mecánicas utilizadas; acordar contrato antes de cargar entidades no soportadas; solución y resistencia por validar |
| PENDING: futuras asociaciones y restauración | Cerrojos/muros y sesión con TXT | OQ-07 y contrato runtime; no atribuir estas tareas al desarrollador de hielo por inferencia |

Siguiente integración segura: revisar el constructor real del consumidor y el nivel cuando se aporten, usando las preguntas del contrato. Este hito no justifica cambio Java independiente (`CODE_CHANGE=NOT_JUSTIFIED`): las brechas restantes son de acuerdo entre módulos, no un fallo nuevo demostrado del parser. No enviar mensajes ni editar otras ramas sin autorización.

## Decisiones y mantenimiento

E01–E11 se conservan únicamente en [DECISION_LOG](docs/decisions/DECISION_LOG.md); pendientes en [OPEN_QUESTIONS](docs/requirements/OPEN_QUESTIONS.md). No reabrir acuerdos por defecto. E04/E08 son TEAM_DECIDED, sin implementación por este cierre. OQ-06 = MODULE_OWNER_DECIDED; OQ-08 = MODULE_OWNER_DECIDED parcial, extensiones abiertas. Java/build global sigue OPEN. Revisar patrones por intención, roles reales y alternativa; SoundManager no es Singleton, callbacks no acreditan Command y el DTO LevelResult no calcula score.

Mantener [arquitectura](docs/architecture/ARCHITECTURE.md), UML implementado y candidato separados; actualizar diagramas en misión autorizada, sin convertirlos en evidencia de clases ausentes. [Derechos y assets](docs/assets/ASSET_POLICY.md), [contratos Punto 5](docs/punto5/INTEGRACION_EQUIPO.md) y [FRESHNESS_POLICY](docs/continuity/FRESHNESS_POLICY.md) siguen vigentes. Revisar humanos/código, pruebas y documentos en cada incremento; conservar cápsulas y checkpoints históricos. Este backlog no concede permiso de implementación o publicación.

## Contexto grupal y tareas de adicionales — complemento 29/10/2026

[Fuente y límites](docs/continuity/SOURCE_REGISTRY.md#contexto-grupal-privado--complemento-29102026). Las cinco áreas del 24/10 fueron una propuesta organizativa, con un ofrecimiento para Undo/Restart/puntaje; no hay asignación nominal completa. Los responsables por módulo de la tabla son planificación, no atribuciones personales. La consulta actual a GitHub no encontró Issues; su uso fue propuesto, no ejecutado.

- **P0 / sin nueva fecha interna:** confirmar en OQ-10 la elegibilidad académica de las dos adicionales. E11 queda preservada; guardar/recuperar deja de descontarse automáticamente del requisito de dos extras.
- **Candidato pendiente:** selector de temas/sprites, TEAM_PROPOSAL / TEACHER_REPORTED_ELIGIBILITY / IMPLEMENTATION_NOT_VERIFIED. Definir aceptación y patrón real tras confirmar el alcance; no tratar conformidad individual ni encuesta incompleta como mayoría.
- **Persistencia:** conservar como propuesta histórica del equipo con contrato pendiente, sin transformarla en obligación docente ni equipararla a snapshots Memento para Undo. Cualquier implementación requiere alcance propio.
- **Patrones:** sugerencias de Factory Method/Abstract Factory del 29/10 no son decisiones ratificadas ni autorizan refactors. La segunda funcionalidad y el cómputo final de ambas siguen pendientes.

El parcial no tiene fecha exacta confirmada aquí. La pregunta sobre 1/11 carece de contexto de voces omitidas: no es deadline. Continúa **18/11/2026** como entrega oficial; las fechas internas de la tabla anterior son sólo propuestas. El estado actual de R7 se describe en «Próximo dominio TXT/niveles»; este complemento conserva su contexto documental anterior.
