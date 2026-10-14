# Estado vivo — Los Force Push

```text
AS_OF = 2026-10-14
REPOSITORY = smatrauade/Los-Force-Push
PRIMARY_DOMAIN = SOKOBAN_BASIC_MOVEMENT
IMPLEMENTATION_STATUS = IMPLEMENTED_EXPERIMENTAL
TEST_STATUS = TESTED
TEAM_ACCEPTANCE = NOT_RECORDED
MOVEMENT_INTEGRATED_IN_MAIN = false
EXPERIMENT_BRANCH = feat/movement-core-spike-v1
EXPERIMENT_BASE = 44b3c63b437465b5f54c1f0f5ca5cbadb5def0e5
VALIDATION_ITERATION = MOVEMENT_R3
LEVEL_LOADING_TESTS = 40_PASS_0_FAIL_ON_JAVA_17_AND_26
MOVEMENT_TESTS = 31_PASS_0_FAIL_ON_JAVA_17_AND_26
LINUX_NATIVE_TEST = PASS
WINDOWS_NATIVE_TEST = NOT_RUN
MACOS_NATIVE_TEST = NOT_RUN
DEFAULT_BRANCH = main
BOOTSTRAP_STATUS = INTEGRATED
PRODUCTION_IMPLEMENTATION = NONE
JAVA_TARGET = OPEN
BUILD_TOOL = OPEN
TXT_FORMAT = OPEN
NEXT_GATE = TEAM_REVIEW_AND_NATIVE_PORTABILITY_TESTS
```

## Estado comprobado y cómo continuar

El preflight R3A del 2026-10-14, vía Git y GitHub CLI como LucasCap12, confirmó el repositorio privado correcto, permisos de lectura/escritura, rama predeterminada `main`, checkout limpio y coincidencia con `origin/main` (divergencia 0/0). En aquel preflight anterior al PR #2 no se encontraron commits posteriores al merge del bootstrap ni otro PR de reconciliación; esa observación se conserva como historia fechada.

**Evento histórico confirmado:** el [PR #1](https://github.com/smatrauade/Los-Force-Push/pull/1) se fusionó el `2026-10-08T20:00:25Z` mediante el commit `ff6fbf170bd9782fb703e1ab4fa1e811ec0782ed`. Ese SHA identifica el merge histórico, no un HEAD permanente. El [preflight inicial](BOOTSTRAP_PREFLIGHT.md) describe el estado anterior al PR y se conserva como evidencia fechada.

El bootstrap documental está integrado en `main`: documentación, UML candidato, script de verificación y estructura `src/main/java`, `src/main/resources` y `src/test/java` con placeholders. La base histórica del merge PR #2 (`1943053ec520ff3d8be0aecb21c2f417f3126380`) no contenía clases Java ni configuración de build. Esta rama incorpora fuentes y tests experimentales, sin ratificación productiva. El merge del bootstrap integró documentación y estructura; no constituye ratificación grupal de las propuestas arquitectónicas.

La fuente del estado actual es Git contrastado con la documentación viva: inspeccionar `git rev-parse HEAD`, `git log`, el árbol y los PRs antes de continuar. Las cápsulas y los informes de preflight anteriores son historia, no sustituyen esa verificación. Las revisiones exactas evaluadas se identifican en el informe de revisión/entrega; evitar autorreferenciar el SHA del commit que contiene este documento.

Requisitos normalizados y aclaraciones están autocontenidos. PDF/libros/materiales originales no están disponibles aquí; SOURCE_REGISTRY distingue bytes presentes de relato histórico. UML v0.5 y cápsula anterior sí estaban en el ZIP y se preservaron byte a byte.

## Baseline experimental de Level Loading posterior al PR #2

El PR #2 se confirmó MERGED el `2026-10-14T17:45:53Z`, commit histórico `1943053ec520ff3d8be0aecb21c2f417f3126380`. Esta es la base histórica del incremento de carga, no la base inmediata del movimiento ni un HEAD permanente. La sincronización local autorizada fue FF-only; no se fusionó ningún PR desde esta ejecución.

El incremento histórico de carga de `feat/level-loading-spike-v1`, versionado localmente en `44b3c63b437465b5f54c1f0f5ca5cbadb5def0e5` y heredado por este snapshot: [TXT candidato y comparación](../experiments/LEVEL_LOADING_R1.md), fuentes bajo `src/main/java/ar/edu/uade/sokoban/level`, fixtures y tests bajo `src/test`, y [UML del spike](../architecture/uml/level-loading-spike-v1.puml). Implementó el subconjunto paredes/piso/destinos/jugador/cajas normales, incluidas superposiciones sobre destinos. Aquel incremento de carga excluía movimiento, cajas especiales, Undo, score, victoria, GUI y audio; el movimiento básico presente se describe a continuación.

Evidencia histórica de Level Loading R3 del 2026-10-14: `scripts/test-level-loading.sh`, compilación/ejecución real con JDK 17.0.20.1 y JDK 26.0.2, opciones estrictas y sólo java.base, **38 PASS / 0 FAIL en cada JDK**. Los tests 25/38 detectaron la desactivación temporal del límite de bytes en una copia descartable (dos fallos esperados, fuente original preservada). PlantUML 1.2020.02 y Graphviz 14.1.2 comprobaron y renderizaron el UML experimental. Las fuentes, fixtures y comandos de esta rama permiten reproducir las pruebas; la base histórica precede a este incremento. Revisión del equipo pendiente; D-011 PREFERRED, OQ-08/OQ-11 OPEN.

Evidencia histórica de Level Loading R4 del 2026-10-14: **40 PASS / 0 FAIL en cada JDK** sobre Linux, incluidas rutas con espacios/Unicode, recarga externa y diagnósticos con Locale.ROOT. Runner Unix compatible por diseño con Bash 3.2/GNU/BSD y runner Windows PowerShell 5.1/7 revisado estáticamente. Copia temporal con checkout/TMPDIR de nombres Unicode y espacios: PASS con ambos JDK; Java 17 también con charset predeterminado US-ASCII. Espacios de niveles protegidos en EditorConfig y atributos LF acotados. Windows/macOS nativos NOT_RUN: verificarlos antes de afirmar portabilidad nativa completa. Código/contrato/modelo siguen experimentales; esta evidencia no ratifica D-011 ni OQ-08/OQ-11.

## Contenido del núcleo experimental de movimiento

Este incremento contiene el núcleo experimental de movimiento cardinal y empuje de una caja normal, con pruebas reproducibles, sobre la base histórica de carga citada. [Informe histórico R1 y contrato de transición](../experiments/MOVEMENT_R1.md); [UML acotado](../architecture/uml/movement-core-spike-v1.puml). Direction, MovementEngine sin estado y MoveResult implementan `Board + Direction → MoveResult(kind, Board)`. Las clases, fixtures y los 40 tests de level se conservan intactos. MOVED/PUSHED crean un Board válido nuevo; BLOCKED reutiliza la misma instancia de entrada. No se agrega un GameState redundante, sesión, contadores ni historial.

### Evidencia separada por iteración — 2026-10-14

| Iteración de movimiento | Evidencia y alcance |
|---|---|
| R1, informe histórico | Reporta 40/40 carga y 31/31 movimiento PASS en Linux/JDK 17.0.20.1 y 26.0.2; copia Unicode/espacios y charset US-ASCII con 17; mutaciones y opciones inválidas detectadas |
| R2, revisión independiente realizada | Reprodujo 40/40 y 31/31 en Java 17; Java 26 NOT_RUN. No encontró defectos funcionales bloqueantes. Reprodujo 11 fallos al desactivar empujes sólo en copia temporal |
| R2, auditoría adicional temporal | Oráculo independiente: 150.000 transiciones satisfactorias sobre 37.500 tableros 2×3; no incorporado a las suites versionadas ni sumado a sus 31 casos |
| R3, validación del incremento | Reprodujo 40/40 carga y 31/31 movimiento en Linux/JDK 17.0.20.1 y 26.0.2, salida 0, release 17, UTF-8, -Xlint:all, -Werror y sólo java.base; temporales del runner eliminados |
| R3, UML | Sintaxis y renderizado comprobados con PlantUML; correspondencia con tipos, firma y contrato revisada |

Los 31 casos incluyen 2.664 transiciones dentro del caso 30, integración TXT, destinos y preservación de estados anteriores. El caso 30 comprueba invariantes, pero no determina independientemente el outcome correcto de cada intento; los casos golden sí comparan Kind y tableros esperados. Ambos runners compilan una vez y admiten la suite adicional opcional; la revisión estática para Bash 3.2 y PowerShell 5.1/7 no demuestra ejecución nativa Windows/macOS, que sigue NOT_RUN. [Comandos, pruebas y límites](../testing/TEST_STRATEGY.md).

RF-03/RF-04 tienen cobertura PARTIAL: jugador y caja normal sobre piso/destino, paredes y bordes. Flechas/UI, hielo, cajas especiales, cerrojos, Undo, Restart, victoria/progresión, score, audio y extras siguen pendientes. IMPLEMENTED_EXPERIMENTAL y TESTED describen capacidades y evidencia; TEAM_ACCEPTED requiere ratificación explícita e INTEGRATED_IN_MAIN requiere evidencia Git separada. Ninguno de los dos últimos está acreditado para movimiento. El checkpoint local no ratifica arquitectura ni cierra OQ-08/OQ-11/D-011.

## Diseño, evidencia y decisiones

Terrain/Occupant, polimorfismo de cajas, Builder, Memento, Observer y Strategy son **PREFERRED**, no ACCEPTED. Herencia/composición sigue abierta. SoundManager Singleton es propuesto; Adapter no se justifica en baseline, Decorator depende de futuras funcionalidades. Métricas Undo separadas son **TEAM-PROPOSAL**, no decisión ratificada ni fórmula aprobada.

Los spikes A/B reportaron 8/8 y 12/12 con Java 21 y sin warnings históricamente. Son `HISTORICAL_EXTERNAL_EVIDENCE`; `REPRODUCIBILITY_IN_CURRENT_REPOSITORY = NOT_VERIFIED`. **No se ejecutaron tests Java en R3A.** En el preflight se ejecutaron `bash scripts/verify-repo-readonly.sh --require-clean` (PASS: 32 archivos y 49 enlaces relativos), `git fsck --full --no-progress` y `git diff --check` (PASS). Son verificaciones documentales y de Git; no validan gameplay ni la semántica del UML mediante un compilador.

Permanecen OQ-01…OQ-11: victoria/llave, salida hielo bloqueada, fragile cero/hielo, reinicio Undo, menos de 5, resistencia inicial, asociación/cierre muros, TXT, score, dos extras, Java/build. También revisar decisiones arquitectónicas y política de audio.

## Riesgos y mitigación

| Riesgo | Acción siguiente |
|---|---|
| TXT hardcodeado o formato tácito | Contrato ratificado y prueba con TXT escrito/modificado por otra persona |
| Patrones ornamentales / interfaces ceremoniales | Comparar alternativa simple y justificar fuerzas/colaboraciones |
| Snapshot alias o reversión incompleta | Pruebas de resistencia/muros/contadores y aislamiento |
| Métricas borran coste de Undo | Ratificar propuesta; ejemplos score y reinicios antes de implementar |
| Controller monolítico, reglas Swing o datos duplicados | Roles explícitos y core testeable sin GUI |
| UML/cápsula se confunden con estado ejecutado | Contrastar HEAD/vivos; pruebas históricas etiquetadas |
| Licencia no cumple dominio público | Ledger por recurso; no incorporar hasta verificar |
| IA no comprendida / entrega incompleta | Revisión humana, declaración y defensa; checklist TODO |
| Repo privado incumple entrega futura | Decisión de equipo antes de entrega, sin cambio en bootstrap |

## Próxima iteración segura

La revisión técnica independiente R2 está realizada y la validación R3 confirma el subconjunto experimental. Los siguientes gates son revisión grupal del contrato/modelo y pruebas nativas Windows/macOS antes de afirmar validación nativa completa. Publicación e integración requieren autorización posterior. Conservar las decisiones abiertas y el alcance; cualquier ampliación funcional requiere una iteración autorizada. Builder permanece PREFERRED, TXT experimental/OQ-08 OPEN y Java/build OQ-11 OPEN.

Resolver las preguntas que afecten cada mecánica antes de implementarla. Obtener los bytes de los spikes históricos si se desean reutilizar, sin tratarlos como código productivo auditado. El spike no convierte la carga en una funcionalidad productiva completa ni autoriza por sí solo su integración o una ampliación de alcance.

No se necesita ningún chat anterior para planificar esa continuación: leer AGENTS → requisitos/preguntas → decisiones → arquitectura/responsabilidades/UML → trazabilidad/pruebas → TODO/frescura. Las cápsulas se usan sólo para profundizar historia, y sus contratos antiguos no bloquean ni autorizan la tarea actual.
