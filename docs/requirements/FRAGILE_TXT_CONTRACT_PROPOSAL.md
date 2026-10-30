# Propuesta de contrato TXT C2 — atributos individuales Fragile (R1)

- `STATUS = MODULE_OWNER_DECIDED_SUPPORTED_SUBSET / EXPERIMENTAL_NON_PLAYABLE_DEFINITION`
- `AS_OF = 2026-10-29`
- `DOMAIN = SOKOBAN_TXT_LEVEL_DEFINITION_AND_VALIDATION`
- `HISTORICAL_PROTOTYPE_BASE = 986ab548e7955752d3542035d6f9d573534b257d`

> **Contrato adoptado por el responsable TXT para el subconjunto soportado; definición especial no jugable.** El nombre histórico de propuesta se conserva para no romper rutas. El prototipo se integró por [PR #11](https://github.com/smatrauade/Los-Force-Push/pull/11); su esquema soportado se adopta expresamente el 29/10 por `MODULE_OWNER_DECISION`, registrada en [D-020](../decisions/DECISION_LOG.md#d-020--decisión-del-responsable-txt--29102026). No es nuevo voto TEAM ni aprobación del código. Las extensiones e integración runtime siguen propuestas/dependencias según [OPEN_QUESTIONS](OPEN_QUESTIONS.md).

Para integrar desde otro módulo: [matriz de interfaces, ejemplos y preguntas pendientes](#traspaso-txt-a-modelmovement--29102026). Describe las APIs existentes; no prescribe una interfaz runtime nueva.

## 1. Autoridad, estado y alcance

- **E01 (TEAM, ACCEPTED_SCOPE):** una capa espacial con símbolos combinados. No define el alfabeto final ni la sección de metadatos.
- **E02 (TEAM, ACCEPTED_SCOPE):** cada caja frágil obtiene su **resistencia inicial individual del TXT**. No define sintaxis, rango ni errores.
- **OQ-06:** `RESOLVED_TXT_SCHEMA_BY_MODULE_OWNER`; sintaxis, rango y validación/errores soportados adoptados en D-020. E02 por sí sola no los votó.
- **OQ-08:** `PARTIAL_RESOLVED_TXT_SUPPORTED_SUBSET / FUTURE_ENTITIES_OPEN`; envoltura, estados ordinarios, Fragile, atributos, dimensiones, charset/límites/errores actuales decididos. Sólo las extensiones no soportadas quedan abiertas.
- **OQ-07:** relación Lock↔Walls y reglas de cierre queda fuera de esta propuesta; no resolverla implícitamente con los IDs de Fragile.
- **OQ-11:** Java/build definitivo no ratificado; `--release 17` es el target de los runners existentes, no una nueva decisión.

El incremento de este documento se limita al **formato, las invariantes y la interfaz de salida del parser de niveles**. No modifica Movement, Swing, Undo, persistencia, victoria ni el modelo del juego. El mecanismo actual de carga jugable sigue rechazando atributos especiales.

## 2. Contrato experimental existente (comportamiento implementado)

El código de referencia vive en [ExperimentalFragileParser](../../src/main/java/ar/edu/uade/sokoban/level/ExperimentalFragileParser.java), [ExperimentalFragileLevel](../../src/main/java/ar/edu/uade/sokoban/level/ExperimentalFragileLevel.java) y [ExperimentalFragileParserTest](../../src/test/java/ar/edu/uade/sokoban/level/ExperimentalFragileParserTest.java).

**AS_IMPLEMENTED / MODULE_OWNER_DECIDED:** ejemplo aceptado por `parse(String)` experimental y `loadExperimentalFragile(Path)`, rechazado por `load(Path)` jugable:

```text
!sokoban-txt 2
[grid]
# # # # # #
# @ {floor|fragile:f1} {goal|fragile:f2} . #
# # # # # #
[attributes]
fragile f1 resistance=3
fragile f2 resistance=12
```

La disposición usa la envoltura C2 (`!sokoban-txt 2`, `[grid]`, `[attributes]`) y una sola capa con tokens separados por **un espacio ASCII**. Para los siete estados ordinarios, el parser reutiliza `LevelParser`/`C2LevelParser`, sin duplicar sus invariantes espaciales. Para Fragile reconoce exclusivamente `{floor|fragile:<id>}` y `{goal|fragile:<id>}` y la declaración `fragile <id> resistance=<entero>`.

Los IDs implementados siguen `[a-z][a-z0-9_]{0,31}`. La resistencia adopta la forma decimal canónica `[1-9][0-9]*`, entre **1 y 2.147.483.647** (sin signo, ceros iniciales ni espacios); no se deduce un máximo de tres por la existencia de sprites. Es dominio de representación/validación TXT, sin garantizar cantidad de empujes ni solución del puzzle; no es resistencia remanente. Cada ID es único por archivo/definición y cada referencia espacial y declaración aparecen exactamente una vez, sin default. El orden de las declaraciones no altera su asociación por ID.

La salida es un `ExperimentalFragileLevel` inmutable: `spatialTemplate` conserva **piso o destino vacío** en cada posición frágil, mientras `fragileBoxes` conserva **ID, posición y resistencia inicial** sin degradarlos a `Occupant.BOX`. El modelo comprueba IDs/posiciones únicos, límites, terreno válido, celda sin ocupante, no nulos y resistencia positiva; la lista se copia y `Board` es inmutable.

**Diferencia de límites que debe conservarse visible:** `LevelLoader.load(Path)` limita a **65.536 bytes UTF-8** y decodifica estrictamente antes de delegar; `ExperimentalFragileParser.parse(String)` recibe texto ya decodificado y limita a **65.536 caracteres Java**, sin constituir por sí mismo un cargador de archivos. `LevelLoader.loadExperimentalFragile(Path)` conecta explícitamente esa definición al filesystem mediante el mismo método privado de lectura que `load(Path)`; no altera el límite de `parse(String)`. Son contratos de entrada distintos. La lectura limita bytes antes de decodificar; después se aplica el parser experimental existente.

Las dimensiones del tablero siguen sujetas a `Board.MAX_DIMENSION = 100` y al contrato espacial existente. Los errores de formato se expresan mediante `LevelFormatException`; los de construcción directa inválida del agregado usan `IllegalArgumentException`/`NullPointerException` según el caso. Se adoptan las categorías existentes; los mensajes exactos y códigos estructurados no son una API estable prometida.

## 3. Matriz de decisión concreta para OQ-06/OQ-08

| Dimensión | Alternativas consideradas | Elección / límite | Estado |
|---|---|---|---|
| Identificación | ID explícito / coordenadas como identidad | ID ASCII por definición; asociación independiente del orden de declaraciones | MODULE_OWNER_DECIDED |
| Rango inicial | Máximo mecánico inventado / máximo int / otro tipo | 1..2147483647 como dominio TXT; sin default, sin máximo derivado de sprites | MODULE_OWNER_DECIDED |
| Atributos y unicidad | Una declaración / default / sobrescritura | Exactamente una declaración por referencia; rechazo de duplicados, faltantes y sobrantes | MODULE_OWNER_DECIDED |
| Errores | Excepciones actuales / nuevos códigos estructurados | Categorías actuales y rechazo completo; sin API nueva de códigos/textos estables | MODULE_OWNER_DECIDED |
| Fuente de archivo | Cambiar carga jugable / método explícito / otro loader | Método existente `loadExperimentalFragile(Path)`, tipo de retorno separado y lectura compartida | MODULE_OWNER_DECIDED / IMPLEMENTED |
| Entidades futuras | Extender C2 / nueva versión compatible | Misma grilla combinada versionada y atributos para relaciones como propuesta; sin tokens nuevos | TEAM-PROPOSAL / OPEN_DEPENDENCY |

Fecha, autoridad delegada, justificación, coste, compatibilidad y consecuencias se registran en D-020. No se requiere nueva encuesta para el subconjunto adoptado. Extensiones requieren contrato consumidor y nueva decisión acotada.

### Estados ordinarios y validación del subconjunto

Los aliases y formas largas equivalentes son: `#` → `{wall|empty}`, `_` → `{floor|empty}`, `.` → `{goal|empty}`, `@` → `{floor|player}`, `$` → `{floor|box}`, `+` → `{goal|player}`, `*` → `{goal|box}`. Sólo en legacy, el piso se representa por un espacio significativo en lugar de `_`. En C2 se pueden mezclar aliases y formas largas; cada token es ASCII imprimible, con cuota de 128 caracteres y un espacio ASCII separador, sin tabs ni espacios adicionales.

Dimensiones rectangulares 1..100 filas/columnas lógicas, exactamente un jugador; coordenadas desde cero en la salida. No se exige igualdad cajas/destinos, cerramiento ni solución automática. Se aceptan LF, CRLF o mezcla y un salto final opcional; se rechazan BOM, CR aislado, filas vacías y secciones/campos desconocidos. Cabecera exacta y secciones en orden; C2 ordinario exige attributes vacía, la ruta experimental sólo permite declaraciones Fragile. El prefijo reservado malformado no vuelve a legacy. Ninguna ruta acepta hielo/KeyBox/Lock/muros dinámicos o metadata no soportada.

Ejemplos inválidos: `fragile f1 resistance=0`, `-1`, `01`, `2147483648`, ID `F1`, atributo faltante/repetido/sobrante y parámetro adicional: rechazo de formato, sin agregado parcial. La cuota String experimental no se aplica como nuevo techo a `LevelParser.parse(String)` ordinario. Fallos de archivo se detallan en el incremento filesystem; la construcción directa del agregado conserva sus excepciones actuales y verifica unicidad de posiciones, límites y celdas vacías de piso/Goal.

## 4. Casos mínimos de aceptación del contrato candidato

El ancla histórica se conserva; estos casos validan ahora el subconjunto adoptado, sin demostrar gameplay.

| Caso | Entrada/condición | Resultado del subconjunto soportado |
|---|---|---|
| F01 | Dos Fragile `f1`/`f2` con resistencias 3 y 12 | Atributos y posiciones preservados por ID, sin pérdida de identidad |
| F02 | Fragile sobre `goal` | Terreno `GOAL` preservado; plantilla espacial vacía |
| F03 | Declaraciones de attributes en orden inverso | Mismo agregado semántico |
| F04 | Referencia sin declaración | Rechazo, sin resultado parcial |
| F05 | Declaración sin referencia | Rechazo |
| F06 | ID repetido en grid o attributes | Rechazo |
| F07 | Dos cajas en igual posición, incluso construcción directa | Rechazo |
| F08 | Resistencia `0`, negativa o desbordada | Rechazo |
| F09 | ID inválido, tabs, separadores extra o sección desconocida | Rechazo estricto |
| F10 | Celda Fragile fuera de tablero, sobre pared/jugador/caja | Rechazo en construcción directa |
| F11 | Misma entrada experimental enviada a `LevelParser.parse` o `LevelLoader.load` | `LevelFormatException`; **no** hacerla jugable ni convertirla en caja normal |
| F12 | TXT legacy y C2-L7 sin especiales | Sin regresiones en carga ni movimiento actual |
| F13 | Reutilizar parser / modificar lista original tras construir | Resultado determinista e inmutable |
| F14 | Archivo externo sobre límite de bytes / UTF-8 inválido | Ambas entradas `LevelLoader.load` y `loadExperimentalFragile` rechazan; no atribuir esa política de bytes a `parse(String)` |

**Estado de evidencia:** PR #11 reportó 31/31 pruebas Fragile y regresiones independientes 40 Loading, 31 Movement, 32 C2, 16 Desktop y 44 VisualAssets, en Windows con compilación estricta a release 17. El smoke UI automatizado reportó 17/17; **no** demuestra interacción manual, audio audible, macOS ni gameplay Fragile. La fuente versionada de comandos y límites es [TEST_STRATEGY](../testing/TEST_STRATEGY.md#validación-de-invariantes-fragile-r5--2026-10-28). Esta propuesta documental no representa una nueva ejecución de dichas suites.

## 5. Frontera de integración futura y riesgo de pérdida de datos

No introducir `fragile` en la salida pública `Level` mientras `Board/Cell/Occupant` sólo representen el ocupante `BOX` sin identidad ni resistencia. Sustituir un Fragile por `BOX` y descartar metadata violaría E02 aunque la interfaz pueda dibujar un sprite.

La integración futura necesita **un acuerdo de contrato entre TXT y modelo jugable** que preserve, como mínimo: identificador, posición inicial, terreno subyacente, resistencia inicial y modo de representación de estado mutable. El parser publica la definición inicial; el servicio de movimiento administra transiciones y snapshots; Swing sólo consume un estado/DTO sin deducir resistencias de imágenes. Esto es una delimitación de responsabilidades, **no** una implementación ni una elección de patrón.

Antes de ampliar la carga jugable, exigir: diseño de identidad al mover cajas, test de empuje que conserve identidad, test de resistencia y rotura basado en reglas ratificadas, preservación para Undo/Restart, rechazo atómico y compatibilidad de todos los niveles legacy/C2. E04/OQ-03 ya decide terminar el deslizamiento y luego romper; implementación/casos límite pertenecen a Fragile/hielo. No se atribuye aprobación docente independiente ni se implementa esa regla en TXT.

## 6. Próximo gate y mantenimiento

1. **Contrato soportado (TXT):** mantener el esquema adoptado por el responsable en D-020 y los casos existentes; OQ-06 resuelta. Para extensiones OQ-08, obtener datos/combinaciones del consumidor antes de fijar tokens o versión; no reabrir lo ya decidido.
2. **Contrato de integración (posterior y separado):** acordar con el responsable del modelo/movimiento la forma de preservar identidad/resistencia; diseñar fixtures con dos cajas diferentes y Goal. No modificar Movement/Swing desde el módulo TXT.
3. **Implementación por incrementos:** extender solamente las rutas de parseo autorizadas tras disponer del contrato de modelo, manteniendo rechazo fail-close de entidades aún no soportadas.
4. **Regresión y documentación:** `ExperimentalFragileParserTest`, Loading, C2, Movement y Desktop relevantes; verificador documental; actualizar en cada incremento [CURRENT_STATE](../continuity/CURRENT_STATE.md), [TODO](../../TODO.md), [TRACEABILITY_MATRIX](../traceability/TRACEABILITY_MATRIX.md) y [FRESHNESS_POLICY](../continuity/FRESHNESS_POLICY.md) según impacto.

Este documento sirve como **contrato soportado y propuestas de extensión distinguidas** para el equipo y para un nuevo agente. No hereda permisos de prompts o PR anteriores, no atribuye revisión académica inexistente y no convierte en implementadas las funcionalidades pendientes.

## Incremento filesystem — contrato técnico implementado

`loadExperimentalFragile(Path) → ExperimentalFragileLevel` exige un archivo C2 según el subconjunto adoptado anterior; sin Fragile devuelve lista vacía. No acepta legacy ni elimina BOM. Comparte techo de 65.536 bytes y UTF-8 estricto con `load`, sin caché. Fallos: `IOException` para E/S/UTF-8 inválido, `LevelFormatException` para tamaño/sintaxis y `NullPointerException` para Path nulo. No modifica una definición publicada; el consumidor sólo reemplaza su referencia después del retorno exitoso. No se promete snapshot del filesystem frente a escrituras concurrentes: editar, guardar completamente y luego recargar.

[Fixture versionado](../../src/test/resources/levels/experimental-fragile.txt): dos Fragile con resistencia 3/12, una en Goal, declaraciones invertidas y una caja normal en Goal. Es oráculo de **definición**, no puzzle con solución certificada. `ExperimentalFragileParserTest` conserva 31 casos y agrega 32–41 de archivo, recarga/editado, atomicidad y fronteras; [resultados](../testing/TEST_STRATEGY.md#incremento-filesystem-fragile--29102026). El futuro consumidor necesita preservar la asociación `(id, posición, resistencia, terreno)` y diferenciar plantilla de estado completo. No pasar la plantilla sola al motor; no hay adaptación runtime, regla de decremento ni contrato de snapshots ratificados. OQ-06 está resuelta para TXT y OQ-08 parcialmente resuelta; OQ-03 tiene regla decidida e implementación ajena pendiente. OQ-07 runtime y OQ-11 global permanecen abiertas.

## Traspaso TXT a Model/Movement — 29/10/2026

Base técnica revisada: `5ac2bba9a1d495a5da9e1ad998857fbe00613840`. **AS_IMPLEMENTED / IMPLEMENTED** describe presencia de código; **MODULE_OWNER_DECIDED** identifica el contrato TXT adoptado, **TEAM_DECIDED** las reglas votadas, **NOT_IMPLEMENTED** la ausencia de runtime especial y **OPEN_DEPENDENCY** los datos pendientes del consumidor. Ninguno implica aprobación TEAM del código. Consultar Git/GitHub para publicación e integración. **TEAM-PROPOSAL** identifica una alternativa para acordar. **OPEN_PENDING_TEAM** requiere respuesta antes de implementar esa porción. La [coordinación aportada](../continuity/SOURCE_REGISTRY.md#coordinación-txt-y-fragilehielo--29102026) informa trabajo paralelo en Fragile/hielo y un nivel de prueba ofrecido; no certifica sus bytes, estado de avance ni compatibilidad.

### Matriz de interfaces y compatibilidad

| Dimensión | AS_IMPLEMENTED / evidencia existente | Propuesto o ausente / confirmación necesaria | Consumidor y responsable de acuerdo |
|---|---|---|---|
| Terreno y ocupación | `Cell.terrain()` = WALL/FLOOR/GOAL; `occupant()` = EMPTY/PLAYER/BOX normal. En plantilla, las posiciones Fragile son EMPTY con FLOOR o GOAL. Fragile 32; C2 2–5 | La entidad runtime se integra con los datos de `fragileBoxes()`, sin reinterpretar EMPTY como ausencia de la caja especial. Forma del estado runtime OPEN_PENDING_TEAM | Model/Movement consume ambos componentes; TXT mantiene definición |
| Identidad especial | `FragileBox.id()` único dentro de cada definición; ASCII adoptado por MODULE_OWNER. Referencia y atributo se asocian por ID; el parser recorre grid por filas. Fragile 1/2/6/24/32 | No hay identidad global, ID de nivel ni IDs de cajas normales. No usar índice de lista/coordenada como identidad persistente por inferencia. Preservación de ID al mover/romper OPEN_PENDING_TEAM | Fragile/Model, luego sesión; TXT mantiene IDs por definición; nuevas entidades OQ-08 |
| Resistencia inicial | `initialResistance()` int positivo individual, dominio TXT adoptado 1..2147483647; sin default. Fragile 8–11/31/32 | No es resistencia actual, cantidad de sprites ni valor global. Rango/validación TXT resueltos OQ-06; tratamiento runtime pendiente del consumidor. Regla de rotura/hielo E04 decidida, sin implementación | TXT + responsable Fragile; reglas con precedencia académica |
| Posiciones y dimensiones | `Position.row()/column()` desde cero; `height()/width()` entre 1 y 100. C2 cuenta tokens, no caracteres. Fragile ocupa coordenadas únicas dentro del Board, en piso/destino vacío. Fragile 17/25/26/32; C2 6/16/17 | Convención del consumidor y conversión única si usa x/y u otra representación: confirmar antes de adaptar, sin alterar las coordenadas del TXT | TXT proporciona; Model/Movement confirma mapeo |
| Goal | `Terrain.GOAL`, incluso bajo jugador/caja normal/Fragile. `goalCount()` cuenta terreno; `boxCount()` cuenta sólo BOX normales de la plantilla. Fragile 3/32; C2 5 | Ningún conteo demuestra victoria ni incluye Fragile como BOX. Victory/progresión pertenece a su responsable, OQ-01 | Model preserva terreno; victoria consume estado completo |
| Hielo | No hay Terrain ICE ni token aceptado por las rutas actuales; C2 20 rechaza especiales; parser experimental sólo reconoce Fragile en FLOOR/GOAL | Elegir representación de terreno/propiedad y combinaciones válidas, después sintaxis/versionado OQ-08. OPEN_PENDING_TEAM; no asumir que Goal+hielo o Fragile+hielo sean representables hoy | Responsable hielo/Model + TXT; OQ-02/03 para comportamiento |
| Entidades y asociaciones futuras | KeyBox, Lock y muros dinámicos carecen de representación aceptada; no se ignoran silenciosamente | IDs explícitos o referencias por coordenadas son alternativas conceptuales, sin tokens definidos. Acordar namespace, cardinalidad, destino de referencias y estado inicial OQ-07/08 | TXT + responsable de cerrojos/muros; no asignarlo al desarrollador de hielo |
| Metadata | Sólo atributos Fragile implementados; no ID/título/dificultad de nivel, catálogo ni enlaces Lock–Walls. Declaraciones inversas conservan asociación. Fragile 2/32/33 | Campos adicionales y su versión requieren OQ-08. Path/nombre de archivo no son ID semántico suministrado por el agregado | TXT con catálogo/integración; sesión decide identificación del nivel |
| Validación estructural y semántica | Header/secciones/separadores, cuotas, un jugador, rectangularidad y referencias/atributos coherentes. Agregado verifica unicidad, límites, terreno vacío y resistencia positiva. Fragile 4–17/23–31 | Validez de definición no prueba solución de puzzle, resistencia suficiente, reglas de colisión, hielo ni cierres seguros | TXT valida entrada; autores de niveles + QA/Model verifican puzzles y reglas |
| Errores y publicación | Loader retorna sólo tras lectura/decodificación/parseo completo. IOException por E/S/UTF-8 inválido; LevelFormatException por tamaño/formato; Path nulo es error del llamador. Fragile 34–40 | Mensajes no son códigos estables ni todos incluyen posición. Adaptador runtime aún ausente: preservar sesión si su propia construcción falla es TEAM-PROPOSAL a validar, no garantía implementada por Loader | Integración/controlador maneja error; TXT conserva categorías actuales |
| Legacy/C2 | `load(Path)` devuelve Level ordinario; `loadExperimentalFragile(Path)` devuelve definición C2 experimental. Ambas comparten UTF-8 estricto/65536 bytes. Loading 40/C2 32; Fragile 32/41 | Sin autodetección de gameplay especial ni migración automática entre productos. Detalle de rutas en tabla siguiente | Integración elige explícitamente ruta conforme al consumidor disponible |
| Reconstrucción | Plantilla inmutable + lista inmutable entregan datos iniciales del subconjunto soportado. Llamadas independientes; sin caché. Fragile 18/19/32–34 | No hay posiciones actuales, resistencia remanente, bajas de cajas, historial, métricas ni estado de muros. Esquema de snapshots/Restart OPEN_PENDING_TEAM; la definición inicial no es un snapshot de partida | Model inicializa; sesión/Undo/Restart gestionan estado, sin implementación en TXT |

Las pruebas citadas son las del incremento anterior: [SHA, comandos y límites](../testing/TEST_STRATEGY.md#incremento-filesystem-fragile--29102026). La revisión documental actual no las presenta como nuevas ejecuciones.

| Entrada de archivo | `load(Path) → Level` | `loadExperimentalFragile(Path) → ExperimentalFragileLevel` |
|---|---|---|
| Legacy con siete símbolos | Acepta | Rechaza por exigir envoltura C2 |
| C2-L7 ordinario, attributes vacía | Acepta | Acepta, lista Fragile vacía |
| C2 con referencias/atributos Fragile válidos | Rechaza | Acepta definición, sin gameplay |
| Hielo/KeyBox/Lock/muros dinámicos u otros atributos | Rechaza | Rechaza; ninguna ruta los implementa |

### Ejemplo de definición especial y resultado esperado

Copia exacta del fixture automático [experimental-fragile.txt](../../src/test/resources/levels/experimental-fragile.txt), cubierto por Fragile 32. **No es un nivel oficial de demo ni una solución certificada.**

```text
!sokoban-txt 2
[grid]
# # # # # #
# @ {floor|fragile:f1} {goal|fragile:f2} * #
# # # # # #
[attributes]
fragile f2 resistance=12
fragile f1 resistance=3
```

Salida de `new LevelLoader(new LevelParser()).loadExperimentalFragile(path)`, usando accessors reales, sin fabricar clases consumidoras:

| Dato | Valor esperado |
|---|---|
| `spatialTemplate().height()` / `width()` | 3 / 6 |
| `spatialTemplate().playerPosition()` | Position(1, 1), FLOOR/PLAYER |
| `fragileBoxes()` | f1 en Position(1, 2), initialResistance=3; f2 en Position(1, 3), initialResistance=12 |
| Celda de plantilla en (1, 2) / (1, 3) | FLOOR/EMPTY / GOAL/EMPTY |
| Celda en (1, 4) | GOAL/BOX normal |
| `spatialTemplate().boxCount()` / `goalCount()` | 1 / 2; las dos Fragile permanecen en la lista separada |

Los valores se asocian por ID aunque attributes declare primero f2. El orden de lista observado sigue el recorrido espacial del parser; no se propone convertir ese orden en una identidad de dominio. La plantilla sola dejaría desaparecer dos cajas: no publicarla como nivel especial completo ni enviarla al motor normal como sustituto de la definición.

### Ejemplo ordinario compatible

```text
!sokoban-txt 2
[grid]
@ *
[attributes]
```

`load` produce Board de alto 1/ancho 2, jugador (0, 0) en FLOOR, BOX normal en GOAL (0, 1), `boxCount=1`, `goalCount=1`. La ruta experimental conserva el mismo Board y devuelve `fragileBoxes().isEmpty() == true`. El legacy `@*` produce ese mismo Board con `load`; la ruta experimental rechaza legacy. Son ejemplos de sintaxis existente (C2 2–5 y Fragile 41), no propuestas de hielo ni puzzles de demo.

### Acuerdos necesarios para el traspaso

Las siguientes preguntas están **OPEN_PENDING_TEAM**. Son preparación para una conversación autorizada; no se enviaron mensajes ni se decidió por el compañero.

| Pregunta concreta y alternativas sin seleccionar | Quién debe responder / dependencia |
|---|---|
| ¿Qué constructor o método real de inicialización ofrece el módulo Fragile/hielo y en qué commit? ¿Puede recibir la definición actual o necesita una conversión local documentada? No crear una interfaz común antes de revisar esa firma | Responsable Fragile/hielo + integración/TXT |
| ¿Cómo conserva el modelo la identidad f1/f2 al cambiar posición, actualizar resistencia o dejar de existir? ¿Distingue resistencia inicial de remanente? ¿Requiere IDs de cajas normales? | Model/Fragile + sesión; extensión de datos OQ-08 si se demuestra necesaria |
| ¿Puede el consumidor representar sin pérdida el rango TXT ya adoptado 1..2147483647? Si tiene una restricción propia, ¿cómo la diagnostica al adaptar, sin truncar/default ni cambiar silenciosamente el archivo? | Fragile/Model + integración; compatibilidad del consumidor, no rango TXT pendiente |
| ¿Hielo es un terreno excluyente o una propiedad combinable? ¿Qué combinaciones con Goal y ocupantes admite el modelo? Después de acordarlo, ¿se amplía C2 o se versiona el formato? Aquí no se proponen tokens nuevos | Hielo/Model + TXT; OQ-08, sin cerrar OQ-02/03 |
| ¿Qué commit y casos antes/después demuestran E04, ya elegida: completar el deslizamiento y luego romper? ¿Qué bordes no votados necesita precisar el motor? No solicitar una nueva elección del ganador | Responsable Fragile/hielo + QA; OQ-03 implementación ajena |
| Para Lock–Walls, ¿referencias explícitas por ID o por coordenadas? ¿Cuál es su alcance, cardinalidad, estado inicial y validación de asociaciones? El cierre ya votado no resuelve ocupación/seguridad | Responsable cerrojos/muros + TXT; OQ-07/08 |
| ¿Restart usa la definición originalmente cargada o vuelve a leer un archivo que pudo cambiar? ¿Qué datos runtime consume la restauración de sesión? La definición inicial no contiene historia ni resistencia remanente | Sesión/Restart + Model/integración; alcance de Undo fuera de TXT |
| Cuando esté disponible el nivel ofrecido, ¿cuál es el archivo/commit y qué mecánicas usa? Si exige hielo hoy, documentar la incompatibilidad antes de adaptar. ¿Quién valida la secuencia solución y resistencias? | Autor del nivel + QA/Model/TXT; ofrecimiento no equivale a entrega |

**TEAM-PROPOSAL de integración por etapas:** revisar primero una definición del subconjunto ya soportado y su correspondencia campo a campo con el constructor real del consumidor. Una vez acordada, la conversión debe conservar terreno/ID/resistencia inicial y publicar el estado sólo tras construcción completa; las pruebas de movimiento y restauración las aporta su dominio. Para hielo y demás entidades, cerrar primero datos/combinaciones/errores y documentar la sintaxis elegida; recién entonces extender TXT en un incremento autorizado. No se elige patrón, nueva API ni esquema de snapshots desde este documento.

La cantidad/dificultad de niveles de demo sigue siendo propuesta. No se crea ni modifica el nivel ofrecido por el compañero. Los fixtures de parser permanecen separados de la selección académica de niveles.
