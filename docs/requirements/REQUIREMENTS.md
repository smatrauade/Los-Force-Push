# Requisitos normalizados

`LAST_REVIEWED = 2026-11-01` (refinamientos TEAM separados; baseline oficial normalizado del 08/10 preservado y aclaración de procedencia documental). Esta redacción propia usa la descripción normalizada del prompt autorizado y contrasta la cápsula recibida. No es transcripción literal del PDF `PDS - TI - Prof Pepe.pdf`; el original no forma parte del clon ni se redistribuye; fue recibido y su SHA-256 verificado en una revisión externa del 21/10/2026, como consta en SOURCE_REGISTRY. Un agente nuevo con solo el repositorio tiene la normalización y la procedencia, no el PDF original. Autoridad y disponibilidad se registran en [SOURCE_REGISTRY](../continuity/SOURCE_REGISTRY.md).

## Baseline oficial

| ID | Requisito `OFFICIAL` | Aceptación futura |
|---|---|---|
| RF-01 | Tablero con paredes, espacios vacíos, cajas normales/frágiles/llave, destinos, terrenos resbaladizos, casilleros cerrojo, muros abiertos/cerrados y jugador Sokoban | Todos los elementos representables y cargables |
| RF-02 | Niveles iniciales desde diferentes `.txt` con caracteres | Cargar distintos archivos externos; no hardcodear niveles |
| RF-03 | Flechas; empujar, no tirar; máximo una caja simultáneamente; no atravesar paredes, cajas ni muros cerrados | Casos positivos, bloqueos y estado preservado ante intento inválido |
| RF-04 | Caja normal desplazada por empuje; deslizamiento en terreno resbaladizo | Empuje y deslizamiento conforme aclaración docente |
| RF-05 | Fragile pierde resistencia en cada empuje y se rompe al llegar a cero | Decremento/rotura; definición inicial TXT según D-020/OQ-06 resuelta; regla hielo E04 decidida, implementación pendiente |
| RF-06 | KeyBox sobre cerrojo abre muros correspondientes | Activación; asociación/cierre dependen de OQ-07 |
| RF-07 | Historial de 15 movimientos, retroceso de 5 por pulsación Undo, hasta 3 usos consecutivos | Restaurar N-5/N-10/N-15; aplicar E07/E08 y la ratificación complementaria del 01/11: Undo restaura tablero, movimientos y empujes; sólo Restart o cambio de nivel reinician el límite de tres usos. Restart inicia un intento nuevo y reinicia las cuatro métricas. |
| RF-08 | Victoria cuando todas las cajas están en destinos; avanzar al siguiente nivel | Victoria/progresión tras cerrar OQ-01 |
| RF-09 | Resumen con movimientos, empujes, usos de Undo y puntaje final; fórmula ratificada por TEAM | `Score = max(0, 1000 - moves - 2 * pushes - 10 * undoUsesTotal)`. Base 1000; −1 por movimiento, −2 adicionales por empuje y −10 por uso de Undo; mínimo cero. Restart reinicia las métricas del intento y no tiene penalización adicional. |
| RF-10 | HUD con movimientos, empujes y nivel actual; botones visibles Undo y Restart | Verificación de presentación e interacción |
| RF-11 | Todos los elementos del tablero mediante imágenes; acciones con efectos sonoros apropiados; assets de dominio público | Cobertura visual/sonora y registro individual de licencias |
| RF-12 | Dos funcionalidades adicionales que impliquen patrones de diseño | Selección OQ-10; no contar automáticamente requisitos obligatorios |
| RT-01 | Java + Swing + GUI 2D | Build/ejecución reproducibles tras OQ-11 |
| RT-02 | Repo GitHub público, README de ejecución, video gameplay, clases en StarUML, informe con decisiones/justificaciones y envío institucional con integrantes en copia | Entregables auditables; visibilidad pública verificada por API el 2026-10-21, demás entregables pendientes |
| RT-03 | SOLID, GRASP, MVC, GoF, calidad de diseño, creatividad y documentación; defensa individual | Diseño/código/evidencia coherentes y explicación por cada integrante |
| RT-04 | IA como apoyo declarada; equipo comprende, valida, adapta y defiende el resultado | Declaración y revisión humana |

## Aclaraciones posteriores y recomendaciones

`TEACHER-EXPLICIT` según prompt normalizado: el equipo documenta el formato TXT; el profesor podría modificarlo o construir uno compatible para verificar carga externa. La consigna no fija el alfabeto: el subconjunto soportado está adoptado por el responsable TXT en [D-020](../decisions/DECISION_LOG.md#d-020--decisión-del-responsable-txt--29102026); [OQ-08](OPEN_QUESTIONS.md) conserva sólo extensiones no soportadas. Esa decisión técnica no amplía lo votado por TEAM.

En el ejemplo de hielo, la caja atraviesa la franja y llega al primer espacio normal libre posterior; el jugador no se desliza. Aquella aclaración no resolvía la salida inmediatamente bloqueada; el alcance TEAM posterior y sus bordes pendientes están en [OQ-02](OPEN_QUESTIONS.md) y [E03 / D-005](../decisions/DECISION_LOG.md#e03).

No hace falta medir tiempo ni implementar animaciones. Kenney y Pixabay fueron recomendados como fuentes posibles, sin garantía automática de dominio público para cada recurso.

Builder fue mencionado como alternativa válida y opcional para construir el nivel leyendo TXT. La nueva evidencia ASR del 21/10 indica incorporar Observer en alguna parte del TPO, con Lock→Walls como ejemplo. Registrar esa fuerza docente no ratifica arquitectura TEAM ni demuestra implementación; ver [procedencia](../continuity/SOURCE_REGISTRY.md#fuentes-académicas-aportadas--29102026).

La cápsula histórica reporta además guía sobre interacción por eventos sin game loop continuo, Memento para recuperar estados y Strategy como política de cálculo. Su procedencia es indirecta; no se agregan requisitos oficiales por esas menciones.

## Propuestas y límites

La discusión del 30/09 proponía separar métricas. E09 establece que Undo restaura movimientos y empujes, mientras que sus usos se acumulan durante el intento. La ratificación complementaria TEAM del 01/11 define `undoUsesTotal`, `consecutiveUndoUses` y el efecto de Restart: inicia un intento nuevo y reinicia `moves`, `pushes`, `undoUsesTotal` y `consecutiveUndoUses`. E08 conserva su regla: sólo Restart o cambio de nivel reinician el límite de tres usos.

La fórmula de score también quedó ratificada el 01/11: `max(0, 1000 - moves - 2 * pushes - 10 * undoUsesTotal)`. No existe penalización adicional por Restart. El tratamiento de las métricas al cambiar de nivel sigue pendiente.

Estas definiciones no ratifican por sí mismas la clase SessionMetrics ni los patrones Memento/Strategy. `ScoreStrategy` continúa siendo una alternativa PREFERRED, no una obligación de diseño. Ver [DECISION_LOG](../decisions/DECISION_LOG.md#ratificación-complementaria--undo-restart-y-score--2026-11-01) y [OPEN_QUESTIONS](OPEN_QUESTIONS.md).

Sintaxis/rango/validación de resistencia inicial TXT soportados fueron adoptados por el responsable en D-020. Siguen sin fijarse extensiones especiales del formato, fórmula numérica, asociación/ocupación cerrojos-muros, segunda adicional, contratos técnicos de persistencia y Java/build. Ver [OPEN_QUESTIONS](OPEN_QUESTIONS.md). Los refinamientos siguientes no cambian el parser experimental ni constituyen pruebas o aceptación productiva.

## Refinamientos TEAM del 2026-10-23

Este apartado es un mapa hacia la autoridad canónica, no otra definición normativa de las reglas. Los acuerdos mayoritarios están sólo en [DECISION_LOG](../decisions/DECISION_LOG.md#ratificación-por-mayoría--2026-10-23); los pendientes sólo en [OPEN_QUESTIONS](OPEN_QUESTIONS.md). No se eleva TEAM a OFFICIAL ni se sustituye la consigna; E04/E08 son elecciones resueltas, sin aprobación docente específica ni implementación atribuida por este registro.

| Requisito afectado | Aspecto y fuente TEAM / decisión | Inventario de alcance y límites |
|---|---|---|
| RF-01 / RF-02 / RF-05 | Disposición TXT y resistencia individual: [E01 / D-020](../decisions/DECISION_LOG.md#e01), [E02 / D-005/D-020](../decisions/DECISION_LOG.md#e02) | [OQ-06/OQ-08](OPEN_QUESTIONS.md): esquema soportado decidido por MODULE_OWNER, extensiones futuras abiertas |
| RF-03 / RF-04 | Resultado contra pared: [E03 / D-005](../decisions/DECISION_LOG.md#e03) | [OQ-02](OPEN_QUESTIONS.md): bordes y otras obstrucciones pendientes |
| RF-05 | Regla Fragile/hielo: [E04 / D-005](../decisions/DECISION_LOG.md#e04) | [OQ-03](OPEN_QUESTIONS.md): TEAM_RULE_DECIDED; completar deslizamiento y luego romper; implementación pendiente en otro módulo |
| RF-06 / RF-08 | Objetivo KeyBox y cierre: [E05 / D-019](../decisions/DECISION_LOG.md#e05), [E06 / D-010](../decisions/DECISION_LOG.md#e06) | [OQ-01/OQ-07](OPEN_QUESTIONS.md): ocupación, IDs y puzzles pendientes |
| RF-07 / RF-10 | Umbral y reinicio de límite: [E07 / D-007](../decisions/DECISION_LOG.md#e07), [E08 / D-008](../decisions/DECISION_LOG.md#e08) | [OQ-05/OQ-04](OPEN_QUESTIONS.md): umbral y evento de reinicio decididos, bordes operativos pendientes en sesión/Undo |
| RF-09 | Métricas y variables penalizadas: [E09 / D-008](../decisions/DECISION_LOG.md#e09), [E10 / D-009](../decisions/DECISION_LOG.md#e10) | [OQ-09](OPEN_QUESTIONS.md): fórmula y resets pendientes |
| RF-12 | Preferencia TEAM histórica: [E11 / D-022](../decisions/DECISION_LOG.md#e11) | [OQ-10](OPEN_QUESTIONS.md#elegibilidad-de-adicionales--complemento-29102026): elegibilidad de guardar cuestionada por reporte indirecto; cómputo de dos adicionales, contratos y GoF pendientes |

La revisión del código sigue `TEAM_ACCEPTANCE = NOT_RECORDED`. OQ-11 no tuvo encuesta: los comandos actuales y release 17 no ratifican Java/build final. Los estados técnicos de arquitectura/UML y patrones se conservan.

## Entrega y aceptación docente — 29/10/2026

**TEACHER-EXPLICIT / USER_SUPPLIED_TEAMS_TEXT:** el miércoles **18 de noviembre de 2026** se presenta y entrega el TI. Demo en vivo de **15–20 minutos**, mostrando lo implementado y explicando detalladamente los patrones aplicados. No se exige mostrar código ni diagramas en la exposición; **no se elimina StarUML de RT-02**.

Un integrante envía a **jpepe@uade.edu.ar**, con copia a los demás, nombre del equipo, apellido y nombre de cada desarrollador, todos los entregables y enlace al repositorio público. Checklist RT-02: repositorio público, README reproducible, video, archivo StarUML e informe técnico con decisiones y justificaciones. RT-04 exige declarar IA y comprensión/defensa individual; no afirmar revisión grupal inexistente.

**Indicación oral resumida / AUDIO_NOT_VERIFIED:** para RF-02 el docente puede crear un TXT compatible o editar válidamente uno del equipo, por ejemplo cambiar pared por caja. Aceptación: archivo externo → Loader/Parser → Board → interfaz debe reflejar el cambio, sin hardcode. Una entrada inválida debe producir error y conservar el nivel previo; no se promete aceptar cualquier edición. Para RF-01/02/05 los niveles elaborados por el equipo deben ser resolubles y su resistencia Fragile coherente con el puzzle; no se prescribe solver automático ni máximo numérico oficial. [Fuentes y timestamps](../continuity/SOURCE_REGISTRY.md#fuentes-académicas-aportadas--29102026).

Estos criterios son objetivos de aceptación, no resultados nuevos. Fragile jugable y las demás entidades especiales siguen pendientes; la definición experimental no satisface por sí sola RF-05. OQ-03/OQ-04 conservan sólo implementación/bordes ajenos pendientes; E04/E08 tienen elección resuelta. La consigna oficial permanece por encima de TEAM, sin una aprobación docente de secuenciación inventada.

## Alcance de RF-07 y RF-12 — complemento 29/10/2026

RF-07 exige Undo con historial 15, bloques 5 y hasta 3 usos consecutivos; la consigna escrita no exige textualmente persistencia en disco. Memento representa estado para restaurar, sin implicar guardar archivos o sobrevivir al cierre del juego. No resolver por esa asociación la ambigüedad del [reporte grupal](../continuity/SOURCE_REGISTRY.md#contexto-grupal-privado--complemento-29102026).

RF-12 sigue exigiendo **dos adicionales académicamente válidas con patrones vistos en clase**. Guardar/recuperar no se computa automáticamente: su exclusión y la elegibilidad del selector fueron atribuidas al docente por un tercero, sin respuesta original verificada. El selector permanece candidato y requiere evidencia de comportamiento y patrón justificado; el cómputo final y la segunda selección siguen pendientes en OQ-10. No se alteran E11 ni la entrega oficial del 18/11/2026.

## Refinamientos TEAM del 2026-11-01

La ratificación complementaria del 01/11 concreta las reglas de Undo, Restart y score. No modifica el baseline oficial de la consigna ni los recuentos históricos E08, E09 y E10.

| Requisito | Aspecto ratificado | Alcance y límites |
|---|---|---|
| RF-07 | Undo restaura tablero, `moves` y `pushes`; `undoUsesTotal` acumula usos dentro del intento; `consecutiveUndoUses` controla el límite de tres. | Sólo Restart o cambio de nivel reinician el límite. Un movimiento normal no recupera usos. Restart inicia un intento nuevo y reinicia las cuatro métricas. |
| RF-09 | Fórmula: `max(0, 1000 - moves - 2 * pushes - 10 * undoUsesTotal)`. | Base 1000; −1 por movimiento; −2 adicionales por empuje; −10 por Undo; mínimo cero. Restart no tiene penalización adicional y el score corresponde al intento actual. |

El cambio de nivel reinicia `consecutiveUndoUses`, pero no se ha ratificado el efecto sobre las demás métricas. La implementación, las pruebas y la integración continúan pendientes. La elección de Memento, SessionMetrics o ScoreStrategy conserva su estado técnico y no queda ratificada por estas reglas.