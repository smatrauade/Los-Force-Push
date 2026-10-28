# TODO — backlog vivo

Revisado 2026-10-23 tras registrar las decisiones mayoritarias, sin cambios de implementación. Tras el merge del PR #5: Swing V1 está integrado en main con el core (#3) y el aislamiento de runners (#4). El bootstrap documental y estructural está integrado. Este snapshot contiene carga, movimiento/empuje básico e integración Swing experimentales; el producto completo y sus mecánicas restantes siguen pendientes. Los resultados históricos de carga y movimiento se distinguen de las reproducciones en [TEST_STRATEGY](docs/testing/TEST_STRATEGY.md).

## Bootstrap completado y verificado

- [x] Incorporar requisitos normalizados, decisiones, arquitectura/UML candidatos, trazabilidad, estrategia de pruebas y continuidad documental.
- [x] Crear la estructura inicial `src/main/java`, `src/main/resources` y `src/test/java` con placeholders, sin clases ni build.
- [x] Integrar el bootstrap en `main` mediante el [PR #1](https://github.com/smatrauade/Los-Force-Push/pull/1), fusionado el 2026-10-08.
- [x] Ejecutar el control read-only de documentación sobre la base integrada y limpia: PASS, 32 archivos y 49 enlaces relativos (2026-10-14).

La integración y estos controles no prueban revisión por otro integrante ni ratificación del diseño. Cada tarea siguiente distingue el trabajo experimental completado de los gates todavía pendientes.

## Experimento local de carga R1

- [x] Spike C2-L7 del 24/10: envelope opt-in, sólo siete combinaciones existentes, atributos/especiales rechazados; `EXPERIMENTAL_NOT_RATIFIED`. Sin publicación al corte inicial de implementación; comprobar el estado posterior en Git/GitHub. [Estado y límites](docs/continuity/CURRENT_STATE.md#spike-local-c2-l7--2026-10-24).
- [x] Ejecutar Loading 40, Movement 31, C2 32 y Swing headless 16 con JDK 17/release 17; [evidencia y ejecución separada de C2](docs/testing/TEST_STRATEGY.md#spike-experimental-c2-l7--2026-10-24). Suites y runners históricos intactos.
- [ ] Revisar el spike C2-L7 con el equipo; no cerrar OQ-06/07/08/11 ni habilitar entidades especiales por estos resultados.
- [x] Contrato experimental R1 y comparación histórica; el alcance mayoritario posterior y gramática pendiente de OQ-08 se consultan en DECISION_LOG/OPEN_QUESTIONS.
- [x] Subconjunto TXT externo → Parser → Builder → Level/Board, sin Swing.
- [x] Compilar sin warnings y ejecutar 40 casos reales con JDK 17 y JDK 26; evidencia en [LEVEL_LOADING_R1](docs/experiments/LEVEL_LOADING_R1.md).
- [x] Runners Unix/Windows sobre las mismas fuentes; revisión Bash 3.2/PowerShell 5.1, pruebas Linux con rutas/temporales Unicode y espacios, protección TXT/EOL.
- [ ] Completar validación nativa macOS de los 40 casos; Windows PS5.1/PS7 ya fue validado en RUNNER R3, registrado en TEST_STRATEGY.
- [ ] Revisar contrato, coste de Builder, modelo y pruebas con el equipo; D-011 sigue PREFERRED.

## Movimiento experimental — implementación y validación R1–R3

- [x] Comparar transición funcional y agregado runtime; ensayar Board inmutable sin GameState redundante.
- [x] Implementar movimiento cardinal y empuje básico de una caja normal sobre piso/destino, con bloqueos por pared/borde y sin arrastre ni cadenas; alcance IMPLEMENTED_EXPERIMENTAL validado, RF-03/RF-04 PARTIAL.
- [x] Mantener íntegros los 40 tests de carga y ejecutar 31 de movimiento con Java 17/26; 2.664 transiciones adicionales dentro del caso de invariantes.
- [x] Runners con suite adicional opcional y una sola compilación; UML y [evidencia](docs/experiments/MOVEMENT_R1.md).
- [x] Revisión técnica independiente R2 del movimiento, tests, UML y documentación; sin defectos funcionales bloqueantes.
- [x] Preparar el incremento experimental de movimiento con código, pruebas, UML y documentación coherentes para su versionado.
- [x] Reproducir en R3 los 40 casos de carga y 31 de movimiento con JDK 17/26 en Linux, sin warnings y con salida 0.
- [ ] Revisar el movimiento con el equipo; la aceptación grupal no está registrada.
- [ ] Completar pruebas nativas macOS de ambas suites; Windows ya validado, preservar decisiones OPEN/PREFERRED.

## Primer incremento Swing — experimental, 2026-10-15

- [x] Integrar carga TXT externa, Board inmutable y MovementEngine sin modificar el core; ventana 2D, cuatro flechas, empuje normal y contadores demostrativos.
- [x] Comando Windows de compilación/ejecución y 16 pruebas de integración sin pantalla.
- [x] Ejecutar core 40/40 + 31/31 e interacción gráfica real Windows con movimiento, empuje y bloqueo; [evidencia y límites](docs/experiments/SWING_VERTICAL_SLICE_V1.md).
- [x] Revisión técnica Swing R3: MVC/responsabilidades, UML implementado frente a candidato y autocontención del baseline recuperado; 87 pruebas PASS desde exportación limpia y correcciones sólo documentales.
- [x] Publicar `feat/swing-playable-notebook-v1` y abrir el [PR #5](https://github.com/smatrauade/Los-Force-Push/pull/5) contra `main`; posteriormente fusionado el 21/10 mediante `54c03d4520dfc542c125686e5446687dc07d85db`. Los cuatro commits del PR se conservan, registrados en [CURRENT_STATE](docs/continuity/CURRENT_STATE.md).
- [ ] Revisión humana independiente **posterior al merge** del código Swing integrado mediante PR #5 y triage de OQ-01…OQ-11; registrar observaciones y propuestas acotadas, y correcciones sólo en otra iteración autorizada. No inferir aprobación grupal de un merge técnico.
- [x] Validación Linux R2 del árbol combinado: 40 Loading + 31 Movement + 16 desktop headless PASS, cero FAIL y salida 0, JDK 17/release 17 y C.utf8; evidencia en la guía y TEST_STRATEGY.
- [x] Revalidar GUI interactiva **Windows del HEAD PR #5**: flechas, empujes, bloqueo, contadores, TXT externos válidos/inválidos; [evidencia](docs/continuity/checkpoints/WINDOWS_PR5_NATIVE_QA_2026-10-21.md).
- [ ] GUI Swing interactiva Linux y macOS nativo; Windows actual no los sustituye.
- [x] Acreditar la regresión Windows del aislamiento #4 en el árbol combinado del PR #5: CLI 9/9 casos por PowerShell 5.1/7; [checkpoint actual](docs/continuity/checkpoints/WINDOWS_PR5_NATIVE_QA_2026-10-21.md).
- [ ] Resolver carga asíncrona/cancelable si se necesitan filesystems lentos o remotos.
- [ ] Ratificación del contrato TXT/Java/build y arquitectura; placeholders no sustituyen imágenes definitivas.

## Bloqueantes y decisiones

Siguiente dominio propuesto: `SOKOBAN_TXT_GRAMMAR_SPECIFICATION`, con las decisiones del PR #7 ya integradas y compatibilidad de reglas especiales en dominios acotados. No repetir una ronda general de encuestas ni implementar por inferencia. `TEAM_ACCEPTANCE = NOT_RECORDED` del código.

- [x] Registrar las once encuestas en [DECISION_LOG](docs/decisions/DECISION_LOG.md#ratificación-por-mayoría--2026-10-23) y sus alcances/pendientes en [OPEN_QUESTIONS](docs/requirements/OPEN_QUESTIONS.md), sin otro catálogo.
- [x] Integrar decisiones mayoritarias mediante PR #7 con autorización del autor; no equivale a revisión formal independiente.
- [ ] Revisar código y propuestas arquitectónicas por separado; `TEAM_ACCEPTANCE = NOT_RECORDED`.
- [ ] OQ-01: completar criterios de victoria/puzzle tras E05 y compatibilidad con E06.
- [ ] OQ-02: especificar bordes de E03, sin reabrir el comportamiento ya mayoritario.
- [ ] OQ-03: resolver compatibilidad normativa de E04 antes de fijar la regla implementable.
- [ ] OQ-04: contrastar la semántica literal de E08 con «consecutivos», sin reinterpretarla.
- [x] OQ-05: registrar el umbral aceptado E07; no implica cerrar el contrato completo.
- [ ] OQ-05: especificar interfaz y casos de borde del historial/disponibilidad.
- [ ] OQ-06: sintaxis, rango y errores de la configuración individual ya aceptada en E02.
- [ ] OQ-07: IDs, cardinalidad, ocupación y Undo del cierre aceptado en E06; combinar con E05.
- [ ] OQ-08: gramática, símbolos/metadata, dimensiones, entidades e IDs de E01/E02; no inventar otra capa.
- [ ] OQ-09: fórmula numérica y efectos de Restart/cambio de nivel tras el alcance E09/E10.
- [ ] OQ-10: segunda adicional con mayoría y contratos/GoF de la primera seleccionada E11; pistas no aprobada.
- [ ] OQ-11: Java target y build; producir y verificar comandos desde clon limpio.
- [ ] Ratificar Builder frente a construcción directa después de revisar la comparación experimental R1; no aceptado por compilar.
- [ ] Ratificar Terrain/Occupant, cajas polimórficas y herencia frente a composición.
- [ ] Revisar fronteras Memento, Observer y ScoreStrategy sin interfaces ceremoniales.
- [ ] Decidir AudioService/SoundManager; Singleton sólo es una propuesta.

## Implementación posterior — otra iteración autorizada

- [ ] Primer build reproducible y organización de packages.
- [ ] Carga productiva TXT externo → parser/construcción → Level/Board: el spike R1 es parcial y experimental; contrato final y entidades restantes siguen pendientes.
- [ ] Consolidar movimiento productivo tras revisión del subconjunto experimental; el incremento Swing agrega flechas; las reglas adicionales y consolidación productiva permanecen pendientes.
- [ ] Cajas especiales, hielo y cerrojos conforme a decisiones cerradas.
- [ ] Undo, estado frágil/muros y métricas; Restart.
- [ ] Victoria/progresión, score y resumen.
- [ ] Completar presentación del producto: HUD definitivo, Undo/Restart funcionales, imágenes auditadas y SFX; el spike Swing básico no cierra este entregable.
- [ ] Especificar y validar primera adicional seleccionada y obtener mayoría para la segunda; no contar guardar/recuperar como dos.
- [ ] Mantener UML/trazabilidad del resto de mecánicas al implementarlas, con código y evidencia real; no integrar spikes sin bytes auditados.

## Entregables

- [ ] README con instalación/build/ejecución/tests comprobados.
- [ ] Diagrama de clases StarUML `.mdj`; reconciliar con PlantUML.
- [ ] Informe de decisiones/justificaciones y declaración de IA.
- [ ] Assets de dominio público con registro de licencia individual.
- [ ] Video de gameplay.
- [x] Visibilidad pública verificada mediante API el 2026-10-21; esta recuperación no la modifica.
- [ ] Entrega por correo institucional con integrantes en copia.
- [ ] Ensayo de defensa individual del diseño, código, pruebas y patrones.

## Mantenimiento

Actualizar estado, decisiones, arquitectura/UML, fuentes y trazabilidad junto a cada cambio según [FRESHNESS_POLICY.md](docs/continuity/FRESHNESS_POLICY.md). Los pasos de implementación no quedan autorizados por su presencia en esta lista.

Las operaciones de publicación e integración de ramas se rastrean mediante Git/GitHub y el PR correspondiente. Este backlog conserva las capacidades implementadas, las tareas funcionales y los controles técnicos o grupales que todavía corresponden al producto.
