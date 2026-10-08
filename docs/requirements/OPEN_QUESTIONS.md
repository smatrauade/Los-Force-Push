# Preguntas abiertas

Todos los ítems son `OPEN` al 2026-10-08. El gate pide una decisión explícita con procedencia, ejemplos y actualización de decisiones/requisitos/trazabilidad; no se selecciona silenciosamente una alternativa.

| ID | Cuestión | Alternativas a discutir y gate | Impacto |
|---|---|---|---|
| OQ-01 | Victoria con KeyBox sobre Lock | ¿Lock cuenta como destino, la llave debe salir a un destino o hay otra regla? Consultar al docente sin cambiar “todas las cajas” por conveniencia | Victoria, nivel, cierre de muros |
| OQ-02 | Hielo termina contra obstáculo | ¿Frenar en última celda libre o rechazar el empuje? Aclaración con diagrama antes/después | Movimiento atómico |
| OQ-03 | Fragile llega a cero al entrar en hielo | ¿Rotura antes/durante/después de deslizar y en qué celda? Registrar ejemplo y respuesta | Resistencia y reversión |
| OQ-04 | Qué reinicia 3 Undo consecutivos | Movimiento nuevo, intento bloqueado, Restart/cambio de nivel; definir cada evento y sus métricas | Historial/sesión |
| OQ-05 | Menos de 5 movimientos disponibles | Deshabilitar/rechazar o retroceso parcial sólo si compatible con aclaración; no asumir | Botón y borde de Undo |
| OQ-06 | Resistencia inicial Fragile | Global o configurable por nivel/caja; especificar rangos, formato y validación | Parser, cajas |
| OQ-07 | Lock↔Walls y cierre posterior | IDs, 1:1/1:N, varios cerrojos y permanencia; pruebas de Undo/Restart | Observer, estado mutable |
| OQ-08 | Formato TXT definitivo | Una/dos capas, símbolos, dimensiones, metadata, charset, errores y representación de todas las entidades | Carga externa |
| OQ-09 | Fórmula score | Variables y límites; ratificar total de Undo no restaurable y penalización con ejemplos | ScoreStrategy/métricas |
| OQ-10 | Dos adicionales | Definir funcionalidad, problema real, patrón, alternativa simple, coste y prueba | Alcance académico |
| OQ-11 | Java/build | Java target y Maven/Gradle/otra opción; instalación, comandos y build desde clon limpio | Primer build reproducible |

Además siguen sin ratificar Builder frente a construcción directa, Terrain/Occupant, herencia frente a composición de cajas y uso de Singleton para audio. Son propuestas de arquitectura, no nuevas reglas oficiales.

La cápsula anterior usa OQ-001…OQ-012 con otra numeración: mapear por tema, no por coincidencia numérica. En estos documentos vivos rige OQ-01…OQ-11 del prompt de bootstrap. Restart y movimiento nuevo están incluidos en OQ-04; Singleton en D-016.

Para cerrar: registrar quién decidió, fecha, fuente/autoridad, alcance, alternativa elegida, consecuencias y casos de aceptación; sólo entonces cambiar estado. Trabajo independiente puede continuar en otra iteración autorizada.
