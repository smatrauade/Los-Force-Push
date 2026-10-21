# TODO — backlog vivo

Revisado 2026-10-21 tras publicar Swing V1 mediante el Draft PR #5 contra main con los PR #3 y #4 integrados. El bootstrap documental y estructural está integrado. Este snapshot contiene carga, movimiento/empuje básico e integración Swing experimentales; el producto completo y sus mecánicas restantes siguen pendientes. Los resultados históricos de carga y movimiento se distinguen de las reproducciones en [TEST_STRATEGY](docs/testing/TEST_STRATEGY.md).

## Bootstrap completado y verificado

- [x] Incorporar requisitos normalizados, decisiones, arquitectura/UML candidatos, trazabilidad, estrategia de pruebas y continuidad documental.
- [x] Crear la estructura inicial `src/main/java`, `src/main/resources` y `src/test/java` con placeholders, sin clases ni build.
- [x] Integrar el bootstrap en `main` mediante el [PR #1](https://github.com/smatrauade/Los-Force-Push/pull/1), fusionado el 2026-10-08.
- [x] Ejecutar el control read-only de documentación sobre la base integrada y limpia: PASS, 32 archivos y 49 enlaces relativos (2026-10-14).

La integración y estos controles no prueban revisión por otro integrante ni ratificación del diseño. Cada tarea siguiente distingue el trabajo experimental completado de los gates todavía pendientes.

## Experimento local de carga R1

- [x] Contrato experimental de una capa y comparación con dos capas; OQ-08 continúa OPEN.
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
- [x] Publicar `feat/swing-playable-notebook-v1` y abrir el [PR #5](https://github.com/smatrauade/Los-Force-Push/pull/5) contra `main`; OPEN/Draft, sin fusionar. Los dos commits publicados en R4 se conservan, registrados en [CURRENT_STATE](docs/continuity/CURRENT_STATE.md).
- [ ] Revisión humana del PR #5 por otro integrante; mantener el Draft mientras están pendientes la revisión y las decisiones del equipo.
- [x] Validación Linux R2 del árbol combinado: 40 Loading + 31 Movement + 16 desktop headless PASS, cero FAIL y salida 0, JDK 17/release 17 y C.utf8; evidencia en la guía y TEST_STRATEGY.
- [ ] GUI Swing interactiva Linux y macOS nativo; Windows histórico no los sustituye.
- [ ] Acreditar la regresión Windows del parche de aislamiento #4; no confundirla con Windows del original.
- [ ] Resolver carga asíncrona/cancelable si se necesitan filesystems lentos o remotos.
- [ ] Ratificación del contrato TXT/Java/build y arquitectura; placeholders no sustituyen imágenes definitivas.

## Bloqueantes y decisiones

- [ ] Revisar con otro integrante y ratificar/corregir las propuestas documentales y arquitectónicas; registrar las decisiones explícitas del equipo.
- [ ] OQ-01: victoria con KeyBox sobre Lock.
- [ ] OQ-02: salida del hielo bloqueada.
- [ ] OQ-03: Fragile a resistencia cero al entrar en hielo.
- [ ] OQ-04: reinicio de consecutividad Undo (incluye movimiento nuevo y Restart).
- [ ] OQ-05: Undo con menos de cinco movimientos.
- [ ] OQ-06: resistencia inicial/configurable Fragile.
- [ ] OQ-07: IDs, cardinalidad Lock↔Walls y cierre posterior.
- [ ] OQ-08: formato TXT, símbolos, capas, dimensiones, metadata, errores y charset.
- [ ] OQ-09: fórmula score y ratificación de métricas no restaurables.
- [ ] OQ-10: dos funcionalidades adicionales, problema, patrón, consecuencias y prueba.
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
- [ ] Dos funcionalidades adicionales ratificadas.
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
