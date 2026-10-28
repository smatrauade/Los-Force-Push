# QA del Punto 5 - registro y comprobaciones pendientes

Revisado 28/10/2026. PR #9 y #8 ya estan integrados. Se ejecuta desde el
[clon](GUIA_INSTALACION_GITHUB_DESKTOP.md), sin reinstalar ZIP. El
[QA combinado anterior](../testing/TEST_STRATEGY.md#qa-combinado-pr-8-y-pr-9--2026-10-28)
probo Windows automatizado sobre el arbol integrado: no sustituye este checklist manual.
El manifiesto actual tiene 43 recursos: 29 sprites base, dos variantes opcionales y 12 WAV.
Los 44 casos de VisualAssets cubren 29 sprites, 12 WAV y tres controles de panel/escala.

## Evidencia historica de preparacion sobre copia aislada (antes del PR #9)

- JDK observado: OpenJDK 21.0.12.1; compilacion `--release 17`,
  `--limit-modules java.base,java.desktop`, `-Xlint:all -Werror`.
- Core, carga de niveles: **40 PASS / 0 FAIL**.
- Core, movimiento: **31 PASS / 0 FAIL**.
- DesktopIntegrationTest: **16 PASS / 0 FAIL**.
- DesktopVisualAssetsTest: **44 PASS / 0 FAIL** (29 PNG, 12 WAV,
  3 verificaciones de panel/escala).
- La preparacion original reporto **41/41 recursos** regenerados; es evidencia historica. El generador actual solo recrea WAV y preserva los PNG finales; no equivale al manifiesto actual de 43.
- DesktopUiSmokeTest con Xvfb: **17 verificaciones** de menu, navegacion, HUD,
  flechas, pausa, callbacks, controles deshabilitados y victoria demostrativa.
- Sesion grafica virtual Xvfb: la ventana principal y menu abren y se dibujan;
  capturas disponibles en `PREVIAS` del ZIP.
- Entorno Linux/UTF-8. Windows fisico, altavoces y accesibilidad manual:
  **NO PROBADOS** manualmente en esa preparacion. Sigue pendiente la QA manual actual.

## Manual Windows actual (marcar solo despues de ejecutarlo)

- [ ] `run-swing.ps1` abre el menu; `NUEVA PARTIDA / DEMO` carga `demo.txt`.
- [ ] El tablero usa sprites en lugar de formas Swing.
- [ ] Flechas funcionan tambien despues de pulsar botones/HUD.
- [ ] Un empuje actualiza movimientos y empujes.
- [ ] Movimiento bloqueado no cambia los contadores.
- [ ] Destino bajo caja sigue identificable (marcador turquesa).
- [ ] `ESC` pausa/reanuda y al pausar las flechas ya no mueven al personaje.
- [ ] `ABRIR TXT` lee otro archivo y si el archivo es invalido conserva el nivel.
- [ ] `R`/Restart no ejecutan reinicio sin el callback del Punto 4.
- [ ] Despues de integrar Punto 4, `R` y el boton invocan su comando real y se verifica la confirmacion segun el diseno del equipo.
- [ ] `M` silencia/activa audio; audio del SO funciona; sin audio no se cuelga.
- [ ] Zoom 1x, 2x y 3x no genera desenfoque.
- [ ] Menu, ayuda, opciones y pausa son navegables.
- [ ] Ejecutar `run-swing.ps1 -UiTest` en Windows con monitor (prueba opcional automatizada).
- [ ] Botones Undo, Restart, guardar, cargar y siguiente nivel siguen deshabilitados y
      no muestran falsas confirmaciones.
- [ ] En un TXT grande, barras de desplazamiento permiten visualizar el tablero.
- [ ] Si una caja especial ya fue implementada por el equipo, se reviso
      manualmente su sprite y se integraron estados/animaciones a DTO reales.
- [ ] Se pidio revision humana y se actualizaron UML/documentacion viva
      cuando el equipo apruebe el diseño final.

## Regresion y colaboracion

No alterar `src/main/java` del motor en una tarea exclusiva de presentacion. Los ultimos acuerdos de reglas
pueden quedar abiertos; este incremento no debe crear nuevos contratos normativos.
Si hubo cambios concurrentes en los 5 archivos reemplazados, reconciliar el diff
manualmente y volver a ejecutar las suites. No declarar que el juego ya cumple
los requisitos de Undo, fragilidad, progresion, score o save/load.
