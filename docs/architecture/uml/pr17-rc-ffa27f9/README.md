# Diagramas de clases

Nueve vistas de las colaboraciones implementadas, con fuentes **PlantUML** editables e imágenes PNG generadas con PlantUML 1.2026.9. El índice separa los formatos por su nombre real.

| Vista | Fuente editable | Imagen |
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

Las vistas seleccionan clases, miembros y relaciones para explicar arquitectura, carga, motor, sesión, Swing, temas, Replay, Observer y campaña; no enumeran cada firma del programa. La [arquitectura](../../ARCHITECTURE.md) y las [responsabilidades](../../RESPONSIBILITIES.md) amplían la explicación.

## Integridad y reproducción

[`RENDER_GATE.json`](RENDER_GATE.json) conserva hashes, dimensiones y versión del generador. [`SOURCE_INVENTORY_PINNED.tsv`](SOURCE_INVENTORY_PINNED.tsv) fija 50 fuentes Java y [`JAVA_TO_UML_TRACEABILITY.csv`](JAVA_TO_UML_TRACEABILITY.csv) relaciona sus tipos con las vistas. Se mantiene el nombre histórico de la carpeta para preservar rutas y referencias de integridad.

`python -B scripts/verify-uml-pr17-readonly.py --root . --verify-java`, desde la raíz del repositorio, comprueba las nueve fuentes, los nueve PNG y los 50 blobs Java. El identificador de código de los registros es procedencia de estos archivos, no un HEAD permanente. Para volver a renderizar, usar la versión indicada en un directorio de salida separado y comparar contra el registro; no sobrescribir las fuentes o imágenes al verificar.

La [carpeta de entrega](../../../../entregables/README.md) incluye copias idénticas de los nueve pares.
