# Movimiento R1 — núcleo experimental

`AS_OF = 2026-10-14`, `STATUS = EXPERIMENTAL`, `DOMAIN = SOKOBAN_BASIC_MOVEMENT`.
Base versionada localmente: `44b3c63b437465b5f54c1f0f5ca5cbadb5def0e5`, incremento [Level Loading R1–R4](LEVEL_LOADING_R1.md). La rama `feat/movement-core-spike-v1` contiene cambios de movimiento pendientes de revisión y todavía sin commit en esta ejecución. No hay publicación ni integración en main. Estos hechos operativos se registran como evidencia fechada; el diseño no depende de que permanezcan así.

## Autoridad y alcance

RF-03 exige movimiento cardinal, colisiones, empuje de una sola caja y ausencia de arrastre; RF-04 incluye caja normal, pero hielo queda fuera de este subconjunto. La solicitud actual autoriza únicamente jugador/caja normal sobre FLOOR/GOAL, WALL y bordes. El [registro de decisiones](../decisions/DECISION_LOG.md) conserva los estados del equipo: TXT experimental/OQ-08 OPEN, Builder PREFERRED, Java/build OQ-11 OPEN. No se resuelven preguntas sobre hielo, cajas especiales, cerrojos, Undo, victoria, score o extras.

## Modelo en ejecución: alternativas

| Fuerza | A: transición funcional | B: agregado runtime encapsulado |
|---|---|---|
| Cohesión / Expert | Board conserva invariantes espaciales; motor calcula una transición | Agregado administra estado dinámico y delega reglas |
| Encapsulamiento / aliasing | Valores inmutables, ninguna mutación del estado previo | Exige controlar mutaciones y vistas expuestas |
| Estado único | Board es el estado espacial completo; no se guardan posiciones paralelas | Separar terreno base y ocupación puede reducir copias, pero exige nuevos contratos |
| Construcción / coste | Busca jugador, copia filas y valida Board: O(alto × ancho) por éxito | Puede reducir copias; aumenta gestión del estado mutable |
| Undo futuro | Estados previos intactos; no constituye implementación Memento | Requiere snapshot/restauración atómica; todavía no implementada |
| Tests / defensa | Entrada/salida explícitas, secuencias independientes y resultados comparables | Debe probarse ciclo de vida y aislamiento del agregado |
| Variación real actual | Un Board sin sesión/estadísticas basta | No hay todavía ciclo de partida que justifique un agregado adicional |

Se elige **A para este experimento**: `Board + Direction → MoveResult(kind, Board)`. No se agrega GameState porque sólo envolvería Board en este alcance. Level conserva su Board inicial; cada llamada recibe el estado espacial vigente y devuelve el siguiente. Un llamador puede retener esa referencia, pero el motor no conserva estado ni una segunda copia vigente. Estados anteriores inmutables son valores históricos, no fuentes de verdad paralelas.

El coste de copiar/validar el rectángulo completo es explícito; máximo experimental 100×100 del cargador. Se reutilizan Cells inmutables donde no cambian ocupantes. Si futuras entidades dinámicas requieren otro modelo, comparar alternativas nuevamente con evidencia. No se promete rendimiento para gameplay ni se adopta este modelo como arquitectura definitiva.

## Responsabilidades y contrato de transición

Fuentes: `src/main/java/ar/edu/uade/sokoban/movement`. [UML acotado](../architecture/uml/movement-core-spike-v1.puml).

| Tipo | Responsabilidad / GRASP |
|---|---|
| Direction | Cuatro desplazamientos cardinales de una celda; sin eventos de teclado |
| MovementEngine | Servicio sin estado: consulta Board, comprueba colisiones y crea un estado válido (Creator) |
| MoveResult y su enum Kind | Datos de resultado MOVED/PUSHED/BLOCKED y Board resultante; sin métricas/HUD/score |
| Board/Cell/Position existentes | Expert espacial e invariantes; no se modifica ningún tipo del paquete level |

SRP: lectura/sintaxis/construcción de niveles permanecen en level; movimiento no lee archivos ni coordina UI. No hay interfaces/jerarquías especulativas, factories GoF ni nuevo patrón adoptado. Los checks sobre Occupant.BOX pertenecen al enum del subconjunto de cajas normales; no son una decisión sobre polimorfismo de cajas especiales.

1. Consultar el único jugador y la celda cardinal siguiente; comprobar coordenadas firmadas antes de crear Position.
2. Fuera del tablero o WALL: BLOCKED.
3. Si hay BOX, comprobar una única celda posterior: dentro de límites, sin WALL y con Occupant.EMPTY; otra caja bloquea. No buscar cadenas ni arrastrar cajas detrás del jugador.
4. Sólo tras pasar los checks se copian filas y se reemplazan ocupantes preservando Terrain: origen vacío, destino jugador y, en un empuje, caja en la celda posterior.
5. Construir Board con las invariantes existentes; sólo después publicar MoveResult. MOVED/PUSHED retornan un Board nuevo; BLOCKED retorna la misma instancia de entrada.

Invariantes: dimensiones, cantidad de cajas y todos los terrenos/destinos conservados; exactamente un jugador dentro del rectángulo; paredes sin ocupantes; estado previo intacto; ausencia de Level/Board parcial observable. Null en APIs nuevas es error de llamador (NullPointerException). Kind es un hecho de transición, no semántica de contabilización de movimientos/empujes. MoveResult es un valor de datos; las garantías entre estado anterior, Kind y estado siguiente las establece MovementEngine.move, no la creación manual de un record por terceros.

## Ejecución reproducible y portabilidad

Se reutilizan los runners existentes para compilar una sola vez todas las fuentes y ejecutar la suite del cargador primero. El modo por defecto conserva únicamente sus 40 casos. La opción de movimiento agrega la segunda suite; si la primera falla, no se continúa como si hubiera pasado.

Desde la raíz del checkout, JDK completo 17 o superior:

```bash
SPIKE_JDK_HOME='<directorio-del-JDK>' bash scripts/test-level-loading.sh --movement
```

```powershell
$env:SPIKE_JDK_HOME = '<directorio-del-JDK>'
powershell -NoProfile -File .\scripts\test-level-loading.ps1 -IncludeMovement
# PowerShell 7: reemplazar powershell por pwsh.
```

Selección SPIKE_JDK_HOME → JAVA_HOME → PATH; también se admite invocación desde otro directorio indicando la ruta del script. Compilación estricta: `--release 17 --limit-modules java.base -encoding UTF-8 -Xlint:all -Werror`. Temporales propios fuera del checkout, limpieza acotada y exitcode no cero ante compilación/tests fallidos. Sin instalación automática, cambio global, Maven/Gradle ni dependencia externa. La lógica de movimiento no usa filesystem, localización, APIs del SO o Swing; la integración de tests usa Path/Files y UTF-8 explícito.

## Evidencia actual — 2026-10-14

| Entorno | Cargador | Movimiento | Alcance |
|---|---|---|---|
| Linux, JDK 17.0.20.1 | 40 PASS / 0 FAIL | 31 PASS / 0 FAIL | Compilación y runtime reales sin java.desktop |
| Linux, JDK 26.0.2 | 40 PASS / 0 FAIL | 31 PASS / 0 FAIL | Release 17; runtime real 26, invocación desde otro directorio |
| Linux, JDK 17, copia con checkout/TMPDIR Unicode y espacios, charset US-ASCII | 40 PASS / 0 FAIL | 31 PASS / 0 FAIL | UTF-8 explícito e invocación externa; temporales limpiados |
| Windows PowerShell 5.1/7 | NOT_RUN nativo | NOT_RUN nativo | Extensión del runner revisada estáticamente |
| macOS / Bash 3.2 / BSD | NOT_RUN nativo | NOT_RUN nativo | Extensión conserva compatibilidad por diseño |

Tests: [MovementTest.java](../../src/test/java/ar/edu/uade/sokoban/movement/MovementTest.java); los 40 casos del cargador permanecen byte a byte intactos. Esta evidencia corresponde a las fuentes de esta rama sobre la base citada; no afirma que el HEAD base contenga movimiento. El informe de revisión identifica los bytes mediante manifiesto; la siguiente revisión debe contrastar Git y el filesystem. No se ejecutaron los spikes A/B históricos.

| Casos de movimiento | Comprobaciones |
|---|---|
| 01–05 | Cuatro direcciones a piso y entrada a destino; tableros completos esperados |
| 06–07 | Paredes y bordes en las cuatro direcciones |
| 08–12 | Empuje normal en cuatro direcciones y sobre destino |
| 13–16 | Pared/borde/segunda caja bloquean; caminar alejándose no arrastra |
| 17–20 | Destinos permanecen al abandonar jugador/caja; entrar otra vez y superposiciones |
| 21–23 | Estado inicial intacto, secuencias independientes y determinismo con outcomes/tablero final exactos |
| 24–25 | TXT externo → Level → Board → movimiento; modificar archivo cambia el resultado, nombre Unicode/espacios, LF/CRLF |
| 26–29 | Identidad en bloqueo, inmutabilidad profunda, nulos y ausencia de Swing/java.desktop |
| 30 | 2.664 transiciones acotadas: todas las posiciones de jugador en 3×3, 0–2 cajas, dos variantes de terreno y cuatro direcciones; invariantes, cardinalidad y preservación |
| 31 | Board máximo 100×100, movimiento válido y bordes |

Son 31 casos nombrados con múltiples verificaciones, no 31 requisitos completos. Los resultados de movimiento se contrastan con tableros esperados y todas las transiciones de la enumeración verifican terreno/cajas/jugador/dimensiones. No dependen de assert habilitable con -ea ni de orden de filesystem.

Controles negativos sobre una copia temporal propia: desactivar todos los empujes produjo 20 PASS / 11 FAIL en movimiento, manteniendo 40 PASS del cargador y salida 1. Un error sintáctico deliberado produjo salida no cero del compilador, sin RESULT falso. Una opción Unix desconocida produjo salida 2; el modo por defecto siguió dando 40 PASS sin ejecutar movimiento. Todos los temporales propios se limpiaron; fuentes originales preservadas.

## Límites y siguiente gate

No hay hielo, cajas especiales, cerrojos/muros dinámicos, Undo/Memento, Restart, victoria/progresión, score, estadísticas, Swing, teclado, audio ni extras. RF-03/RF-04 se cubren sólo en el subconjunto espacial de caja normal; el input por flechas y deslizamiento siguen pendientes. El TXT y el modelo de entidades futuras no se amplían. PlantUML del incremento es evidencia de diseño experimental y no completa StarUML.

Siguiente iteración: revisión técnica independiente de código/tests/UML/diff y de la alternativa funcional, seguida de preparación de un commit local sólo con autorización separada. Pruebas Windows/macOS siguen pendientes. No ampliar mecánicas ni ratificar decisiones a partir de los PASS. Incremento preparado con apoyo de IA, sujeto a comprensión y defensa individual del equipo.
