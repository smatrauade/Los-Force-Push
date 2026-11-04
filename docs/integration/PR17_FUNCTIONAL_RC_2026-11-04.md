# Auditoría funcional y candidato del PR #17

**Contexto actual:** el PR #17 fue integrado en `main`. Esta auditoría conserva el diagnóstico, resultados y estados del corte en que se ejecutaron; la puesta en común y los entregables externos siguen en la [agenda vigente](../../TODO.md).

`AS_OF=2026-11-04` · `DOMAIN=SOKOBAN_PR17_FUNCTIONAL_RELEASE_CANDIDATE` · `STATUS=SOFTWARE_RC_PENDING_EXTERNAL_GATES`.

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

## Matriz de requisitos del candidato

`VERIFIED` identifica implementación con evidencia técnica ejecutada; no implica aprobación académica/TEAM. La autoridad normativa permanece en REQUIREMENTS/DECISION_LOG; esta matriz no agrega reglas ni cierra OQ por inferencia.

| Requisito | Auditoría inicial b73b480 | Estado RC / evidencia | Pendiente externo o límite |
|---|---|---|---|
| RF-01 tablero/entidades | IMPLEMENTED | VERIFIED: RuntimeState, renderer, core 258, assets 44, campaña release | Ratificación de detalles OQ-07/08 |
| RF-02 TXT externos | IMPLEMENTED; README incorrecto | VERIFIED: Loader/parsers, Desktop 40, carga fallida atómica, victoria inicial 3 | Formato completo/extensiones OQ-08; ensayo docente humano |
| RF-03 movimiento | IMPLEMENTED | VERIFIED: Movement 37, runtime y bindings nativos | Sin regla nueva |
| RF-04 cajas/ICE | IMPLEMENTED | VERIFIED: RuntimeIceFragile 34, campaña, animación 14 y GUI | Bordes OQ-02 no se ratifican por tests |
| RF-05 Fragile | IMPLEMENTED | VERIFIED: identidad/resistencia, E04, Undo/Restart, TXT nuevo resoluble | D-020 intacta; no inicial Fragile sobre ICE |
| RF-06 Key/Lock/muros | IMPLEMENTED | VERIFIED: Dynamic 6, runtime, campaña y Observer 6 | OR/cierre ocupado son contrato implementado pendiente de ratificación OQ-07 |
| RF-07 Undo | IMPLEMENTED | VERIFIED: Undo 58, Desktop y ReplayController; 15/5/3, E07–E09 | Revisión TEAM del código |
| RF-08 victoria/avance | PARTIAL: campaña de dos TXT y borde carga | VERIFIED: Completion 38, campaña 41 + release 6, borde inicial 3, GUI 7 | Criterios OQ-01 se preservan; no inferir nuevos acuerdos |
| RF-09 score/resumen | IMPLEMENTED | VERIFIED: fórmula TEAM, saturación, retención y métricas Replay iguales | Reset de moves/pushes/undoUses al cambiar nivel sigue hipótesis OQ-09 |
| RF-10 HUD/controles | PARTIAL: score sólo al ganar | VERIFIED: HUD permanente, disponibilidad Undo/Restart/Next, GUI 17+7 | QA humana pendiente |
| RF-11 imágenes/SFX/dominio público | PARTIAL | PARTIAL: 43 hashes/formatos válidos, Assets 44, reproducción nativa 12 | BLOCKED jurídico por evidencia de titularidad/licencia; escucha humana NOT_RUN |
| RF-12 dos adicionales con patrones | PARTIAL: temas + Replay core | PARTIAL: selector Abstract Factory y Replay Command funcionales y probados | OPEN OQ-10: elegibilidad y selección TEAM/docente no acreditadas |
| RT-01 Java/Swing | IMPLEMENTED | VERIFIED técnico: JDK17 real, release17/lint/módulos mínimos | OPEN OQ-11 toolchain final; Windows/PowerShell/macOS NOT_RUN actual |
| RT-02 entregables | PARTIAL | PARTIAL: repositorio público, README reproducible y documentación técnica | StarUML final, informe/video/presentación/envío fuera de esta misión |
| RT-03 diseño/defensa | PARTIAL | PARTIAL: MVC/GRASP y colaboraciones reales, pruebas de patrones | Revisión/defensa individual TEAM; UML final pendiente |
| RT-04 IA/revisión | PARTIAL | PARTIAL: asistencia Codex declarada en esta auditoría y fuentes | Comprensión/revisión humana y declaración en informe final pendientes |

### Correspondencia TXT efectiva

- STANDARD: legacy/C2 ordinario + ICE. `LevelParser` puede representar Fragile sin IDs, pero `LevelLoader.load` lo rechaza expresamente; no es ruta runtime con pérdida.
- EXPERIMENTAL_FRAGILE: plantilla + IDs/resistencia en piso/Goal; el adaptador conserva ambas partes. ICE ordinario adyacente está soportado. Token Fragile inicialmente sobre ICE se rechaza en esta definición (D-020 no se amplía).
- EXTENDED: tokens Key/Lock/Wall y `wall <id> locks=<ids>` con validación de referencias y cierre ocupado. La asociación OR es la hipótesis de runtime ya existente; no una decisión nueva. No se promete combinar ICE/Fragile con Lock en un único TXT: ExtendedParser rechaza esas combinaciones, y el catálogo las demuestra en niveles separados.
- Carga TXT válida publica el runtime completo; carga/Next fallidos conservan mundo, métricas, resultado y traza. Los parsers no son un solver ni aceptan cualquier edición del profesor. El catálogo compartido tiene testigos concretos; no se valida resolubilidad general.

## Freeze, comandos y evidencia final de software

`SOFTWARE_FREEZE_SHA=0bbc60e381c0666e4c5f2803bcf469ff23830267`. Compilación y suites ejecutadas por Codex, Ubuntu 26.04.1 LTS/Linux x86_64, OpenJDK **java/javac 17.0.20.1**, release 17, UTF-8, `-Xlint:all -Werror`; sólo java.base para core y java.base,java.desktop para desktop. El `java` por defecto del sistema era 25: se eligió explícitamente la pareja 17, sin modificar el sistema.

| Comando real sobre freeze | Resultado | Exit |
|---|---|---|
| `bash scripts/test-replay-i3d1.sh` | Replay core 20 PASS | 0 |
| `bash scripts/test-integration-i2.sh` | Undo 58; core 258 = Loading40/Movement37/Extended26/Dynamic6/Completion38/C2 36/Fragile41/Runtime34 | 0 |
| mismo runner, desktop transitivo | Desktop40/campaña41/temas7/animación14/assets44; nuevas ReplayController8/ReplayPresentation8/ReleaseCampaign6/Observer6/Boundary3 PASS | 0 |
| `PATH=<JDK17>/bin:$PATH bash scripts/run-swing.sh --ui-test <carpeta-externa>` | Ventana real: UI17 + ReleaseUI7 PASS; navegación, Replay, cinco niveles y cancelación | 0 |
| `PATH=<JDK17>/bin:$PATH bash scripts/run-swing.sh --audio-test` sobre delta Observer incorporado al freeze | Dispositivo: 12 clips completos, sin certificar escucha | 0 |
| `bash scripts/verify-repo-readonly.sh` con documentación final previa al commit | 200 archivos/592 enlaces/43 hashes y formatos PASS al corte; cifras pueden aumentar con enlaces de este cierre | 0 |
| `git diff --check`; `git diff --cached --check` | Sin errores en cada checkpoint | 0 |

La repetición postcommit de los runners es gate de publicación sobre el candidato exacto; no agrega escenarios. Logs locales conservados fuera del clon (incluidos los dos fallos intermedios descritos); no se publican capturas privadas, fuentes docentes, credenciales ni transcripciones. El informe versionado conserva comandos, resultados y SHAs suficientes para reproducir sin esos logs. Sin estados/checks CI ni workflow runs registrados en b73b480; no se añadió GitHub Actions por prohibición de AGENTS.

`GUI_AUTOMATED_NATIVE_LINUX=PASS`; `AUDIO_DEVICE_PLAYBACK=PASS`; `HUMAN_VISUAL_QA=NOT_RUN`; `AUDIO_HUMAN_LISTENING=NOT_RUN`; `WINDOWS_POWERSHELL_CURRENT=NOT_RUN`; `MACOS_CURRENT=NOT_RUN`; `STARUML_NATIVE=NOT_RUN`; `TEAM_ACCEPTANCE=NOT_RECORDED`.

## Commits, alcance y frescura

| Commit / padre | Cambio coherente | Rutas principales / justificación |
|---|---|---|
| `ad4575d9e070d347b56e067e1a84a54ef17a8172` / b73b480 | Replay controlador + pantalla independiente | desktop, dos suites, runners y docs por impacto; cerrar núcleo sin GUI |
| `366fccabeb02e324730249d473ea73c752fc3f7f` / ad4575d | Campaña y navegación | catálogo compartido, TXT Fragile/ICE, HUD, dos suites, README y docs; brechas reproducibles |
| `607fdd28c60901f724e0ac8718ab63c5f0d53fc8` / 366fcca | Observer de hechos Lock | subject/suscriptores, limpieza, suite Observer, audio nativo opcional y docs; muros sin listeners |
| `0bbc60e381c0666e4c5f2803bcf469ff23830267` / 607fdd2 | Victoria inicial | controlador, suite Boundary y runners/docs; fallo confirmado antes del fix |
| commit de este cierre documental / 0bbc60e | Estado y traspaso autocontenidos | Sólo Markdown vivo; SHA final se registra fuera del propio commit (PR/entrega) |

Las contribuciones originales de `victoria`, `Objetivo2` y `feature/undo-restart-score` mantienen historia y autores. Temas/Replay core anteriores y Subject de esta sesión son adaptaciones selectivas de la implementación **individual** `impaciencia`; no se atribuyen a compañeros ni constituyen fusión de esa rama. No hubo importación de código externo, cambio de assets, nueva licencia ni investigación bibliográfica en esta sesión. Asistencia de IA: Codex implementó/revisó/ejecutó esta iteración; la revisión del equipo no se afirma realizada.

| Fuentes vivas | Revisión por impacto |
|---|---|
| README, TODO, CURRENT_STATE, índices, integración, arquitectura, responsabilidades, trazabilidad, pruebas y contrato Punto5 | UPDATED: capacidades, ejecución y límites actuales |
| STARUML_STATUS | UPDATED: alcance histórico y gate .mdj explícito; no modificar diagramas |
| AGENTS, FRESHNESS_POLICY, BRANCH_AWARE_FRESHNESS, CONTRIBUTING | UNCHANGED_VERIFIED: política aplicada; ninguna autorización nueva |
| REQUIREMENTS, OPEN_QUESTIONS, DECISION_LOG, SOURCE_REGISTRY, bibliografía | UNCHANGED_VERIFIED: no nueva norma/voto/fuente docente; E01–E11 y D-020 intactos |
| src/main/java, PNG/WAV, manifiesto/derechos, .puml, cápsulas/checkpoints/fotografías históricas | UNCHANGED_VERIFIED respecto de b73b480: sin cambio en esta sesión |
| StarUML R10 de impaciencia, video/informe final/presentación/correo | NOT_APPLICABLE a edición: otra misión y gate humano |

Preflight: `REPOSITORY_ACCESS=VERIFIED_API_AND_LS_REMOTE`; `REPOSITORY_IDENTITY=smatrauade/Los-Force-Push (1408916139)`; `WRITE_PERMISSION=API_PUSH_TRUE`; `DEFAULT_BRANCH=main`; `BASE_HEAD=f4a28fd`; `WORKTREE=ISOLATED_CLONE_RC`; `EXISTING_BOOTSTRAP_PR=NOT_APPLICABLE (usar PR17 existente)`; `README_STATE=REFRESHED`; `COLLISIONS=NONE_IN_SANDBOX`. `HYDRATION_GATE=PASS` para las fuentes del clon; originales privados ausentes declarados. El título del adjunto no cambió modelo/toolchain ni concedió permisos adicionales.

## Resultado y siguiente gate

`RESULTADO_SOFTWARE=PASS_SOFTWARE_RC_PENDING_EXTERNAL_GATES`. No quedan fallos funcionales conocidos en el alcance probado. No se certifican puzzles arbitrarios, latencia de archivos remotos ni comportamientos no incluidos en los contratos soportados. El gate de publicación requiere diff/rutas/historia limpios, Java17 PASS y remoto fresco; sólo fast-forward normal a `integration/tpo-unificado-2026-11-03`, PR17 **Draft**, sin tocar main/ramas fuente. Tras push se verifica el SHA remoto y se actualizan metadatos en otra operación; un 403 o retraso de API no autoriza repetir el push.

Próxima misión: ratificar elegibilidad RF-12/derechos/revisión TEAM y QA humana/Windows → actualizar `.mdj` desde este freeze y validar en StarUML nativo → informe con IA → video de todas las mecánicas → ensayo de 15–20 minutos y entrega institucional. No se produjo ni envió correo ni se prepararon esos entregables finales. Ready/merge necesitan autorización posterior.
