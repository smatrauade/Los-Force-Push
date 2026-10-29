# Registro vivo de decisiones

`LAST_REVIEWED = 2026-10-23` (ratificación mayoritaria de aspectos de reglas, no del código ni de la arquitectura). La autoridad se separa del estado. [Ratificación por mayoría — 2026-10-23](#ratificación-por-mayoría--2026-10-23) es el registro canónico de E01–E11; [OPEN_QUESTIONS](../requirements/OPEN_QUESTIONS.md) mantiene los pendientes. `ACCEPTED_SCOPE` significa `TEAM_MAJORITY_ACCEPTED_SCOPE` sólo en la dimensión votada. Los patrones PREFERRED y las propuestas técnicas no votadas conservan su estado; `TEAM_ACCEPTANCE = NOT_RECORDED` para el código. Las pruebas y merges no ratifican decisiones.

| ID | Problema / opción | Alternativa y estado | Justificación y consecuencias |
|---|---|---|---|
| D-001 | Stack 2D | Java/Swing `OFFICIAL`; versiones/build OQ-11 `OPEN` | Permite GUI exigida; no elegir versión por spikes Java 21 |
| D-002 | Core testeable y MVC | Independencia de Swing `PREFERRED`; lógica UI descartada | Reglas en Model, pruebas sin GUI; exige frontera de presentación |
| D-003 | Interacción discreta | Eventos `PREFERRED`, guiado por relato docente; loop continuo no justificado | Evita actualización permanente sin necesidad; reconsiderar sólo ante requisito real |
| D-004 | Estado de celda | Terrain + Occupant `PREFERRED`; clases combinatorias descartadas | Evita BoxOnGoal/PlayerOnIce; separa espacio y ocupación |
| D-005 | Variaciones de caja | Polimorfismo `PREFERRED`; checks distribuidos por tipo descartados | LSP y variación real sin ratificación nueva; aspectos de reglas en [E02](#e02), [E03](#e03) y [E04](#e04), con compatibilidad pendiente en E04 |
| D-006 | Implementar variantes | Herencia frente a composición `OPEN` | UML usa jerarquía ilustrativa; comparar claridad y extensibilidad real |
| D-007 | Revertir estado complejo | Memento `PREFERRED`; operaciones inversas como alternativa | Diseño de snapshots sin ratificar; [E07](#e07) acepta sólo el umbral de disponibilidad Undo, no Memento |
| D-008 | No borrar coste de Undo | Comportamiento de métricas [E09](#e09) `ACCEPTED_SCOPE`; SessionMetrics fuera del snapshot sigue `TEAM-PROPOSAL` | [E08](#e08) requiere revisión semántica; efectos de Restart en métricas pendientes; [OQ-04/OQ-09](../requirements/OPEN_QUESTIONS.md) |
| D-009 | Puntaje | ScoreStrategy `PREFERRED`; métricas penalizadas [E10](#e10) `ACCEPTED_SCOPE`; fórmula `OPEN` | [E09](#e09) aporta la distinción de contadores; base, coeficientes y normalización pendientes en [OQ-09](../requirements/OPEN_QUESTIONS.md) |
| D-010 | Lock notifica muros | Observer `PREFERRED`, con indicación oral de incorporación según ASR del 21/10; conducta de cierre [E06](#e06) `ACCEPTED_SCOPE` | IDs, cardinalidad, ocupación al cerrar y restauración siguen pendientes en [OQ-07](../requirements/OPEN_QUESTIONS.md); no ratifica Observer |
| D-011 | Armar nivel de TXT | Builder `PREFERRED`, construcción directa alternativa pendiente | Sugerido como válido por profesor; separar sintaxis/ensamblado sólo si spike aporta claridad |
| D-012 | Coordinar construcción | Parser como Director opcional `PREFERRED` | Puede dirigir pasos sin clase extra; operaciones dependen de TXT ratificado |
| D-013 | Loader con clones | Prototype `REJECTED-FOR-NOW` | No hay creación costosa/clonación demostrada; reconsiderar si cambia el problema |
| D-014 | Loader con fábrica GoF | Factory Method `REJECTED-FOR-NOW` como mecanismo principal | No surge jerarquía Creator; helper con new no es Factory Method |
| D-015 | Familias de productos | Abstract Factory `REJECTED-FOR-NOW` para loader | No hay familias coherentes intercambiables; no inventarlas |
| D-016 | Sonido global | Singleton `OPEN`, propuesto; AudioService frontera `PREFERRED` | Ejemplo didáctico no obligación; dependencia explícita facilita test y evita global en dominio |
| D-017 | Interfaz incompatible | Adapter `REJECTED-FOR-NOW` en baseline | No hay interfaz externa/legada incompatible identificada |
| D-018 | Combinación de extras | Decorator `OPEN` como candidato futuro | Sin funcionalidad seleccionada no hay justificación; no forzar en cajas base |
| D-019 | Progreso de niveles | LevelCatalog `PREFERRED`; simplificar interfaz como alternativa | El aspecto de victoria [E05](#e05) es `ACCEPTED_SCOPE`; validación de puzzle y combinación con E06 pendientes en [OQ-01](../requirements/OPEN_QUESTIONS.md) |
| D-020 | Contrato TXT externo | Carga externa `TEACHER-EXPLICIT`; alcances [E01](#e01)/[E02](#e02) `ACCEPTED_SCOPE`; gramática `OPEN` | [OQ-06/OQ-08](../requirements/OPEN_QUESTIONS.md): disposición y resistencia individual no fijan alfabeto, metadatos ni parámetros; D-011/D-012 siguen PREFERRED |
| D-021 | Licencias | Dominio público `OFFICIAL`; Kenney/Pixabay fuentes sugeridas | Verificar recurso individual; recomendación del sitio no prueba licencia |
| D-022 | Adicionales | Dos exigidas `OFFICIAL`; primera opción [E11](#e11) `ACCEPTED_SCOPE`; segunda `OPEN` | Pistas 2/5 `NOT_APPROVED`; patrón y criterios de persistencia pendientes en [OQ-10](../requirements/OPEN_QUESTIONS.md); guardar/recuperar cuenta como una funcionalidad |
| D-023 | Diseño antes de código | Revisión UML/responsabilidades `PREFERRED`, criterio docente | Evita primer output IA sin revisión; UML candidato no basta como evidencia de ejecución |
| D-024 | Abstracciones y datos | Fuente única de estado y variación real `PREFERRED`, criterio COURSE | Evitar interfaces ceremoniales, redundancia, Controller monolítico y violaciones LSP |
| D-025 | Historial documental | Archivos vivos + cápsulas inmutables | Contrato de bootstrap; esta integración corrige vivos y conserva bytes históricos |

## Evolución y ratificación

El relato histórico pasó de fábrica probable a Builder preferido tras Clase 9; Prototype dejó de ser sólo pendiente para ser descartado provisionalmente. La idea de métricas Undo dentro del snapshot fue desplazada **como propuesta** por separación de SessionMetrics. El comportamiento de métricas se ratifica ahora sólo en el alcance de E09; la elección de SessionMetrics y el resto de la arquitectura siguen sin ratificación.

Los IDs D-* de la cápsula anterior tienen otra asignación: mapear por tema, no inferir identidad sólo por número. La evidencia `E3` de allí se reexpresa como `HISTORICAL_EXTERNAL_EVIDENCE` en documentos vivos.

Para cada nueva decisión registrar problema, opciones, estado, autoridad/fuente, fecha, participantes que ratifican, justificación, consecuencias, pruebas y documentos afectados. Una supersesión referencia la decisión anterior; no borra historia ni reescribe cápsulas.

Los apartados experimentales siguientes conservan el estado y la evidencia de sus fechas; la ratificación del 23/10 está en la sección posterior. No leer sus antiguas OQ OPEN como negación de acuerdos posteriores.

## Seguimiento experimental R1 — 2026-10-14

D-011/D-012 permanecen PREFERRED: [comparación Builder/directa](../experiments/LEVEL_LOADING_R1.md) sobre el mismo contrato e invariantes. Se ensaya un Builder concreto interno por carga, sin interfaz/Director adicional; encapsula acumulación y publicación a coste de una clase. La alternativa directa puede satisfacer las mismas garantías. 30 casos PASS no equivalen a ratificación del equipo.

El formato referido por D-020 y OQ-08 sigue OPEN: una capa provisional limitada a siete símbolos, sin resolver resistencia, IDs ni entidades fuera del alcance. Java target/build (OQ-11, dentro de D-001) siguen OPEN; Java/Swing permanece OFFICIAL: release 17 con compilador/runtime 26.0.2 es un entorno experimental, no target/build aceptado. Autoridad de esta ejecución: solicitud del usuario del 2026-10-14; hipótesis y análisis propios, sin nuevas aclaraciones docentes ni decisiones grupales. No se cierran otras preguntas ni se adoptan jerarquías de cajas a partir de enums temporales.

## Seguimiento de validación R3 — 2026-10-14

Correcciones del diagnóstico de tamaño y validación temprana, 38 casos PASS con compilación/ejecución real en JDK 17 y JDK 26, contrafactual detectado y UML experimental compilado/renderizado. Son controles del incremento, no decisiones del equipo: D-011/D-012 continúan PREFERRED, OQ-08/OQ-11 OPEN. [Detalle reproducible](../experiments/LEVEL_LOADING_R1.md).

## Hipótesis experimental de movimiento R1 — 2026-10-14

Por solicitud del usuario se ensaya transición funcional `Board + Direction → MoveResult + Board`, frente a agregado runtime encapsulado. Se reutiliza Board inmutable de Level Loading sin modificarlo ni duplicar posiciones. El coste de copia/validación y la alternativa futura quedan en [MOVEMENT_R1](../experiments/MOVEMENT_R1.md). Alcance: piso/destino/pared/borde/jugador/caja normal; no Memento ni otras mecánicas. Estado EXPERIMENTAL, sin ratificación del equipo ni nueva autoridad docente. D-001/002/003/004/005/006/007/011/012 y OQ-01…OQ-11 mantienen sus estados anteriores; los PASS sólo validan este subconjunto.

## Ensayo Swing autorizado — 2026-10-15

Procedencia: solicitud directa del usuario para SOKOBAN_SWING_UI_INTEGRATION; implementación EXPERIMENTAL, sin ratificación TEAM. Se ensayan D-002/D-003 con eventos discretos y separación MVC. GameView permite pruebas sin pantalla frente a acoplar el controlador a JFrame. GamePresentation comparte el Board inmutable; copiar una grilla mutable introduciría otra autoridad espacial sin necesidad. En el baseline del 15/10, las fuentes desktop separadas mantenían el límite java.base del core frente a runners que recorrían raíces completas. El PR #4 posteriormente restringió core a los paquetes level/movement. La recuperación del 21/10 conserva ambos mecanismos, sin cambiar el estado de estas decisiones.

[Implementación, consecuencias, pruebas y límites](../experiments/SWING_VERTICAL_SLICE_V1.md). Contadores sólo demostrativos, sin adoptar semántica Undo/score. La lectura local acotada en EDT evita concurrencia adicional en este spike, con riesgo explícito de demora en filesystems lentos. No se modifican estados de las decisiones, no se cierra OQ-01…OQ-11 y no se agregan patrones para mecánicas pendientes.

## Ratificación por mayoría — 2026-10-23

Autoridad **TEAM**, subordinada al enunciado y aclaraciones documentadas de cátedra. El solicitante confirmó mayoría absoluta de **cinco integrantes**, umbral **3/5** personas por opción. Participación y número de selecciones son magnitudes distintas: el ausente no se cuenta como voto y el denominador sigue siendo cinco. No se repetirán las encuestas para reabrir lo ya mayoritario.

```text
SOURCE = USER_SUPPLIED_SCREENSHOT_TRANSCRIPTION
RAW_SCREENSHOTS = EXTERNAL_PRIVATE
RAW_VERIFICATION = NOT_PERFORMED_BY_THIS_AGENT
GAME_RULE_DECISIONS = MAJORITY_ACCEPTED_PARTIAL
TEAM_ACCEPTANCE = NOT_RECORDED
```

Fuente: transcripción de once capturas del 23/10 aportada y revisada por el solicitante, con método confirmado, no lectura independiente de originales en esta ejecución. La exportación de WhatsApp contiene preguntas/discusiones, **no votos**. Capturas, nombres, teléfonos, fotos e IDs particulares no se publican; no se inventan hashes ni participantes. [Disponibilidad](../continuity/SOURCE_REGISTRY.md#encuestas-del-equipo--2026-10-23). Las encuestas no son una aprobación del profesor, de patrones, UML, implementación o funcionamiento.

### Recuentos y alcance canónico

Los IDs E01–E11 siguientes localizan las decisiones dentro de este registro, no crean otro catálogo D. `ACCEPTED_SCOPE` acepta exactamente la dimensión indicada; `OPEN_DETAILS` conserva las especificaciones no votadas. `CONFLICT_REVIEW_REQUIRED` conserva el resultado mayoritario pero impide afirmar compatibilidad normativa o cerrar toda la OQ.

| Encuesta | Participaron | Alternativa ganadora literal | Votos ganadora | Resto de selecciones | Estado y alcance / trazabilidad |
|---|---|---|---|---|---|
| <a name="e01"></a>E01 | 5/5 | Una capa con símbolos combinados | 4/5 | Dos capas: 1; otra: 0 | `ACCEPTED_SCOPE`; disposición del tablero, no alfabeto/metadata. `OPEN_DETAILS`; D-020 / OQ-08 |
| <a name="e02"></a>E02 | 5/5 | Resistencia configurable por cada caja frágil en TXT | 4/5 | Valor fijo: 1; por nivel: 0 | `ACCEPTED_SCOPE`; individualidad, no sintaxis/rango/errores. `OPEN_DETAILS`; D-005/D-020 / OQ-06/OQ-08 |
| <a name="e03"></a>E03 | 4/5 | Hielo contra pared: detenerse en última casilla libre | 4/5 | Cancelar empuje: 0 | `ACCEPTED_SCOPE`; sólo el comportamiento votado. `OPEN_DETAILS` para bordes y otras obstrucciones; D-005 / OQ-02 |
| <a name="e04"></a>E04 | 5/5 | Fragile que llega a cero al entrar en hielo: terminar deslizamiento y romperse después | 4/5 | Rotura inmediata: 2 | Mayoría registrada; `CONFLICT_REVIEW_REQUIRED` frente a rotura al cero. Seis selecciones: una persona eligió ambas; D-005 / OQ-03 |
| <a name="e05"></a>E05 | 5/5 | KeyBox en Lock NO cuenta como destino final: también debe llegar a Goal normal | 5/5 | Lock sí cuenta como destino: 1 | `ACCEPTED_SCOPE`; seis selecciones, una persona eligió ambas: no unanimidad exclusiva. `OPEN_DETAILS` de victoria/puzzle; D-019 / OQ-01 |
| <a name="e06"></a>E06 | 4/5 | Al retirar KeyBox del Lock, los muros vuelven a cerrarse | 3/5 | Permanecen abiertos: 1 | `ACCEPTED_SCOPE`; retorno a cerrado, sin decidir IDs/cardinalidad/ocupación. `OPEN_DETAILS`; D-010 / OQ-07 |
| <a name="e07"></a>E07 | 4/5 | Undo no procede con menos de cinco movimientos disponibles | 4/5 | Deshacer parcialmente: 0 | `ACCEPTED_SCOPE`; umbral resuelto, no interfaz ni pruebas de borde. `OPEN_DETAILS`; D-007 / OQ-05 |
| <a name="e08"></a>E08 | 5/5 | Sólo Restart o cambio de nivel reinician el límite de tres Undo | 5/5 | Movimiento válido: 0; cualquier intento: 0 | Mayoría literal; `CONFLICT_REVIEW_REQUIRED` con «consecutivos». Movimiento posterior no recupera usos según la opción, sin darla por compatible; D-008 / OQ-04 |
| <a name="e09"></a>E09 | 4/5 | Undo revierte movimientos/empujes; total de usos de Undo se acumula | 3/5 | Todas las métricas acumuladas: 1 | `ACCEPTED_SCOPE`; contadores restaurables frente a total acumulado, no efectos de Restart. `OPEN_DETAILS`; D-008/D-009 / OQ-09 |
| <a name="e10"></a>E10 | 5/5 | Score penaliza movimientos, empujes y Undo | 5/5 | Otras dos opciones: 0 y 0 (textos no aportados) | `ACCEPTED_SCOPE`; variables, no fórmula, base, coeficientes ni normalización. `OPEN_DETAILS`; D-009 / OQ-09 |
| <a name="e11"></a>E11 (múltiple) | 5/5 | Guardar y recuperar partidas | 4/5 | Pistas: 2; otra propuesta: 1; resto: 0 | `ACCEPTED_SCOPE` como primera adicional. Pistas 2/5 y propuesta sin descripción aprobada 1/5: `NOT_APPROVED`; segunda, contratos y GoF `OPEN_DETAILS`; D-022 / OQ-10 |

E01–E10 alcanzan el umbral de al menos 3/5 en la alternativa mayoritaria; E04 y E08 requieren revisión de compatibilidad antes de convertir esa preferencia en regla implementable. En E03/E06/E07/E09 participaron cuatro personas; no representan «4/4 del equipo». E04/E05 conservaron selecciones contrapuestas y E11 fue múltiple: no sumar selecciones como personas, ni llamar a E05 unanimidad exclusiva. No se deduce identidad de votantes ni se completa el voto ausente.

### Compatibilidad y especificación pendiente

1. **E01/E02 — TXT:** una sola capa de símbolos combinados debe poder expresar resistencia individual. Alfabeto parametrizado, tokens, leyenda o metadata asociada son alternativas sin elegir; no introducir una segunda capa encubierta. Especificar ejemplos válidos/inválidos y errores en el siguiente dominio, preservando el parser experimental actual.
2. **E04 — Fragile/hielo:** la consigna normalizada exige romper al llegar a cero. La preferencia de terminar el deslizamiento se conserva, pero su compatibilidad no está demostrada. Interpretar el empuje como transición atómica es sólo una **hipótesis no ratificada**. Bloquear cierre de OQ-03 e implementación de esa regla; contrastar fuentes y ejemplos, consultar al profesor sólo si persiste contradicción material.
3. **E05/E06 — victoria y cierre:** no asumir resolubilidad de niveles ni que sea válido cerrar un muro sobre jugador/caja. Ocupación, múltiples cerrojos, IDs, Undo y validación de puzzle son pendientes; no inventar una solución al cierre.
4. **E07/E08/E09 — Undo:** el umbral aprobado no redefine historial 15 ni retroceso exacto 5. E08 dice literalmente que sólo Restart/cambio de nivel reinician el límite; no reinterpretar un movimiento nuevo como reinicio. Su tensión con «consecutivos» sigue en OQ-04. E09 no fija qué ocurre con cada métrica en Restart/cambio de nivel.
5. **E10 — score:** no elegir base, pesos, límites, signo final o normalización por inferencia. D-009 conserva la fórmula abierta y ScoreStrategy PREFERRED.
6. **E11 — persistencia:** guardar/recuperar es una sola funcionalidad, no dos. Formato, validación/seguridad, estado de Undo, compatibilidad y patrón GoF real siguen pendientes. Pistas no está aprobada; otra confirmación de al menos 3/5 o una alternativa con mayoría es necesaria para la segunda, sin repetir la elección de la primera.

### Límites de la ratificación

Sin encuesta: OQ-11 Java/build permanece OPEN; `--release 17` y los comandos actuales son experimentales. D-011/D-012, D-016 y demás patrones, herencia/composición, UML y código mantienen sus estados técnicos. No hay nueva prueba ejecutada ni aceptación académica global. [REQUIREMENTS](../requirements/REQUIREMENTS.md) conserva el texto normativo normalizado y separa los refinamientos TEAM; [OPEN_QUESTIONS](../requirements/OPEN_QUESTIONS.md) es el único inventario canónico de deuda.

Siguiente gate: revisión humana del Draft documental y, en otra misión, `SOKOBAN_TXT_GRAMMAR_SPECIFICATION` con compatibilidad de reglas especiales en dominios acotados. No otra ronda general para volver a votar aspectos ya mayoritarios; sin permiso de merge ni implementación por este registro.

## Evidencia docente adicional — 29/10/2026

[Procedencia y timestamps](../continuity/SOURCE_REGISTRY.md#fuentes-académicas-aportadas--29102026): USER_SUPPLIED_TRANSCRIPT_SUMMARY / AUDIO_NOT_VERIFIED. Esta revisión agrega evidencia; conserva íntegros los recuentos E01–E11 y sus estados. TEAM_ACCEPTANCE = NOT_RECORDED.

- D-010: según ASR del 21/10, Observer debe incorporarse en alguna parte del TPO. Lock→Walls es el ejemplo docente concreto, pendiente de OQ-07 e implementación. Es una indicación docente más fuerte que la sugerencia registrada antes; no es ratificación TEAM del diseño ni código existente.
- D-011/D-012: Builder es opcional con alternativa justificada; el LevelBuilder concreto ensaya ensamblado incremental. Defender intención, alternativa y consecuencias antes de atribuir Builder GoF completo. Considerar creación/factorías no impone Factory Method ni Abstract Factory; D-014/D-015 no cambian.
- D-007: comparar Command (acciones) y Memento (estados) para Undo; no exigir ambos ni presentar Board inmutable como Memento implementado.
- D-009: Strategy es candidato para score; E10 no fija fórmula. D-018: Decorator/power-up sigue ejemplo posible, sin aprobar segunda adicional.
- D-013/D-016: no hay justificación actual de Prototype; audio no obliga Singleton. MVC se defiende por reglas del Model, coordinación del Controller y presentación Swing, según el código; no se reconstruyen frases docentes ausentes.
