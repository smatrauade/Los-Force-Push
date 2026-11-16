# Evidencia de ejecución Windows

Registro histórico del **13/11/2026**. Código ejecutado y grabado: `ee2681148f8dde7425968b61b19b631e5532bb04`. Entorno: Windows 11 x64, build 26200, Oracle JDK 26.0.1; compilación `--release 17`, UTF-8, `-Xlint:all -Werror`. Esta evidencia no se presenta como una ejecución nueva de la revisión editorial.

## Comandos y resultados registrados

Desde la raíz del clon:

```powershell
powershell.exe -NoProfile -File .\scripts\test-level-loading.ps1 -IncludeMovement
powershell.exe -NoProfile -File .\scripts\run-swing.ps1 -Test
powershell.exe -NoProfile -File .\scripts\run-swing.ps1 -UiTest
```

| Runner | Suites ejecutadas | Resultado histórico |
|---|---|---|
| Core | Loading 40, Movement 37, Extended 26, DynamicWorld 6, Completion 38 | 147 aprobadas, exit 0 |
| Desktop | Desktop 40, Assets 44, Theme 7, Animation 14, ReplayController 8, ReplayPresentation 8, ShippedLevels 41, ReleaseCampaign 6, LockObserver 6, ControllerBoundary 3 | 177 aprobadas, exit 0 |
| UI | UI smoke 17, release UI smoke 7 | 24 aprobadas, exit 0 |

Se conservan sin alterar las salidas [Core](evidencia/core.txt), [Desktop](evidencia/desktop.txt) y [UI](evidencia/ui.txt). El mensaje de notificación Lock de `LockObserverTest` corresponde a una prueba deliberada de aislamiento de errores. No se suman repeticiones como casos nuevos ni se afirma haber ejecutado toda suite Java del repositorio.

## Campaña y video

La ventana Swing se operó mediante automatización de teclado/ratón. Se completaron los cinco niveles con puntajes 993, 983, 996, 991 y 989. Se recorrieron temas, Replay, Undo, Restart, HUD, cerrojos/muros, hielo, resistencia/rotura y carga de un TXT externo mediante selector de archivos real.

OBS Studio 33.0.1 capturó ventana y audio de aplicación, con micrófono y escritorio silenciados. El video combina el recorrido y un complemento del selector modal. FFmpeg recompuso H.264 CRF18 con copia del AAC. FFprobe registró 1280×720, 30 FPS y 476,77 s; la decodificación completa terminó sin errores. La señal AAC no acredita escucha humana.

## Alcance de esta evidencia

La compilación a release 17 no prueba un runtime JDK 17 nativo. La automatización no se presenta como ensayo humano de periféricos, percepción visual o escucha. Los hashes identifican los archivos y no certifican derechos de recursos, aprobación docente ni entrega por correo.

El MP4 y las salidas de tests se mantienen como evidencia histórica. Los [diagramas PlantUML/PNG](../../docs/architecture/uml/pr17-rc-ffa27f9/README.md) tienen un registro técnico propio; los documentos editables/PDF y los hashes actuales se identifican en el [índice](README.md).
