# Estado de UML y StarUML

`LAST_REVIEWED = 2026-10-08`, `STARUML_DELIVERABLE = PENDING`.

La entrega exige un diagrama de clases en **StarUML**. No hay `.mdj` ni se afirma haber abierto StarUML. El PlantUML facilita revisión/versionado y no sustituye el entregable.

`sokoban-candidate.puml` es el archivo v0.5 disponible en el ZIP: SHA-256 `07e86f32bc2ea5c7091508698b415efbf18e9bce45464bb477cb154e5a268901`, preservado byte a byte. La cápsula histórica también contiene el source. Los títulos/fechas y participantes reflejan ese candidato, no aprobación actual.

Arquitectura y UML comparten GameSession, estado/Board, cajas, carga/Builder, Memento, Observer, Strategy, HUD, audio y progresión. Herencia ilustrativa, interfaces y firmas son revisables. `SoundManager` figura como propuesto; `SessionMetrics` representa una propuesta aún no ratificada. No hay extras definitivas modeladas.

En esta iteración se verifican límites `@startuml`/`@enduml`, identidad con el archivo fuente y correspondencia conceptual. **No se verificó render ni semántica mediante compilador PlantUML/StarUML**; no se instala una dependencia para fingir esa validación.

Siguiente gate: revisión grupal de responsabilidades/relaciones/firmas, cierre de decisiones afectadas y creación del `.mdj`. Cuando exista, declarar fuente de diseño canónica y mantener ambos artefactos sincronizados en el mismo PR o documentar explícitamente supersesión. Evitar divergencia respecto del código.
