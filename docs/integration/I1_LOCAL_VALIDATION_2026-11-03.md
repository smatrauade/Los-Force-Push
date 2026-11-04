# I1 — Auditoría local previa al merge commit (03/11/2026)

<!-- I1_PR17_PUBLICATION_2026_11_03 -->

## Estado posterior a esta auditoría — publicación verificada (03/11/2026)

`PUBLICATION=PR17_OPEN_DRAFT` · `INTEGRATION_HEAD=6abb8ad003ad9af6642fc1a1e11a91f5d238f2b2` · `IN_MAIN=NO` · `TEAM_ACCEPTANCE=NOT_RECORDED`.

La auditoría local registrada debajo precede al commit y debe leerse como **histórica**: las rutas `UU` y `MERGE_HEAD` dejaron de estar pendientes. Se creó un merge real de **dos padres** (`5af4a586001ecd8da1856483da023ce15eeccfc1` y `ac728a2241d2fae8c4c32b78e29fd4c9bc6228eb`) que conserva los dos commits originales de `Objetivo2`; los **38 archivos** auditados entraron en `6abb8ad0` y se publicaron al mismo [PR #17](https://github.com/smatrauade/Los-Force-Push/pull/17). El avance local y el push sin fuerza fueron verificados; `main` no cambió.

**Evidencia sin extrapolación:** `316/316 PASS` en el árbol local I1.C con OpenJDK 17.0.20.1, antes del commit; el postcommit auditó los blobs, pero **no hubo nueva ejecución Java sobre el SHA publicado**. Pendientes I2, I3, I4, GUI real, Windows y aceptación TEAM. Todo lo indicado a continuación como «pendiente» se refiere al checkpoint previo a esta publicación.

`DOMAIN=TXT_RUNTIME_MOVEMENT_INTEGRATION` · `STATE=HISTORICAL_PRECOMMIT_WITH_PUBLISHED_CHECKPOINT` · `AUTHORSHIP=GIT_ANCESTRY_TO_BE_PRESERVED` · `TEAM_ACCEPTANCE=NOT_RECORDED` · `IN_MAIN=NO`

## Rama y procedencia

- Repositorio `smatrauade/Los-Force-Push`; único [PR #17](https://github.com/smatrauade/Los-Force-Push/pull/17), Draft, base `main`, head remoto `integration/tpo-unificado-2026-11-03`.
- Worktree **auxiliar local**, rama `integration/i1-santino-stage-2026-11-03`, `HEAD=5af4a586001ecd8da1856483da023ce15eeccfc1`, `MERGE_HEAD=ac728a2241d2fae8c4c32b78e29fd4c9bc6228eb`, merge real **iniciado pero no confirmado**.
- `Objetivo2` contiene commits originales `bb0df7312689463384fa31159303dec0a8dcd240` y `ac728a2241d2fae8c4c32b78e29fd4c9bc6228eb`; el autor registrado por Git es `smatrauade`. La atribución funcional al módulo de Santino no autoriza reescribir los autores históricos.
- `victoria@a7df64172dbb857e7a10709ec9ea85a5f80aacf7` (módulo de Juan) es antecesor de `HEAD`. `main` y el worktree principal del PR no se alteran por esta preparación local.
- **No** interpretar `MERGE_HEAD` como un commit incorporado al PR, ni una salida de pruebas como aceptación del equipo. Verificar los SHA y el estado remoto otra vez antes de publicar.

## Cambios locales en evaluación

| Incremento | Implementación | Límite |
|---|---|---|
| I1.A | `BoardPanel`, `GameController`, `SwingApplication` reconciliados textualmente; la sesión, progresión y `RuntimeState` siguen siendo la autoridad | Índice conserva tres entradas `UU`, no se ejecutó `git add` |
| I1.B0 | `LevelLoader.load` rechaza conversión Fragile con pérdida de ID; `loadExperimentalFragile` mantiene identidad y resistencia | La gramática D-020 no se amplía por inferencia |
| I1.B1 | `RuntimeState` incorpora ICE y Fragile con ID/resistencia; trayectoria de empuje y rotura tras deslizamiento; `LevelCatalog` opt-in | Animación de trayectoria/destrucción en GUI aún pendiente de I3; selector TXT no autodetecta Fragile |
| I1.C | Testigo INT-01 de dos Fragiles con resistencias **2 y 3**, dos empujes y estado restaurable; INT-02 atraviesa `LevelProgression`; INT-03 comprueba sesión sin reinicio; INT-04 comprueba cierre ocupado; runner versionado | **Ejecución local posterior: 316/316 PASS con JDK17**, sin commit ni push |

## Evidencia de ejecución previa, sin extrapolación

Resultados **reportados por la ejecución local del usuario** el 03/11/2026 en Ubuntu 26.04.1, `java`/`javac 17.0.20.1`, compilación estricta `--release 17 -encoding UTF-8 -Xlint:all -Werror`:

- Core: Level Loading 40, Movement 37, Extended 26, Dynamic World 6, Completion 36, C2 36, Experimental Fragile 41, Runtime ICE/Fragile 30 = **252/252 PASS**.
- Desktop headless **16/16 PASS** y Visual Assets **44/44 PASS** con recursos reales; **312/312** total en aquella versión previa del test.
- Verificador externo final `RESULTADO_GLOBAL=PASS_I1B1_VERIFIED`, `INDEPENDENT_POSTCHECK=PASS`, índice Git y fuentes comprobados sin cambios por los tests.
- El precommit read-only también informó los tres archivos Swing sin marcadores, `git diff --check` y `git diff --cached --check` sin errores, y nueve entradas no fusionadas en el índice.
- Estos resultados son de un **worktree con modificaciones locales sin commit**, no del HEAD remoto, `main`, Windows, macOS ni GUI interactiva. La salida completa de aquella ejecución permanece como evidencia externa al repositorio; no fue importada.

## Repetición autocontenida tras I1.C

```bash
bash scripts/test-integration-i1.sh
bash scripts/verify-repo-readonly.sh
```

El primer comando selecciona `java` y `javac` de **un mismo JDK 17**, compila el core aislado (`java.base`) y ejecuta las ocho suites core, luego la suite Desktop y Visual Assets. Tras el cambio de testigo de I1.C, el **oráculo observado y reportado en el checkout local**, es **256 core + 16 Desktop + 44 Visual Assets = 316 tests**, cero fallos. Comprueba inmutabilidad de fuentes, estado Git e índice durante las pruebas. Si no pasa, no crear commit ni publicar.

`INT-01`: dos Fragiles de ID distinto, resistencias iniciales 2/3 y dos empujes; `INT-02`: ICE desde TXT mediante `LevelProgression`; `INT-03`: deslizamiento, rotura final, una pérdida por empuje y ninguna restauración automática de sesión; `INT-04`: cambios Lock/DynamicWall y bloqueo atómico de cierre ocupado. Las pruebas de estos contratos son **core headless**: no certifican frames ni sonidos de la GUI.

## Evidencia posterior a I1.C — ejecución local verificada por el usuario (03/11/2026)

La salida de terminal proporcionada por el usuario para `bash scripts/test-integration-i1.sh` en Ubuntu con `java` y `javac` **17.0.20.1** identifica compilación estricta de core `CORE_COMPILE=PASS` y las ocho suites: Loading **40/40**, Movement **37/37**, Extended **26/26**, Dynamic World **6/6**, Completion **36/36**, C2 **36/36**, Experimental Fragile **41/41** y Runtime ICE/Fragile **34/34**, total core **256/256**. Desktop Integration **16/16** y Visual Assets **44/44**, total **316/316 PASS**, salida `RESULTADO_GLOBAL=PASS_I1C_LOCAL_TESTS_316`, exit 0. `GIT_STATE_PRESERVED=YES` al finalizar el runner. El resumen de salida fue aportado en la conversación; los logs completos no se incorporaron al repositorio.

El usuario ejecutó además `bash scripts/verify-repo-readonly.sh`: **PASS**, 170 archivos, 541 enlaces relativos, 43 PNG/WAV registrados (formato/hash válidos), sin validar con ello la GUI real ni los derechos. La auditoría de contenido read-only posterior, corregida para ignorar diferencias POSIX `0644`/`0664` sin cambio del bit ejecutable de Git, informó: 2 commits fuente por preservar, tres conflictos resueltos en texto, nueve entradas de índice `UU`, **38 paths esperados, 0 inesperados**, hashes del árbol local y `git diff --check` PASS; `INDEPENDENT_POSTCHECK=PASS`. No hubo `git add`, commit, push ni publicación de I1.

**Alcance probatorio:** árbol local I1.A/B0/B1/C sin commit, JDK17 Linux, pruebas core, desktop headless y assets; **no** GUI interactiva, sonido audible, Windows/macOS, autorización del equipo ni HEAD remoto. La edición documental posterior a estos tests requiere verificar sus propios hashes/enlaces; no atribuir a los 316 tests una segunda ejecución inexistente.

## Bloqueos, autorizaciones y siguiente puerta

- `I1=LOCAL_PENDING_REVIEW`; el merge commit de dos padres y el push al **mismo** PR #17 quedan pendientes de auditoría explícita. Ninguna ejecución de este documento realiza `git add`, `commit` ni `push`.
- La sintaxis TXT de Fragile inicialmente ubicado sobre ICE **no está ratificada** en D-020; el caso soportado parte de Fragile sobre piso y desliza sobre ICE. No ampliar por omisión.
- `INT-07` y `INT-09` (victoria combinada y animación completa) tienen evaluación pendiente; no afirmar que I1 termina la campaña/UI.
- I2: reconciliar Undo/Restart/Score de Martina sin duplicar `GameSession`; I3: conectar HUD, animación, eventos y selector; I4: recompilar en HEAD integrado, GUI real, Windows/PowerShell, derechos, StarUML, informe/video y aceptación humana.
- Antes del commit: ejecutar runner, `git diff --check`, `git diff --cached --check`, revisar todos los cambios fusionados, resolver manualmente las tres rutas `UU` mediante un `git add` **específico y autorizado**, comprobar `MERGE_HEAD` y crear merge commit con dos padres sin reescritura de autores. Antes de promover al PR, verificar origen, SHA y destino; `main` no debe tocarse.

## Documentos y fuentes

Autoridad: [decisiones E03/E04/E05/E06 y D-020](../decisions/DECISION_LOG.md), [requisitos](../requirements/REQUIREMENTS.md), [preguntas abiertas](../requirements/OPEN_QUESTIONS.md), [matriz de aceptación](INTEGRATION_ACCEPTANCE.md), [estrategia de pruebas](../testing/TEST_STRATEGY.md), [trazabilidad](../traceability/TRACEABILITY_MATRIX.md), [frescura por rama](../continuity/BRANCH_AWARE_FRESHNESS.md) y [plan I0-I4](TEAM_INTEGRATION_PLAN.md).
