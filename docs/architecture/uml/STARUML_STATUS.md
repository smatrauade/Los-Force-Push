# Estado de UML y StarUML

`LAST_REVIEWED = 2026-10-21`, `STARUML_DELIVERABLE = PENDING`.

La entrega exige un diagrama de clases en **StarUML**. No hay `.mdj` versionado ni se afirma haber abierto StarUML. PlantUML facilita revisión/versionado y no sustituye ese entregable.

## Alcance de los diagramas

| Diagrama | Clasificación | Alcance |
|---|---|---|
| [level-loading-spike-v1.puml](level-loading-spike-v1.puml) | AS_IMPLEMENTED | Contratos y asociaciones del cargador experimental, Board inmutable y Builder concreto interno |
| [movement-core-spike-v1.puml](movement-core-spike-v1.puml) | AS_IMPLEMENTED | Motor sin estado, Direction, MoveResult y referencias inmutables al core |
| [swing-playable-spike-v1.puml](swing-playable-spike-v1.puml) | AS_IMPLEMENTED | Coordinación MVC, GameView/GameWindow, BoardPanel, bindings y Board compartido |
| [sokoban-candidate.puml](sokoban-candidate.puml) | TO_BE / CANDIDATE | Modelo integral v0.5 y decisiones pendientes; no representa toda la implementación actual |

El candidato v0.5 estaba en el ZIP: SHA-256 `07e86f32bc2ea5c7091508698b415efbf18e9bce45464bb477cb154e5a268901`, preservado byte a byte. La cápsula histórica también contiene el source. Sus títulos, fechas y participantes no significan aprobación actual.

GameSession, cajas especiales, Memento, Observer, Strategy, audio y progresión pertenecen al candidato, no al incremento Swing implementado. Herencia, interfaces y firmas candidatas siguen revisables; SoundManager es propuesto y SessionMetrics no está ratificado. No hay extras definitivas modeladas.

## Validación por iteración

En el bootstrap del 08/10 se verificaron límites @startuml/@enduml, identidad del source y correspondencia conceptual; no se ejecutó un compilador/render en aquella iteración. Esa evidencia histórica no describe una validación nueva.

La revisión Swing R3 del 21/10 contrastó los tres diagramas AS_IMPLEMENTED con clases, firmas relevantes, direcciones de colaboración y ownership reales. No se detectó un desajuste material que requiera cambiar los `.puml`. El diagrama Swing muestra GameController → GameView/LevelLoader/MovementEngine/GamePresentation; GameWindow implementa GameView y posee panel/controlador; GamePresentation y BoardPanel referencian el mismo Board inmutable. El candidato permanece separado.

Los cuatro `.puml` pasaron comprobación sintáctica y render PNG mediante PlantUML 1.2020.02 y Graphviz 14.1.2 ya instalados, con `java.awt.headless=true` sólo en el proceso. La JVM de esa herramienta fue Java 25.0.4.1; las pruebas del proyecto usaron JDK 17.0.20.1/release 17. Renderizar no demuestra semántica del dominio ni apertura en StarUML. Los outputs y logs de auditoría son externos al repositorio.

Siguiente gate académico: revisión grupal de responsabilidades/relaciones/firmas, cierre de decisiones afectadas y creación del `.mdj`. Cuando exista, declarar fuente de diseño canónica y mantener ambos artefactos sincronizados o documentar explícitamente supersesión. La falta del `.mdj` no impide revisar un Draft PR de este subconjunto experimental.
