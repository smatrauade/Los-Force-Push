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
