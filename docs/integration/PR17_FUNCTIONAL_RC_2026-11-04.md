# Auditoría funcional y candidato del PR #17

`AS_OF=2026-11-04` · `DOMAIN=SOKOBAN_PR17_FUNCTIONAL_RELEASE_CANDIDATE` · `STATUS=IN_PROGRESS`.

Base auditada: `b73b4807f9387b94e7aef9c18c7e1fb45401137d`. Base del PR/main: `f4a28fd6c1d869fcd6f6189e5cbc2da49bb2f7a2`. GitHub y `git ls-remote` confirmaron los seis HEAD indicados por la solicitud; PR [#17](https://github.com/smatrauade/Los-Force-Push/pull/17) abierto/Draft, 24 commits, 89 rutas, sin reviews. Cuenta autenticada con push, repositorio público, default `main`. Sandbox independiente, sin modificar los worktrees existentes. Tres ramas fuente son ancestros del PR; `impaciencia` diverge (26/12 commits exclusivos), sólo referencia individual.

Rehidratación: AGENTS, índice, fuentes vivas normativas/de diseño/de QA y derechos leídos; originales PDF/audio/chats ausentes no inspeccionados. E01–E11, D-020 y fórmula TEAM preservados. El adjunto de misión se leyó como contexto, sin heredar autorizaciones diferentes de la solicitud actual.

## Diagnóstico previo a editar código

| Prioridad | Evidencia | Acción segura / gate |
|---|---|---|
| P0 del alcance | Replay core sin colaboración de controlador ni pantalla | Conectar sin tocar motor/sesión; verificar trazas y aislamiento |
| P1 | startDemo sólo demo/campaign-b; no recorre ICE/Fragile | Catálogo compartido y TXT con testigos resolubles |
| P1 | README/TODO afirman I2 local y animación pendiente; placeholders save/load | Corregir capacidades y disponibilidad, conservar historia |
| P1 | Continuar y error de carga durante victoria animada requieren auditoría | Pruebas nativas de navegación y cancelación |
| P2 | E/S local síncrona EDT; UML candidato antiguo | Registrar límite; no ampliar a I/O asíncrona ni StarUML en esta misión |

## Incremento Replay

Se conserva sin cambios el paquete core `replay`. El controlador registra únicamente éxitos, recorta cinco acciones tras Undo válido y abre otro intento tras Restart/carga/Next exitosos. Carga/Next fallidos conservan el intento y su traza. En victoria compara mundo completo y moves/pushes usando `ReplayRecorder.verified`; overflow/incoherencia deshabilitan sólo Replay. Sólo se ofrece el nivel completado actual; tras avanzar se retira, sin caché global de partidas.

`ReplayPanel` ofrece Play/Pause/Step/Reset/Back, tablero propio y Timer EDT con token de generación. Reproduce pasos discretos del motor existente, sin SFX, segunda animación ICE, score, historial Undo ni progresión. `VisualThemeController` aplica la misma fábrica a ambos tableros. `VictoryGate` sigue administrando la victoria de la partida real.

Evidencia por Codex, Ubuntu 26.04/Linux x86_64, java/javac 17.0.20.1 del mismo JDK. Fuentes UTF-8, `--release 17 -Xlint:all -Werror`; core java.base, desktop java.base,java.desktop. Logs locales fuera de Git conservados en la carpeta de evidencia de la sesión; comandos reproducibles versionados.

| Ejecución | Contenido ensayado | Resultado | Exit |
|---|---|---|---|
| `bash scripts/test-replay-i3d1.sh` | Base exacta b73b480 | Replay 20 PASS | 0 |
| `bash scripts/test-integration-i2.sh` | Base exacta b73b480 | Undo 58/core 258/Desktop 40/campaña 41/temas 7/animación 14/assets 44 PASS | 0 |
| `bash scripts/test-integration-i2.sh` | Base + delta Replay antes de commit | Bases conservadas; ReplayController 8 y ReplayPresentation 8 PASS | 0 |

Las dos suites nuevas se ejecutan desde los runners Bash/PowerShell; las bases no se sustituyen ni se suman repeticiones como casos nuevos. Ventana nativa, escucha y gate final se registrarán tras su ejecución. TEAM_ACCEPTANCE sigue NOT_RECORDED; RF-12/OQ-10, derechos, StarUML y entregables externos no se cierran por estos tests.

## Campaña y hardening

Catálogo único `DemoCampaign`: demo → campaign-b → ICE → Fragile → Fragile/ICE. Nuevo TXT `fragile-ice-demo.txt` reutiliza gramática vigente: resistencia 1, piso inicial, franja ICE y caja normal hacia Goal. Testigo → ↓ → → → → →, 7 movimientos, 2 empujes, score 989. La primera caja completa el path y rompe; el motor y la regla de victoria no cambian. Los cinco resultados esperados son 993/983/996/991/989. Se conservan intactas las 41 comprobaciones de niveles anteriores; suite nueva de release: 6 escenarios.

Defectos confirmados en Swing: Continuar dirigía siempre a GAME, incluso con sesión finalizada; una carga inválida durante animación de victoria descartaba `VictoryGate` y dejaba el nivel congelado sin resumen. Se conserva ahora el resultado presentado y se resuelve la victoria diferida antes de una carga. Nueva partida siempre usa los TXT distribuidos, incluso si la ventana abrió antes un archivo externo. Los botones de persistencia siguen deshabilitados con explicación explícita, sin imponer un servicio inexistente. HUD mantiene score/usos Undo, además de las métricas previas.

Pruebas del delta sobre `ad4575d`: `PATH=<JDK17>/bin:$PATH bash scripts/run-swing.sh --test`, exit 0, suites previas preservadas y ReleaseCampaign 6 PASS. `bash scripts/run-swing.sh --ui-test` con la pareja JDK17 y acceso al escritorio, exit 0: UI Smoke 17 + Release UI 7 PASS. Esta última usa ventana visible, bindings y botones reales, verifica Replay aislado, Back/ESC/Continue, cambio de temas, progresión de cinco niveles, carga inválida durante victoria ICE y Restart durante rotura, cierre con timer activo. Es QA automatizada Linux; no es evaluación visual humana ni escucha. Los screenshots del smoke anterior son pintura de la ventana, no evidencia de escucha. Windows/macOS y PowerShell nativo: NOT_RUN.

El runner PowerShell omitía `ShippedLevelsIntegrationTest`; ahora ejecuta esa suite y las nuevas de Replay/campaña con chequeos de exit. Cambio inspeccionado estáticamente; no ejecutado en Windows. Se conserva el baseline Java 17 y no se añade CI (AGENTS lo prohíbe).

## Observer y revisión de diseño

Se adaptó selectivamente `LockChangeSubject` de `impaciencia@b2c6332`, referencia individual, sin fusionar su mundo/sesión/score. Problema real: desacoplar avisos de presentación y audio de las diferencias Lock confirmadas, incluyendo restauraciones. Subject: `LockChangeSubject`; observadores efectivos: `GameView.onLockChange` (aviso Swing) y suscriptor de efectos; cliente publicador: `GameController`, después de confirmar y representar la transición. MOVE emite LOCK/GATE una vez; Undo/Restart notifican cambios pero conservan sus sonidos de acción. LEVEL_CHANGE usa baseline nulo para no comparar IDs de niveles distintos. Cerrar ventana libera ambos suscriptores.

Alternativa: llamadas directas del controlador a cada efecto, con diferencias repetidas en move/undo/restart/carga. Se eligió un dispatcher pequeño con evento inmutable y suscriptores reemplazables, a coste de un tipo más y disciplina de ciclo de vida. No es Observer Lock→Walls mutante: los muros siguen derivados exclusivamente de `RuntimeState`; no se afirma aceptación docente de esta variante del patrón. El payload no expone la sesión ni permite vetar una transición. Excepciones Runtime de presentación se registran y aíslan del resto de observadores y de la victoria.

`LockObserverTest`: 6 escenarios (movimiento/Undo y SFX únicos, Restart/carga fallida/limpieza, deduplicación/unsubscribe/evento inmutable, cierre ocupado sin evento, error de listener aislado/no-op, grupos independientes). El primer testigo de Undo falló con exit 1 porque sus cinco pasos no retiraban la llave; se corrigió el testigo para alcanzar el otro lado del Lock, sin cambiar el motor. Log fallido conservado. Retest desktop exit 0, bases preservadas y Observer 6 PASS. El mensaje stderr de fallo de observador en la prueba negativa es intencional.

SOLID/GRASP/MVC: el motor es experto en movimiento; RuntimeState en invariantes y muros; GameSession en historial/métricas; LevelProgression en finalización; controlador coordina; GameView separa Swing; Replay es cursor propio. Las variantes de caja son datos validados con dispatch central en motor/renderer, no jerarquía ficticia ni resistencia impuesta a toda caja. Compromisos existentes: copia de estado por transición, parsers legacy/runtime separados y E/S síncrona EDT acotada a 64 KiB (sin garantía de latencia de filesystem). No hay patrones decorativos nuevos.

Patrones defendibles: Abstract Factory (familias de paleta + transformación de sprites, tres fábricas, consumidores reales); Command (`MoveCommand`, receptor `RuntimeMovementEngine`, invocador `ReplayPlayer`, cliente/registro del controlador); Observer (subject/observadores citados). Builder es simplificado: `LevelBuilder` interno por carga, parser director implícito, alternativa construcción directa. Undo utiliza snapshots privados estilo Memento, sin afirmar un Caretaker externo formal. `ScoreCalculator` no es Strategy; SoundManager no es Singleton; Decorator, Prototype y Factory Method no están implementados por estos nombres/callbacks. Las pruebas de cada colaboración están en las suites de temas/Replay/Observer y la regresión existente. Ninguno de esos patrones ratifica RF-12.

## Borde confirmado: victoria inicial

Test contrafactual sobre `607fdd2`: TXT válido `##### / #@#*# / #####`, ya resuelto y sin ningún movimiento posible. `ControllerBoundaryTest` produjo exit 1: no se presentaba resultado al cargar. Se extrajo `completeIfWon` del controlador y se invoca después de publicar una carga o avance exitoso, conservando el orden animación→victoria del movimiento. El evaluador, engine y sesión no se modifican. Se finaliza una sola vez, score 1000, traza vacía válida y ningún timer Replay. Retest desktop: exit 0, nuevas 3 pruebas de carga/finalización única, Replay vacío y Next a nivel resuelto; bases preservadas. Logs before/after conservados.

Audio nativo opcional: `PATH=<JDK17>/bin:$PATH bash scripts/run-swing.sh --audio-test`, exit 0; `NativeAudioSmokeTest` abrió y completó los 12 WAV por el dispositivo de sonido, con posición final de clip comprobada. Esto acredita reproducción por dispositivo, **no escucha humana ni derechos**. Si no hay dispositivo, la prueba informa `NOT_RUN_NO_DEVICE`; la aplicación conserva degradación controlada.
