# Requisitos normalizados

`LAST_REVIEWED = 2026-10-21` (estado de visibilidad; normalización de requisitos del 2026-10-08 preservada). Esta redacción propia usa la descripción normalizada del prompt autorizado y contrasta la cápsula recibida. No es transcripción literal del PDF `PDS - TI - Prof Pepe.pdf`; el original no está disponible y su hash histórico no se ha recalculado. Autoridad y disponibilidad se registran en [SOURCE_REGISTRY](../continuity/SOURCE_REGISTRY.md).

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

`TEACHER-EXPLICIT` según prompt normalizado: el equipo documenta el formato TXT; el profesor podría modificarlo o construir uno compatible para verificar carga externa. La consigna no fija el alfabeto: **OQ-08 sigue OPEN**.

En el ejemplo de hielo, la caja atraviesa la franja y llega al primer espacio normal libre posterior; el jugador no se desliza. La salida inmediatamente bloqueada no quedó resuelta (OQ-02).

No hace falta medir tiempo ni implementar animaciones. Kenney y Pixabay fueron recomendados como fuentes posibles, sin garantía automática de dominio público para cada recurso.

Builder fue mencionado como alternativa válida para construir el nivel leyendo TXT; Observer fue sugerido para Lock→Walls. Esto respalda candidatos, no ratifica decisiones del equipo.

La cápsula histórica reporta además guía sobre interacción por eventos sin game loop continuo, Memento para recuperar estados y Strategy como política de cálculo. Su procedencia es indirecta; no se agregan requisitos oficiales por esas menciones.

## Propuestas y límites

Separar tablero/movimientos/empujes restaurables de `undoUsesTotal` y `consecutiveUndoUses` es **TEAM-PROPOSAL** de la discusión del 30 de septiembre. Que Undo penalice score y cómo se reinician métricas no está ratificado. Restart es un botón requerido; sus efectos exactos sobre métricas deben definirse.

No se fija resistencia inicial Fragile, fórmula score, asociación cerrojos/muros, selección de extras, Java target ni herramienta de build. Ver [OPEN_QUESTIONS](OPEN_QUESTIONS.md). No hay implementación ni tests productivos: las aceptaciones de esta tabla son futuras.
