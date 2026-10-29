# Propuesta de contrato TXT C2 — atributos individuales Fragile (R1)

- `STATUS = TEAM-PROPOSAL / EXPERIMENTAL_NOT_RATIFIED`
- `AS_OF = 2026-10-28`
- `DOMAIN = SOKOBAN_TXT_LEVEL_FORMAT`
- `BASE_MAIN = 986ab548e7955752d3542035d6f9d573534b257d`

> **No es una gramática aprobada ni una habilitación de gameplay.** Este documento consolida el comportamiento del prototipo integrado por [PR #11](https://github.com/smatrauade/Los-Force-Push/pull/11), propone un contrato verificable para decidir los detalles pendientes y delimita la futura integración. La autoridad canónica de acuerdos continúa en [DECISION_LOG](../decisions/DECISION_LOG.md#ratificación-por-mayoría--2026-10-23); las preguntas abiertas, en [OPEN_QUESTIONS](OPEN_QUESTIONS.md). Ningún valor técnico del spike se convierte en decisión del equipo por figurar aquí.

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

**Diferencia de límites que debe conservarse visible:** `LevelLoader.load(Path)` limita a **65.536 bytes UTF-8** y decodifica estrictamente antes de delegar; `ExperimentalFragileParser.parse(String)` recibe texto ya decodificado y limita a **65.536 caracteres Java**, sin constituir por sí mismo un cargador de archivos. En esta rama, `LevelLoader.loadExperimentalFragile(Path)` conecta explícitamente esa definición al filesystem mediante el mismo método privado de lectura que `load(Path)`; no altera el límite de `parse(String)`. Son contratos de entrada distintos. La lectura limita bytes antes de decodificar; después se aplica el parser experimental existente.

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
| Fuente de carga | Cambiar `load` / método explícito / nuevo loader | Implementado técnicamente en esta rama: `loadExperimentalFragile(Path)` separado por nombre y tipo de retorno; gramática y futura integración jugable no ratificadas | `EXPERIMENTAL_NOT_RATIFIED` |

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

## Incremento filesystem — contrato técnico disponible en esta rama

`loadExperimentalFragile(Path) → ExperimentalFragileLevel` exige un archivo C2 según la gramática provisional anterior; sin Fragile devuelve lista vacía. No acepta legacy ni elimina BOM. Comparte techo de 65.536 bytes y UTF-8 estricto con `load`, sin caché. Fallos: `IOException` para E/S/UTF-8 inválido, `LevelFormatException` para tamaño/sintaxis y `NullPointerException` para Path nulo. No modifica una definición publicada; el consumidor sólo reemplaza su referencia después del retorno exitoso. No se promete snapshot del filesystem frente a escrituras concurrentes: editar, guardar completamente y luego recargar.

[Fixture versionado](../../src/test/resources/levels/experimental-fragile.txt): dos Fragile con resistencia 3/12, una en Goal, declaraciones invertidas y una caja normal en Goal. Es oráculo de **definición**, no puzzle con solución certificada. `ExperimentalFragileParserTest` conserva 31 casos y agrega 32–41 de archivo, recarga/editado, atomicidad y fronteras; [resultados](../testing/TEST_STRATEGY.md#incremento-filesystem-fragile--29102026). El futuro consumidor necesita preservar la asociación `(id, posición, resistencia, terreno)` y diferenciar plantilla de estado completo. No pasar la plantilla sola al motor; no hay adaptación runtime, regla de decremento ni contrato de snapshots ratificados. OQ-03/06/07/08/11 continúan abiertas en su alcance previo.
