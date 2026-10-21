# Checkpoint Windows nativo del PR #5 — 2026-10-21

**Dominio técnico probado:** `SOKOBAN_WINDOWS_PR5_NATIVE_VALIDATION`.
**Resultado de agente:** `PASS`, documentado con artefactos coherentes y hashes contrastados; esto **no constituye ejecución nueva de este script**.

## Identidad e integración

- Repositorio: [smatrauade/Los-Force-Push](https://github.com/smatrauade/Los-Force-Push), [PR #5](https://github.com/smatrauade/Los-Force-Push/pull/5), **OPEN/Draft** sin merge ni revisión humana formal al corte del 21/10/2026.
- Rama PR: `feat/swing-playable-notebook-v1`. HEAD exacto **del código ejecutado**: `0670a4d5137f0e9f1db221866a02c29987024847`; base `main` exacta `ee83f9ebd30ee1b553c555c89d9ea453131d6678`; PR 3 commits por delante y ninguno detrás.
- El commit que publique **estos documentos** será posterior al SHA probado; no atribuirle un test que no se ejecutó. `main` aún no contiene Swing. Revalidar los refs antes de actuar.

## Resultado Windows nativo (información reportada, no una reejecución aquí)

| Gate | Alcance | Evidencia del agente |
|---|---|---|
| G0–G1 | Windows 11 Pro 25H2 build 26200.9711 x64; refs y clon limpio; Temurin `java`/`javac` 21.0.12.1 | PASS reportado |
| G2 | PowerShell 5.1.26100.9711 y 7.6.5; nueve casos CLI por shell, **18/18**, exit 0 | PASS reportado |
| G2 | 40 Level Loading y 31 Movement **casos únicos Java**, cubiertos transitivamente en escenarios positivos CLI; los negativos salen con error **esperado** sin Java | PASS reportado; ningún core standalone duplicado |
| G3 | `scripts/run-swing.ps1 -Test`, `--release 17`, UTF-8, `-Xlint:all -Werror`; **16/16 desktop headless**, exit 0 | PASS reportado |
| G4 | Ventana Swing real controlada desde el escritorio mediante Computer Use; flechas, empujes, bloqueos, HUD y TXT externo válido/inválido | PASS reportado con capturas nuevas |
| G5–G6 | Manifiesto/evidencia, código intacto, dos checkouts históricos preservados, remoto sin cambio | PASS reportado |

**Secuencia visual demo:** jugador (2,2), caja (2,4), destino (2,6), HUD movimientos/empujes 0/0; `Right` → 1/0, `Right` → 2/1, `Right` → 3/2 con caja en destino, `Up` → 4/2, `Up` bloqueado deja 4/2. Las imágenes anterior/posterior al bloqueo tienen SHA-256 idéntico `24db7e6c232ab7ca2d71451daeaadfae00bef39178c0e2547c0226e9b91a5d76`.

**TXT externos:** se abrió un nivel válido con `ñ` en el nombre, tablero 3×5, HUD reseteado 0/0 y un empuje 1/1; luego un TXT con símbolo desconocido `U+007E` produjo `Error de carga` y dejó la sesión anterior intacta. La aplicación se cerró normalmente, exit 0. La validación headless por sí sola **no** certifica interacción GUI; la evidencia visual se registró de manera separada.

## Fuentes, hashes y disponibilidad

El agente generó `report.md`, `result.json`, `SHA256SUMS.txt`, logs, fixtures y nueve JPEG: el manifiesto original enumera **48 archivos**. Se verificaron SHA-256 de los dos documentos recibidos y sus referencias de manifiesto, y se inspeccionaron las nueve capturas en la conversación previa. **Los 48 archivos originales no forman parte del repositorio**, por lo que un nuevo clon sólo dispone de este informe y el [resumen JSON](WINDOWS_PR5_NATIVE_QA_2026-10-21.json), no de los logs/capturas originales.

- `report.md`: `4720bfe8679b306a4c374b4cb7a964355c7c7a30c344e79beb2dfb6abf682fbc`.
- `result.json`: `7500b808422caf9c5187ce18f1aa32b8070078e7ac2d30958a4a9f177b0ca27`.
- `SHA256SUMS.txt`: `9327efa2ae9f7c66791e894545ee01f418eaaa2a3be27ad10c7190d5d7a077c7`.
- Carpeta original de evidencia externa Windows: `V:\VioletDump\SOKOBAN-PR5-WINDOWS-QA\run-20261021T215430Z-e8332b68e344\evidence` (**no portable**).
- Los dos worktrees Windows históricos fueron reportados sin cambios, 123 archivos comparados. Este documento no puede verificar de manera independiente el sistema de archivos de esa PC.

**Clasificación:** `GITHUB_VERIFIED_AS_OF_DATE` para refs; `HASH_VERIFIED` para adjuntos; `REPORTED_BY_AGENT` para invocaciones/GUI/preservación; `HISTORICAL` para Linux R2/R3 y Windows del original; `NOT_RUN` para GUI Linux y macOS, aceptación del equipo y juego completo. No inflar conteos de casos únicos con las repeticiones intencionales de la CLI.

## Restricciones académicas y siguiente puerta

La consigna exige Java/Swing 2D, nivel TXT real, cajas normal/frágil/llave, hielo, cerraduras, Undo, victoria, HUD, gráficos de dominio público, audio y dos extras con patrones; además README, video, StarUML `.mdj`, informe y defensa. Las decisiones OQ-01…OQ-11 y patrones `PREFERRED` siguen abiertas. Este incremento es **experimental**; el PASS no equivale a aceptación productiva, aprobación de UML ni resolución de reglas.

`NEXT_GATE = POST_PR5_INTEGRATION_AUDIT_AND_SCOPED_BACKLOG`. El usuario decidió habilitar integración como autor, sujeta a gates Git de forma separada, sin **autoaprobación formal** (GitHub no la permite) ni falsear revisión del equipo. Después del merge verificar `main`, documentos, ramas y continuar con revisión colaborativa posterior. No publicar evidencia privada ni implementar nuevas mecánicas por inferencia. Para continuar: [AGENTS](../../../AGENTS.md) → [CURRENT_STATE](../CURRENT_STATE.md) → [FRESHNESS_POLICY](../FRESHNESS_POLICY.md) → [requisitos](../../requirements/REQUIREMENTS.md) → [decisiones](../../decisions/DECISION_LOG.md) → [UML](../../architecture/uml/STARUML_STATUS.md) → [TODO](../../../TODO.md). Verificar HEAD vivo antes de cada nueva acción.

## Contrato postintegración (lectura de futuro)

La integración del PR #5 queda registrada **por el evento GitHub real**, no anticipada en este archivo: `gh pr view 5 --json state,isDraft,mergedAt,mergeCommit,headRefOid,baseRefOid`. Un lector de `main` debe contrastar esa salida con `CURRENT_STATE`, registrar el SHA del merge y distinguir código experimental integrado de aceptación final. Si el PR sigue Draft, **no** afirmar integración. Revisión de equipo: `NOT_RECORDED` hasta evidencia separada.
