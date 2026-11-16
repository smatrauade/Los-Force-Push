# Formato de niveles TXT

Los cinco [niveles incluidos](../../src/desktop/resources/levels) son ejemplos ejecutables. Para un nivel externo, usá ABRIR NIVEL TXT o el parámetro indicado en el [README](../../README.md). Editá el archivo, guardalo por completo y volvé a abrirlo: Restart restaura el nivel cargado, sin releer el disco.

## Límites comunes

Archivos UTF-8 estrictos sin BOM, hasta **65.536 bytes**, grilla rectangular de **1 a 100 filas y columnas**, exactamente un jugador. Los parsers rechazan tokens/atributos desconocidos, separadores incorrectos y referencias incoherentes. `LevelLoader` puede informar errores de E/S o de formato; la interfaz conserva la sesión previa ante un fallo. La validez estructural no certifica solución del puzzle.

## Formato clásico

Cada carácter representa una celda. Los espacios son significativos, incluidos los finales. No se usan tabulaciones.

| Símbolo | Significado |
|---|---|
| `#` | Pared |
| espacio | Piso vacío |
| `.` | Destino |
| `@` | Jugador |
| `$` | Caja normal |
| `+` | Jugador en destino |
| `*` | Caja normal en destino |

```text
#####
#@$.#
#####
```

## Formato C2

La primera línea es `!sokoban-txt 2`, seguida de `[grid]`, las filas y `[attributes]`. Los tokens se separan con **un espacio ASCII**. `_` representa piso vacío; los otros símbolos clásicos conservan su significado. Las formas largas usan `{terreno|ocupante}`, por ejemplo `{floor|player}`, `{goal|box}` o `{ice|empty}`. Hielo admite empty/player/box; no implica combinar Goal e ICE en una misma celda.

```text
!sokoban-txt 2
[grid]
# # # # #
# @ $ . #
# # # # #
[attributes]
```

## Cajas frágiles

En `[grid]`, `{floor|fragile:glass}` o `{goal|fragile:glass}` refiere a una caja. En `[attributes]`, `fragile glass resistance=3` declara su resistencia inicial. Cada ID cumple `[a-z][a-z0-9_]{0,31}`, es único en la definición y tiene exactamente una declaración. No hay resistencia por defecto: se acepta un decimal canónico positivo entre 1 y 2147483647, sin signo ni ceros iniciales. El rango representa datos, no un máximo de dificultad recomendado.

Ejemplo con hielo y Fragile: [fragile-ice-demo.txt](../../src/desktop/resources/levels/fragile-ice-demo.txt). El ID y la resistencia deben conservarse al construir el runtime. `LevelLoader.loadExperimentalFragile` devuelve plantilla y metadatos; pasar solo la plantilla al motor perdería las cajas especiales. `LevelLoader.load` rechaza esa conversión con pérdida.

## Cajas llave, cerrojos y muros

`ExtendedLevelParser` incorpora `{floor|key:k}`, `{goal|key:k}`, `{lock:l|empty}` y `{wall:w|empty}`. Las posiciones de cerrojos/muros pueden contener los ocupantes admitidos por esa extensión. Cada muro declara `wall w locks=l` o una lista de IDs separados por coma. La definición valida IDs y referencias; las asociaciones pertenecen al muro, y cualquier cerrojo asociado activo lo abre.

El ejemplo [campaign-b.txt](../../src/desktop/resources/levels/campaign-b.txt) contiene jugador inicialmente sobre Goal, KeyBox, Lock y muro. La extensión reutiliza la definición Fragile, por lo que puede preservar sus atributos cuando están presentes; no admite combinaciones arbitrarias de tokens ni ignora errores.

## Rutas de lectura

| Entrada | Producto | Uso |
|---|---|---|
| `LevelLoader.load(Path)` | `Level` | Clásico/C2 ordinario e ICE, sin pérdida de metadatos especiales |
| `loadExperimentalFragile(Path)` | `ExperimentalFragileLevel` | Plantilla y cajas frágiles con atributos |
| `loadExtended(Path)` | `ExtendedLevelDefinition` | KeyBox, Lock, muros y definición base |

`GameController` prueba estas rutas estrictas para cargar un nivel sin depender de su nombre. Las coordenadas de las APIs usan fila/columna desde cero. El catálogo distingue formatos explícitos al construir sus estados. La [trazabilidad](../traceability/TRACEABILITY_MATRIX.md) identifica pruebas de límites, recarga y atomicidad.
