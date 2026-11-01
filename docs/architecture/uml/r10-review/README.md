# R10 — Modelo de clases `impaciencia` (DRAFT)

**Modelo fuente:** `e7d4c33f3fb17bd3a72bca25ddee1ad37f529087`. **Estado:** `PARTIAL_VERIFIED_UML_MODEL`.

Este directorio es un incremento de **arquitectura/UML exclusivamente**, no modifica el videojuego ni las reglas de negocio. El entregable formal de StarUML sigue **PENDING** hasta confirmar la apertura, editabilidad, guardado y reapertura del archivo `.mdj` en la aplicación nativa.

## Contenido

- `Los_Force_Push_R10_AS_IMPLEMENTED_DRAFT.mdj`: proyecto StarUML *preliminar*, 9 diagramas y 82 tipos de modelo.
- `00_*.puml` a `08_*.puml`: nueve vistas de revisión, sin certificación de compilador PlantUML.
- `INFORME_R10_SANDBOX.md` y `AUDITORIA_ARQUITECTURA_R10_PREVIA.md`: análisis de diseño y discrepancias; la segunda es evidencia histórica inmutable.
- `TRAZABILIDAD_TIPOS.csv`, `TRAZABILIDAD_RELACIONES.csv`, `INVENTARIO_GITHUB_50_FUENTES.txt`: trazabilidad y cobertura de identidad de fuentes Java (50/50, no AST exhaustivo).
- `verificar_paquete.py`, `RESULTADOS_VALIDACION.json`, `COBERTURA_JAVA_UML.md`: pruebas estáticas documentadas, no validación StarUML.

## Validación estática reproducible

Desde esta misma carpeta, con Python 3 instalado:

```powershell
python .\verificar_paquete.py
```

No se requiere Python ni StarUML para ejecutar **el juego**; esta herramienta sirve exclusivamente al entregable UML.

## Revisión nativa obligatoria

1. Abrir `.mdj` en StarUML, inspeccionar los nueve diagramas y confirmar que cada elemento es editable.
2. Editar temporalmente un elemento, deshacer, guardar una **copia** y reabrirla.
3. Confirmar las jerarquías privadas anidadas `VisualThemes.*`, `LockChangeSubject.Observer`, `GameSession.Snapshot` y los vínculos MVC, Builder, Memento, Strategy, Abstract Factory, Command, Observer.
4. Documentar versión real y resultado. Si existe fallo de apertura, **no** sustituirlo por imagen o afirmar PASS.

## Restricciones de autoridad

`impaciencia` es una línea individual. `RF12_ACADEMIC_ACCEPTANCE=NOT_CONFIRMED`; `TEAM_ACCEPTANCE=NOT_RECORDED`; `ASSET_RIGHTS=PENDING_EXTERNAL_EVIDENCE`. La existencia de un diagrama no autoriza su integración en `main`.

Fuentes canónicas: [estado UML](../STARUML_STATUS.md), [requisitos](../../../requirements/REQUIREMENTS.md), [decisiones](../../../decisions/DECISION_LOG.md), [preguntas](../../../requirements/OPEN_QUESTIONS.md).
