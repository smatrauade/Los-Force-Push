# Validación nativa Windows — R10

Fecha: 13/11/2026. Actor: Codex mediante comandos nativos y automatización gráfica.
Código ejecutado: `ee2681148f8dde7425968b61b19b631e5532bb04` de `main`.
Windows 11 x64, build 26200; Oracle JDK 26.0.1, compilación `--release 17`, UTF-8, `-Xlint:all -Werror`.
Los resultados siguientes son ejecuciones nuevas de esta misión, no los de `impaciencia`.

## Comandos reproducibles desde la raíz

```powershell
powershell.exe -NoProfile -File .\scripts\test-level-loading.ps1 -IncludeMovement
powershell.exe -NoProfile -File .\scripts\run-swing.ps1 -Test
powershell.exe -NoProfile -File .\scripts\run-swing.ps1 -UiTest
powershell.exe -NoProfile -File .\scripts\run-swing.ps1
```

Todos los runners de pruebas terminaron con exit 0. Los tres primeros comandos son pruebas; el último abre el juego.
No se usaron `-Full` ni `-Extended`, ausentes en los runners de este commit.

| Runner | Suites realmente ejecutadas | Resultado |
|---|---|---|
| Core `-IncludeMovement` | Loading 40, Movement 37, Extended 26, DynamicWorld 6, Completion 38 | 147 PASS, exit 0 |
| Desktop `-Test` | Desktop 40, Assets 44, Theme 7, Animation 14, ReplayController 8, ReplayPresentation 8, ShippedLevels 41, ReleaseCampaign 6, LockObserver 6, ControllerBoundary 3 | 177 PASS, exit 0 |
| UI `-UiTest` | UI smoke 17, release UI smoke 7 | 24 PASS, exit 0 |

El diagnóstico de notificación Lock durante `LockObserverTest` corresponde a su prueba deliberada de aislamiento de errores; la suite pasó.
No se suman estas repeticiones como casos nuevos de los registros históricos. No se ejecutó toda suite Java disponible ni el verificador Bash del repositorio en esta misión.

## Ventana real y grabación

Se operó Swing nativo mediante teclado/ratón automatizados, inspeccionando foco y estado. Esto no acredita uso humano de periféricos físicos.
Se completó la campaña de cinco niveles, con score 993, 983, 996, 991 y 989 respectivamente.
Se verificaron tres temas, Replay Step/Reset/Play/Back independiente, Undo después de cinco movimientos, Restart, HUD, Lock/muros, hielo, resistencia/rotura Fragile y avance.
El selector real de archivos cargó una copia externa de `demo.txt`, mostrando `DEMO_EXTERNO.TXT` y su tablero.

OBS Studio 33.0.1 capturó la ventana Java y audio de aplicación; micrófono y escritorio silenciados.
El video concatena el recorrido principal y un complemento auténtico del selector de archivos, cuya ventana modal requería otra fuente OBS.
FFmpeg recompuso el MP4 a H.264 CRF18, con copia del AAC; 1280×720, 30 FPS, 476.77 s.
FFprobe confirmó codecs, tamaño, frecuencia y duración; FFmpeg decodificó el archivo completo sin errores (exit 0).
Se revisaron fotogramas y las seis páginas renderizadas del PDF. Audio AAC estéreo con señal no nula; escucha humana `NOT_VERIFIED`.

## Integridad y límites

ZIP: CRC de todas las entradas y SHA-256 individual verificados. PDF: seis páginas; DOCX: ZIP interno íntegro; PNG: nueve decodificados.
UML: los 18 archivos se extrajeron como blobs Git exactos, cotejados con `RENDER_GATE.json`; 50 referencias Java fijadas coinciden con el commit ejecutado.
El verificador UML sobre checkout falló por conversión CRLF de los `.puml`; no se alteró su script ni se ocultó el fallo. La verificación alternativa usó bytes Git exactos.
No se recompiló PlantUML ni se generó un StarUML ficticio. JDK 17 nativo, HiDPI adicional, escucha humana y aceptación académica formal: no acreditados por esta misión.
Las herramientas portables y las tomas originales se conservan fuera del repositorio; no son dependencias para jugar.

Salidas de esta ejecución: [Core](evidencia/core.txt), [Desktop](evidencia/desktop.txt), [UI](evidencia/ui.txt).
