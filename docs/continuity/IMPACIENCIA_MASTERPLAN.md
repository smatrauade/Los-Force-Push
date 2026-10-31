# MasterPlan individual — Sokoban

`DOMAIN = SOKOBAN_DOMAIN_RUNTIME_AND_MOVEMENT` · `AS_OF = 2026-10-31`

## Producto y separación

La meta es el TPO completo con niveles externos, mecánicas, interfaz, pruebas y entregables defendibles. El baseline común es `f3f91f92829b7e8e4bd3c0c07e3af4ffb0beb142`; la rama individual parte de él. El `main` remoto observado en R2, `f4a28fd6c1d869fcd6f6189e5cbc2da49bb2f7a2`, avanzó sólo en documentación frente al ancestro común. Ningún merge, rebase o incorporación es automático. Ver [estado individual](IMPACIENCIA_STATE.md) y fuentes normativas desde [AGENTS](../../AGENTS.md).

AS_IMPLEMENTED: carga legacy/C2 y definición Fragile inicial; motor heredado normal; runtime inmutable individual con cajas normales/Fragile/KeyBox, Lock, muros, movimiento e hielo construidos programáticamente; Swing conectado al runtime y a la campaña individual. R4 aporta sintaxis TXT individual de hielo y asociaciones. R5 incorpora sesión, historial y Restart; R6 implementa victoria, score y progresión individuales. TO_BE: adicionales, QA manual y entregables.

## Cobertura por requisito

| Requisito | Estado individual / dependencia |
|---|---|
| RF-01 | Entidades representadas en Swing; QA manual y aceptación pendientes. |
| RF-02 | Lector externo individual y Swing integrados; contrato oficial y aceptación pendientes. |
| RF-03 | Movimiento y empuje del runtime conectados a Swing; QA manual pendiente. |
| RF-04 | Hielo y caja normal en runtime; bordes no votados son hipótesis individuales. |
| RF-05 | Fragile y E04 en runtime; aún sin integración jugable. |
| RF-06 | KeyBox/Lock/muros conectados a Swing; OQ-07 y aceptación oficiales pendientes. |
| RF-07 | Sesión y controles 15/5/3 conectados; aceptación y bordes oficiales pendientes. |
| RF-08 | Evaluación y progresión conectadas a Swing; OQ-01 pendiente. |
| RF-09 | Score individual visible en Swing; fórmula TEAM OQ-09 pendiente. |
| RF-10 | HUD y Undo/Restart conectados en Swing; QA manual y aceptación pendientes. |
| RF-11 | Recursos presentes; integración completa, audio audible y derechos pendientes. |
| RF-12 | Dos adicionales y patrones elegibles pendientes de OQ-10. |
| RT-01 | Java/Swing parcial; build definitivo OQ-11. |
| RT-02 | README disponible; StarUML, informe y video pendientes. |
| RT-03 | Diseño y defensa individual pendientes; patrones requieren evidencia real. |
| RT-04 | IA declarada; comprensión y revisión humana pendientes. |

## Fases y Definition of Done

| Fase | Dependencia, resultado y gate mínimo |
|---|---|
| R2 | Runtime/Movement: adaptación sin pérdida, reglas autorizadas, tests y UML coherente; no integra Swing. |
| R3 | Núcleo individual KeyBox/Lock/muros: asociaciones, cierre atómico y regresión comprobadas; Observer permanece pendiente. |
| R4 | Extensión TXT individual implementada y conectada en R7; OQ-07/08 siguen abiertas oficialmente. |
| R5 | Sesión/Undo/Restart individuales implementados y probados con snapshots 15/5/3 y métricas E07–E09. |
| R6 | Victoria/progresión/score individuales implementados y probados; OQ-01/09 oficiales permanecen abiertas. |
| R7 | Swing integrado al runtime, Undo, Restart, score y campaña; probado headless y smoke gráfico. |
| R8 | Seleccionar dos adicionales elegibles con evidencia docente/equipo, patrón real y tests. |
| R9 | QA integral por misiones separadas: niveles resolubles, plataformas, GUI/audio y licencias verificadas. |
| R10 | Entregables independientes: StarUML `.mdj`, informe, video, README y ensayo de defensa. |

Cada fase requiere autorización, comparación con `main`, contratos, tests relevantes, documentación por impacto y evidencia del SHA probado. Ninguna fila habilita automáticamente la siguiente.

## Diseño y decisiones

PROBLEM → Board sólo representa cajas normales. OPTIONS → ampliar Board perdiendo identidad o crear estado tipado independiente. CHOICE → `RuntimeState` inmutable con terreno, jugador y cajas tipadas; adaptación de Level/ExperimentalFragileLevel. TRADEOFF → conversión explícita, sin segunda ocupación mutable; no se envía la plantilla incompleta a Movement. TEST → identidad, terreno, mezcla y rechazo de solapamientos.

PROBLEM → movimiento con hielo y rotura debe ser atómico. OPTIONS → mutar celdas incrementalmente o calcular transición antes de construir. CHOICE → `RuntimeMovementEngine` sin estado y `RuntimeMoveResult` con origen/destino/distancia/rotura. TRADEOFF → copia de lista y validación por transición; tests de bloqueo, deslizamiento y estado anterior.

`INDIVIDUAL_EXPERIMENTAL_DECISION`: si una caja entra en hielo y la salida está ocupada o fuera de límites, termina en la última celda libre; si la primera celda está bloqueada, no hay empuje ni decremento. El hielo es terreno exclusivo, sin combinación ICE+GOAL; R4 añadió sintaxis TXT individual. Ninguna hipótesis cierra OQ-02/08. E03 contra pared y E04 completar el deslizamiento antes de romper se respetan; E01/E02 y D-020 mantienen la autoridad registrada en [DECISION_LOG](../decisions/DECISION_LOG.md). E08 no se implementa aquí.

Candidatos Builder, Memento, Observer y Strategy sólo se acreditan con problema, roles, variación real, alternativa, consecuencias y pruebas de implementación. El estado inmutable no es por sí solo Memento; R5 implementa roles separados. El futuro Observer depende de OQ-07. No se declara aceptación TEAM ni del docente.

## Riesgos y protocolo

Integración necesita consumidor real, equivalencia de identidad/terreno, representación de hielo y asociaciones sin imponer gramática. Faltan criterios de cierre de muros, fórmula, selección de adicionales, derechos individuales de assets, StarUML `.mdj`, QA de GUI/audio y defensa; entrega informada 18/11. Las fuentes externas no disponibles no se redistribuyen.

Antes de cada incremento: rehidratar desde AGENTS → índice → estado individual → este plan → normas y código/pruebas del dominio; verificar branch, cambios locales, SHA remoto de `main` y rama individual. Comparar nuevos commits por contratos, comportamiento y pruebas, no sólo archivos; bloquear solapamientos no reconciliados. No incorporar avances automáticamente.

Regresión: compilar core con `--release 17 -encoding UTF-8 -Xlint:all -Werror` y sólo java.base; ejecutar Loading, Movement/runtime, C2 y Fragile por sus entrypoints, desktop headless y verificador según impacto. Registrar SHA, entorno, resultados y NOT_RUN. Publicar sólo con autorización vigente y fast-forward verificado de la rama individual; no alterar `main` ni usar force. Ver [TEST_STRATEGY](../testing/TEST_STRATEGY.md) para comandos reproducibles.

## Mecánicas dinámicas individuales R3

PROBLEM → Lock debe activar muros sin guardar estado divergente ni dejar entidades encerradas. OPTIONS → listeners mutables persistentes o derivar apertura del estado final. CHOICE → `RuntimeState.Lock` y `DynamicWall` son valores inmutables asociados por ID; un muro abre si al menos un Lock asociado contiene KeyBox. `RuntimeMoveResult.lockChanges()` informa sólo activaciones que cambiaron en una transición confirmada. TRADEOFF → se recalcula ocupación; no hay listeners globales ni segundo estado de apertura. TEST → múltiples Lock/muros, cambios efectivos, repetición y cierre ocupado.

`INDIVIDUAL_EXPERIMENTAL_DECISION`: IDs runtime por categoría; ID de cajas especiales único; cada celda especial tiene una definición; asociaciones no vacías; apertura OR entre cerrojos. Si desactivar un Lock deja un muro cerrado ocupado, se rechaza el empuje entero. El jugador abandona su casilla al empujar, por lo que no puede permanecer sobre un muro que se cierre en esa misma transición con las reglas actuales; se validan por separado el cierre hipotético y la construcción. ICE, LOCK y muro son terrenos excluyentes. Estas hipótesis son reversibles y no cierran OQ-07/08. Lock no es Goal; en R3 la victoria seguía pendiente, preservando E05. E06 se aplica al desactivar, salvo la transición inválida rechazada por la hipótesis individual de integridad.

Observer GoF **no implementado**: no hay Subject con suscripciones ni observadores concretos; los eventos inmutables de transición son datos y no constituyen el patrón. Listeners persistentes con estado duplicarían la apertura derivada y dificultarían restauración/atomicidad. Evaluar Observer real al integrar consumidores de nivel con ciclo de vida explícito, sin declarar D-010 ratificada.

## Carga externa individual R4

`LevelLoader.loadExtended(Path)` produce definición inmutable independiente del motor; `RuntimeState.fromExtended` preserva cajas, terreno, Lock y asociaciones. [Contrato individual](../requirements/IMPACIENCIA_RUNTIME_TXT_CONTRACT.md); no cierra OQ-07/08 ni modifica D-020. El fixture combinado prueba transiciones, no resolución del puzzle. R5 se implementa en la rama individual; Observer y StarUML `.mdj` siguen pendientes.

## Sesión individual R5

PROBLEM → Undo debe restaurar mundo y métricas de trayectoria sin borrar su costo. OPTIONS → lista interna de estados o Memento con caretaker separado. CHOICE → `GameSession` originator crea y restaura un snapshot privado; `GameMemento` es token opaco y `UndoManager` sólo almacena y limita 15 snapshots. TRADEOFF → tres responsabilidades pequeñas frente a lista interna; la encapsulación permite cambiar el contenido sin que el caretaker lo interprete. TEST → restauración exacta de cinco, límites, rotura y muros.

`INDIVIDUAL_EXPERIMENTAL_DECISION`: Restart recupera el estado inicial en memoria, limpia historia y reinicia movimientos/empujes y presupuesto, conservando el total de Undo del nivel. Cambio explícito de nivel reinicia también ese total; no hay métrica global ni lectura TXT en Restart. Los intentos bloqueados no cuentan; deslizamiento, rotura y eventos de muros integran un solo empuje. No cierra OQ-04/05/09 ni atribuye ratificación de Memento. E07/E08/E09 permanecen en [DECISION_LOG](../decisions/DECISION_LOG.md).

## Finalización individual R6

PROBLEM → RF-08 requiere detectar victoria sin estado duplicado. OPTIONS → guardar bandera en sesión o evaluación pura. CHOICE → `VictoryEvaluator` calcula desde `RuntimeState`; undo y restart anteriores al cierre actualizan el resultado automáticamente. TRADEOFF → recorrido de celdas por consulta. TEST → Goals vacíos, cajas extra, Fragile y E05.

`INDIVIDUAL_EXPERIMENTAL_DECISION`: ganar exige al menos un Goal y una caja superviviente, todos los Goals ocupados y todas las cajas en Goal. Normal, Fragile y Key cuentan sobre Goal; Key sobre Lock no cuenta. Muros abiertos no son Goal y no se exige mantener Lock activo. Una Fragile destruida no ocupa Goal; su rotura puede impedir completar un mapa. No cierra OQ-01 ni garantiza resolubilidad.

PROBLEM → RF-09/E10 requieren cálculo sustituible. OPTIONS → fórmula fija o Strategy inyectada. CHOICE → `ScoreStrategy` y `WeightedPenaltyScoreStrategy`; `LevelStats` procede de `GameSession`. TRADEOFF → una interfaz para una variación demostrada por prueba de otra estrategia. `INDIVIDUAL_EXPERIMENTAL_DECISION`: `max(0, 10000 - 10*moves - 25*pushes - 100*undoUsesTotal)`, sustracción saturada sin overflow; configuración no negativa. Undo restaura movimientos/empujes y acumula usos; Restart conserva esa penalización del nivel. No cierra OQ-09 ni ratifica la fórmula por TEAM.

PROBLEM → progresión no debe contaminar movimiento ni perder resultado ante un archivo inválido. CHOICE → `LevelCatalog` inmutable con IDs explícitos y formatos STANDARD/EXTENDED; `LevelProgression` carga sin fallback, conserva resultados y cambia nivel sólo tras validar carga. El cierre explícito es idempotente. Tras finalizar, move/Undo/Restart mediante el coordinador rechazan con `IllegalStateException` hasta avanzar; `GameSession` conserva su API. Al final se informa campaña completa. Sólo memoria, sin persistencia.

Fixtures realmente resueltos por el motor: [A](../../src/test/resources/levels/completion-a.txt) `RIGHT` (1 movimiento, 1 empuje) y [B](../../src/test/resources/levels/completion-b.txt) `RIGHT LEFT DOWN DOWN RIGHT RIGHT UP RIGHT UP LEFT LEFT` (11 movimientos, 3 empujes). En B la Key activa Lock, abre el muro de paso y termina en Goal; se verifica E05. [Pruebas](../testing/TEST_STRATEGY.md#finalización-individual-r6--2026-10-31). En R6, Swing, Observer, elegibilidad de extras, `.mdj` y entregables estaban pendientes.

## Integración Swing individual R7

IMPLEMENTED: `GameController` coordina `LevelProgression`; `GamePresentation` transporta `RuntimeState` inmutable y métricas, `BoardPanel` representa sus entidades y estado derivado. La selección STANDARD/EXTENDED es explícita; fallo de carga conserva partida. Dos niveles de `src/desktop/resources/levels` son copias verificadas de los fixtures resueltos de R6. Undo/Restart, victoria, score y avance provienen del core. Fragile con resistencia superior a tres usa sprite 3 sin cambiar el valor. GATE suena sólo si cambia un muro. TESTED_HEADLESS y TESTED_UI_SMOKE; PENDING_MANUAL_QA y audio audible. Observer GoF, adicionales elegibles, `.mdj` y entrega siguen pendientes. R8 requiere autorización.
