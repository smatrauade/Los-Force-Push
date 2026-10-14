# TODO — backlog vivo

Revisado 2026-10-14 tras verificar los merges de PR #1/#2 y ejecutar el spike local. El bootstrap documental y estructural está integrado. Esta rama incorpora un incremento experimental de carga; ninguna mecánica jugable está implementada.

## Bootstrap completado y verificado

- [x] Incorporar requisitos normalizados, decisiones, arquitectura/UML candidatos, trazabilidad, estrategia de pruebas y continuidad documental.
- [x] Crear la estructura inicial `src/main/java`, `src/main/resources` y `src/test/java` con placeholders, sin clases ni build.
- [x] Integrar el bootstrap en `main` mediante el [PR #1](https://github.com/smatrauade/Los-Force-Push/pull/1), fusionado el 2026-10-08.
- [x] Ejecutar el control read-only de documentación sobre la base integrada y limpia: PASS, 32 archivos y 49 enlaces relativos (2026-10-14).

La integración y estos controles no prueban revisión por otro integrante ni ratificación del diseño. Las tareas siguientes conservan su estado pendiente.

## Experimento local de carga R1

- [x] Contrato experimental de una capa y comparación con dos capas; OQ-08 continúa OPEN.
- [x] Subconjunto TXT externo → Parser → Builder → Level/Board, sin Swing.
- [x] Compilar sin warnings y ejecutar 40 casos reales con JDK 17 y JDK 26; evidencia en [LEVEL_LOADING_R1](docs/experiments/LEVEL_LOADING_R1.md).
- [x] Runners Unix/Windows sobre las mismas fuentes; revisión Bash 3.2/PowerShell 5.1, pruebas Linux con rutas/temporales Unicode y espacios, protección TXT/EOL.
- [ ] Ejecutar los mismos 40 casos nativamente en Windows 10/11 (PowerShell 5.1 y 7) y macOS; registrar sistema/JDK/comando/salida y cleanup.
- [ ] Revisar contrato, coste de Builder, modelo y pruebas con el equipo; D-011 sigue PREFERRED.
- [ ] Revisar el incremento y autorizar por separado las operaciones de integración/publicación.

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
- [ ] Movimiento y bloqueo; empuje de una sola caja.
- [ ] Cajas especiales, hielo y cerrojos conforme a decisiones cerradas.
- [ ] Undo, estado frágil/muros y métricas; Restart.
- [ ] Victoria/progresión, score y resumen.
- [ ] Swing: flechas, HUD, Undo/Restart visibles, render y SFX.
- [ ] Dos funcionalidades adicionales ratificadas.
- [ ] Actualizar UML/trazabilidad con código y evidencia real; no integrar spikes sin bytes auditados.

## Entregables

- [ ] README con instalación/build/ejecución/tests comprobados.
- [ ] Diagrama de clases StarUML `.mdj`; reconciliar con PlantUML.
- [ ] Informe de decisiones/justificaciones y declaración de IA.
- [ ] Assets de dominio público con registro de licencia individual.
- [ ] Video de gameplay.
- [ ] Decisión del equipo y cambio autorizado a repositorio público antes de entregar.
- [ ] Entrega por correo institucional con integrantes en copia.
- [ ] Ensayo de defensa individual del diseño, código, pruebas y patrones.

## Mantenimiento

Actualizar estado, decisiones, arquitectura/UML, fuentes y trazabilidad junto a cada cambio según [FRESHNESS_POLICY.md](docs/continuity/FRESHNESS_POLICY.md). Los pasos de implementación no quedan autorizados por su presencia en esta lista.
