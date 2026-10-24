# Requisitos normalizados

`LAST_REVIEWED = 2026-10-23` (refinamientos TEAM separados; baseline oficial normalizado del 08/10 preservado). Esta redacción propia usa la descripción normalizada del prompt autorizado y contrasta la cápsula recibida. No es transcripción literal del PDF `PDS - TI - Prof Pepe.pdf`; el original no está disponible y su hash histórico no se ha recalculado. Autoridad y disponibilidad se registran en [SOURCE_REGISTRY](../continuity/SOURCE_REGISTRY.md).

## Baseline oficial

| ID | Requisito `OFFICIAL` | Aceptación futura |
|---|---|---|
| RF-01 | Tablero con paredes, espacios vacíos, cajas normales/frágiles/llave, destinos, terrenos resbaladizos, casilleros cerrojo, muros abiertos/cerrados y jugador Sokoban | Todos los elementos representables y cargables |
| RF-02 | Niveles iniciales desde diferentes `.txt` con caracteres | Cargar distintos archivos externos; no hardcodear niveles |
| RF-03 | Flechas; empujar, no tirar; máximo una caja simultáneamente; no atravesar paredes, cajas ni muros cerrados | Casos positivos, bloqueos y estado preservado ante intento inválido |
| RF-04 | Caja normal desplazada por empuje; deslizamiento en terreno resbaladizo | Empuje y deslizamiento conforme aclaración docente |
| RF-05 | Fragile pierde resistencia en cada empuje y se rompe al llegar a cero | Decremento/rotura; valor inicial y hielo requieren OQ-03/OQ-06 |
| RF-06 | KeyBox sobre cerrojo abre muros correspondientes | Activación; asociación/cierre dependen de OQ-07 |
| RF-07 | Historial de 15 movimientos, retroceso de 5 por pulsación Undo, hasta 3 usos consecutivos | Restaurar N-5/N-10/N-15; OQ-04/OQ-05 para bordes |
| RF-08 | Victoria cuando todas las cajas están en destinos; avanzar al siguiente nivel | Victoria/progresión tras cerrar OQ-01 |
| RF-09 | Resumen con movimientos, empujes, usos de Undo y puntaje final; fórmula del equipo | Ejemplos deterministas tras OQ-09 |
| RF-10 | HUD con movimientos, empujes y nivel actual; botones visibles Undo y Restart | Verificación de presentación e interacción |
| RF-11 | Todos los elementos del tablero mediante imágenes; acciones con efectos sonoros apropiados; assets de dominio público | Cobertura visual/sonora y registro individual de licencias |
| RF-12 | Dos funcionalidades adicionales que impliquen patrones de diseño | Selección OQ-10; no contar automáticamente requisitos obligatorios |
| RT-01 | Java + Swing + GUI 2D | Build/ejecución reproducibles tras OQ-11 |
| RT-02 | Repo GitHub público, README de ejecución, video gameplay, clases en StarUML, informe con decisiones/justificaciones y envío institucional con integrantes en copia | Entregables auditables; visibilidad pública verificada por API el 2026-10-21, demás entregables pendientes |
| RT-03 | SOLID, GRASP, MVC, GoF, calidad de diseño, creatividad y documentación; defensa individual | Diseño/código/evidencia coherentes y explicación por cada integrante |
| RT-04 | IA como apoyo declarada; equipo comprende, valida, adapta y defiende el resultado | Declaración y revisión humana |

## Aclaraciones posteriores y recomendaciones

`TEACHER-EXPLICIT` según prompt normalizado: el equipo documenta el formato TXT; el profesor podría modificarlo o construir uno compatible para verificar carga externa. La consigna no fija el alfabeto: la gramática final de [OQ-08](OPEN_QUESTIONS.md) sigue pendiente, con aspectos TEAM ya ratificados en el registro de decisiones.

En el ejemplo de hielo, la caja atraviesa la franja y llega al primer espacio normal libre posterior; el jugador no se desliza. Aquella aclaración no resolvía la salida inmediatamente bloqueada; el alcance TEAM posterior y sus bordes pendientes están en [OQ-02](OPEN_QUESTIONS.md) y [E03 / D-005](../decisions/DECISION_LOG.md#e03).

No hace falta medir tiempo ni implementar animaciones. Kenney y Pixabay fueron recomendados como fuentes posibles, sin garantía automática de dominio público para cada recurso.

Builder fue mencionado como alternativa válida para construir el nivel leyendo TXT; Observer fue sugerido para Lock→Walls. Esto respalda candidatos, no ratifica decisiones del equipo.

La cápsula histórica reporta además guía sobre interacción por eventos sin game loop continuo, Memento para recuperar estados y Strategy como política de cálculo. Su procedencia es indirecta; no se agregan requisitos oficiales por esas menciones.

## Propuestas y límites

La discusión del 30/09 proponía separar métricas. El alcance de comportamiento ahora aceptado está en [E09 / D-008](../decisions/DECISION_LOG.md#e09) y [E10 / D-009](../decisions/DECISION_LOG.md#e10); no ratifica la clase SessionMetrics ni el patrón Memento/Strategy. Los resets de métricas y fórmula siguen en [OQ-09](OPEN_QUESTIONS.md); la compatibilidad de [E08 / OQ-04](../decisions/DECISION_LOG.md#e08) con «consecutivos» requiere revisión. Restart es requerido, pero no se extrapolan efectos sobre cada contador.

Siguen sin fijarse sintaxis/rangos de resistencia individual, fórmula numérica, asociación/ocupación cerrojos-muros, segunda adicional, contratos técnicos de persistencia y Java/build. Ver [OPEN_QUESTIONS](OPEN_QUESTIONS.md). Los refinamientos siguientes no cambian el parser experimental ni constituyen pruebas o aceptación productiva.

## Refinamientos TEAM del 2026-10-23

Este apartado es un mapa hacia la autoridad canónica, no otra definición normativa de las reglas. Los acuerdos mayoritarios están sólo en [DECISION_LOG](../decisions/DECISION_LOG.md#ratificación-por-mayoría--2026-10-23); los pendientes sólo en [OPEN_QUESTIONS](OPEN_QUESTIONS.md). No se eleva TEAM a OFFICIAL ni se sustituye la consigna; tampoco se presupone compatibilidad de E04/E08.

| Requisito afectado | Aspecto y fuente TEAM / decisión | Inventario de alcance y límites |
|---|---|---|
| RF-01 / RF-02 / RF-05 | Disposición TXT y resistencia individual: [E01 / D-020](../decisions/DECISION_LOG.md#e01), [E02 / D-005/D-020](../decisions/DECISION_LOG.md#e02) | [OQ-06/OQ-08](OPEN_QUESTIONS.md): gramática, rangos y errores pendientes |
| RF-03 / RF-04 | Resultado contra pared: [E03 / D-005](../decisions/DECISION_LOG.md#e03) | [OQ-02](OPEN_QUESTIONS.md): bordes y otras obstrucciones pendientes |
| RF-05 | Preferencia Fragile/hielo: [E04 / D-005](../decisions/DECISION_LOG.md#e04) | [OQ-03](OPEN_QUESTIONS.md): CONFLICT_REVIEW_REQUIRED; no regla implementable definitiva |
| RF-06 / RF-08 | Objetivo KeyBox y cierre: [E05 / D-019](../decisions/DECISION_LOG.md#e05), [E06 / D-010](../decisions/DECISION_LOG.md#e06) | [OQ-01/OQ-07](OPEN_QUESTIONS.md): ocupación, IDs y puzzles pendientes |
| RF-07 / RF-10 | Umbral y reinicio de límite: [E07 / D-007](../decisions/DECISION_LOG.md#e07), [E08 / D-008](../decisions/DECISION_LOG.md#e08) | [OQ-05/OQ-04](OPEN_QUESTIONS.md): umbral aceptado, compatibilidad de consecutividad pendiente |
| RF-09 | Métricas y variables penalizadas: [E09 / D-008](../decisions/DECISION_LOG.md#e09), [E10 / D-009](../decisions/DECISION_LOG.md#e10) | [OQ-09](OPEN_QUESTIONS.md): fórmula y resets pendientes |
| RF-12 | Primera adicional: [E11 / D-022](../decisions/DECISION_LOG.md#e11) | [OQ-10](OPEN_QUESTIONS.md): segunda sin mayoría, contratos y GoF pendientes |

La revisión del código sigue `TEAM_ACCEPTANCE = NOT_RECORDED`. OQ-11 no tuvo encuesta: los comandos actuales y release 17 no ratifican Java/build final. Los estados técnicos de arquitectura/UML y patrones se conservan.
