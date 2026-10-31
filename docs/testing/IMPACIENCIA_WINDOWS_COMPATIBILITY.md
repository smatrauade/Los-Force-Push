# Compatibilidad Windows de impaciencia

## R9W: evidencia anterior

En Windows 11, sobre `28932aa579ae015abbd73f390a41c308c5c0a1a2`, se reportaron Loading 40, Movement 31, Desktop 19, Assets 55, Extensions 31, Campaign 87 y UI Smoke 22 PASS; la campaña se completó por automatización con 9965/9815/9955. Esa evidencia corresponde al árbol anterior, no a una nueva ejecución de este incremento.

## R9W.1: ejecución nativa

Windows 11 build 26200 x64; PowerShell 5.1.26100.9711 y 7.6.5; Git 2.54.0.windows.1; Oracle JDK 26.0.1, compilación `--release 17`, UTF-8, `-Xlint:all -Werror`. Se ensayó el árbol de esta modificación basado en `28932aa579ae015abbd73f390a41c308c5c0a1a2`; el historial Git identifica el contenido confirmado. JDK 17 nativo: NOT_RUN.

```powershell
powershell.exe -NoProfile -File .\scripts\test-level-loading.ps1 -Full
powershell.exe -NoProfile -File .\scripts\run-swing.ps1 -Test
powershell.exe -NoProfile -File .\scripts\run-swing.ps1 -UiTest
```

Core Full, PowerShell 5.1 y 7: Loading 40, Movement 31, Runtime 26, Dynamic 36, Extended 51, Session 34, Completion 58, C2 32 y Fragile 41 PASS, exit 0. Desktop headless: Integration 19, Assets 55, Extensions 31, Campaign 87 PASS, exit 0. UI Smoke: 38 PASS, exit 0; acciones Swing programáticas, no teclado físico. Comprueba victoria A/C, replay, temas, Undo y Restart; al mínimo 880×720 y tamaño inicial comprueba rectángulos visibles de los cuatro controles de victoria. El ensayo no demuestra escucha audible ni uso físico de mouse/teclado.

El botón final estaba recortado: stack con alto fijo 520, mínimo calculado 555; botón 395×44 con sólo 38 píxeles visibles. Escala observada 100 %. Se eliminó la altura fija y se agregó desplazamiento para conservar el acceso con poco espacio. Otras escalas: NOT_RUN.

El inicio `run-swing.ps1 -LevelPath '.\src\desktop\resources\levels\campaign-c.txt' -Extended` abrió realmente el tablero con Fragile y hielo. En JFileChooser se cargó A como STANDARD y C como EXTENDED; C como STANDARD produjo error y conservó el tablero A. Los comandos `-Extended` sin ruta, `-Test -UiTest`, `-Test -Extended` y `-UiTest -LevelPath foo` devolvieron exit 1 con diagnóstico. No se verificó audio audible; tampoco teclado o mouse físicos. El diálogo se manejó mediante automatización de ventana.

Después del ajuste se completó la campaña en la ventana con eventos automatizados de teclado y mouse: 9965, 9815 y 9955, y el botón final volvió al menú. Una copia temporal de C llamada `nivél ñ.txt` se abrió mediante ruta relativa desde otro directorio, con checkout y nombre con espacios; salió 0 al cerrar. `test-swing-arguments.ps1`: cinco errores esperados en cada uno de PowerShell 5.1 y 7, exit 0. Un JDK inexistente y un nivel ausente produjeron exit 1. El verificador Git Bash/Python ejecutado fuera del sandbox por una restricción MSYS verificó 528 enlaces y 43 hashes, exit 0.

La ruta de checkout incluye espacios; las suites de carga prueban nombres Unicode. Los derechos de assets, elegibilidad académica RF-12, aceptación TEAM y StarUML siguen pendientes; R10 no se ejecutó. El score y EXTENDED son decisiones individuales experimentales.
