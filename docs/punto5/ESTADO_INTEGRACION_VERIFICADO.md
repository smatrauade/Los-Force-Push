# Punto 5 — estado integrado y evidencia de preparacion

Revisado 28/10/2026. PR #9 esta MERGED en main, junto con C2-L7 por PR #8.
[CURRENT_STATE](../continuity/CURRENT_STATE.md) registra los merges historicos.
El runtime contiene 31 PNG y 12 WAV, con 43 entradas en el [manifiesto](ASSETS_REGISTRO_SHA256.csv).
Los apartados de preparacion siguientes corresponden a la copia del 27/10, no al HEAD actual.
La [QA combinada anterior](../testing/TEST_STRATEGY.md#qa-combinado-pr-8-y-pr-9--2026-10-28)
se distingue de las comprobaciones nuevas del mantenimiento documental.

## Alcance real y base

- Punto de partida recibido: `Los-Force-Push.zip`, HEAD empaquetado `5bf6b65`.
- Insumo UI: `Los_Force_Push_Punto_5_Ladrillo_Cobre_Almacen_Actualizado.zip`.
- Se integraron los archivos `PARA_COPIAR_EN_TU_REPO` sobre la base de prueba.
- La entrega integrada **NO incluye `.git`**. Para colaborar, aplicar los cambios UI en una rama del clon real y abrir un pull request; jamas reemplazar la carpeta `.git`.
- No se modifico ninguna clase bajo `src/main/java`. Se conserva el TXT y el motor del proyecto.
- Ajuste menor de compatibilidad: el sprite `box_goal` se usa tanto si el terreno se denomina `GOAL` como si se denomina `DESTINATION`.

## QA reproducido en entorno Linux

Se uso OpenJDK 21.0.12.1 con `javac --release 17`, `-Xlint:all -Werror`, modulos `java.base,java.desktop`. Ejecucion real, no pruebas inventadas:

| Comando | Resultado |
|---|---|
| `bash scripts/test-level-loading.sh --movement` | 40/40 TXT, 31/31 movimiento |
| `bash scripts/run-swing.sh --test` | 16/16 integration, 44/44 resources/panel |
| `xvfb-run -a bash scripts/run-swing.sh --ui-test [carpeta-de-capturas]` | 17/17 UI real en entorno virtual |
| Auditoria SHA-256 de `docs/punto5/ASSETS_REGISTRO_SHA256.csv` | 43/43 hashes correctos |
| Comprobacion de 12 WAV mediante `wave` | Todos PCM mono 16 bits, 22050 Hz |

**Nota del entorno:** la terminal de prueba Linux estaba iniciada con locale `POSIX`, que no admite rutas con `ñ`; para los tests Unicode se aplico `LANG=C.UTF-8 LC_ALL=C.UTF-8`. No es un fallo en `LevelLoader`: las rutas UTF-8 pasaron con la configuracion correcta. En Windows validar con `run-swing.ps1 -Test`.

**Prueba grafica:** Xvfb permite abrir ventanas Swing y tomar capturas genuinas de menu, juego, pausa y pantalla final. En ese entorno **no habia dispositivo de audio**; el codigo registro el problema sin abortar. La salida real de audio no esta comprobada y necesita un PC con altavoces.

## Cumplimiento del punto 5 y dependencias

- Listo: menu, vista de juego, HUD real (nivel, movimientos, empujes), flechas, zoom 1x/2x/3x, pausa, opciones, ayuda, lector de TXT de la base, dibujo por imagen y SFX para moverse, empujar, bloquearse y menu.
- Listo para conectar: botones Undo, Restart, Save/Load y Next; pantalla final parametrizada por `LevelResult`; eventos de audio para hielo, cerrojo, fragilidad, final. No estan activos mientras falten servicios de los otros miembros.
- No listo en el core: cargas especiales, hielo, fragilidad, cajas llave, cerrojos, Undo real, Restart real, puntuacion, victoria automatica y progresion. Este ZIP no simula sus comportamientos.
- Al ampliarse el core: adaptar `BoardPanel.spriteForTerrain` / `spriteForOccupant` a los tipos y estados reales; **la resistencia de cajas fragiles no puede inferirse desde Swing**.

## Riesgo legal de recursos / IA

Los PNG son producciones originales desarrolladas con asistencia de IA y los WAV se generaron programaticamente; no se declaran activos descargados de terceros. No obstante, **original/IA no prueba automaticamente que un recurso sea de dominio publico**. La consigna exige dominio publico: documentar titularidad y licencia/CC0 con los integrantes antes de entregar. La tabla CSV conserva la procedencia y el hash de cada uno; la declaracion de IA debe figurar en el informe.

## Ejecucion actual desde clon

Seguir la [guia desde GitHub](GUIA_INSTALACION_GITHUB_DESKTOP.md): main ya incluye
los archivos del Punto 5. No crear nuevamente la rama UI ni aplicar el instalador ZIP.
Los paquetes e instrucciones de copia fueron insumos historicos de preparacion.
Las pruebas actuales y limites estan en [TEST_STRATEGY](../testing/TEST_STRATEGY.md).

## Diseno defendible

La separacion MVC se conserva: el modelo `Board` es de solo lectura desde la vista; `MovementEngine` decide movimientos; `GameController` coordina y publica presentaciones; `GameView` es un contrato desacoplado de Swing; `BoardPanel` solo pinta sprites; `SoundManager` solo reproduce sonidos. `UiCommands` permite conectar servicios sin meter reglas en los botones. Se requieren actualizaciones de UML/informe cuando todo el equipo cierre la arquitectura.
