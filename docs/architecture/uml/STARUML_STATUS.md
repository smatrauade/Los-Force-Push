# Estado de UML y StarUML

`LAST_REVIEWED = 2026-10-29`, `STARUML_DELIVERABLE = PENDING`.

La entrega exige un diagrama de clases en **StarUML**. No hay `.mdj` versionado ni se afirma haber abierto StarUML. PlantUML facilita revisión/versionado y no sustituye ese entregable.

## Reconciliacion tras PR #8/#9 — 28/10/2026

Los diagramas de implementacion de Loading y Swing se actualizaron para C2LevelParser,
PixelAssets, PixelTheme, SoundManager/Effect, UiCommands, LevelResult y hooks default de
GameView. El diagrama Movement conserva sus bytes: no cambio ese contrato.
El candidato integral conserva su hash historico y no representa clases implementadas
por inferencia. SoundManager desktop es por ventana, no el Singleton propuesto;
LevelResult desktop solo muestra datos calculados por un futuro servicio.

La evidencia de validacion R3 que sigue corresponde a los diagramas del 21/10,
no certifica estos cambios. La comprobacion nueva de sintaxis se registra en
[TEST_STRATEGY](../../testing/TEST_STRATEGY.md). Sigue pendiente crear/revisar el .mdj
en StarUML; ni un render ni el compilador de PlantUML lo sustituyen.

## Alcance de los diagramas

| Diagrama | Clasificación | Alcance |
|---|---|---|
| [level-loading-spike-v1.puml](level-loading-spike-v1.puml) | AS_IMPLEMENTED | Legacy/C2-L7, Board/Builder y carga explícita de definición ExperimentalFragileLevel; no gameplay especial |
| [movement-core-spike-v1.puml](movement-core-spike-v1.puml) | AS_IMPLEMENTED | Motor sin estado, Direction, MoveResult y referencias inmutables al core |
| [swing-playable-spike-v1.puml](swing-playable-spike-v1.puml) | AS_IMPLEMENTED | MVC pixel art, hooks GameView, recursos, audio y callbacks; Board compartido |
| [sokoban-candidate.puml](sokoban-candidate.puml) | TO_BE / CANDIDATE | Modelo integral v0.5 y decisiones pendientes; no representa toda la implementación actual |

El candidato v0.5 estaba en el ZIP: SHA-256 `07e86f32bc2ea5c7091508698b415efbf18e9bce45464bb477cb154e5a268901`, preservado byte a byte. La cápsula histórica también contiene el source. Sus títulos, fechas y participantes no significan aprobación actual.

GameSession, cajas especiales, Memento, Observer, Strategy y progresion siguen siendo candidatos. Audio de presentacion ya esta implementado por SoundManager; el Singleton del candidato sigue propuesto. Herencia, interfaces y firmas candidatas siguen revisables; SessionMetrics no esta ratificado. No hay extras definitivas modeladas.

## Validación por iteración

En el bootstrap del 08/10 se verificaron límites @startuml/@enduml, identidad del source y correspondencia conceptual; no se ejecutó un compilador/render en aquella iteración. Esa evidencia histórica no describe una validación nueva.

La revisión Swing R3 del 21/10 contrastó los tres diagramas AS_IMPLEMENTED con clases, firmas relevantes, direcciones de colaboración y ownership reales. No se detectó un desajuste material que requiera cambiar los `.puml`. El diagrama Swing muestra GameController → GameView/LevelLoader/MovementEngine/GamePresentation; GameWindow implementa GameView y posee panel/controlador; GamePresentation y BoardPanel referencian el mismo Board inmutable. El candidato permanece separado.

Los cuatro `.puml` pasaron comprobación sintáctica y render PNG mediante PlantUML 1.2020.02 y Graphviz 14.1.2 ya instalados, con `java.awt.headless=true` sólo en el proceso. La JVM de esa herramienta fue Java 25.0.4.1; las pruebas del proyecto usaron JDK 17.0.20.1/release 17. Renderizar no demuestra semántica del dominio ni apertura en StarUML. Los outputs y logs de auditoría son externos al repositorio.

Siguiente gate académico: revisión grupal de responsabilidades/relaciones/firmas, cierre de decisiones afectadas y creación del `.mdj`. Cuando exista, declarar fuente de diseño canónica y mantener ambos artefactos sincronizados o documentar explícitamente supersesión. La falta del `.mdj` no impide revisar un Draft PR de este subconjunto experimental.

## Impacto de la entrega — 29/10/2026

Según [Teams aportado](../../requirements/REQUIREMENTS.md#entrega-y-aceptación-docente--29102026), no hace falta mostrar código/diagramas en la demo de 15–20 minutos del 18/11; **el archivo StarUML sigue requerido**. En la reconciliación documental anterior no se editaron ni compilaron diagramas. El código y el diagrama de Loading presentes reflejan el agregado experimental Fragile, su record anidado y las dos entradas de Loader en el UML de Loading; los futuros roles runtime siguen pendientes y el candidato conserva su carácter propuesto. La indicación ASR de Observer es deuda de implementación/defensa, no evidencia de clases existentes. Coordinar la actualización de diagramas y .mdj en un incremento autorizado.

## Incremento filesystem TXT — 29/10/2026

El diagrama de Loading fue contrastado con firmas y relaciones reales: `readText` privado compartido, método explícito experimental, parser existente y agregado/FragileBox inmutables. No hay nueva clase Java ni cambio de Movement/Swing/candidato. La comprobación de sintaxis PlantUML de este diagrama se registra en TEST_STRATEGY; no certifica StarUML ni aprobación del diseño. El `.mdj` final sigue pendiente.

## Alcance semántico del contrato — 29/10/2026

No se alteraron diagramas ni arquitectura funcional en este cierre. Las notas antiguas de `level-loading-spike-v1.puml` «OQ-06/OQ-08 pending» y «grammar not ratified» conservan el corte previo del spike: quedan supersedidas por [D-020](../../decisions/DECISION_LOG.md#d-020--decisión-del-responsable-txt--29102026) para el subconjunto soportado, adoptado por MODULE_OWNER_DECISION. OQ-06 resuelta en TXT; OQ-08 extensiones abiertas. Las firmas/relaciones del diagrama siguen describiendo el código, la definición sigue no jugable y el candidato integral no se ratifica. Los `.puml` se preservan por la restricción de no editar UML funcional de esta misión; esta aclaración evita interpretar una anotación histórica como inventario vigente de decisiones.


## Runtime individual R2 — 31/10/2026

El diagrama Movement incorpora `RuntimeState`, cajas tipadas, resultado y motor sin estado, junto al motor heredado. Este modelo inmutable sustituye para este incremento las entidades mutables ilustrativas del candidato, evitando duplicar ocupación y preservando snapshots; no ratifica el candidato ni acredita StarUML. TXT de hielo, integración Swing y `.mdj` siguen pendientes. Ver [plan y decisiones](../../continuity/IMPACIENCIA_MASTERPLAN.md).


## Mundo dinámico individual R3 — 31/10/2026

El diagrama Movement refleja Lock, muros, asociaciones y eventos de transición derivados; Observer GoF permanece pendiente. El `.mdj` de StarUML sigue pendiente; el candidato v0.5 se conserva sin cambios. [Hipótesis y alternativas](../../continuity/IMPACIENCIA_MASTERPLAN.md).


## Carga externa individual R4 — 31/10/2026

El diagrama de Loading incorpora parser y definición extendidos y la dependencia del adaptador desde movement hacia level. El candidato histórico permanece sin cambios; `.mdj` de StarUML sigue pendiente. La extensión es individual y Swing no la consume.


## Sesión individual R5 — 31/10/2026

El UML Movement muestra sesión, token opaco y caretaker, con snapshot privado del originator y métricas acumulativas fuera del memento. El candidato histórico permanece sin cambios; `.mdj` de StarUML sigue pendiente.


## Finalización individual R6 — 31/10/2026

El diagrama Movement incorpora evaluación pura, Strategy de score y progresión con catálogo explícito y resultados. El candidato histórico no cambia; `.mdj` de StarUML sigue pendiente.
