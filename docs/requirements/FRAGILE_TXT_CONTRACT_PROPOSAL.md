# Propuesta de contrato TXT C2 — atributos individuales Fragile (R1)

- `STATUS = TEAM-PROPOSAL / EXPERIMENTAL_NOT_RATIFIED`
- `AS_OF = 2026-10-28`
- `DOMAIN = SOKOBAN_TXT_LEVEL_FORMAT`
- `BASE_MAIN = 986ab548e7955752d3542035d6f9d573534b257d`

> **No es una gramática aprobada ni una habilitación de gameplay.** Este documento consolida el comportamiento del prototipo integrado por [PR #11](https://github.com/smatrauade/Los-Force-Push/pull/11), propone un contrato verificable para decidir los detalles pendientes y delimita la futura integración. La autoridad canónica de acuerdos continúa en [DECISION_LOG](../decisions/DECISION_LOG.md#ratificación-por-mayoría--2026-10-23); las preguntas abiertas, en [OPEN_QUESTIONS](OPEN_QUESTIONS.md). Ningún valor técnico del spike se convierte en decisión del equipo por figurar aquí.

Para integrar desde otro módulo: [matriz de interfaces, ejemplos y preguntas pendientes](#traspaso-txt-a-modelmovement--29102026). Describe las APIs existentes; no prescribe una interfaz runtime nueva.

## 1. Autoridad, estado y alcance

- **E01 (TEAM, ACCEPTED_SCOPE):** una capa espacial con símbolos combinados. No define el alfabeto final ni la sección de metadatos.
- **E02 (TEAM, ACCEPTED_SCOPE):** cada caja frágil obtiene su **resistencia inicial individual del TXT**. No define sintaxis, rango ni errores.
- **OQ-06:** sintaxis, rango y errores de resistencia individual permanecen `PENDING_DETAILS`.
- **OQ-08:** contrato TXT completo (alfabeto, entidades, metadatos, charset, límites, ejemplos y errores) permanece `PENDING_DETAILS`.
- **OQ-07:** relación Lock↔Walls y reglas de cierre queda fuera de esta propuesta; no resolverla implícitamente con los IDs de Fragile.
- **OQ-11:** Java/build definitivo no ratificado; `--release 17` es el target de los runners existentes, no una nueva decisión.

El incremento de este documento se limita al **formato, las invariantes y la interfaz de salida del parser de niveles**. No modifica Movement, Swing, Undo, persistencia, victoria ni el modelo del juego. El mecanismo actual de carga jugable sigue rechazando atributos especiales.

## 2. Contrato experimental existente (comportamiento implementado)

El código de referencia vive en [ExperimentalFragileParser](../../src/main/java/ar/edu/uade/sokoban/level/ExperimentalFragileParser.java), [ExperimentalFragileLevel](../../src/main/java/ar/edu/uade/sokoban/level/ExperimentalFragileLevel.java) y [ExperimentalFragileParserTest](../../src/test/java/ar/edu/uade/sokoban/level/ExperimentalFragileParserTest.java).

**Ejemplo aceptado por el prototipo** (no por el `LevelLoader` jugable):

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

Los IDs implementados siguen `[a-z][a-z0-9_]{0,31}`. La resistencia implementada es decimal positiva entre **1 y 2.147.483.647**; no se deduce un máximo de tres por la existencia de sprites. Cada referencia espacial y cada atributo aparecen exactamente una vez. El orden de las declaraciones no altera su asociación por ID.

La salida es un `ExperimentalFragileLevel` inmutable: `spatialTemplate` conserva **piso o destino vacío** en cada posición frágil, mientras `fragileBoxes` conserva **ID, posición y resistencia inicial** sin degradarlos a `Occupant.BOX`. El modelo comprueba IDs/posiciones únicos, límites, terreno válido, celda sin ocupante, no nulos y resistencia positiva; la lista se copia y `Board` es inmutable.

**Diferencia de límites que debe conservarse visible:** `LevelLoader.load(Path)` limita a **65.536 bytes UTF-8** y decodifica estrictamente antes de delegar; `ExperimentalFragileParser.parse(String)` recibe texto ya decodificado y limita a **65.536 caracteres Java**, sin constituir por sí mismo un cargador de archivos. `LevelLoader.loadExperimentalFragile(Path)` conecta explícitamente esa definición al filesystem mediante el mismo método privado de lectura que `load(Path)`; no altera el límite de `parse(String)`. Son contratos de entrada distintos. La lectura limita bytes antes de decodificar; después se aplica el parser experimental existente.

Las dimensiones del tablero siguen sujetas a `Board.MAX_DIMENSION = 100` y al contrato espacial existente. Los errores de formato se expresan mediante `LevelFormatException`; los de construcción directa inválida del agregado usan `IllegalArgumentException`/`NullPointerException` según el caso. El mensaje exacto no constituye aún una API de errores ratificada.

## 3. Matriz de decisión concreta para OQ-06/OQ-08

| Dimensión | Alternativas viables | Propuesta técnica para evaluar | Estado |
|---|---|---|---|
| Identificación individual | ID explícito en celda / indexación por coordenadas | ID explícito `{floor|fragile:f1}` vinculado a metadata; resiste reordenamiento de declaraciones | `TEAM-PROPOSAL` |
| Gramática del ID | Alfanumérico ASCII acotado / Unicode o esquema distinto | Mantener el subconjunto ASCII del spike hasta verificar portabilidad y necesidades | `TEAM-PROPOSAL` |
| Resistencia inicial | Entero positivo acotado por mecánica / hasta `int` / otro tipo | Mantener `int` positivo **como límite técnico provisional**, elegir máximo del producto mediante acuerdo; no adoptar 3 automáticamente | `OPEN` |
| Atributo obligatorio | Exactamente uno por ID / default implícito | Exactamente uno, sin default silencioso | `TEAM-PROPOSAL` |
| Identificadores duplicados | Rechazo / sobrescritura | Rechazo determinista, sin publicación parcial | `TEAM-PROPOSAL` |
| Error de formato | Mensaje libre / código estructurado y posición | Conservar `LevelFormatException`; evaluar códigos estables para CLI/UI sólo con consumidor real | `OPEN` |
| Evolución de entidades | Extender C2 v2 / nueva versión | No activar `ice`, `key`, `lock`, walls dinámicos ni otros atributos hasta especificar sus contratos | `OPEN` |
| Fuente de carga | Cambiar `load` / método explícito / nuevo loader | AS_IMPLEMENTED en el código presente: `loadExperimentalFragile(Path)` separado por nombre y tipo de retorno; gramática y futura integración jugable no ratificadas | `EXPERIMENTAL_NOT_RATIFIED` |

**Para aprobar una opción:** contrastar primero la consigna oficial y aclaraciones aplicables; registrar acuerdo del equipo, fecha, alcance, consecuencia y casos de aceptación en DECISION_LOG, y actualizar las filas OQ-06/OQ-08 y la trazabilidad. No reabrir E01/E02, ya aceptadas en sus dimensiones.

## 4. Casos mínimos de aceptación del contrato candidato

| Caso | Entrada/condición | Resultado exigible del candidato |
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

Antes de ampliar la carga pública, exigir: diseño de identidad al mover cajas, test de empuje que conserve identidad, test de resistencia y rotura basado en reglas ratificadas, preservación para Undo/Restart, rechazo atómico y compatibilidad de todos los niveles legacy/C2. E04/OQ-03 (rotura en hielo) queda fuera de alcance y bajo revisión normativa.

## 6. Próximo gate y mantenimiento

1. **Especificación (TXT, este dominio):** obtener decisión del equipo sobre sintaxis de IDs, rango final, codificación/errores y versionado; actualizar DECISION_LOG y OQ únicamente con ratificación verificable. Conservar explícitas las dimensiones aún abiertas.
2. **Contrato de integración (posterior y separado):** acordar con el responsable del modelo/movimiento la forma de preservar identidad/resistencia; diseñar fixtures con dos cajas diferentes y Goal. No modificar Movement/Swing desde el módulo TXT.
3. **Implementación por incrementos:** extender solamente las rutas de parseo autorizadas tras disponer del contrato de modelo, manteniendo rechazo fail-close de entidades aún no soportadas.
4. **Regresión y documentación:** `ExperimentalFragileParserTest`, Loading, C2, Movement y Desktop relevantes; verificador documental; actualizar en cada incremento [CURRENT_STATE](../continuity/CURRENT_STATE.md), [TODO](../../TODO.md), [TRACEABILITY_MATRIX](../traceability/TRACEABILITY_MATRIX.md) y [FRESHNESS_POLICY](../continuity/FRESHNESS_POLICY.md) según impacto.

Esta propuesta sirve como **material de decisión técnica** para el equipo y para un nuevo agente. No hereda permisos de prompts o PR anteriores, no atribuye revisión académica inexistente y no convierte en implementadas las funcionalidades pendientes.

## Incremento filesystem — contrato técnico implementado

`loadExperimentalFragile(Path) → ExperimentalFragileLevel` exige un archivo C2 según la gramática provisional anterior; sin Fragile devuelve lista vacía. No acepta legacy ni elimina BOM. Comparte techo de 65.536 bytes y UTF-8 estricto con `load`, sin caché. Fallos: `IOException` para E/S/UTF-8 inválido, `LevelFormatException` para tamaño/sintaxis y `NullPointerException` para Path nulo. No modifica una definición publicada; el consumidor sólo reemplaza su referencia después del retorno exitoso. No se promete snapshot del filesystem frente a escrituras concurrentes: editar, guardar completamente y luego recargar.

[Fixture versionado](../../src/test/resources/levels/experimental-fragile.txt): dos Fragile con resistencia 3/12, una en Goal, declaraciones invertidas y una caja normal en Goal. Es oráculo de **definición**, no puzzle con solución certificada. `ExperimentalFragileParserTest` conserva 31 casos y agrega 32–41 de archivo, recarga/editado, atomicidad y fronteras; [resultados](../testing/TEST_STRATEGY.md#incremento-filesystem-fragile--29102026). El futuro consumidor necesita preservar la asociación `(id, posición, resistencia, terreno)` y diferenciar plantilla de estado completo. No pasar la plantilla sola al motor; no hay adaptación runtime, regla de decremento ni contrato de snapshots ratificados. OQ-03/06/07/08/11 continúan abiertas en su alcance previo.

## Traspaso TXT a Model/Movement — 29/10/2026

Base técnica revisada: `5ac2bba9a1d495a5da9e1ad998857fbe00613840`. **AS_IMPLEMENTED** describe capacidades del código presente; no significa contrato ratificado. Consultar Git/GitHub para publicación e integración. **TEAM-PROPOSAL** identifica una alternativa para acordar. **OPEN_PENDING_TEAM** requiere respuesta antes de implementar esa porción. La [coordinación aportada](../continuity/SOURCE_REGISTRY.md#coordinación-txt-y-fragilehielo--29102026) informa trabajo paralelo en Fragile/hielo y un nivel de prueba ofrecido; no certifica sus bytes, estado de avance ni compatibilidad.

### Matriz de interfaces y compatibilidad

| Dimensión | AS_IMPLEMENTED / evidencia existente | Propuesto o ausente / confirmación necesaria | Consumidor y responsable de acuerdo |
|---|---|---|---|
| Terreno y ocupación | `Cell.terrain()` = WALL/FLOOR/GOAL; `occupant()` = EMPTY/PLAYER/BOX normal. En plantilla, las posiciones Fragile son EMPTY con FLOOR o GOAL. Fragile 32; C2 2–5 | La entidad runtime se integra con los datos de `fragileBoxes()`, sin reinterpretar EMPTY como ausencia de la caja especial. Forma del estado runtime OPEN_PENDING_TEAM | Model/Movement consume ambos componentes; TXT mantiene definición |
| Identidad especial | `FragileBox.id()` único dentro de cada definición; ASCII provisional. Referencia y atributo se asocian por ID; el parser recorre grid por filas. Fragile 1/2/6/24/32 | No hay identidad global, ID de nivel ni IDs de cajas normales. No usar índice de lista/coordenada como identidad persistente por inferencia. Preservación de ID al mover/romper OPEN_PENDING_TEAM | Fragile/Model, luego sesión; TXT acuerda alcance de IDs OQ-06/08 |
| Resistencia inicial | `initialResistance()` int positivo individual, límite técnico 2147483647; sin default. Fragile 8–11/31/32 | No es resistencia actual, cantidad de sprites ni valor global. Rango final y tratamiento runtime pendientes OQ-06; rotura/hielo OQ-03 no resuelta | TXT + responsable Fragile; reglas con precedencia académica |
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
| ¿Cómo conserva el modelo la identidad f1/f2 al cambiar posición, actualizar resistencia o dejar de existir? ¿Distingue resistencia inicial de remanente? ¿Requiere IDs de cajas normales? | Model/Fragile + sesión; OQ-06/08 para datos de archivo |
| ¿Qué rango inicial necesita el modelo y qué debe pasar con valores fuera del rango acordado? El int positivo del spike no ratifica el rango del producto | TXT + Fragile; OQ-06 |
| ¿Hielo es un terreno excluyente o una propiedad combinable? ¿Qué combinaciones con Goal y ocupantes admite el modelo? Después de acordarlo, ¿se amplía C2 o se versiona el formato? Aquí no se proponen tokens nuevos | Hielo/Model + TXT; OQ-08, sin cerrar OQ-02/03 |
| ¿Qué ejemplos antes/después y fuente académica resuelven rotura al cero al entrar en hielo? La preferencia E04 queda conservada, sin escoger su implementación | Equipo de reglas/Fragile/hielo; OQ-03, docente si persiste conflicto |
| Para Lock–Walls, ¿referencias explícitas por ID o por coordenadas? ¿Cuál es su alcance, cardinalidad, estado inicial y validación de asociaciones? El cierre ya votado no resuelve ocupación/seguridad | Responsable cerrojos/muros + TXT; OQ-07/08 |
| ¿Restart usa la definición originalmente cargada o vuelve a leer un archivo que pudo cambiar? ¿Qué datos runtime consume la restauración de sesión? La definición inicial no contiene historia ni resistencia remanente | Sesión/Restart + Model/integración; alcance de Undo fuera de TXT |
| Cuando esté disponible el nivel ofrecido, ¿cuál es el archivo/commit y qué mecánicas usa? Si exige hielo hoy, documentar la incompatibilidad antes de adaptar. ¿Quién valida la secuencia solución y resistencias? | Autor del nivel + QA/Model/TXT; ofrecimiento no equivale a entrega |

**TEAM-PROPOSAL de integración por etapas:** revisar primero una definición del subconjunto ya soportado y su correspondencia campo a campo con el constructor real del consumidor. Una vez acordada, la conversión debe conservar terreno/ID/resistencia inicial y publicar el estado sólo tras construcción completa; las pruebas de movimiento y restauración las aporta su dominio. Para hielo y demás entidades, cerrar primero datos/combinaciones/errores y documentar la sintaxis elegida; recién entonces extender TXT en un incremento autorizado. No se elige patrón, nueva API ni esquema de snapshots desde este documento.

La cantidad/dificultad de niveles de demo sigue siendo propuesta. No se crea ni modifica el nivel ofrecido por el compañero. Los fixtures de parser permanecen separados de la selección académica de niveles.
