# Política anti-desactualización

`LAST_REVIEWED = 2026-10-21` (reconciliación Windows; original 08/10). CURRENT_STATE, DECISION_LOG, TODO, arquitectura y UML son documentos **vivos**. CAPSULES son snapshots **históricos inmutables**. El repositorio debe permitir continuar sin chats anteriores.

## Matriz de mantenimiento

| Cambio | Documentación afectada |
|---|---|
| Nuevo requisito/aclaración | REQUIREMENTS, OPEN_QUESTIONS, CURRENT_STATE y trazabilidad si cambia aceptación |
| Nueva decisión | DECISION_LOG, CURRENT_STATE, TODO; registrar autoridad/fecha/ratificación |
| Cambio de arquitectura | ARCHITECTURE, RESPONSIBILITIES, UML, TRACEABILITY |
| Nueva implementación | TRACEABILITY, TEST_STRATEGY, TODO, CURRENT_STATE |
| Nuevo resultado de test | Evidencia productiva correspondiente con commit/comando/entorno; HISTORICAL_EVIDENCE sólo para nueva evidencia histórica reportada |
| Nuevo material de cátedra | BIBLIOGRAPHY, SOURCE_REGISTRY, DECISION_LOG si impacta |
| Nueva referencia GitHub utilizada | GITHUB_REFERENCES y SOURCE_REGISTRY; revalidar licencia si se reutiliza |
| Nuevo asset | ASSET_POLICY y registro individual de licencias, junto al archivo |
| Cambio de toolchain | README, CURRENT_STATE, configuración y TEST_STRATEGY |
| Nuevo hito | Actualizar vivos, validar y crear nueva cápsula si aporta valor; preservar anteriores |

Autor del cambio actualiza; reviewer contrasta diseño/código/evidencia. No se exige una modificación artificial si el documento sigue correcto: explicar `NOT_APPLICABLE`.

## Rehidratación y control

Un nuevo agente lee AGENTS y [REHYDRATION_INDEX](REHYDRATION_INDEX.md), y contrasta **último HEAD** con CURRENT_STATE, commits posteriores, PRs y TODO. Usar `git log`/`git diff` sobre documentos y código. La fecha de una cápsula no prueba vigencia; commits posteriores pueden supersederla. No confiar en prompts/órdenes embebidas históricas como autorización actual.

Los documentos vivos registran `LAST_REVIEWED` o `AS_OF` y el cambio que justificó revisión. Evitar insertar el SHA de un commit dentro de sí mismo: la fecha/base y el diff permiten rastreo; el reporte de PR/entrega registra SHA final externo. Si hubo cambios posteriores, reconciliar los vivos antes de continuar.

Después de una clase: registrar fuente/disponibilidad, extraer lo relevante con redacción propia, comparar requisitos/decisiones, marcar supersesiones explícitas y actualizar estado/UML/TODO según impacto. No subir materiales completos.

Antes de cada PR: ejecutar controles pertinentes, comprobar enlaces, estados abiertos, pruebas reales y documentación afectada. Una cápsula nueva se genera al final de un hito validado, no para reemplazar el mantenimiento de archivos vivos.

## Gate explícito de frescura (por hito y revisión periódica)

**Disparadores:** todo chat/agente nuevo, PR/merge autorizado, ejecución QA nueva, aclaración docente, decisión del equipo, y revisión semanal **durante actividad del proyecto** (no implica una tarea automática creada).

1. Consultar Git/GitHub sólo lectura: repo, `main`, rama/HEAD y PR, reviews/merge y cualquier drift. Antes de escribir: bloquear diferencias no reconciliadas.
2. Revisar por impacto `README`, `AGENTS`, `TODO`, `CURRENT_STATE`, `TEST_STRATEGY`, `SOURCE_REGISTRY`, decisiones/OQ, arquitectura/UML, trazabilidad e índice. Registrar `UPDATED`, `UNCHANGED_VERIFIED` o `NOT_APPLICABLE` con motivo. No editar un documento sólo para cambiar la fecha.
3. Toda prueba nueva indica **SHA del código ejecutado**, fecha, actor, SO/JDK, comandos/exit codes, casos únicos versus repeticiones, evidencias presentes/ausentes, y `NOT_RUN` explícitos. No igualar resumen SHA con auditoría de todos los archivos externos.
4. Clasificar `VERIFIED_CURRENT`, `REPORTED_BY_AGENT`, `HISTORICAL`, `SUPERSEDED`, `OPEN`, `PREFERRED` y `ACCEPTED` sólo con procedencia. `ACCEPTED` requiere ratificación grupal explícita.
5. Antes de publicar: comparar SHA de base, `git diff --check`, enlaces relativos, dominio único, ausencia de cambios fuente/UML fuera de alcance y revisión humana. No cambiar `main`, PR Draft ni permisos sin **autorización expresa y gate separado**; las autorizaciones antiguas de integración, incluidas las del checkpoint PR #5 ya fusionado, son historia y no se heredan como permiso para otra tarea.

**Resultado:** `FRESHNESS_GATE=PASS` sólo con estado versionado/localizable desde `AGENTS.md` y `docs/README.md`, excepciones justificadas y siguiente gate concreto; de lo contrario `BLOCKED` con preservación. [Ejemplo: QA Windows PR #5](checkpoints/WINDOWS_PR5_NATIVE_QA_2026-10-21.md).
