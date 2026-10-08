# Registro vivo de decisiones

`LAST_REVIEWED = 2026-10-08`. La autoridad se separa del estado. No hay ratificación formal de arquitectura aportada a este bootstrap. Ninguna fila `PREFERRED`/`TEAM-PROPOSAL` se considera `ACCEPTED`. Las pruebas históricas no modifican ese estado.

| ID | Problema / opción | Alternativa y estado | Justificación y consecuencias |
|---|---|---|---|
| D-001 | Stack 2D | Java/Swing `OFFICIAL`; versiones/build OQ-11 `OPEN` | Permite GUI exigida; no elegir versión por spikes Java 21 |
| D-002 | Core testeable y MVC | Independencia de Swing `PREFERRED`; lógica UI descartada | Reglas en Model, pruebas sin GUI; exige frontera de presentación |
| D-003 | Interacción discreta | Eventos `PREFERRED`, guiado por relato docente; loop continuo no justificado | Evita actualización permanente sin necesidad; reconsiderar sólo ante requisito real |
| D-004 | Estado de celda | Terrain + Occupant `PREFERRED`; clases combinatorias descartadas | Evita BoxOnGoal/PlayerOnIce; separa espacio y ocupación |
| D-005 | Variaciones de caja | Polimorfismo `PREFERRED`; checks distribuidos por tipo descartados | Comportamiento cerca de datos; contrato base debe respetar LSP |
| D-006 | Implementar variantes | Herencia frente a composición `OPEN` | UML usa jerarquía ilustrativa; comparar claridad y extensibilidad real |
| D-007 | Revertir estado complejo | Memento `PREFERRED`; operaciones inversas como alternativa | Snapshots incluyen cajas/muros/contadores; coste memoria y aislamiento; evidencia histórica A |
| D-008 | No borrar coste de Undo | SessionMetrics fuera del snapshot `TEAM-PROPOSAL` | Discusión 30/09; moves/pushes retroceden, total Undo no; reinicios pendientes |
| D-009 | Puntaje | ScoreStrategy `PREFERRED`, fórmula/penalización `OPEN` | Política explícita y determinista; no fabricar strategies ni aceptar penalización sin acuerdo |
| D-010 | Lock notifica muros | Observer `PREFERRED`, sugerencia docente | Desacopla Subject de muros concretos; asociación/cierre OQ-07 y restauración coherente |
| D-011 | Armar nivel de TXT | Builder `PREFERRED`, construcción directa alternativa pendiente | Sugerido como válido por profesor; separar sintaxis/ensamblado sólo si spike aporta claridad |
| D-012 | Coordinar construcción | Parser como Director opcional `PREFERRED` | Puede dirigir pasos sin clase extra; operaciones dependen de TXT ratificado |
| D-013 | Loader con clones | Prototype `REJECTED-FOR-NOW` | No hay creación costosa/clonación demostrada; reconsiderar si cambia el problema |
| D-014 | Loader con fábrica GoF | Factory Method `REJECTED-FOR-NOW` como mecanismo principal | No surge jerarquía Creator; helper con new no es Factory Method |
| D-015 | Familias de productos | Abstract Factory `REJECTED-FOR-NOW` para loader | No hay familias coherentes intercambiables; no inventarlas |
| D-016 | Sonido global | Singleton `OPEN`, propuesto; AudioService frontera `PREFERRED` | Ejemplo didáctico no obligación; dependencia explícita facilita test y evita global en dominio |
| D-017 | Interfaz incompatible | Adapter `REJECTED-FOR-NOW` en baseline | No hay interfaz externa/legada incompatible identificada |
| D-018 | Combinación de extras | Decorator `OPEN` como candidato futuro | Sin funcionalidad seleccionada no hay justificación; no forzar en cajas base |
| D-019 | Progreso de niveles | LevelCatalog `PREFERRED`; simplificar interfaz como alternativa | Hace explícito siguiente nivel; justificar frontera y final del catálogo |
| D-020 | Contrato TXT externo | Documentarlo/cargarlo `TEACHER-EXPLICIT`; formato OQ-08 `OPEN` | Profesor podría editar/escribir TXT; aceptación por autor distinto del parser |
| D-021 | Licencias | Dominio público `OFFICIAL`; Kenney/Pixabay fuentes sugeridas | Verificar recurso individual; recomendación del sitio no prueba licencia |
| D-022 | Adicionales | Dos exigidas `OFFICIAL`, selección OQ-10 `OPEN` | Patrón de obligación base no cuenta automáticamente; controlar alcance |
| D-023 | Diseño antes de código | Revisión UML/responsabilidades `PREFERRED`, criterio docente | Evita primer output IA sin revisión; UML candidato no basta como evidencia de ejecución |
| D-024 | Abstracciones y datos | Fuente única de estado y variación real `PREFERRED`, criterio COURSE | Evitar interfaces ceremoniales, redundancia, Controller monolítico y violaciones LSP |
| D-025 | Historial documental | Archivos vivos + cápsulas inmutables | Contrato de bootstrap; esta integración corrige vivos y conserva bytes históricos |

## Evolución y ratificación

El relato histórico pasó de fábrica probable a Builder preferido tras Clase 9; Prototype dejó de ser sólo pendiente para ser descartado provisionalmente. La idea de métricas Undo dentro del snapshot fue desplazada **como propuesta** por separación de SessionMetrics; no hubo ratificación formal aportada.

Los IDs D-* de la cápsula anterior tienen otra asignación: mapear por tema, no inferir identidad sólo por número. La evidencia `E3` de allí se reexpresa como `HISTORICAL_EXTERNAL_EVIDENCE` en documentos vivos.

Para cada nueva decisión registrar problema, opciones, estado, autoridad/fuente, fecha, participantes que ratifican, justificación, consecuencias, pruebas y documentos afectados. Una supersesión referencia la decisión anterior; no borra historia ni reescribe cápsulas.
