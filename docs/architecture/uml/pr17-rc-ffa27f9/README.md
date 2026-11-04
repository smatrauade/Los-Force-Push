# UML de clases AS_IMPLEMENTED — PR #17 (04/11/2026)

**Referencia congelada:** `ffa27f9d8c72bd204db76f887b2ca0cd8e6f454c` (no equivale al HEAD futuro).

Nueve diagramas editables `.puml` y nueve imágenes PNG auténticas generadas con **PlantUML 1.2026.9**. El registro de hashes, dimensiones, versión y procedencia está en [`RENDER_GATE.json`](RENDER_GATE.json). El inventario de 50 archivos Java y la trazabilidad de tipos están en los TSV/CSV de esta carpeta. `scripts/verify-uml-pr17-readonly.py` comprueba su integridad frente a las fuentes congeladas.

## Vistas

| Vista | Fuente | PNG |
|---|---|---|
| 00 Arquitectura MVC | [PlantUML](plantuml/00_Arquitectura_MVC.puml) | [PNG](png/00_Arquitectura_MVC.png) |
| 01 Carga TXT | [PlantUML](plantuml/01_Carga_TXT.puml) | [PNG](png/01_Carga_TXT.png) |
| 02 Motor y Mundo Inmutable | [PlantUML](plantuml/02_Motor_y_Mundo_Inmutable.puml) | [PNG](png/02_Motor_y_Mundo_Inmutable.png) |
| 03 Sesion Undo Victoria | [PlantUML](plantuml/03_Sesion_Undo_Victoria.puml) | [PNG](png/03_Sesion_Undo_Victoria.png) |
| 04 Swing Presentacion | [PlantUML](plantuml/04_Swing_Presentacion.puml) | [PNG](png/04_Swing_Presentacion.png) |
| 05 Abstract Factory Temas | [PlantUML](plantuml/05_Abstract_Factory_Temas.puml) | [PNG](png/05_Abstract_Factory_Temas.png) |
| 06 Command Replay | [PlantUML](plantuml/06_Command_Replay.puml) | [PNG](png/06_Command_Replay.png) |
| 07 Observer Cerrojos | [PlantUML](plantuml/07_Observer_Cerrojos.puml) | [PNG](png/07_Observer_Cerrojos.png) |
| 08 Campana y Flujo | [PlantUML](plantuml/08_Campana_y_Flujo.puml) | [PNG](png/08_Campana_y_Flujo.png) |

Estos diagramas representan **colaboraciones seleccionadas**, no todas las firmas o dependencias existentes; los miembros visibles de los patrones son una selección contrastada con el código. La vista MVC se ha simplificado para no replicar todas las relaciones especializadas.

## Reproducción y alcance

Se generaron localmente con Java y el JAR `plantuml-1.2026.9.jar` verificado SHA-256 `aa4a1d06f8911c4b842beff4828dd955be431692b631a312d04c0e9fab9581de`. Archivos de origen: los `.puml` versionados. Para re-renderizar, usar PlantUML 1.2026.9 sobre un **directorio de salida distinto** y comparar los PNG resultantes con `RENDER_GATE.json`. No se incorporan previsualizaciones Graphviz ni marcas de agua de StarUML Trial.

La consigna del docente solicita literalmente un diagrama de clases **en StarUML**. Esta versión PlantUML + PNG **no acredita por sí sola** esa exigencia formal. Existe un `.mdj` de QA por separado que se preserva como respaldo, pero no se promueve ni se declara aceptado aquí. Elegibilidad RF-12/OQ-10, licencias RF-11 y aceptación TEAM siguen abiertas.

Procedencia: el código funcional es integración del PR #17. `impaciencia` fue una implementación **individual** distinta y no debe confundirse con las ramas de compañeros. Esta publicación UML no toca Java, `main` ni las decisiones TEAM.
