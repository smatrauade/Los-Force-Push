# TODO — backlog vivo

Revisado 2026-10-08. El bootstrap prepara documentación y estructura; ninguna mecánica está implementada.

## Bloqueantes y decisiones

- [ ] Revisar el PR documental por otro integrante y ratificar/corregir las propuestas.
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
- [ ] Comparar Builder con construcción directa mediante spike acotado, tras especificar TXT.
- [ ] Ratificar Terrain/Occupant, cajas polimórficas y herencia frente a composición.
- [ ] Revisar fronteras Memento, Observer y ScoreStrategy sin interfaces ceremoniales.
- [ ] Decidir AudioService/SoundManager; Singleton sólo es una propuesta.

## Implementación posterior — otra iteración autorizada

- [ ] Primer build reproducible y organización de packages.
- [ ] TXT externo → parser/construcción → Level/Board y pruebas de validación.
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
