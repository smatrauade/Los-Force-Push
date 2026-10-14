# Estado vivo — Los Force Push

```text
AS_OF = 2026-10-14
REPOSITORY = smatrauade/Los-Force-Push
PRIMARY_DOMAIN = SOKOBAN_LEVEL_LOADING
IMPLEMENTATION_STATUS = EXPERIMENTAL
EXPERIMENT_BRANCH = feat/level-loading-spike-v1
EXPERIMENT_BASE = 1943053ec520ff3d8be0aecb21c2f417f3126380
EXPERIMENT_TESTS = 40_PASS_0_FAIL_ON_JAVA_17_AND_26
LINUX_NATIVE_TEST = PASS
WINDOWS_NATIVE_TEST = NOT_RUN
MACOS_NATIVE_TEST = NOT_RUN
BASE_BRANCH = main
BOOTSTRAP_STATUS = INTEGRATED
PRODUCTION_IMPLEMENTATION = NONE
JAVA_TARGET = OPEN
BUILD_TOOL = OPEN
TXT_FORMAT = OPEN
NEXT_TECHNICAL_DOMAIN = SOKOBAN_LEVEL_LOADING
```

## Estado comprobado y cómo continuar

El preflight R3A del 2026-10-14, vía Git y GitHub CLI como LucasCap12, confirmó el repositorio privado correcto, permisos de lectura/escritura, rama predeterminada `main`, checkout limpio y coincidencia con `origin/main` (divergencia 0/0). En aquel preflight anterior al PR #2 no se encontraron commits posteriores al merge del bootstrap ni otro PR de reconciliación; esa observación se conserva como historia fechada.

**Evento histórico confirmado:** el [PR #1](https://github.com/smatrauade/Los-Force-Push/pull/1) se fusionó el `2026-10-08T20:00:25Z` mediante el commit `ff6fbf170bd9782fb703e1ab4fa1e811ec0782ed`. Ese SHA identifica el merge histórico, no un HEAD permanente. El [preflight inicial](BOOTSTRAP_PREFLIGHT.md) describe el estado anterior al PR y se conserva como evidencia fechada.

El bootstrap documental está integrado en `main`: documentación, UML candidato, script de verificación y estructura `src/main/java`, `src/main/resources` y `src/test/java` con placeholders. La base histórica indicada en EXPERIMENT_BASE no contenía clases Java ni configuración de build. Esta rama incorpora fuentes y tests experimentales, sin ratificación productiva. El merge del bootstrap integró documentación y estructura; no constituye ratificación grupal de las propuestas arquitectónicas.

La fuente del estado actual es Git contrastado con la documentación viva: inspeccionar `git rev-parse HEAD`, `git log`, el árbol y los PRs antes de continuar. Las cápsulas y los informes de preflight anteriores son historia, no sustituyen esa verificación. Las revisiones exactas evaluadas se identifican en el informe de revisión/entrega; evitar autorreferenciar el SHA del commit que contiene este documento.

Requisitos normalizados y aclaraciones están autocontenidos. PDF/libros/materiales originales no están disponibles aquí; SOURCE_REGISTRY distingue bytes presentes de relato histórico. UML v0.5 y cápsula anterior sí estaban en el ZIP y se preservaron byte a byte.

## Incremento experimental posterior al PR #2

El PR #2 se confirmó MERGED el `2026-10-14T17:45:53Z`, commit histórico `1943053ec520ff3d8be0aecb21c2f417f3126380`. Esta es la base del experimento, no un HEAD permanente. La sincronización local autorizada fue FF-only; no se fusionó ningún PR desde esta ejecución.

Sólo en `feat/level-loading-spike-v1`: [TXT candidato y comparación](../experiments/LEVEL_LOADING_R1.md), fuentes bajo `src/main/java/ar/edu/uade/sokoban/level`, fixtures y tests bajo `src/test`, y [UML del spike](../architecture/uml/level-loading-spike-v1.puml). Implementado el subconjunto paredes/piso/destinos/jugador/cajas normales, incluidas superposiciones sobre destinos. Sin movimiento, cajas especiales, Undo, score, victoria, GUI ni audio.

Evidencia R3 del 2026-10-14: `scripts/test-level-loading.sh`, compilación/ejecución real con JDK 17.0.20.1 y JDK 26.0.2, opciones estrictas y sólo java.base, **38 PASS / 0 FAIL en cada JDK**. Los tests 25/38 detectaron la desactivación temporal del límite de bytes en una copia descartable (dos fallos esperados, fuente original preservada). PlantUML 1.2020.02 y Graphviz 14.1.2 comprobaron y renderizaron el UML experimental. Las fuentes, fixtures y comandos de esta rama permiten reproducir las pruebas; la base histórica precede a este incremento. Revisión del equipo pendiente; D-011 PREFERRED, OQ-08/OQ-11 OPEN.

Evidencia R4 del 2026-10-14: **40 PASS / 0 FAIL en cada JDK** sobre Linux, incluidas rutas con espacios/Unicode, recarga externa y diagnósticos con Locale.ROOT. Runner Unix compatible por diseño con Bash 3.2/GNU/BSD y runner Windows PowerShell 5.1/7 revisado estáticamente. Copia temporal con checkout/TMPDIR de nombres Unicode y espacios: PASS con ambos JDK; Java 17 también con charset predeterminado US-ASCII. Espacios de niveles protegidos en EditorConfig y atributos LF acotados. Windows/macOS nativos NOT_RUN: verificarlos antes de afirmar portabilidad nativa completa. Código/contrato/modelo siguen experimentales; esta evidencia no ratifica D-011 ni OQ-08/OQ-11.

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

Revisar el incremento experimental **SOKOBAN_LEVEL_LOADING**, su contrato TXT candidato y la comparación Builder/directa junto con pruebas nativas Windows/macOS antes de decidir su publicación. El experimento `TXT → Parser → Builder → Level → Board` está implementado en esta rama, con hipótesis explícitas y pruebas reproducibles. OQ-08 y OQ-11 siguen abiertas; revisar alcance y entorno antes de ampliar el experimento o convertirlo en carga productiva, sin presentar el formato ni la versión Java usada como decisiones definitivas. Builder conserva `PREFERRED` hasta una ratificación explícita del equipo.

Resolver las preguntas que afecten cada mecánica antes de implementarla. Obtener los bytes de los spikes históricos si se desean reutilizar, sin tratarlos como código productivo auditado. El spike no convierte la carga en una funcionalidad productiva completa ni autoriza por sí solo su integración o una ampliación de alcance.

No se necesita ningún chat anterior para planificar esa continuación: leer AGENTS → requisitos/preguntas → decisiones → arquitectura/responsabilidades/UML → trazabilidad/pruebas → TODO/frescura. Las cápsulas se usan sólo para profundizar historia, y sus contratos antiguos no bloquean ni autorizan la tarea actual.
