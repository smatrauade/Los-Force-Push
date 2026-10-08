# Estado vivo — Los Force Push

```text
AS_OF = 2026-10-08
REPOSITORY = smatrauade/Los-Force-Push
PRIMARY_DOMAIN = SOKOBAN_REPOSITORY_BOOTSTRAP_AND_PROJECT_CONTINUITY
BASE_BRANCH = main
BASE_HEAD = 12f9a7fbc956f40bd3bac526ec2aaf66d8458593
BOOTSTRAP_BRANCH = docs/bootstrap-sokoban-autocontenido-v1
PRODUCTION_IMPLEMENTATION = NONE
JAVA_TARGET = OPEN
BUILD_TOOL = OPEN
```

## Estado comprobado y cómo continuar

El preflight actual, vía GitHub CLI como LucasCap12, confirmó repo privado, main, lectura/escritura, un commit Initial commit, sólo README trivial, sin otras ramas ni PRs. El conector alternativo devolvió 404; la CLI superó esa limitación. Ver BOOTSTRAP_PREFLIGHT para evidencia fechada.

La rama de bootstrap integra documentación y placeholders sin gameplay, toolchain, dependencias ni CI. Este documento pertenece a la rama propuesta: su presencia no afirma que `main` la haya adoptado. Consultar el PR y el HEAD real para publicación/revisión/merge. El SHA final y URL real se registran en el informe externo/PR; obtener el checkout con `git rev-parse HEAD`.

Requisitos normalizados y aclaraciones están autocontenidos. PDF/libros/materiales originales no están disponibles aquí; SOURCE_REGISTRY distingue bytes presentes de relato histórico. UML v0.5 y cápsula anterior sí estaban en el ZIP y se preservaron byte a byte.

## Diseño, evidencia y decisiones

Terrain/Occupant, polimorfismo de cajas, Builder, Memento, Observer y Strategy son **PREFERRED**, no ACCEPTED. Herencia/composición sigue abierta. SoundManager Singleton es propuesto; Adapter no se justifica en baseline, Decorator depende de futuras funcionalidades. Métricas Undo separadas son **TEAM-PROPOSAL**, no decisión ratificada ni fórmula aprobada.

Los spikes A/B reportaron 8/8 y 12/12 con Java 21 y sin warnings históricamente. Son `HISTORICAL_EXTERNAL_EVIDENCE`; `REPRODUCIBILITY_IN_CURRENT_REPOSITORY = NOT_VERIFIED`. **No se ejecutaron tests Java en esta iteración.** Las verificaciones actuales auditan documentación, integridad y Git.

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

Revisión humana del PR documental. Después, con nueva tarea autorizada: acordar TXT y Java/build, comparar Builder con construcción directa y ratificar diseño; resolver preguntas que afecten cada mecánica antes de implementarla. Obtener spikes reales si se desean reutilizar, sin tratarlos como código productivo auditado.

No se necesita ningún chat anterior para planificar esa continuación: leer AGENTS → requisitos/preguntas → decisiones → arquitectura/responsabilidades/UML → trazabilidad/pruebas → TODO/frescura. Las cápsulas se usan sólo para profundizar historia, y sus contratos antiguos no bloquean ni autorizan la tarea actual.
