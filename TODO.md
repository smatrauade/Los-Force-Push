# TODO — backlog vivo

Revisado 28/10/2026. PR #8 (TXT C2-L7) y #9 (Swing pixel art) están integrados; [estado y merges](docs/continuity/CURRENT_STATE.md). Implementación experimental y cobertura parcial no equivalen a aceptación del producto: TEAM_ACCEPTANCE = NOT_RECORDED.

## Base disponible

- [x] Bootstrap, requisitos normalizados, decisiones TEAM y continuidad.
- [x] Loading UTF-8 externo, Board inmutable y movimiento/empuje normal.
- [x] C2-L7 integrado: opt-in, siete combinaciones; atributos/especiales rechazados.
- [x] Swing pixel art con menú, HUD, pausa, opciones, ayuda, zoom y audio.
- [x] Versionar 31 PNG y 12 WAV con manifiesto; contratos de integración del Punto 5.
- [x] QA combinado anterior del 28/10: Loading 40, Movement 31, C2 32, Desktop 16, Assets 44, UI 17 y CLI 18; [procedencia](docs/testing/TEST_STRATEGY.md).
- [ ] Revisión humana del código integrado y comprensión/defensa individual.
- [ ] Completar QA manual actual, escucha de audio y validación nativa macOS; no extrapolar de smoke automatizado ni pruebas antiguas.

## Próximo dominio TXT/niveles

**Contrato experimental de atributos individuales.** Existe una [propuesta verificable para OQ-06/OQ-08](docs/requirements/FRAGILE_TXT_CONTRACT_PROPOSAL.md), pendiente de acuerdo del equipo. Revisar el prototipo inmutable y especificar su integración futura sin habilitar gameplay ni ratificar límites por inferencia.

- [ ] Definir sintaxis, rangos, errores y ejemplos de resistencia individual aceptada en E02, preservando la disposición E01.
- [ ] Definir IDs, cardinalidad, ocupación al cerrar y restauración Lock↔Walls (OQ-07).
- [ ] Ratificar el contrato TXT completo (OQ-08) sin confundir el spike C2 con aceptación.
- [ ] Mantener compatibilidad legacy y rechazo explícito de funciones no soportadas hasta su implementación autorizada.

## Reglas y módulos pendientes

Los acuerdos cerrados y conflictos están sólo en [DECISION_LOG](docs/decisions/DECISION_LOG.md); [OPEN_QUESTIONS](docs/requirements/OPEN_QUESTIONS.md) conserva OQ-01…OQ-11. No repetir una encuesta general.

- [ ] OQ-01/02/07: completar bordes de victoria, hielo y cierre de muros según los alcances votados.
- [ ] OQ-03/04: resolver compatibilidad normativa E04/E08 antes de implementar esas transiciones.
- [ ] OQ-05: interfaz y pruebas del umbral Undo ya aceptado; conservar historial 15 y retroceso 5.
- [ ] OQ-09: fórmula numérica y efectos de Restart/cambio de nivel en métricas.
- [ ] OQ-10: contratos de guardar/recuperar como primera adicional y mayoría para una segunda; pistas no aprobada.
- [ ] OQ-11: decidir Java/build del producto con evidencia; release 17 actual es provisional.
- [ ] Implementar y probar entidades especiales, hielo, Fragile/KeyBox/Lock y muros dinámicos en dominios propios.
- [ ] Implementar sesión, Undo, Restart y métricas; conectar UiCommands con disponibilidad real.
- [ ] Implementar victoria/progresión y score; entregar LevelResult calculado por el servicio correspondiente.
- [ ] Integrar persistencia real y la segunda adicional, con GoF justificado.
- [ ] Adaptar BoardPanel a los tipos/DTO reales; no inferir resistencia desde sprites.

## Diseño y entrega

- [ ] Ratificar Builder/directa, Terrain/Occupant, herencia/composición y los patrones candidatos; compilar no los ratifica.
- [ ] Mantener UML de implementación y candidato separados; crear StarUML .mdj.
- [ ] Revisar titularidad/derechos y documentar licencia de cada recurso: [política](docs/assets/ASSET_POLICY.md) y [manifiesto](docs/punto5/ASSETS_REGISTRO_SHA256.csv).
- [ ] Informe con decisiones/justificaciones, declaración de IA y evidencia de revisión real.
- [ ] Video de gameplay, entrega institucional con integrantes en copia y ensayo de defensa.
- [ ] Evaluar E/S cancelable fuera del EDT si se necesitan archivos lentos/remotos.

## Mantenimiento

Actualizar [fuentes vivas](docs/continuity/FRESHNESS_POLICY.md), pruebas y trazabilidad junto a los cambios; preservar cápsulas/checkpoints. El verificador y su regresión se ejecutan según [README](README.md). Este backlog no autoriza implementar ni publicar por sí mismo.

## Spike experimental de atributos individuales

- [x] Probar representación inmutable experimental para Fragile sin habilitar gameplay.
- [ ] Revisar con el equipo sintaxis, rango y errores del contrato final OQ-06/OQ-08; los límites del spike no son reglas aceptadas.
