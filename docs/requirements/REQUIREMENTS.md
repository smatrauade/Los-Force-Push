# Requisitos del Trabajo Integrador

Procesos de Desarrollo de Software, UADE. Docente: Jonathan Leonel Pepe. Esta síntesis distingue las exigencias académicas del comportamiento concreto del programa. La consigna original de cátedra no se redistribuye en el repositorio.

## Consigna académica

Desarrollar un Sokoban 2D en Java y Swing, aplicando MVC, SOLID, GRASP y patrones GoF. Se requieren niveles externos TXT, movimiento por flechas y empuje, cajas normales/frágiles/llave, hielo, cerrojos y muros, Undo, Restart, victoria, puntaje, HUD, imágenes y sonidos. Se solicitan dos funcionalidades adicionales vinculadas a patrones de diseño.

La entrega exige repositorio público y README de ejecución, video de gameplay, diagrama de clases en StarUML, informe técnico con decisiones justificadas y declaración de IA. Los recursos visuales y sonoros deben satisfacer la condición de dominio público de la consigna. Estos requisitos de formato y derechos no se sustituyen por una comprobación de hashes.

Un integrante envía el material desde su correo institucional a `jpepe@uade.edu.ar`, con copia al resto e identificación de todos los desarrolladores. La presentación del 18/11/2026 es una demo de 15–20 minutos con explicación de patrones. No es obligatorio mostrar código o diagramas durante la exposición; los archivos siguen formando parte de la entrega. Cada integrante debe comprender y defender el trabajo.

## Comportamiento de la versión implementada

| ID | Requisito funcional | Implementación concreta |
|---|---|---|
| RF-01 | Tablero con terrenos y entidades especiales | Piso, pared, destino, hielo, jugador, cajas normales/frágiles/llave, cerrojos y muros dinámicos |
| RF-02 | Carga desde TXT | Lectura externa UTF-8 estricta, validación completa y reemplazo de sesión solo tras éxito |
| RF-03 | Movimiento y empuje | Flechas cardinales, una caja por empuje, sin tirar; intentos bloqueados no cambian mundo ni métricas |
| RF-04 | Hielo | Las cajas atraviesan la franja hasta la primera celda normal libre o se detienen ante un obstáculo; el jugador no se desliza |
| RF-05 | Caja frágil | Resistencia individual en TXT; pierde una unidad por empuje, completa el trayecto y se retira si llega a cero |
| RF-06 | Caja llave, cerrojos y muros | Solo KeyBox activa Lock. Los muros abren con al menos un cerrojo asociado activo; retirar la llave vuelve a cerrarlos. Se bloquea una transición que cerraría un muro ocupado |
| RF-07 | Undo | Historial de 15 movimientos exitosos; retroceso exacto de cinco; hasta tres usos por intento; sin retroceso parcial |
| RF-08 | Victoria y avance | Al menos un destino y una caja; igual número de cajas y destinos, todos cubiertos. Lock no cuenta como destino. Avance disponible si queda otro nivel |
| RF-09 | Resumen y puntaje | Movimientos, empujes, usos de Undo y `max(0, 1000 - moves - 2*pushes - 10*undoUsesTotal)` |
| RF-10 | HUD y controles | Nivel, métricas y puntaje; botones y atajos para Undo y Restart |
| RF-11 | Presentación visual y sonora | 31 sprites y 12 efectos; inventario individual de procedencia y hashes |
| RF-12 | Dos adicionales con patrones | Selector de tres temas con Abstract Factory y Replay con Command |

Undo restaura el mundo, los movimientos y los empujes; el total de usos de Undo no se revierte. Un nuevo movimiento no recupera usos. Restart y cambio de nivel reinician historial y métricas del intento. `LevelProgression` conserva los resultados de niveles terminados.

Estas descripciones documentan el programa entregado; no atribuyen una aprobación docente particular a cada decisión de implementación. Las decisiones técnicas y sus alternativas están en [diseño](../decisions/DECISION_LOG.md), y el contrato de entradas en [TXT](TXT_FORMAT.md).

## Alcance de las adicionales

El selector cambia familias visuales completas sin modificar las reglas. Replay vuelve a ejecutar la secuencia exitosa de una partida terminada en otro estado; no reemplaza a Undo. Observer comunica hechos confirmados de cerrojos a la interfaz y al sonido, como colaboración de una mecánica obligatoria.

No se implementó persistencia de partidas: los controles de guardar/cargar están deshabilitados. Abrir un nivel TXT crea una partida nueva. No se agregan requisitos de guardado por asociar Undo con snapshots.

La [trazabilidad](../traceability/TRACEABILITY_MATRIX.md) relaciona cada requisito con sus clases y pruebas. Los [entregables publicados](../../entregables/r10-20261113/README.md) identifican los formatos disponibles sin atribuir a PlantUML otro formato de origen.
