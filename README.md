# Los Force Push — Trabajo Integrador Sokoban

Proyecto académico de Ingeniería en Informática, UADE: **Proceso de Desarrollo de Software**, profesor **Jonathan Leonel Pepe**. La tecnología exigida es **Java, Swing y GUI 2D**. El objetivo es un diseño defendible mediante SOLID, GRASP, MVC y GoF.

## Estado al 2026-10-08

Este bootstrap es documental y estructural. **Todavía no hay juego ejecutable, clases Java ni build configurado.** El repositorio auditado tenía sólo `README.md` con `# Los-Force-Push`, un commit, una rama `main` y ningún PR. La base fue `12f9a7fbc956f40bd3bac526ec2aaf66d8458593`.

| Área | Estado |
|---|---|
| Requisitos | Normalizados desde el prompt autorizado; PDF original no disponible |
| Arquitectura y UML | Candidatos; requieren revisión del equipo |
| Builder / Memento / Observer / Strategy | `PREFERRED`, no decisiones aceptadas |
| Métricas de Undo fuera del Memento | `TEAM-PROPOSAL`, pendiente de ratificación |
| Spikes Java 21 | Resultados históricos reportados 8/8 y 12/12; no reproducidos aquí |
| TXT, score, dos funcionalidades adicionales | `OPEN` |
| Versión Java / herramienta de build | `OPEN` |
| StarUML, assets, video e informe final | Pendientes |

Consultar siempre el [estado vivo](docs/continuity/CURRENT_STATE.md) y contrastarlo con el HEAD actual. Las cápsulas son snapshots históricos y no reemplazan ese contraste.

## Empezar sin chats anteriores

1. Leer [AGENTS.md](AGENTS.md), el contrato operativo.
2. Leer [CURRENT_STATE.md](docs/continuity/CURRENT_STATE.md).
3. Leer los [requisitos normalizados](docs/requirements/REQUIREMENTS.md) y [preguntas abiertas](docs/requirements/OPEN_QUESTIONS.md).
4. Revisar el [registro de decisiones](docs/decisions/DECISION_LOG.md), la [arquitectura](docs/architecture/ARCHITECTURE.md) y las [responsabilidades](docs/architecture/RESPONSIBILITIES.md).
5. Consultar [trazabilidad](docs/traceability/TRACEABILITY_MATRIX.md), [TODO](TODO.md) y [política de actualización](docs/continuity/FRESHNESS_POLICY.md).

El [índice documental](docs/README.md) enlaza fuentes, evidencia, UML y continuidad. La [cápsula de bootstrap](docs/continuity/capsules/BOOTSTRAP_2026-10-08.md) resume esta integración; la cápsula anterior se preserva como evidencia recibida.

## Requisitos centrales

Niveles leídos realmente desde TXT externos; jugador por flechas; empuje de una sola caja; cajas normal, frágil y llave; destinos, hielo, cerrojos y muros abiertos/cerrados. Undo conserva hasta 15 movimientos y retrocede 5 por uso, con hasta 3 usos consecutivos. Victoria avanza al próximo nivel. HUD muestra movimientos, empujes y nivel, con botones Undo y Restart. El resumen de nivel muestra también usos de Undo y puntaje.

Todos los elementos del tablero usan imágenes y las acciones tienen efectos sonoros. Los assets deben ser de dominio público, con licencia verificada individualmente. Se requieren **dos funcionalidades adicionales** que apliquen patrones; resolver un requisito obligatorio con un patrón no lo convierte automáticamente en adicional.

El profesor podría modificar o escribir un TXT compatible para verificar que los niveles no están hardcodeados. No hace falta medir tiempo ni incorporar animaciones. Las ambigüedades están identificadas como OQ-01 a OQ-11 y no se resuelven en este PR.

## Clonar y verificar la documentación

```bash
git clone https://github.com/smatrauade/Los-Force-Push.git
cd Los-Force-Push
bash scripts/verify-repo-readonly.sh
```

Requiere Git, Bash y Python 3 para la auditoría documental; **Python no es una decisión sobre el runtime del juego**. El script inspecciona archivos y Git, no hace fetch, commits ni escrituras. Para auditar el alcance de este bootstrap usar `--bootstrap`; después del commit agregar `--require-clean`. No reemplaza el preflight remoto de permisos/PRs.

No existe aún un comando para ejecutar el juego. El primer build reproducible requiere cerrar OQ-11 con una versión Java y sistema de construcción, documentar instalación/compilación/ejecución/pruebas y verificar esos comandos desde un clon limpio. La estructura `src/main/java`, `src/main/resources` y `src/test/java` se reserva sin clases vacías ni versiones inventadas.

## Colaboración y entrega

Seguir [CONTRIBUTING.md](CONTRIBUTING.md): rama de alcance acotado, commits, PR y revisión por otro integrante. No escribir directamente en `main`, usar force-push ni activar GitHub Actions en este alcance.

El repositorio permanece **privado**. La consigna exige uno público para la entrega: el equipo deberá decidir y ejecutar ese cambio más adelante. También faltan README de ejecución, gameplay en video, diagrama de clases StarUML e informe técnico; la entrega institucional deberá incluir a los integrantes en copia.

Este bootstrap fue preparado y revisado con apoyo de IA (Codex). El equipo debe revisar las propuestas, declarar el uso de IA en el informe y comprender, validar, adaptar y defender individualmente el resultado. No se implementó gameplay ni se volvió a ejecutar ningún spike histórico.
