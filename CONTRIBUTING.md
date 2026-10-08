# Contribuir

Una rama contiene un objetivo técnico principal. Antes de escribir, realizar el preflight de [AGENTS.md](AGENTS.md), inspeccionar cambios de compañeros y PRs equivalentes, y partir de un HEAD actualizado sin modificar directamente `main`.

Tras confirmar permisos, identidad y worktree limpio:

```bash
git fetch origin
# Revisar origin/main y su SHA antes de crear la rama.
git switch -c <rama-de-alcance-unico> origin/main
```

No ejecutar este ejemplo ciegamente si hay trabajo pendiente o una rama equivalente. Usar commits pequeños y explicables (`docs:`, `feat:`, `test:`, `fix:`). No force-push, acciones CI nuevas, pagos ni cambios globales de Git.

Cada PR explica problema, comportamiento resultante, alcance, decisiones, alternativas, pruebas ejecutadas, documentos afectados y riesgos. Solicitar revisión de otro integrante. La IA debe declararse y su resultado debe ser comprendido; no autoaprobar ni hacer merge automático.

Antes del PR, revisar `git diff --check`, diff completo y verificaciones pertinentes. Para documentación ejecutar `bash scripts/verify-repo-readonly.sh`; para código, las pruebas definidas cuando exista toolchain. Las reglas del core se probarán sin Swing. Un resultado histórico no cuenta como ejecución actual.

Actualizar [política de frescura](docs/continuity/FRESHNESS_POLICY.md), decisiones, arquitectura/UML y trazabilidad cuando corresponda. No editar cápsulas antiguas. No agregar una dependencia sin justificar problema, alternativa estándar, coste y decisión. No implementar una regla abierta sin registrar su cierre.

La aprobación y el merge pertenecen al equipo y requieren autorización separada. La visibilidad pública de entrega también es una decisión futura, no una acción de este bootstrap.
