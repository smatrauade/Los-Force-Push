# Estado vivo — Los Force Push

```text
AS_OF = 2026-10-14
REPOSITORY = smatrauade/Los-Force-Push
PRIMARY_DOMAIN = PROJECT_DOCUMENTATION_RECONCILIATION
BASE_BRANCH = main
BOOTSTRAP_STATUS = INTEGRATED
PRODUCTION_IMPLEMENTATION = NONE
JAVA_TARGET = OPEN
BUILD_TOOL = OPEN
TXT_FORMAT = OPEN
NEXT_TECHNICAL_DOMAIN = SOKOBAN_LEVEL_LOADING
```

## Estado comprobado y cómo continuar

El preflight R3A del 2026-10-14, vía Git y GitHub CLI como LucasCap12, confirmó el repositorio privado correcto, permisos de lectura/escritura, rama predeterminada `main`, checkout limpio y coincidencia con `origin/main` (divergencia 0/0). No se encontraron commits posteriores al merge del bootstrap ni otro PR de reconciliación.

**Evento histórico confirmado:** el [PR #1](https://github.com/smatrauade/Los-Force-Push/pull/1) se fusionó el `2026-10-08T20:00:25Z` mediante el commit `ff6fbf170bd9782fb703e1ab4fa1e811ec0782ed`. Ese SHA identifica el merge histórico, no un HEAD permanente. El [preflight inicial](BOOTSTRAP_PREFLIGHT.md) describe el estado anterior al PR y se conserva como evidencia fechada.

El bootstrap documental está integrado en `main`: documentación, UML candidato, script de verificación y estructura `src/main/java`, `src/main/resources` y `src/test/java` con placeholders. No hay clases Java, configuración de build ni implementación productiva verificada. El merge integra esos archivos; no constituye ratificación grupal de las propuestas arquitectónicas.

La fuente del estado actual es Git contrastado con la documentación viva: inspeccionar `git rev-parse HEAD`, `git log`, el árbol y los PRs antes de continuar. Las cápsulas y los informes de preflight anteriores son historia, no sustituyen esa verificación. El SHA final de esta reconciliación y su PR se registran fuera de su propio commit, en el informe de entrega/PR.

Requisitos normalizados y aclaraciones están autocontenidos. PDF/libros/materiales originales no están disponibles aquí; SOURCE_REGISTRY distingue bytes presentes de relato histórico. UML v0.5 y cápsula anterior sí estaban en el ZIP y se preservaron byte a byte.

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

Tras la revisión humana de esta reconciliación, el próximo dominio técnico previsto es **SOKOBAN_LEVEL_LOADING**: preparar un contrato TXT candidato y comparar Builder con construcción directa mediante un spike acotado `TXT → Parser → Builder → Level`, con hipótesis explícitas y pruebas reproducibles. OQ-08 y OQ-11 siguen abiertas; acordar el alcance experimental y el entorno antes de ejecutarlo, sin presentar el formato ni la versión Java usada como decisiones definitivas. Builder conserva `PREFERRED` hasta una ratificación explícita del equipo.

Resolver las preguntas que afecten cada mecánica antes de implementarla. Obtener los bytes de los spikes históricos si se desean reutilizar, sin tratarlos como código productivo auditado. Esta reconciliación no implementa el cargador ni autoriza por sí sola la siguiente ejecución.

No se necesita ningún chat anterior para planificar esa continuación: leer AGENTS → requisitos/preguntas → decisiones → arquitectura/responsabilidades/UML → trazabilidad/pruebas → TODO/frescura. Las cápsulas se usan sólo para profundizar historia, y sus contratos antiguos no bloquean ni autorizan la tarea actual.
