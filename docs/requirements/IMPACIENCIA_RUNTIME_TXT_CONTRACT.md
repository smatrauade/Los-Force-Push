# Extensión TXT individual para runtime

`STATUS = INDIVIDUAL_EXPERIMENTAL_TXT_EXTENSION` · `AS_OF = 2026-10-31`

Esta extensión de la envoltura C2 es individual y reversible; no ratifica OQ-07/08 ni altera [D-020 y el contrato Fragile soportado](FRAGILE_TXT_CONTRACT_PROPOSAL.md). La entrada explícita es `LevelLoader.loadExtended(Path) → ExtendedLevelDefinition`; `RuntimeState.fromExtended` adapta la definición al motor. No cambia `load(Path)` ni `loadExperimentalFragile(Path)`, no activa Swing y no convierte especiales en cajas normales.

## Alternativas y límites

Extender el parser histórico arriesgaría aceptar tokens que antes rechazaba. Se eligió un parser explícito que normaliza la extensión al subconjunto existente y reutiliza `ExperimentalFragileParser` para estructura, estados ordinarios y atributos Fragile. `level` no depende de `movement`; el adaptador vive en `movement`. El coste es validar la estructura especial al construir su definición, además de las invariantes del runtime. Pruebas de ambas rutas y rechazo histórico se encuentran en [TEST_STRATEGY](../testing/TEST_STRATEGY.md).

Archivo externo: UTF-8 estricto, sin BOM, hasta 65.536 bytes; entrada String hasta 65.536 caracteres Java. Una grilla rectangular de 1..100 filas y columnas, exactamente un jugador, tokens ASCII de hasta 128 caracteres, separados por un espacio ASCII. Cabecera exacta `!sokoban-txt 2`, luego `[grid]`, luego `[attributes]`; LF/CRLF y un salto final opcional. No hay fallback a legacy ni publicación parcial. Los errores de formato usan `LevelFormatException`, E/S o UTF-8 inválido `IOException` y Path nulo `NullPointerException`.

## Tokens y atributos

Los siete aliases y formas largas ordinarias, y `{floor|fragile:id}` / `{goal|fragile:id}` con `fragile id resistance=N`, conservan el contrato enlazado. IDs: `[a-z][a-z0-9_]{0,31}`; resistencia decimal canónica 1..2147483647; exactamente una declaración por Fragile.

| Terreno | Forma extendida | Ocupantes admitidos |
|---|---|---|
| Piso o Goal | `{floor|key:k1}`, `{goal|key:k1}` | KeyBox; ordinarios y Fragile según contrato previo |
| Hielo | `{ice|empty}`, `{ice|player}`, `{ice|box}`, `{ice|fragile:f1}`, `{ice|key:k1}` | Vacío, jugador, normal, Fragile o KeyBox |
| Lock | `{lock:l1|empty}` y mismas variantes de ocupante del hielo | El ID identifica la casilla, no implica Goal |
| Muro dinámico | `{wall:w1|empty}` y mismas variantes de ocupante del hielo | Sólo puede contener ocupante si inicia abierto |

Cada KeyBox tiene ID; IDs de cajas especiales Fragile y KeyBox comparten un espacio de nombres único. Los IDs de Lock y muro son únicos en su categoría. Cada muro requiere exactamente una declaración `wall w1 locks=l1,l2`; cada referencia debe identificar un Lock existente y no repetirse. El orden de declaraciones y de referencias no cambia la asociación. No hay declaración adicional de KeyBox o Lock. Las combinaciones ICE+GOAL y cualquier otra no representada se rechazan.

Los muros abren inicialmente si al menos un Lock asociado contiene KeyBox. Rechazar posiciones, referencias, solapamientos, IDs y atributos inválidos, así como un muro cerrado ocupado. Las asociaciones OR y el rechazo de cierre ocupado son hipótesis individuales de runtime, no resolución TEAM de OQ-07. No se garantiza que un mapa válido sea resoluble.

Ejemplo: [fixture combinado](../../src/test/resources/levels/extended-runtime.txt) y [fixture ordinario](../../src/test/resources/levels/extended-ordinary.txt). Ejemplos inválidos: `fragile f1 resistance=0`, `{ice|goal}`, `wall w1 locks=desconocido`, declaración de muro faltante o duplicada. No se han certificado como puzzles resueltos.
